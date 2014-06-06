package com.worksap.labs.solr.schema;

import com.worksap.labs.solr.analysis.MultiLangAnalyzer;
import com.worksap.labs.solr.helper.MultiLangFieldSettingHelper;
import com.worksap.labs.solr.setting.AnalyzerMode;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TextField;

import java.util.Map;

/**
 * <code>MultiLangField</code> is the custom type for configurable multilingual
 * text analysis.
 */
public class MultiLangField extends TextField {

	@Override
	protected void init(IndexSchema schema, Map<String, String> args) {
		super.init(schema, args);
		this.setAnalyzer(new MultiLangAnalyzer(schema, MultiLangFieldSettingHelper.valueOf(args, AnalyzerMode.index)));
		this.setQueryAnalyzer(new MultiLangAnalyzer(schema, MultiLangFieldSettingHelper.valueOf(args,
				AnalyzerMode.query)));
		this.setMultiTermAnalyzer(new MultiLangAnalyzer(schema, MultiLangFieldSettingHelper.valueOf(args,
				AnalyzerMode.multiTerm)));
		MultiLangFieldSettingHelper.clearArgs(args);
	}

	@Override
	public IndexableField createField(SchemaField field, Object value, float boost) {
		return super.createField(field, value, boost);
	}
}
