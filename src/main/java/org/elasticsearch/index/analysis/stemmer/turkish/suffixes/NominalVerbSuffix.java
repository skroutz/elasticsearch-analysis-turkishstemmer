package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import java.util.regex.Pattern;

public enum NominalVerbSuffix implements Suffix {
  //   name       pattern       optional letter     check harmony
  S1  ("-(y)Um", "ım|im|um|üm", "y", true),
  S2  ("-sUn", "sın|sin|sun|sün", null, true),
  S3  ("-(y)Uz", "ız|iz|uz|üz", "y", true),
  S4  ("-sUnUz","sınız|siniz|sunuz|sünüz", null, true),
  S5  ("-lAr", "lar|ler", null, true),
  S6  ("-m", "m", null, true),
  S7  ("-n", "n", null, true),
  S8  ("-k", "k", null, true),
  S9  ("-nUz", "nız|niz|nuz|nüz", null, true),
  S10 ("-DUr", "tır|tir|tur|tür|dır|dir|dur|dür", null, true),
  S11 ("-cAsInA", "casına|çasına|cesine|çesine", null, true),
  S12 ("-(y)DU", "dı|di|du|dü|tı|ti|tu|tü", "y", true),
  S13 ("-(y)sA", "sa|se", "y", true),
  S14 ("-(y)mUş", "muş|miş|müş|mış", "y", true),
  S15 ("-(y)ken", "ken","y",true );

  private final String  name;
  private final Pattern pattern;
  private final boolean optionalLetterCheck;
  private final Pattern optionalLetterPattern;
  private final boolean checkHarmony;

  private Pattern pattern() { return pattern; }

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
      this.optionalLetterPattern = Pattern.compile(optionalLetter);
    }

    this.checkHarmony = checkHarmony;
  }

  @Override
  public boolean match(final String word) {
    return pattern().matcher(word).find();
  }

}
