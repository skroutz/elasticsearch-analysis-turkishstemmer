package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import java.util.regex.Pattern;

public enum NounSuffix implements Suffix {
  //   name       pattern       optional letter     check harmony
  S1  ("-lAr",    "lar|ler",             null,      true),
  S2  ("-(U)m",   "m",                   "ı|i|u|ü", true),
  S3  ("-(U)mUz", "mız|miz|muz|müz",     "ı|i|u|ü", true),
  S4  ("-Un",     "ın|in|un|ün",         null,      true),
  S5  ("-(U)nUz", "nız|niz|nuz|nüz",     "ı|i|u|ü", true),
  S6  ("-(s)U",   "ı|i|u|ü",             "s",       true),
  S7  ("-lArl",   "larl|lerl",           null,      true),
  S8  ("-(y)U",   "ı|i|u|ü",             "y",       true),
  S9  ("-nU",     "nı|ni|nu|nü",         null,      true),
  S10 ("-(n)Un",  "ın|in|un|ün",         "n",       true),
  S11 ("-(y)A",   "a|e",                 "y",       true),
  S12 ("-nA",     "na|ne",               null,      true),
  S13 ("-DA",     "da|de|ta|te",         null,      true),
  S14 ("-nDA",    "nta|nte|nda|nde",     null,      true),
  S15 ("-DAn",    "dan|tan|den|ten",     null,      true),
  S16 ("-nDAn",   "ndan|ntan|nden|nten", null,      true),
  S17 ("-(y)lA",  "la|le",               "y",       true),
  S18 ("-ki",     "ki",                  null,      false),
  S19 ("-(n)cA",  "ca|ce",               "n",       true);

  private final String name;
  private final Pattern pattern;
  private final boolean optionalLetterCheck;
  private final Pattern optionalLetterPattern;
  private final boolean checkHarmony;

  private NounSuffix(final String name, final String pattern,
      final String optionalLetter, final boolean checkHarmony) {

    this.name = name;
    this.pattern = Pattern.compile("(" + pattern + ")$");
    if (optionalLetter == null) {
      this.optionalLetterCheck = false;
      this.optionalLetterPattern = null;
    } else {
      this.optionalLetterCheck = true;
      this.optionalLetterPattern = Pattern.compile(optionalLetter);
    }
    this.checkHarmony = checkHarmony;
  }

  private Pattern pattern() { return pattern; }

  @Override
  public boolean match(final String word) {
    return pattern().matcher(word).find();
  }

}
