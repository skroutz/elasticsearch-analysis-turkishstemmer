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

  @Override
  public String removeSuffix(final String word) {
    return suffixMatcher(word).replaceAll("");
  }

  @Override
  public boolean checkHarmony() { return this.checkHarmony; }

  @Override
  public String toString() {
    return String.format("%s (%s)", this.name, name());
  }
}
