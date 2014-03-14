package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NominalVerbSuffix implements Suffix {
  // The order of the enum definition determines the priority of the suffix.
  // For example, -(y)ken (S15 suffix) is  checked before -n (S7 suffix).

  //   name       pattern                   optional letter     check harmony
  S11 ("-cAsInA", "casına|çasına|cesine|çesine",     null, true),
  S4  ("-sUnUz",  "sınız|siniz|sunuz|sünüz",         null, true),
  S14 ("-(y)mUş", "muş|miş|müş|mış",                 "y",  true),
  S15 ("-(y)ken", "ken",                             "y",  true),
  S2  ("-sUn",    "sın|sin|sun|sün",                 null, true),
  S5  ("-lAr",    "lar|ler",                         null, true),
  S9  ("-nUz",    "nız|niz|nuz|nüz",                 null, true),
  S10 ("-DUr",    "tır|tir|tur|tür|dır|dir|dur|dür", null, true),
  S3  ("-(y)Uz",  "ız|iz|uz|üz",                     "y",  true),
  S1  ("-(y)Um",  "ım|im|um|üm",                     "y",  true),
  S12 ("-(y)DU",  "dı|di|du|dü|tı|ti|tu|tü",         "y",  true),
  S13 ("-(y)sA",  "sa|se",                           "y",  true),
  S6  ("-m",      "m",                               null, true),
  S7  ("-n",      "n",                               null, true),
  S8  ("-k",      "k",                               null, true);

  private final String  name;
  private final Pattern pattern;
  private final boolean optionalLetterCheck;
  private final Pattern optionalLetterPattern;
  private final boolean checkHarmony;


  private NominalVerbSuffix(final String name,
                            final String pattern,
                            final String optionalLetter,
                            final boolean checkHarmony) {

    this.name = name;
    this.pattern =  Pattern.compile("(" + pattern + ")$");
    if (optionalLetter == null) {
      this.optionalLetterCheck = false;
      this.optionalLetterPattern = null;
    } else {
      this.optionalLetterCheck = true;
      this.optionalLetterPattern = Pattern.compile("(" + optionalLetter +")$");
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
