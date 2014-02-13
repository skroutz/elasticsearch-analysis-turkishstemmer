package org.elasticsearch.index.analysis;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public class TurkishStemmerTokenFilter extends TokenFilter {
  private final TurkishStemmer stemmer = new TurkishStemmer();
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);


  public TurkishStemmerTokenFilter(TokenStream input) {
    super(input);
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
