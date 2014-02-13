package org.elasticsearch.index.analysis;

import org.elasticsearch.index.analysis.AnalysisModule.AnalysisBinderProcessor;

public class TurkishStemmerBinderProcessor extends AnalysisBinderProcessor {

  @Override
  public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
    tokenFiltersBindings.processTokenFilter("turkish_stemmer", TurkishStemmerTokenFilterFactory.class);
  }

}
