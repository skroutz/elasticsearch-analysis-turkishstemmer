package org.elasticsearch.index.analysis;

import com.google.common.base.CharMatcher;   // Guava
import org.apache.commons.lang3.StringUtils; // Apache StringUtils
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.StringIndexOutOfBoundsException;

public class TurkishStemmer {

  public static final String ALPHABET = "abcçdefgğhıijklmnoöprsştuüvyz";
  public static final String VOWELS = "üiıueöao";
  public static final String CONSONANTS = "bcçdfgğhjklmnprsştvyz";
  public static final String ROUNDED_VOWELS            = "oöuü";
  public static final String UNROUNDED_VOWELS          = "iıea";
  public static final String FOLLOWING_ROUNDED_VOWELS  = "aeuü";
  public static final String FRONT_VOWELS              = "eiöü";
  public static final String BACK_VOWELS               = "ıuao";

  public static final String DEFAULT_PROTECTED_WORDS_FILE = "protected_words.txt";
  public static final String DEFAULT_VOWEL_HARMONY_EXCEPTIONS_FILE = "vowel_harmony_exceptions.txt";
  public static final String DEFAULT_LAST_CONSONANT_EXCEPTIONS_FILE = "last_consonant_exceptions.txt";

  private final CharArraySet protectedWords;
  private final CharArraySet vowelHarmonyExceptions;
  private final CharArraySet lastConsonantExceptions;

  public TurkishStemmer() {
    this.protectedWords          = TurkishStemmer.getDefaultProtectedWordSet();
    this.vowelHarmonyExceptions  = TurkishStemmer.getDefaultVowelHarmonySet();
    this.lastConsonantExceptions = TurkishStemmer.getDefaultLastConsonantSet();
  }

  public TurkishStemmer(CharArraySet protectedWords,
                        CharArraySet vowelHarmonyExceptions,
                        CharArraySet lastConsonantExceptions) {
    this.protectedWords          = protectedWords;
    this.vowelHarmonyExceptions  = vowelHarmonyExceptions;
    this.lastConsonantExceptions = lastConsonantExceptions;
  }

  public int stem(char s[], int len) {
    return len;
  }

  public static final CharArraySet getDefaultProtectedWordSet(){
    return DefaultSetHolder.DEFAULT_PROTECTED_WORDS;
  }

  public static final CharArraySet getDefaultVowelHarmonySet(){
    return DefaultSetHolder.DEFAULT_VOWEL_HARMONY_EXCEPTIONS;
  }

  public static final CharArraySet getDefaultLastConsonantSet(){
    return DefaultSetHolder.DEFAULT_LAST_CONSONANT_EXCEPTIONS;
  }


  /**
   * Gets the vowels of a word.
   *
   * @param   word  the word to get its vowels
   * @return        the vowels
   */
  public static String vowels(String word) {
    CharMatcher char_matcher = CharMatcher.anyOf(CONSONANTS);

    return char_matcher.removeFrom(word);
  }

  /**
   * Gets the number of syllables of a word.
   *
   * @param   word  the word to count its syllables
   * @return        the number of syllables
   */
  public static int countSyllables(String word) {
    return vowels(word).length();
  }

  /**
   * Checks the frontness harmony of two characters.
   *
   * @param   vowel     the first character
   * @param   candidate the second character
   * @return            whether the two characters have frontness harmony or not.
   */
  public static boolean hasFrontness(char vowel, char candidate) {
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
  public static boolean hasRoundness(char vowel, char candidate) {
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
  public static boolean vowelHarmony(char vowel, char candidate) {
    return hasRoundness(vowel, candidate) && hasFrontness(vowel, candidate);
  }

  /**
   * Checks the vowel harmony of a word.
   *
   * @param   word  the word to check its vowel harmony
   * @return        whether the word has vowel harmony or not.
   */
  public static boolean hasVowelHarmony(String word) {
    String vowelsOfWord = vowels(word);
    Integer wordLength  = vowelsOfWord.length();

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
  public static String lastConsonant(String word) {
    Integer wordLength = word.length();
    char lastChar = word.charAt(wordLength - 1);

    switch (lastChar) {
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
   */
  public static boolean validOptionalLetter(String word, char candidate) {
    Integer wordLength  = word.length();
    char previousChar;

    try {
      previousChar = word.charAt(wordLength - 2);
    } catch(StringIndexOutOfBoundsException e) {
      return false;
    }

    if (StringUtils.containsAny(VOWELS, candidate)) {
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
  public static boolean turkish(String word) {
    return StringUtils.containsOnly(word, ALPHABET);
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
  private static CharArraySet loadWordSet(InputStream file,
      Version matchVersion) throws IOException {
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
            TurkishStemmer.class.getResourceAsStream(DEFAULT_PROTECTED_WORDS_FILE),
            Version.LUCENE_46);
      } catch(IOException ex) {
        throw new RuntimeException("Unable to load default protected words");
      }

      try {
        DEFAULT_VOWEL_HARMONY_EXCEPTIONS = loadWordSet(
            TurkishStemmer.class.getResourceAsStream(DEFAULT_VOWEL_HARMONY_EXCEPTIONS_FILE),
            Version.LUCENE_46);
      } catch(IOException ex) {
        throw new RuntimeException("Unable to load default vowel harmony exceptions");
      }

      try {
        DEFAULT_LAST_CONSONANT_EXCEPTIONS = loadWordSet(
            TurkishStemmer.class.getResourceAsStream(DEFAULT_LAST_CONSONANT_EXCEPTIONS_FILE),
            Version.LUCENE_46);
      } catch(IOException ex) {
        throw new RuntimeException("Unable to load default vowel harmony exceptions");
      }
    }

  }
}
