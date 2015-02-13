/*
 * Implements DFS to be called later to find the paths!
 */

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;



class find_paths {
	
	LexicalizedParser lp;
	public static String dependencyString;
	public ArrayList<dependencyTriple> dependencyTripleList;
	
	public find_paths() {
		dependencyString = null;
		dependencyTripleList = new ArrayList<dependencyTriple>();
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-maxLength", "80",
				"-retainTmpSubcategories");
		//System.out.println("Model loaded!");
	}
	
	public String getDependencyStringFromSentence(String sentence) {
		LinkedList <String> sentence_tokens = new LinkedList<String>();
		
		PTBTokenizer ptbt1 = new PTBTokenizer(new StringReader(sentence),
				new CoreLabelTokenFactory(), "");
		
		int index = 0;
		for (CoreLabel label1; ptbt1.hasNext(); ) {
			label1 = (CoreLabel)ptbt1.next();
			//System.out.println(label1.toString()+"|||");
			sentence_tokens.add(label1.toString());	
		}


		String[] new_sent = new String[sentence_tokens.size()];

		for (int i=0; i<sentence_tokens.size(); i++) {
			//System.out.println("tt\t"+sentence_tokens.get(i));
			new_sent[i] = sentence_tokens.get(i);
		}

		String[] sent = sentence.split(" ");
		//System.out.println(Sentence.toWordList(sent)+"\t"+this.lp);
		Tree parse = this.lp.apply(Sentence.toWordList(new_sent));
		
		
		//System.out.println(Sentence.toWordList(new_sent).toString() +"\t"+index);
		
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		//Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		Collection<TypedDependency> tdl = gs.allTypedDependencies();
		dependencyString = tdl.toString();
		
		return dependencyString;
	}
	
	
	private void getDependencyTripleList2() {
		if (dependencyString.isEmpty()){
			return;
		}
		
		String tmpString = dependencyString.replace("), ", "),~");
		String []dependencyTripleListTmp = tmpString.split(",~");
		
		for (int i=0; i<dependencyTripleListTmp.length; i++) {
			String dependencyTripleString = dependencyTripleListTmp[i];
			System.out.println(dependencyTripleString);
			
			dependencyTriple dt = new dependencyTriple();
			dt.relation = dependencyTripleString.split("(")[0];
			dt.gov = dependencyTripleString.split("(")[0].split(",")[0];
			dt.dep = dependencyTripleString.split("(")[0].split(",")[1].split(")")[0];
			
			System.out.println(dt.relation);
			System.out.println(dt.gov);
			System.out.println(dt.dep);
		}
		
		return;
	}
	
	private void getDependencyTripleList() {
		if (dependencyString.isEmpty()){
			return;
		}
		
		String tmpString = dependencyString.replace("), ", "),~");
		String []dependencyTripleListTmp = tmpString.split(",~");
		
		for (int i=0; i<dependencyTripleListTmp.length; i++) {
			String dependencyTripleString = dependencyTripleListTmp[i];
			//System.out.println(dependencyTripleString);
			
			Pattern dependencyTriplePattern = Pattern.compile("([a-z_]+)\\((.*?-[0-9]+)\\, (.*?-[0-9]+)");					
			Matcher dependencyTripleMatcher = dependencyTriplePattern.matcher(dependencyTripleString);
			
			while(dependencyTripleMatcher.find()) {
				String dependencyRelationString = dependencyTripleMatcher.group(1);
				String dependencyGovString = dependencyTripleMatcher.group(2);
				String dependencyDepString = dependencyTripleMatcher.group(3);
				//System.out.println(dependencyTripleMatcher.pattern());
				//System.out.println(dependencyGovString);
				//System.out.println(dependencyDepString);
				dependencyTriple dt = new dependencyTriple();
				dt.dep = dependencyDepString;
				dt.gov = dependencyGovString;
				dt.relation = dependencyRelationString;
				dependencyTripleList.add(dt);
			}
		}
		
		return;
	}
	
	public String getTheRelationBetween(String word1, String word2) {
		if (dependencyString.isEmpty()){
			return null;
		}
		else if (dependencyTripleList.isEmpty()){
			getDependencyTripleList();
		}
		
		ArrayList<dependencyTriple> visited = new ArrayList<dependencyTriple>();

		//System.out.println(dependencyTripleList.get(0).dep+"\t"+dependencyTripleList.get(0).gov+"\t"+dependencyTripleList.get(0).relation);
		String relation = dfs(visited, word1, word2, "");
		
		return relation;
	}
	
	private String dfs(ArrayList<dependencyTriple> visited, String root, String goal, String relation) {
		//System.out.println(root+"\t"+goal+"\t");
		for (int i=0; i< dependencyTripleList.size();i++) {
			if (dependencyTripleList.get(i).dep.equals(root) && dependencyTripleList.get(i).gov.equals(goal)){
				return relation+"-"+dependencyTripleList.get(i).relation;
			}
			else if(dependencyTripleList.get(i).gov.equals(root) && dependencyTripleList.get(i).dep.equals(goal)){
				return relation+"-"+dependencyTripleList.get(i).relation;
			}
		}
		
		for (int i=0; i < dependencyTripleList.size();i++) {
			if (dependencyTripleList.get(i).dep.equals(root) && !visited.contains(dependencyTripleList.get(i))) {
				visited.add(dependencyTripleList.get(i));
				String relationNew = dfs(visited, dependencyTripleList.get(i).gov, goal, relation+"-"+dependencyTripleList.get(i).relation);
				if (!relationNew.contentEquals(relation+"-"+dependencyTripleList.get(i).relation)) {
					return relationNew;
				}
			}
			else if (dependencyTripleList.get(i).gov.equals(root) && !visited.contains(dependencyTripleList.get(i))) {
				visited.add(dependencyTripleList.get(i));
				String relationNew = dfs(visited, dependencyTripleList.get(i).dep, goal, relation+"-"+dependencyTripleList.get(i).relation);
				if (!relationNew.contentEquals(relation+"-"+dependencyTripleList.get(i).relation)) {
					return relationNew;
				}
			}
		}
		return relation;
	}
	
	public String getTheRelationBetween_words(String word1, String word2) {
		if (dependencyString.isEmpty()){
			return null;
		}
		else if (dependencyTripleList.isEmpty()){
			getDependencyTripleList();
		}
		
		ArrayList<dependencyTriple> visited = new ArrayList<dependencyTriple>();

		//System.out.println(dependencyTripleList.get(0).dep+"\t"+dependencyTripleList.get(0).gov+"\t"+dependencyTripleList.get(0).relation);
		String relation = dfs_words(visited, word1, word2, "");
		
		return relation;
	}
	
	private String dfs_words(ArrayList<dependencyTriple> visited, String root, String goal, String relation) {
		//System.out.println(root+"\t"+goal+"\t");
		for (int i=0; i< dependencyTripleList.size();i++) {
			if (dependencyTripleList.get(i).dep.equals(root) && dependencyTripleList.get(i).gov.equals(goal)){
				return relation+"-"+dependencyTripleList.get(i).relation+"("+dependencyTripleList.get(i).dep+")";
			}
			else if(dependencyTripleList.get(i).gov.equals(root) && dependencyTripleList.get(i).dep.equals(goal)){
				return relation+"-"+dependencyTripleList.get(i).relation+"("+dependencyTripleList.get(i).dep+")";
			}
		}
		
		for (int i=0; i < dependencyTripleList.size();i++) {
			if (dependencyTripleList.get(i).dep.equals(root) && !visited.contains(dependencyTripleList.get(i))) {
				visited.add(dependencyTripleList.get(i));
				String relationNew = dfs_words(visited, dependencyTripleList.get(i).gov, goal, relation+"-"+dependencyTripleList.get(i).relation+"("+dependencyTripleList.get(i).dep+")");
				if (!relationNew.contentEquals(relation+"-"+dependencyTripleList.get(i).relation+"("+dependencyTripleList.get(i).dep+")")) {
					return relationNew;
				}
			}
			else if (dependencyTripleList.get(i).gov.equals(root) && !visited.contains(dependencyTripleList.get(i))) {
				visited.add(dependencyTripleList.get(i));
				String relationNew = dfs_words(visited, dependencyTripleList.get(i).dep, goal, relation+"-"+dependencyTripleList.get(i).relation+"("+dependencyTripleList.get(i).dep+")");
				if (!relationNew.contentEquals(relation+"-"+dependencyTripleList.get(i).relation+"("+dependencyTripleList.get(i).dep+")")) {
					return relationNew;
				}
			}
		}
		return relation;
	}

	
	public static void main(String []args) {
		String sentence1 = "Radha's Ram but Ram \" likes shyam which makes Radha mad";
		find_paths p = new find_paths();
		System.out.println(p.getDependencyStringFromSentence(sentence1));
		
		String word1 = "Radha";
		String word2 = "shyam";
		
		String []tokens = sentence1.split(" ");
		for (int k=0; k<tokens.length; k++) {
			if (tokens[k].equals(word1)) {
				word1 = word1+"-"+(k+1);
			}
			if (tokens[k].equals(word2)) {
				word2 = word2+"-"+(k+1);
			}
		}
		System.out.println(p.getTheRelationBetween(word1, word2));
		String st = "In Berlin, Bodo Hombach, coordinator of the Balkans Stability Pact, said several delegations to the Brussels conference told him it was now \"easier and more motivating\" to supply Belgrade with the aid it desperately needs.";
		char charac = st.charAt(58);
		char charc1 = st.charAt(59); //58	60	222
		char charc2 = st.charAt(60);
		System.out.println(charac+"\t"+charc1+"\t"+charc2);
		
	    Properties props = new Properties();
	    //props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    String document_1 = "Radha 's likes his habit of liking apples.";
	    Annotation document = new Annotation(document_1);
	    pipeline.annotate(document);


	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    for(CoreMap sentence: sentences) {
	        // traversing the words in the current sentence
	        // a CoreLabel is a CoreMap with additional token-specific methods
	        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	          // this is the text of the token
	          String word = token.get(TextAnnotation.class);
	          // this is the POS tag of the token
	          //String pos = token.get(PartOfSpeechAnnotation.class);
	          // this is the NER label of the token
	          String holder_ne = token.get(NamedEntityTagAnnotation.class);  
	          System.out.println(holder_ne);
	                 
	        }
	    }

	}

}

class dependencyTriple {
	public String gov;
	public String dep;
	public String relation;
}


