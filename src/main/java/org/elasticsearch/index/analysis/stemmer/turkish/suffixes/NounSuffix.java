package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NounSuffix implements Suffix {
  // The order of the enum definition determines the priority of the suffix.
  // For example, -nU (S9 suffix) is  checked before -(s)U (S6 suffix).

  //   name       pattern       optional letter     check harmony
  S16 ("-nDAn",   "ndan|ntan|nden|nten", null,      true),
  S7  ("-lArI",   "ları|leri",           null,      true),
  S3  ("-(U)mUz", "mız|miz|muz|müz",     "ı|i|u|ü", true),
  S5  ("-(U)nUz", "nız|niz|nuz|nüz",     "ı|i|u|ü", true),
  S1  ("-lAr",    "lar|ler",             null,      true),
  S14 ("-nDA",    "nta|nte|nda|nde",     null,      true),
  S15 ("-DAn",    "dan|tan|den|ten",     null,      true),
  S17 ("-(y)lA",  "la|le",               "y",       true),
  S10 ("-(n)Un",  "ın|in|un|ün",         "n",       true),
  S19 ("-(n)cA",  "ca|ce",               "n",       true),
  S4  ("-Un",     "ın|in|un|ün",         null,      true),
  S9  ("-nU",     "nı|ni|nu|nü",         null,      true),
  S12 ("-nA",     "na|ne",               null,      true),
  S13 ("-DA",     "da|de|ta|te",         null,      true),
  S18 ("-ki",     "ki",                  null,      false),
  S2  ("-(U)m",   "m",                   "ı|i|u|ü", true),
  S6  ("-(s)U",   "ı|i|u|ü",             "s",       true),
  S8  ("-(y)U",   "ı|i|u|ü",             "y",       true),
  S11 ("-(y)A",   "a|e",                 "y",       true);

  private final String name;
  private final Pattern pattern;
  private final boolean optionalLetterCheck;
  private final Pattern optionalLetterPattern;
  private final boolean checkHarmony;

  private NounSuffix(final String name,
                     final String pattern,
                     final String optionalLetter,
                     final boolean checkHarmony) {

    this.name = name;
    this.pattern = Pattern.compile("(" + pattern + ")$");
    if (optionalLetter == null) {
      this.optionalLetterCheck = false;
      this.optionalLetterPattern = null;
    } else {
      this.optionalLetterCheck = true;
      this.optionalLetterPattern = Pattern.compile("(" + optionalLetter + ")$");
    }
    this.checkHarmony = checkHarmony;
  }

  private Matcher suffixMatcher(final String word) {
    return this.pattern.matcher(word);
  }

  private boolean optionalLetterCheck() { return this.optionalLetterCheck; }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean match(final String word) {
    return suffixMatcher(word).find();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char optionalLetter(String word) {
    if(optionalLetterCheck()) {
      Matcher matcher = this.optionalLetterPattern.matcher(word);

      if(matcher.find()) {
        return matcher.group().charAt(0);
      }
    }

    return '\0';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String removeSuffix(final String word) {
    return suffixMatcher(word).replaceAll("");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkHarmony() { return this.checkHarmony; }

  @Override
  public String toString() {
    return String.format("%s (%s)", this.name, name());
  }
}
