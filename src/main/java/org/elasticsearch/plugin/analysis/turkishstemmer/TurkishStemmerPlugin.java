package org.elasticsearch.plugin.analysis.turkishstemmer;

import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.index.analysis.TurkishStemmerTokenFilterFactory;

import java.util.Map;
 
import static java.util.Collections.singletonMap;

public class TurkishStemmerPlugin extends Plugin implements AnalysisPlugin {

	// Use singletonMap to register our token filter,
	// since we only have one in our plugin.
	@Override
	public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
		return singletonMap("turkish_stemmer", TurkishStemmerTokenFilterFactory::new);
	}
}
