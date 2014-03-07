package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import java.util.regex.Pattern;

public enum DerivationalSuffix implements Suffix {
  S1  ("-lU", "lı|li|lu|lü", null, true);

  private final String  name;
  private final Pattern pattern;
  private final boolean optionalLetterCheck;
  private final Pattern optionalLetterPattern;
  private final boolean checkHarmony;

  private Pattern pattern() { return pattern; }

  private DerivationalSuffix(final String name,
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
