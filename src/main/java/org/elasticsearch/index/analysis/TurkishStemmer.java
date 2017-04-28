package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils; // Apache StringUtils
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.index.analysis.stemmer.turkish.states.DerivationalState;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NominalVerbState;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NounState;
import org.elasticsearch.index.analysis.stemmer.turkish.states.State;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.Suffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.Transition;

import com.google.common.base.CharMatcher;   // Guava

public class TurkishStemmer {

  /**
   * Elasticsearch logger.
   */
  protected final ESLogger logger = Loggers.getLogger("turkish-stemmer");
  /**
   * The turkish characters. They are used for skipping not turkish words.
   */
  public static final String ALPHABET = "abcçdefgğhıijklmnoöprsştuüvyz";
  /**
   * The turkish vowels.
   */
  public static final String VOWELS = "üiıueöao";
  /**
   * The turkish consonants.
   */
  public static final String CONSONANTS = "bcçdfgğhjklmnprsştvyz";
  /**
   * Rounded vowels which are used for checking roundness harmony.
   */
  public static final String ROUNDED_VOWELS = "oöuü";
  /**
   * Vowels that follow rounded vowels.
   * They are combined with {@link ROUNDED_VOWELS} to check roundness harmony.
   */
  public static final String FOLLOWING_ROUNDED_VOWELS = "aeuü";
  /**
   * The unrounded vowels which are used for checking roundness harmony.
   */
  public static final String UNROUNDED_VOWELS = "iıea";
  /**
   * Front vowels which are used for checking frontness harmony.
   */
  public static final String FRONT_VOWELS = "eiöü";
  /**
   * Front vowels which are used for checking frontness harmony.
   */
  public static final String BACK_VOWELS = "ıuao";

  /**
   * The path of the file that contains the default set of protected words.
   */
  public static final String DEFAULT_PROTECTED_WORDS_FILE = "protected_words.txt";
  /**
   * The path of the file that contains the default set of vowel harmony
   * exceptions.
   */
  public static final String DEFAULT_VOWEL_HARMONY_EXCEPTIONS_FILE = "vowel_harmony_exceptions.txt";
  /**
   * The path of the file that contains the default set of last consonant
   * exceptions.
   */
  public static final String DEFAULT_LAST_CONSONANT_EXCEPTIONS_FILE = "last_consonant_exceptions.txt";
  /**
   * The path of the file that contains the  default set of average stem size
   * exceptions.
   */
  public static final String DEFAULT_AVERAGE_STEM_SIZE_EXCEPTION_FILE = "average_stem_size_exceptions.txt";

  /**
   * The set of nominal verb states that a word may pass during the stemming
   * phase.
   */
  private static final EnumSet<NominalVerbState> nominalVerbStates = EnumSet.allOf(NominalVerbState.class);
  /**
   * The set of noun states that a word may pass during the stemming
   * phase.
   */
  private static final EnumSet<NounState> nounStates = EnumSet.allOf(NounState.class);
  /**
   * The set of derivational states that a word may pass during the stemming
   * phase.
   */
  private static final EnumSet<DerivationalState> derivationalStates = EnumSet.allOf(DerivationalState.class);
  /**
   * The set of nominal verb suffixes that the stemmer recognizes.
   */
  private static final EnumSet<NominalVerbSuffix>  nominalVerbSuffixes = EnumSet.allOf(NominalVerbSuffix.class);
  /**
   * The set of noun suffixes that the stemmer recognizes.
   */
  private static final EnumSet<NounSuffix> nounSuffixes = EnumSet.allOf(NounSuffix.class);
  /**
   * The set of derivational suffixes that the stemmer recognizes.
   */
  private static final EnumSet<DerivationalSuffix> derivationalSuffixes = EnumSet.allOf(DerivationalSuffix.class);

  /**
   * The average size of turkish stems based on which the selection of the final
   * stem is performed.
   *
   * The idea behind the selection process is based on the paper
   * F.Can, S.Kocberber, E.Balcik, C.Kaynak, H.Cagdas, O.Calan, O.Vursavas
   * "Information Retrieval on Turkish Texts"
   */
  private static final int AVERAGE_STEMMED_SIZE = 4;

  private final CharArraySet protectedWords;
  private final CharArraySet vowelHarmonyExceptions;
  private final CharArraySet lastConsonantExceptions;
  private final CharArraySet averageStemSizeExceptions;

