package org.elasticsearch.index.analysis;

import com.google.common.base.CharMatcher;   // Guava
import org.apache.commons.lang3.StringUtils; // Apache StringUtils

import java.lang.StringIndexOutOfBoundsException;

public class TurkishStemmer {

  public static final String VOWELS = "üiıueöao";
  public static final String CONSONANTS = "bcçdfgğhjklmnprsştvyz";
  public static final String ROUNDED_VOWELS            = "oöuü";
  public static final String UNROUNDED_VOWELS          = "iıea";
  public static final String FOLLOWING_ROUNDED_VOWELS  = "aeuü";
  public static final String FRONT_VOWELS              = "eiöü";
  public static final String BACK_VOWELS               = "ıuao";

  public TurkishStemmer() { }

  public int stem(char s[], int len) {
    return len;
  }

  public static String vowels(String word) {
    CharMatcher char_matcher = CharMatcher.anyOf(CONSONANTS);

    return char_matcher.removeFrom(word);
  }

  public static int countSyllables(String word) {
    return vowels(word).length();
  }

  public static boolean hasFrontness(char vowel, char candidate) {
    if ((StringUtils.containsAny(FRONT_VOWELS, vowel) &&
        StringUtils.containsAny(FRONT_VOWELS, candidate)) ||
       (StringUtils.containsAny(BACK_VOWELS, vowel) &&
         StringUtils.containsAny(BACK_VOWELS, candidate)))
      return true;

    return false;
  }

  public static boolean hasRoundness(char vowel, char candidate) {
    if ((StringUtils.containsAny(UNROUNDED_VOWELS, vowel) &&
        StringUtils.containsAny(UNROUNDED_VOWELS, candidate)) ||
       (StringUtils.containsAny(ROUNDED_VOWELS, vowel) &&
        StringUtils.containsAny(FOLLOWING_ROUNDED_VOWELS, candidate)))
      return true;

    return false;
  }

  public static boolean vowelHarmony(char vowel, char candidate) {
    return hasRoundness(vowel, candidate) && hasFrontness(vowel, candidate);
  }

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
}
