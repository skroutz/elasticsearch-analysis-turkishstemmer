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
  public void testStem() {
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
    Assert.assertEquals(stemmer.vowels("ükulş"), "üu");
    Assert.assertEquals(stemmer.vowels(""), "");
  }

  @Test
  public void testHasFrontness() {
    Assert.assertEquals(stemmer.hasFrontness('e', 'i'), true);
    Assert.assertEquals(stemmer.hasFrontness('a', 'i'), false);
  }

  @Test
  public void testHasRoundness() {
    Assert.assertEquals(stemmer.hasRoundness('o', 'i'), false);
    Assert.assertEquals(stemmer.hasRoundness('o', 'u'), true);
  }

  @Test
  public void testVowelHarmony() {
    Assert.assertEquals(stemmer.vowelHarmony('a', 'i'), false);
    Assert.assertEquals(stemmer.vowelHarmony('e', 'i'), true);
  }

  @Test
  public void testHasVowelHarmony() {
    Assert.assertEquals(stemmer.hasVowelHarmony("okul"), true);
    Assert.assertEquals(stemmer.hasVowelHarmony("okuler"), false);
    Assert.assertEquals(stemmer.hasVowelHarmony("k"), true);
  }

  @Test
  public void testLastConsonant() {
    Assert.assertEquals(stemmer.lastConsonant("kebab"), "kebap");
  }

  @Test
  public void testValidOptionalLetter() {
    Assert.assertEquals(stemmer.validOptionalLetter("kebap", 'p'), true);
    Assert.assertEquals(stemmer.validOptionalLetter("kebp", 'p'), false);
    Assert.assertEquals(stemmer.validOptionalLetter("keba", 'a'), true);
    Assert.assertEquals(stemmer.validOptionalLetter("kea", 'a'), false);
  }

  @Test
  public void testTurkish() {
    Assert.assertEquals(stemmer.turkish("kebaçi"), true);
    Assert.assertEquals(stemmer.turkish("τεστ"), false);
  }

  @Test
  public void testProceedToStem() {
    Assert.assertEquals(stemmer.proceedToStem("αθήνα"), false);
    Assert.assertEquals(stemmer.proceedToStem("kedi"), false);
    Assert.assertEquals(stemmer.proceedToStem(""), false);
    Assert.assertEquals(stemmer.proceedToStem("a"), false);
    Assert.assertEquals(stemmer.proceedToStem("saatler"), true);
  }
}
