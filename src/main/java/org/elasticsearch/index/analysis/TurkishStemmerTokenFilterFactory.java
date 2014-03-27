package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.FailedToResolveConfigException;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

public class TurkishStemmerTokenFilterFactory extends AbstractTokenFilterFactory {

  private final CharArraySet protectedWords;
  private final CharArraySet lastConsonantExceptions;
  private final CharArraySet vowelHarmonyExceptions;
  private final CharArraySet averageStemSizeExceptions;


  @Inject
  public TurkishStemmerTokenFilterFactory(Index index,
      @IndexSettings Settings indexSettings,
      Environment env, @Assisted String name,
      @Assisted Settings settings) {

    super(index, indexSettings, name, settings);
    this.protectedWords = parseProtectedWords(env, settings,
        "protected_words_path", Version.LUCENE_44);
    this.vowelHarmonyExceptions = parseVowelHarmonyExceptions(env, settings,
        "vowel_harmony_exceptions_path", Version.LUCENE_44);
    this.lastConsonantExceptions = parseLastConsonantExceptions(env, settings,
        "last_consonant_exceptions_path", Version.LUCENE_44);
    this.averageStemSizeExceptions = parseAverageStemSizeExceptions(env, settings,
        "average_stem_size_exceptions_path", Version.LUCENE_44);
  }

  @Override
  public TokenStream create(TokenStream tokenStream) {
    return new TurkishStemmerTokenFilter(tokenStream,
                                         protectedWords,
                                         vowelHarmonyExceptions,
                                         lastConsonantExceptions,
                                         averageStemSizeExceptions);
  }

  private CharArraySet parseProtectedWords(Environment env, Settings settings,
      String settingPrefix, Version version) {

    CharArraySet protectedWords = null;

    try{
      protectedWords = parseExceptions(env, settings, settingPrefix, version);
    } catch(IOException e) {
      logger.info("Failed to load given protected words, using default set");
    }

    if (protectedWords == null) {
      protectedWords = TurkishStemmer.getDefaultProtectedWordSet();
    }

    return protectedWords;
  }

  private CharArraySet parseLastConsonantExceptions(Environment env,
      Settings settings, String settingPrefix, Version version) {

    CharArraySet lastConsonantExceptions = null;

    try {
      lastConsonantExceptions =
          parseExceptions(env, settings, settingPrefix, version);
    } catch (IOException e) {
      logger.info("Failed to load given last consonant exceptions, using default set");
    }

    if (lastConsonantExceptions == null) {
      lastConsonantExceptions = TurkishStemmer.getDefaultLastConsonantSet();
    }

    return lastConsonantExceptions;
  }

  private CharArraySet parseVowelHarmonyExceptions(Environment env,
      Settings settings, String settingPrefix, Version version) {

    CharArraySet vowelHarmonyExceptions = null;

    try {
      vowelHarmonyExceptions =
          parseExceptions(env, settings, settingPrefix, version);
    } catch (IOException e) {
      logger.info("Failed to load given last consonant exceptions, using default set");
    }

    if (vowelHarmonyExceptions == null) {
      vowelHarmonyExceptions = TurkishStemmer.getDefaultVowelHarmonySet();
    }

    return vowelHarmonyExceptions;
  }

  private CharArraySet parseAverageStemSizeExceptions(Environment env,
      Settings settings, String settingPrefix, Version version) {

    CharArraySet averageStemSizeExceptions = null;

    try {
      averageStemSizeExceptions =
          parseExceptions(env, settings, settingPrefix, version);
    } catch (IOException e) {
      logger.info("Failed to load given average stem size exceptions, using default set");
    }

    if (averageStemSizeExceptions == null) {
      averageStemSizeExceptions = TurkishStemmer.getDefaultAverageStemSizeSet();
    }

    return averageStemSizeExceptions;
  }

  private CharArraySet parseExceptions(Environment env, Settings settings,
      String settingPrefix, Version version) throws IOException {

    List<String> exceptionsList = new ArrayList<String>();
    Reader exceptionsReader = null;

    try {
      exceptionsReader = Analysis.getReaderFromFile(env, settings, settingPrefix);
    } catch (FailedToResolveConfigException e) {
      logger.info("failed to find the " + settingPrefix + ", using the default set");
    }

    if (exceptionsReader != null) {
      try {
        exceptionsList = Analysis.loadWordList(exceptionsReader, "#");
        if (exceptionsList.isEmpty()) {
          return CharArraySet.EMPTY_SET;
        } else {
          return new CharArraySet(version, exceptionsList, false);
        }
      } finally {
        if (exceptionsReader != null)
          exceptionsReader.close();
      }
    } else {
      return null;
    }
  }
}
