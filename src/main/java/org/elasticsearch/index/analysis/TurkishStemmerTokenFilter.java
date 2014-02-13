package org.elasticsearch.index.analysis;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;

public class TurkishStemmerTokenFilter extends TokenStream {
  
  public TurkishStemmerTokenFilter(TokenStream input) {
    super(input);
  }

  @Override
  public boolean incrementToken() throws IOException {
    // TODO Auto-generated method stub
    return false;
  }

}
