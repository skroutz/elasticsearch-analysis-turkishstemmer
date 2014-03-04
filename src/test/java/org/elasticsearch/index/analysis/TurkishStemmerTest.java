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

}
