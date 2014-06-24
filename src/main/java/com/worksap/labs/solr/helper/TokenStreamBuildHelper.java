package com.worksap.labs.solr.helper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.lucene.analysis.Token;

import java.util.List;
import java.util.Map;

public final class TokenStreamBuildHelper {

	public static Map<Integer, List<Token>> build(Map<Integer, List<Token>> source) {
		Map<Integer, List<Token>> target = deepClone(source);
		Map<Token, Integer> filterMap = generateFilterMap(source);
		for (Map.Entry<Integer, List<Token>> entry : source.entrySet()) {
			int pos = entry.getKey();
			List<Token> tokens = entry.getValue();
			for (Token token : tokens) {
				for (Map.Entry<Token, Integer> filter : filterMap.entrySet()) {
					int filterPos = filter.getValue();
					Token filterToken = filter.getKey();
					if (pos <= filterPos) {
						break;
					}
					if (filterToken.toString().indexOf(token.toString()) != -1) {
						target.get(filterPos).add(token);
						target.get(pos).remove(token);
						if (target.get(pos).isEmpty()) {
							target.remove(pos);
						}
					}
				}
			}
		}
		return target;
	}

	private static Map<Integer, List<Token>> deepClone(Map<Integer, List<Token>> source) {
		Map<Integer, List<Token>> target = Maps.newTreeMap();
		for (Map.Entry<Integer, List<Token>> entry : source.entrySet()) {
			target.put(entry.getKey(), Lists.newArrayList(entry.getValue()));
		}
		return target;
	}

	private static boolean isEnglishWord(Token token) {
		boolean isEnglishWord = false;
		for (int i = 0; i < token.length(); i++) {
			if (0x40 < token.buffer()[i] && token.buffer()[i] < 0x7b) {
				isEnglishWord = true;
				break;
			}
		}
		return isEnglishWord;
	}

	private static Map<Token, Integer> generateFilterMap(Map<Integer, List<Token>> source) {
		Map<Token, Integer> filterMap = Maps.newHashMap();

		for (Map.Entry<Integer, List<Token>> entry : source.entrySet()) {
			int pos = entry.getKey();
			List<Token> tokens = entry.getValue();

			for (Token token : tokens) {
				if (token.length() > 1 && !isEnglishWord(token)) {
					filterMap.put(token, pos);
				}
			}
		}

		return filterMap;
	}
}
