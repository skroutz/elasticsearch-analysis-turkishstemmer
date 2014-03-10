package org.elasticsearch.index.analysis;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

public class TurkishStemmerTokenFilter extends TokenFilter {
  private final TurkishStemmer stemmer;
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);


  public TurkishStemmerTokenFilter(TokenStream input,
                                   CharArraySet protectedWords,
                                   CharArraySet vowelHarmonyExceptions,
                                   CharArraySet lastConsonantExceptions) {
    super(input);
    this.stemmer = new TurkishStemmer(protectedWords,
        vowelHarmonyExceptions, lastConsonantExceptions);
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      if (!keywordAttr.isKeyword()) {
        final int newlen = stemmer.stem(termAtt.buffer(), termAtt.length());
        termAtt.setLength(newlen);
      }
      return true;
    } else {
      return false;
    }
  }

}
