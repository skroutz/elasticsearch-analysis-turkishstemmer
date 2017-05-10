package org.elasticsearch.index.analysis;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.plugin.analysis.turkishstemmer.TurkishStemmerPlugin;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;

public class SimpleTurkishStemmerAnalysisTest extends ESTestCase {
	public void testTurkishStemmerAnalysis() throws IOException {
		TestAnalysis analysis = createTestAnalysis(new Index("test", "_na_"),
				Settings.EMPTY, new TurkishStemmerPlugin());

		TokenFilterFactory filterFactory = analysis.tokenFilter.get("turkish_stemmer");
		assertThat(filterFactory, instanceOf(TurkishStemmerTokenFilterFactory.class));
	}
}
