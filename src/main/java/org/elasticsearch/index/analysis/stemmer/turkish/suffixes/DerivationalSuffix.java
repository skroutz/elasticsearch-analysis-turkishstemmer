package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DerivationalSuffix implements Suffix {
  S1  ("-lU", "lı|li|lu|lü", null, true);

  private final String  name;
  private final Pattern pattern;
  private final boolean optionalLetterCheck;
  private final Pattern optionalLetterPattern;
  private final boolean checkHarmony;

  private Matcher suffixMatcher(final String word) {
    return this.pattern.matcher(word);
  }

  private Matcher optionalLetterMatcher(final String word) {
    if(this.optionalLetterCheck) {
    return this.optionalLetterPattern.matcher(word);
    } else {
      return null;
    }
  }

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
    return suffixMatcher(word).find();
  }

}
