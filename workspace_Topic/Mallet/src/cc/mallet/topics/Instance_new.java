package cc.mallet.topics;

import cc.mallet.types.FeatureSequence;


public class Instance_new {
	String line;
	FeatureSequence lexical_tokens;
	FeatureSequence path_tokens;
	
	Instance_new (String line, FeatureSequence lexical_tokens, FeatureSequence path_tokens) {
		this.line = line;
		this.lexical_tokens = lexical_tokens;
		this.path_tokens = path_tokens;
	}
	
	public FeatureSequence getlexical_Data() {
		return this.lexical_tokens;
	}
	
	public FeatureSequence getpath_Data() {
		return this.path_tokens;
	}
}