  public TurkishStemmer() {
    this.protectedWords            = TurkishStemmer.getDefaultProtectedWordSet();
    this.vowelHarmonyExceptions    = TurkishStemmer.getDefaultVowelHarmonySet();
    this.lastConsonantExceptions   = TurkishStemmer.getDefaultLastConsonantSet();
    this.averageStemSizeExceptions = TurkishStemmer.getDefaultAverageStemSizeSet();
  }

  public TurkishStemmer(final CharArraySet protectedWords,
                        final CharArraySet vowelHarmonyExceptions,
                        final CharArraySet lastConsonantExceptions,
                        final CharArraySet averageStemSizeExceptions) {
    this.protectedWords          = protectedWords;
    this.vowelHarmonyExceptions  = vowelHarmonyExceptions;
    this.lastConsonantExceptions = lastConsonantExceptions;
    this.averageStemSizeExceptions = averageStemSizeExceptions;
  }

  /**
   * Finds the stem of a given word.
   *
   * @param s an array with the characters of the word
   * @param len the length of the word
   * @return the stemmed word
   */
  public String stem(char s[], int len) {

    String originalWord = new String(s, 0, len);

    if(!proceedToStem(originalWord)) {
      return originalWord;
    }

    Set<String> stems;
    Set<String> wordsToStem;
    stems = new HashSet<String>();

    // Process the word with the nominal verb suffix state machine.
    nominalVerbSuffixStripper(originalWord, stems);

    wordsToStem = new HashSet<String>(stems);
    wordsToStem.add(originalWord);

    for(String word : wordsToStem) {
      // Process each possible stem with the noun suffix state machine.
      nounSuffixStripper(word, stems);
    }

    wordsToStem = new HashSet<String>(stems);
    wordsToStem.add(originalWord);

    for(String word : wordsToStem) {
      // Process each possible stem with the derivational suffix state machine.
      derivationalSuffixStripper(word, stems);
    }

    return postProcess(stems, originalWord);
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
  public final void nominalVerbSuffixStripper(final String word,
                                              final Set<String> stems) {
    NominalVerbState initialState;

    if(nominalVerbStates.isEmpty() || nominalVerbSuffixes.isEmpty()) {
      return;
    }

    initialState = NominalVerbState.getInitialState();
    genericSuffixStripper(initialState, word, stems, "NominalVerb");
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
    NounState initialState;

    if(nounStates.isEmpty() || nounSuffixes.isEmpty()) {
      return;
    }

    initialState = NounState.getInitialState();
    genericSuffixStripper(initialState, word, stems, "Noun");
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
    DerivationalState initialState;

    if(derivationalStates.isEmpty() || derivationalSuffixes.isEmpty()) {
      return;
    }

    initialState = DerivationalState.getInitialState();
    genericSuffixStripper(initialState, word, stems, "Derivational");
  }

  /**
   * Given the initial state of a state machine, it adds possible stems to a
   * set of stems.
   *
   * @param initialState an initial state
   * @param word the word to stem
   * @param stems the set to populate
   * @param machine a string representing the name of the state machine. It is
   * used for debugging reasons only.
   */
  private final void genericSuffixStripper(final State initialState,
                                           final String word,
                                           final Set<String> stems,
                                           final String machine) {
    String stem, wordToStem;
    Transition transition;
    List<Transition> transitions;

    wordToStem = word;
    transitions = new ArrayList<Transition>();

    initialState.addTransitions(wordToStem, transitions, false);
    logger.debug("[{}SuffixStripper] Initial Transitions: [{}]", machine, transitions);


    while(!transitions.isEmpty()) {
      transition = transitions.remove(0);
      logger.debug("[{}SuffixStripper] Processing transition: [{}]", machine, transition);

      wordToStem = transition.word;

      stem = stemWord(wordToStem, transition.suffix);

      if(!stem.equals(wordToStem)) {
        logger.debug("[{}SuffixStripper] Word stemmed: [{}] -> [{}]", machine, wordToStem, stem);
        if(transition.nextState.finalState()) {
          for(Transition transitionToRemove : transitions.toArray(new Transition[transitions.size()])) {
            if((transitionToRemove.startState == transition.startState &&
                transitionToRemove.nextState == transition.nextState) ||
                transitionToRemove.marked) {
              transitions.remove(transitionToRemove);
            }
          }

          logger.debug("[{}SuffixStripper] Adding stem: [{}]", machine, stem);

          stems.add(stem);
          transition.nextState.addTransitions(stem, transitions, false);
        } else {
          logger.debug("[{}SuffixStripper] Marking non-final transitions", machine);

          for(Transition similarTransition : transition
              .similarTransitions(transitions)) {
            similarTransition.marked = true;
          }

          transition.nextState.addTransitions(stem, transitions, true);
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

    stems.remove(originalWord);

    for(String word : stems) {
      if (countSyllables(word) > 0)
        finalStems.add(lastConsonant(word));
    }

    List<String> sortedStems;
    sortedStems = new ArrayList<String>(finalStems);

    Collections.sort(sortedStems, new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {

        if(averageStemSizeExceptions.contains(s1)) {
          return -1;
        } else if(averageStemSizeExceptions.contains(s2)) {
          return 1;
        }

        int average_distance = Math.abs(s1.length() - AVERAGE_STEMMED_SIZE) - Math.abs(s2.length() - AVERAGE_STEMMED_SIZE);
        if(average_distance == 0) {
          return s1.length() - s2.length();
        } else {
          return average_distance;
        }
      }
    });

    logger.debug("Sorted candidate stems: " + sortedStems);

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
   * Note:  One should check if candidate character exists or not.
   *
   * @param   word      the word to check its last letter
   * @param   candidate the last character candidate
   * @return            whether is valid or not
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
    if(word.isEmpty()) {
      logger.debug("Skipping empty word");
      return false;
    }

    if(!turkish(word)) {
      logger.debug("Skipping non-turkish word: [{}]", word);
      return false;
    }

    if(this.protectedWords.contains(word)) {
      logger.debug("Skipping protected word: [{}]", word);
      return false;
    }

    if(countSyllables(word) < 2) {
      logger.debug("Skipping small word: [{}]", word);
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
   * Gets the default set of average stem size exceptions.
   *
   * @return a set of average stem size exceptions
   */
  public static CharArraySet getDefaultAverageStemSizeSet() {
    return DefaultSetHolder.DEFAULT_AVERAGE_STEM_SIZE_EXCEPTIONS;
  }


  /**
   * Creates a CharArraySet from a file.
   *
   * @param stopwords
   *          Input stream from the stopwords file
   * @return a CharArraySet containing the distinct stopwords from the given
   *         file
   * @throws IOException
   *           if loading the stopwords throws an {@link IOException}
   */
  private static CharArraySet loadWordSet(InputStream file)
      throws IOException {
    Reader reader = null;
    try {
      reader = IOUtils.getDecodingReader(file, StandardCharsets.UTF_8);
      return WordlistLoader.getWordSet(reader);
    } finally {
      IOUtils.close(reader);
    }
  }

  private static class DefaultSetHolder {
    private static final CharArraySet DEFAULT_PROTECTED_WORDS;
    private static final CharArraySet DEFAULT_VOWEL_HARMONY_EXCEPTIONS;
    private static final CharArraySet DEFAULT_LAST_CONSONANT_EXCEPTIONS;
    private static final CharArraySet DEFAULT_AVERAGE_STEM_SIZE_EXCEPTIONS;

    static {
      try {
        DEFAULT_PROTECTED_WORDS = loadWordSet(
            TurkishStemmer.class
                .getResourceAsStream(DEFAULT_PROTECTED_WORDS_FILE));
      } catch(IOException ex) {
        throw new RuntimeException("Unable to load default protected words");
      }

      try {
        DEFAULT_VOWEL_HARMONY_EXCEPTIONS = loadWordSet(
            TurkishStemmer.class
                .getResourceAsStream(DEFAULT_VOWEL_HARMONY_EXCEPTIONS_FILE));
      } catch(IOException ex) {
        throw new RuntimeException(
            "Unable to load default vowel harmony exceptions");
      }

      try {
        DEFAULT_LAST_CONSONANT_EXCEPTIONS = loadWordSet(
            TurkishStemmer.class
                .getResourceAsStream(DEFAULT_LAST_CONSONANT_EXCEPTIONS_FILE));
      } catch(IOException ex) {
        throw new RuntimeException(
            "Unable to load default vowel harmony exceptions");
      }

      try {
        DEFAULT_AVERAGE_STEM_SIZE_EXCEPTIONS = loadWordSet(
            TurkishStemmer.class
                .getResourceAsStream(DEFAULT_AVERAGE_STEM_SIZE_EXCEPTION_FILE));
      } catch(IOException ex) {
        throw new RuntimeException(
            "Unable to load default average stem size exceptions");
      }
    }

  }

}
