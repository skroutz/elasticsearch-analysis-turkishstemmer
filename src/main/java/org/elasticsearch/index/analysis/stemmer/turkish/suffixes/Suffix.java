package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

public interface Suffix {

  boolean match(final String word);

}
