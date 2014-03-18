package org.elasticsearch.index.analysis;

import com.google.common.base.CharMatcher;   // Guava
import org.apache.commons.lang3.StringUtils; // Apache StringUtils
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.elasticsearch.index.analysis.stemmer.turkish.states.DerivationalState;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NominalVerbState;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NounState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.Suffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.DerivationalTransition;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.NominalVerbTransition;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.NounTransition;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.StringIndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TurkishStemmer {

  public static final String ALPHABET = "abcçdefgğhıijklmnoöprsştuüvyz";
  public static final String VOWELS = "üiıueöao";
  public static final String CONSONANTS = "bcçdfgğhjklmnprsştvyz";
  public static final String ROUNDED_VOWELS = "oöuü";
  public static final String UNROUNDED_VOWELS = "iıea";
  public static final String FOLLOWING_ROUNDED_VOWELS = "aeuü";
  public static final String FRONT_VOWELS = "eiöü";
  public static final String BACK_VOWELS = "ıuao";

  public static final String DEFAULT_PROTECTED_WORDS_FILE = "protected_words.txt";
  public static final String DEFAULT_VOWEL_HARMONY_EXCEPTIONS_FILE = "vowel_harmony_exceptions.txt";
  public static final String DEFAULT_LAST_CONSONANT_EXCEPTIONS_FILE = "last_consonant_exceptions.txt";

  private static final EnumSet<NominalVerbState> nominalVerbStates = EnumSet.allOf(NominalVerbState.class);
  private static final EnumSet<NounState> nounStates = EnumSet.allOf(NounState.class);
  private static final EnumSet<DerivationalState> derivationalStates = EnumSet.allOf(DerivationalState.class);
  private static final EnumSet<NominalVerbSuffix>  nominalVerbSuffixes = EnumSet.allOf(NominalVerbSuffix.class);
  private static final EnumSet<NounSuffix> nounSuffixes = EnumSet.allOf(NounSuffix.class);
  private static final EnumSet<DerivationalSuffix> derivationalSuffixes = EnumSet.allOf(DerivationalSuffix.class);

  private static final int AVERAGE_STEMMED_SIZE = 4;

  private final CharArraySet protectedWords;
  private final CharArraySet vowelHarmonyExceptions;
  private final CharArraySet lastConsonantExceptions;

  public TurkishStemmer() {
    this.protectedWords          = TurkishStemmer.getDefaultProtectedWordSet();
    this.vowelHarmonyExceptions  = TurkishStemmer.getDefaultVowelHarmonySet();
    this.lastConsonantExceptions = TurkishStemmer.getDefaultLastConsonantSet();
  }

  public TurkishStemmer(final CharArraySet protectedWords,
                        final CharArraySet vowelHarmonyExceptions,
                        final CharArraySet lastConsonantExceptions) {
    this.protectedWords          = protectedWords;
    this.vowelHarmonyExceptions  = vowelHarmonyExceptions;
    this.lastConsonantExceptions = lastConsonantExceptions;
  }

  public int stem(char s[], int len) {
    return len;
  }

  /**
   * This method implements the state machine about nominal verb suffixes.
   *
   * It finds the possible stems of a word after applying the nominal verb
   * suffix removal.
   *
   * @param word the word that will get stemmed
   * @param stems a set of stems to populate
   */
  public final void nominalVerbsSuffixStripper(final String word,
                                               final Set<String> stems) {
    String stem, wordToStem;
    List<NominalVerbTransition> transitions;
    NominalVerbState initialState;
    NominalVerbTransition transition;

    wordToStem = word;

    if(nominalVerbStates.isEmpty() || nominalVerbSuffixes.isEmpty()) {
      stems.add(word);
      return;
    }

    initialState = NominalVerbState.getInitialState();

    transitions = new ArrayList<NominalVerbTransition>();

    initialState.addTransitions(wordToStem, transitions, null, false);

    while(!transitions.isEmpty()) {
      transition = transitions.remove(0);

      wordToStem = transition.word;

      stem = stemWord(wordToStem, transition.suffix);

      if(!stem.equals(wordToStem)) {
        if(transition.nextState.finalState()) {
          Iterator<NominalVerbTransition> iterator = transitions.iterator();
          NominalVerbTransition transitionToRemove;
          while(iterator.hasNext()) {
            transitionToRemove = iterator.next();
            if((transitionToRemove.startState == transition.startState &&
                transitionToRemove.nextState == transition.nextState) ||
                transitionToRemove.marked) {
              iterator.remove();
            }
          }

          stems.add(stem);
          transition.nextState.addTransitions(stem, transitions, null, false);
        } else {
          for(NominalVerbTransition similarTransition : transition
              .similarTransitions(transitions)) {
            similarTransition.marked = true;
          }

          transition.nextState.addTransitions(stem, transitions,
              transition.rollbackWord, true);
        }
      } else {
        if(transition.rollbackWord != null
            && transition.similarTransitions(transitions).isEmpty()) {
          stems.add(transition.rollbackWord);
        }
      }
    }

    if(stems.isEmpty())
      stems.add(word);
  }

  /**
   * This method implements the state machine about noun suffixes.
   *
   * It finds the possible stems of a word after applying the noun suffix removal.
   *
   * @param word the word that will get stemmed
   * @param stems a set of stems to populate
   */
  public final void nounSuffixStripper(final String word,
                                       final Set<String> stems) {
    String stem, wordToStem;
    List<NounTransition> transitions;
    NounState initialState;
    NounTransition transition;

    wordToStem = word;

    if(nounStates.isEmpty() || nounSuffixes.isEmpty()) {
      return;
    }

    initialState = NounState.getInitialState();

    transitions = new ArrayList<NounTransition>();

    initialState.addTransitions(wordToStem, transitions, null, false);

    while(!transitions.isEmpty()) {
      transition = transitions.remove(0);
      wordToStem = transition.word;

      stem = stemWord(wordToStem, transition.suffix);

      if(!stem.equals(wordToStem)) {
        if(transition.nextState.finalState()) {
          Iterator<NounTransition> iterator = transitions.iterator();
          NounTransition transitionToRemove;
          while(iterator.hasNext()) {
            transitionToRemove = iterator.next();
            if((transitionToRemove.startState == transition.startState &&
                transitionToRemove.nextState == transition.nextState)  ||
                transitionToRemove.marked) {
              transitions.remove(transitionToRemove);
            }
          }

          stems.add(stem);
          transition.nextState.addTransitions(stem, transitions, null, false);
        } else {
          for(NounTransition similarTransition : transition
              .similarTransitions(transitions)) {
            similarTransition.marked = true;
          }
          transition.nextState.addTransitions(stem, transitions,
              transition.rollbackWord, true);
        }
      } else {
        if(transition.rollbackWord != null
            && transition.similarTransitions(transitions).isEmpty()) {
          stems.add(transition.rollbackWord);
        }
      }
    }
  }

  /**
   * This method implements the state machine about derivational suffixes.
   *
   * It finds the possible stems of a word after applying the derivational
   * suffix removal.
   *
   * @param word the word that will get stemmed
   * @param stems a set of stems to populate
   */
  public final void derivationalSuffixStripper(final String word,
                                               final Set<String> stems) {
    String stem, wordToStem;
    List<DerivationalTransition> transitions;
    DerivationalState initialState;
    DerivationalTransition transition;

    wordToStem = word;

    if(derivationalStates.isEmpty() || derivationalSuffixes.isEmpty()) {
      return;
    }

    initialState = DerivationalState.getInitialState();

    transitions = new ArrayList<DerivationalTransition>();

    initialState.addTransitions(wordToStem, transitions, null, false);

    while(!transitions.isEmpty()) {
      transition = transitions.remove(0);
      wordToStem = transition.word;

      stem = stemWord(wordToStem, transition.suffix);

      if(!stem.equals(wordToStem)) {
        if(transition.nextState.finalState()) {
          Iterator<DerivationalTransition> iterator = transitions.iterator();
          DerivationalTransition transitionToRemove;
          while(iterator.hasNext()) {
            transitionToRemove = iterator.next();
            if((transitionToRemove.startState == transition.startState &&
                transitionToRemove.nextState == transition.nextState) ||
                transitionToRemove.marked) {
              transitions.remove(transitionToRemove);
            }
          }

          stems.add(stem);
          transition.nextState.addTransitions(stem, transitions, null, false);
        } else {
          for(DerivationalTransition similarTransition : transition
              .similarTransitions(transitions)) {
            similarTransition.marked = true;
          }
          transition.nextState.addTransitions(stem, transitions,
              transition.rollbackWord, true);
        }
      } else {
        if(transition.rollbackWord != null
            && transition.similarTransitions(transitions).isEmpty()) {
          stems.add(transition.rollbackWord);
        }
      }
    }
  }

  /**
   * Removes a certain suffix from the given word.
   *
   * @param word the word to remove the suffix from
   * @param suffix the suffix to be removed from the word
   * @return the stemmed word
   */
  public final String stemWord(String word, Suffix suffix) {
    String stemmedWord = word;

    if(shouldBeMarked(word, suffix) && suffix.match(word)) {
      stemmedWord = suffix.removeSuffix(stemmedWord);

      char optionalLetter = suffix.optionalLetter(stemmedWord);

      if(optionalLetter != '\0') {
        if(validOptionalLetter(stemmedWord, optionalLetter)) {
          // Remove the optional letter
          stemmedWord = stemmedWord.substring(0, stemmedWord.length() - 1);
        } else {
          stemmedWord = word;
        }
      }
    }

    return stemmedWord;
  }

  /**
   * It performs a post stemming process and returns the final stem.
   *
   * @param stems a set of possible stems
   * @param originalWord the original word that was stemmed
   * @return the final stem
   */
  public String postProcess(final Set<String> stems, final String originalWord) {
    Set<String> finalStems;
    finalStems = new HashSet<String>();

    for(String word : stems) {
      if (countSyllables(word) > 0)
        finalStems.add(lastConsonant(word));
    }

    List<String> sortedStems;
    sortedStems = new ArrayList<String>(finalStems);

    Collections.sort(new ArrayList<String>(finalStems), new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        return Math.abs(s1.length() - AVERAGE_STEMMED_SIZE) - Math.abs(s2.length() - AVERAGE_STEMMED_SIZE);
      }
    });

    if(sortedStems.isEmpty()) {
      return originalWord;
    } else {
      return sortedStems.get(0);
    }
  }

  /**
   * Gets the vowels of a word.
   *
   * @param   word  the word to get its vowels
   * @return        the vowels
   */
  public String vowels(String word) {
    CharMatcher char_matcher = CharMatcher.anyOf(CONSONANTS);

    return char_matcher.removeFrom(word);
  }

  /**
   * Gets the number of syllables of a word.
   *
   * @param   word  the word to count its syllables
   * @return        the number of syllables
   */
  public int countSyllables(String word) {
    return vowels(word).length();
  }

  /**
   * Checks the frontness harmony of two characters.
   *
   * @param   vowel     the first character
   * @param   candidate the second character
   * @return            whether the two characters have frontness harmony or not.
   */
  public boolean hasFrontness(char vowel, char candidate) {
    if ((StringUtils.containsAny(FRONT_VOWELS, vowel) &&
         StringUtils.containsAny(FRONT_VOWELS, candidate)) ||
        (StringUtils.containsAny(BACK_VOWELS, vowel) &&
         StringUtils.containsAny(BACK_VOWELS, candidate)))
      return true;

    return false;
  }

  /**
   * Checks the roundness harmony of two characters.
   *
   * @param   vowel     the first character
   * @param   candidate the second character
   * @return            whether the two characters have roundness harmony or not.
   */
  public boolean hasRoundness(char vowel, char candidate) {
    if ((StringUtils.containsAny(UNROUNDED_VOWELS, vowel) &&
        StringUtils.containsAny(UNROUNDED_VOWELS, candidate)) ||
       (StringUtils.containsAny(ROUNDED_VOWELS, vowel) &&
        StringUtils.containsAny(FOLLOWING_ROUNDED_VOWELS, candidate)))
      return true;

    return false;
  }

  /**
   * Checks the vowel harmony of two characters.
   *
   * @param   vowel     the first character
   * @param   candidate the second character
   * @return            whether the two characters have vowel harmony or not.
   */
  public boolean vowelHarmony(char vowel, char candidate) {
    return hasRoundness(vowel, candidate) && hasFrontness(vowel, candidate);
  }

  /**
   * Checks the vowel harmony of a word.
   *
   * @param   word  the word to check its vowel harmony
   * @return        whether the word has vowel harmony or not.
   */
  public boolean hasVowelHarmony(String word) {
    String vowelsOfWord = vowels(word);
    int wordLength  = vowelsOfWord.length();

    char vowel, candidate;

    try {
      vowel = vowelsOfWord.charAt(wordLength - 2);
    } catch(StringIndexOutOfBoundsException e) {
      return true;
    }

    try {
      candidate = vowelsOfWord.charAt(wordLength - 1);
    } catch(StringIndexOutOfBoundsException e) {
      return true;
    }

    return vowelHarmony(vowel, candidate);
  }

  /**
   * Checks the last consonant rule of a word.
   *
   * @param   word  the word to check its last consonant
   * @return        the new word affected by the last consonant rule
   */
  public String lastConsonant(String word) {
    if(lastConsonantExceptions.contains(word))
      return word;

    int wordLength = word.length();
    char lastChar = word.charAt(wordLength - 1);

    switch(lastChar) {
      case 'b':
        lastChar = 'p';
        break;
      case 'c':
        lastChar = 'ç';
        break;
      case 'd':
        lastChar = 't';
        break;
      case 'ğ':
        lastChar = 'k';
        break;
    }

    return StringUtils.chop(word) + lastChar;
  }

  /**
   * Checks whether an optional letter is valid or not.
   *
   * @param   word      the word to check its last letter
   * @param   candidate the last character candidate
   * @return            whether is valid or not
   * @note    One should check if candidate character exists or not.
   */
  public boolean validOptionalLetter(String word, char candidate) {
    int wordLength = word.length();
    char previousChar;

    try {
      previousChar = word.charAt(wordLength - 2);
    } catch(StringIndexOutOfBoundsException e) {
      return false;
    }

    if(StringUtils.containsAny(VOWELS, candidate)) {
      return StringUtils.containsAny(CONSONANTS, previousChar);
    } else {
      return StringUtils.containsAny(VOWELS, previousChar);
    }
  }

  /**
   * Checks whether a word is written in Turkish alphabet or not.
   *
   * @param  word  the word to check its letters
   * @return       whether contains only Turkish letters or not.
   */
  public boolean turkish(String word) {
    return StringUtils.containsOnly(word, ALPHABET);
  }

  /**
   * Checks whether a stem process should proceed or not.
   *
   * @param word the word to check for stem
   * @return     whether to proceed or not
   */
  public boolean proceedToStem(String word) {
    if(!turkish(word) || this.protectedWords.contains(word) || word.isEmpty() ||
        countSyllables(word) <= 1 ||
        (!hasVowelHarmony(word) &&
            !this.vowelHarmonyExceptions.contains(word))) {
      return false;
    }

    return true;
  }

  /**
   * Checks if a word should be stemmed or not.
   *
   * @param word the word to be checked
   * @param suffix the suffix that will be removed from the word
   * @return whether the word should be stemmed or not
   */
  public boolean shouldBeMarked(final String word, final Suffix suffix) {
    if(!this.protectedWords.contains(word) &&
        (suffix.checkHarmony() &&
            (hasVowelHarmony(word) ||
             this.vowelHarmonyExceptions.contains(word))) ||
         !suffix.checkHarmony()) {
      return true;
    }

    return false;
  }

  /**
   * Gets the default set of protected words.
   *
   * @return a set of protected words
   */
  public static final CharArraySet getDefaultProtectedWordSet() {
    return DefaultSetHolder.DEFAULT_PROTECTED_WORDS;
  }

  /**
   * Gets the default set of vowel harmony exceptions.
   *
   * @return a set of vowel harmony exceptions
   */
  public static final CharArraySet getDefaultVowelHarmonySet() {
    return DefaultSetHolder.DEFAULT_VOWEL_HARMONY_EXCEPTIONS;
  }

  /**
   * Gets the default set of last consonant exceptions.
   *
   * @return a set of last consonant exceptions
   */
  public static final CharArraySet getDefaultLastConsonantSet() {
    return DefaultSetHolder.DEFAULT_LAST_CONSONANT_EXCEPTIONS;
  }

  /**
   * Creates a CharArraySet from a file.
   *
   * @param stopwords
   *          Input stream from the stopwords file
   *
   * @param matchVersion
   *          the Lucene version for cross version compatibility
   * @return a CharArraySet containing the distinct stopwords from the given
   *         file
   * @throws IOException
   *           if loading the stopwords throws an {@link IOException}
   */
  private static CharArraySet loadWordSet(InputStream file, Version matchVersion)
      throws IOException {
    Reader reader = null;
    try {
      reader = IOUtils.getDecodingReader(file, IOUtils.CHARSET_UTF_8);
      return WordlistLoader.getWordSet(reader, matchVersion);
    } finally {
      IOUtils.close(reader);
    }
  }

  private static class DefaultSetHolder {
    private static final CharArraySet DEFAULT_PROTECTED_WORDS;
    private static final CharArraySet DEFAULT_VOWEL_HARMONY_EXCEPTIONS;
    private static final CharArraySet DEFAULT_LAST_CONSONANT_EXCEPTIONS;

    static {
      try {
        DEFAULT_PROTECTED_WORDS = loadWordSet(
            TurkishStemmer.class
                .getResourceAsStream(DEFAULT_PROTECTED_WORDS_FILE),
            Version.LUCENE_46);
      } catch(IOException ex) {
        throw new RuntimeException("Unable to load default protected words");
      }

      try {
        DEFAULT_VOWEL_HARMONY_EXCEPTIONS = loadWordSet(
            TurkishStemmer.class
                .getResourceAsStream(DEFAULT_VOWEL_HARMONY_EXCEPTIONS_FILE),
            Version.LUCENE_46);
      } catch(IOException ex) {
        throw new RuntimeException(
            "Unable to load default vowel harmony exceptions");
      }

      try {
        DEFAULT_LAST_CONSONANT_EXCEPTIONS = loadWordSet(
            TurkishStemmer.class
                .getResourceAsStream(DEFAULT_LAST_CONSONANT_EXCEPTIONS_FILE),
            Version.LUCENE_46);
      } catch(IOException ex) {
        throw new RuntimeException(
            "Unable to load default vowel harmony exceptions");
      }
    }

  }

}
