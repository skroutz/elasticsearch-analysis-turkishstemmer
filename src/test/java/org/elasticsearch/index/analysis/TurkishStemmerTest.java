package org.elasticsearch.index.analysis;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TurkishStemmerTest {
  private final TurkishStemmer stemmer = new TurkishStemmer();
  private char[] token;
  private String stem;
  private int tokenLength;

  @DataProvider(name = "stems")
  public Object[][] stemmingSamples() {
    return new Object[][] {
      { "ayfon", "ayfon" },
      { "adrese", "adre" },
      { "abiyeler", "abiye" },
      { "eriklimişsincesine", "erik" },
      { "guzelim", "guzel" },
      { "satıyorsunuz", "satıyor" },
      { "taksicisiniz", "taksiç" },
      { "türkiyedir", "türki" },
      { "telefonları" , "telefon" },
      { "acana", "acan" },
      { "telefonu", "telefon" },
      { "telefon", "telefon" },
      { "alarm", "alarm" },
      { "alarmı", "alarm" },
      { "altında", "alt" },
      { "adını", "adın" },
      { "adın", "adın" },
      { "altın", "al" },
      { "aparatı", "aparat" },
      { "arada", "ara" },
      { "arasındaki", "ara" },
      { "gozluklerinde", "gozluk" }
    };
  }

  @Test(dataProvider = "stems")
  public void testStem(String word, String expectedStem) {
    token = word.toCharArray();
    tokenLength = word.length();
    stem = stemmer.stem(token, tokenLength);

    Assert.assertEquals(stem, expectedStem);
  }

  @Test
  public void testNominalVerbSuffixStripper() {
    Set<String> stems = new LinkedHashSet<String>();

    stemmer.nominalVerbSuffixStripper("satıyorsunuz", stems);

    Assert.assertEquals(stems.size(), 1);
    Assert.assertEquals(stems.toArray(), new String[] { "satıyor" });
  }

  @Test
  public void testNounSuffixStripper() {
    Set<String> stems = new LinkedHashSet<String>();

    stemmer.nounSuffixStripper("telefonları", stems);

    Assert.assertEquals(stems.size(), 2);
    Assert.assertEquals(stems.toArray(), new String[] { "telefon", "telefonlar" });
  }

  @Test
  public void testPostProcess() {
    Assert.assertEquals(stemmer.postProcess(new LinkedHashSet<String>(), "originalWord"), "originalWord");

    String[] stems = {"kitap", "k" };

    Assert.assertEquals(stemmer.postProcess(new LinkedHashSet<String>(Arrays.asList(stems)),
        "originalWord"), "kitap");
  }

  @Test
  public void testDerivationalStripper() {
    Set<String> stems = new LinkedHashSet<String>();

    stemmer.derivationalSuffixStripper("telefonlu", stems);

    Assert.assertEquals(stems.size(), 1);
    Assert.assertEquals(stems.toArray(), new String[] { "telefon" });
  }

  @Test
  public void testStemWord() {
    Assert.assertEquals(stemmer.stemWord("gozlu", DerivationalSuffix.S1), "goz");
    Assert.assertEquals(stemmer.stemWord("ağda", NounSuffix.S13), "ağda");
    Assert.assertEquals(stemmer.stemWord("satıyorsunuz", NominalVerbSuffix.S4), "satıyor");
    Assert.assertEquals(stemmer.stemWord("saatler", NominalVerbSuffix.S5), "saat");
  }

  @Test
  public void testShouldBeMarked() {
    Assert.assertTrue(stemmer.shouldBeMarked("gozlu", DerivationalSuffix.S1));
    Assert.assertFalse(stemmer.shouldBeMarked("ağda", NounSuffix.S13));
    Assert.assertTrue(stemmer.shouldBeMarked("saatler", NominalVerbSuffix.S5));
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
    Assert.assertEquals(stemmer.proceedToStem("ağda"), false);
    Assert.assertEquals(stemmer.proceedToStem(""), false);
    Assert.assertEquals(stemmer.proceedToStem("a"), false);
    Assert.assertEquals(stemmer.proceedToStem("saatler"), true);
  }
}
