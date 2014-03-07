package org.elasticsearch.index.analysis;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TurkishStemmerTest {
  private final TurkishStemmer stemmer = new TurkishStemmer();

  /**
   * a sample of Turkish words that should be stemmed by the Turkish stemmer.
   */
  private static final String[] words = { };

  /**
   * the stems that should be returned by the stemmer for the above words.
   */
  private static final String[] stems = { };

  private char[] token;
  private String stem;
  private int tokenLength, stemLength;

  @Test
  public void testTurkishStemmer() {
    for (int i = 0; i < words.length; i++) {
      token = words[i].toCharArray();
      tokenLength = words[i].length();
      stemLength = stemmer.stem(token, tokenLength);
      stem = new String(token, 0, stemLength);

      Assert.assertEquals(stem, stems[i]);
    }
  }

  @Test
  public void testCountSyllables() {
    Assert.assertEquals(stemmer.countSyllables("okul"), 2);
    Assert.assertEquals(stemmer.countSyllables(""), 0);
  }

  @Test
  public void testVowels() {
    Assert.assertEquals(TurkishStemmer.vowels("ükulş"), "üu");
    Assert.assertEquals(TurkishStemmer.vowels(""), "");
  }

  @Test
  public void testhasFrontness() {
    Assert.assertEquals(TurkishStemmer.hasFrontness('e', 'i'), true);
    Assert.assertEquals(TurkishStemmer.hasFrontness('a', 'i'), false);
  }

  @Test
  public void testhasRoundness() {
    Assert.assertEquals(TurkishStemmer.hasRoundness('o', 'i'), false);
    Assert.assertEquals(TurkishStemmer.hasRoundness('o', 'u'), true);
  }

  @Test
  public void testVowelHarmony() {
    Assert.assertEquals(TurkishStemmer.vowelHarmony('a', 'i'), false);
    Assert.assertEquals(TurkishStemmer.vowelHarmony('e', 'i'), true);
  }

  @Test
  public void testHasVowelHarmony() {
    Assert.assertEquals(TurkishStemmer.hasVowelHarmony("okul"), true);
    Assert.assertEquals(TurkishStemmer.hasVowelHarmony("okuler"), false);
    Assert.assertEquals(TurkishStemmer.hasVowelHarmony("k"), true);
  }

  @Test
  public void testLastConsonant() {
    Assert.assertEquals(TurkishStemmer.lastConsonant("kebab"), "kebap");
  }
}
