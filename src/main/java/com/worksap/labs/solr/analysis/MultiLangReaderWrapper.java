package com.worksap.labs.solr.analysis;


import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Set;

public class MultiLangReaderWrapper {

	public final String keyFromTextDelimiter;
	public final String multiKeyDelimiter;

	private Reader reader;
	private Set<String> langKeys;
	private int strippingLength;

	public MultiLangReaderWrapper(Reader reader, String keyFromTextDelimiter, String multiKeyDelimiter)
			throws IOException {
		this.keyFromTextDelimiter = keyFromTextDelimiter;
		this.multiKeyDelimiter = multiKeyDelimiter;
		wrapReader(reader);
	}

	public void wrapReader(Reader reader) throws IOException {
		StringBuffer langBuffer = new StringBuffer();
		StringBuffer contentBuffer = new StringBuffer();

		this.langKeys = new LinkedHashSet<String>();
		this.strippingLength = 0;

		boolean delimiterWasHit = false;
		int next = reader.read();
		while (next != -1) {
			char cur = (char) next;
			if (!delimiterWasHit && this.keyFromTextDelimiter.equals(String.valueOf(cur))) {
				delimiterWasHit = true;
			} else {
				if (delimiterWasHit) {
					contentBuffer.append(cur);
				} else {
					langBuffer.append(cur);
				}
			}
			next = reader.read();
		}

		String lang = langBuffer.toString();
		String content = contentBuffer.toString();

		if (delimiterWasHit) {
			strippingLength = (lang + this.keyFromTextDelimiter).length();

			String[] keys = lang.split(this.multiKeyDelimiter);
			for (String key : keys) {
				if (StringUtils.isNotEmpty(key)) {
					this.langKeys.add(key.trim());
				}
			}
		} else {
			content = lang;
			this.strippingLength = 0;
		}

		this.reader = new StringReader(content);
	}

	public int getStrippingLength() {
		return strippingLength > 0 ? strippingLength : 0;
	}

	public Reader getReader() {
		return reader;
	}

	public Set<String> getLangKeys() {
		return langKeys;
	}
}
