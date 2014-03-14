package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

public interface Suffix {

  /**
   * Checks if a word has the certain suffix.
   * @param word the word to check about the suffix match
   * @return whether the word has the certain suffix or not
   */
  boolean match(String word);
  /**
   * Gets the optional last letter of the word if exists after removing the
   * suffix.
   * @param word the word to get the optional letter for
   * @return the optional letter if exists
   */
  char optionalLetter(String word);
  /**
   * Checks if the suffix requires the word to be checked for vowel harmony
   * @return whether vowel harmony check is required or not
   */
  boolean checkHarmony();
  /**
   * Gets the suffix from a given word.
   * @param word the word to remove the suffix from
   * @return the suffix
   */
  String removeSuffix(String word);

}
