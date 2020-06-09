package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.CharArraySet;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class TurkishStemmerTokenFilterFactory extends AbstractTokenFilterFactory {

  private final CharArraySet protectedWords;
  private final CharArraySet lastConsonantExceptions;
  private final CharArraySet vowelHarmonyExceptions;
  private final CharArraySet averageStemSizeExceptions;

  @Inject
  public TurkishStemmerTokenFilterFactory(IndexSettings indexSettings,
      Environment env, @Assisted String name,
      @Assisted Settings settings) {

    super(indexSettings, name, settings);
    this.protectedWords = parseProtectedWords(env, settings,
        "protected_words_path");
    this.vowelHarmonyExceptions = parseVowelHarmonyExceptions(env, settings,
        "vowel_harmony_exceptions_path");
    this.lastConsonantExceptions = parseLastConsonantExceptions(env, settings,
        "last_consonant_exceptions_path");
    this.averageStemSizeExceptions = parseAverageStemSizeExceptions(env, settings,
        "average_stem_size_exceptions_path");
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
      String settingPrefix) {

    CharArraySet protectedWords = parseExceptions(env, settings, settingPrefix);

    if (protectedWords == null) {
      protectedWords = TurkishStemmer.getDefaultProtectedWordSet();
    }

    return protectedWords;
  }

  private CharArraySet parseLastConsonantExceptions(Environment env,
      Settings settings, String settingPrefix) {

    CharArraySet lastConsonantExceptions = parseExceptions(env, settings, settingPrefix);
    if (lastConsonantExceptions == null) {
      lastConsonantExceptions = TurkishStemmer.getDefaultLastConsonantSet();
    }

    return lastConsonantExceptions;
  }

  private CharArraySet parseVowelHarmonyExceptions(Environment env,
      Settings settings, String settingPrefix) {

    CharArraySet vowelHarmonyExceptions = parseExceptions(env, settings, settingPrefix);

    if (vowelHarmonyExceptions == null) {
      vowelHarmonyExceptions = TurkishStemmer.getDefaultVowelHarmonySet();
    }

    return vowelHarmonyExceptions;
  }

  private CharArraySet parseAverageStemSizeExceptions(Environment env,
      Settings settings, String settingPrefix) {

    CharArraySet averageStemSizeExceptions = parseExceptions(env, settings, settingPrefix);

    if (averageStemSizeExceptions == null) {
      averageStemSizeExceptions = TurkishStemmer.getDefaultAverageStemSizeSet();
    }

    return averageStemSizeExceptions;
  }

  private CharArraySet parseExceptions(Environment env, Settings settings, String settingPrefix) {
    List<String> exceptionsList  = Analysis.getWordList(env, settings, settingPrefix);
    if (exceptionsList.isEmpty()) {
      return CharArraySet.EMPTY_SET;
    } else {
      return new CharArraySet(exceptionsList, false);
    }
  }
}
