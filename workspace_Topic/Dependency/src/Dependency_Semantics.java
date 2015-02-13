/*
 * Read the dse_holder and dse_target file and then use them to build new paths which include 
 * 1) holder word/ target word
 * 2) expression word
 * 3) both
 * 4) Named Entity
 */

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;


import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;



class Dependency_Semantics {		
	
	static String holder_file = "dse_holders.txt";
	static String target_file = "dse_targets.txt";
	
	static HashMap<String, String> sentence_document = new HashMap<String, String>();
	
	static HashMap<String, Integer> holder_word_counter = new HashMap<String, Integer>();
	static HashMap<String, Integer> holder_name_counter = new HashMap<String, Integer>();
	static HashMap<String, Integer> holder_entity_counter = new HashMap<String, Integer>();
	static HashMap<String, Integer> holder_pos_counter = new HashMap<String, Integer>();

	
	static HashMap<String, Integer> holder_expr_counter = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> target_word_counter = new HashMap<String, Integer>();
	static HashMap<String, Integer> target_name_counter = new HashMap<String, Integer>();
	static HashMap<String, Integer> target_entity_counter = new HashMap<String, Integer>();
	static HashMap<String, Integer> target_pos_counter = new HashMap<String, Integer>();

	
	
	public static void expression_word_path() throws IOException {
		BufferedReader br_h = new BufferedReader(new FileReader(holder_file));
		String st;
		
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String []line = st.split("\t");
			String holder = line[1];
			String expression_word = next_line.split("\t")[1];
			String path = next_line.split("\t")[2];
			
			String new_path = path+"-"+expression_word.split("-")[0];
			
			if (holder_expr_counter.keySet().contains(new_path)) {
				holder_expr_counter.put(new_path, holder_expr_counter.get(new_path)+1);
			}
			else {
				holder_expr_counter.put(new_path, 1);
			}
		}
		
		br_h.close();
		
		BufferedWriter bw_h = new BufferedWriter(new FileWriter("dse_holders_expr.txt")) ;
		
		for (String key : holder_expr_counter.keySet()) {
			bw_h.write(key+"\t"+holder_expr_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();
		
	}

	
	
	public static void holder_name_path() throws IOException {
		BufferedReader br_h = new BufferedReader(new FileReader(holder_file));
		String st;
		
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String []line = st.split("\t");
			String holder = line[1];
			String holder_word = next_line.split("\t")[0];
			String path = next_line.split("\t")[2];
			
			String new_path = holder_word.split("-")[0] + path;
			
			if (holder_word_counter.keySet().contains(new_path)) {
				holder_word_counter.put(new_path, holder_word_counter.get(new_path)+1);
			}
			else {
				holder_word_counter.put(new_path, 1);
			}
			
			String new_path_complete = holder + path;
			
			if (holder_name_counter.keySet().contains(new_path_complete)) {
				holder_name_counter.put(new_path_complete, holder_name_counter.get(new_path_complete) + 1);
			}
			else {
				holder_name_counter.put(new_path_complete, 1);
			}
		}
		
		br_h.close();
		
		BufferedWriter bw_h = new BufferedWriter(new FileWriter("dse_holders_name.txt")) ;
		
		for (String key : holder_name_counter.keySet()) {
			bw_h.write(key+"\t"+holder_name_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();
		
		bw_h = new BufferedWriter(new BufferedWriter(new FileWriter("dse_holders_word.txt")));
		
		for (String key : holder_word_counter.keySet()) {
			bw_h.write(key+"\t"+holder_word_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();
	}
	
	/*
	 * TODO : Since we are using named-entity here. Then the path should include the path 
	 * between the named entity
	 * and the opinion expression!
	 */
	
	public static void holder_entity_path() throws IOException{
		BufferedReader br_h = new BufferedReader(new FileReader(holder_file));
		String st;
		
	    Properties props = new Properties();
	    //props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String []line = st.split("\t");
			String holder = line[1];
			String holder_word = next_line.split("\t")[0];
			String holder_ne = "NIL";
			String path = next_line.split("\t")[2];
		    Annotation document = new Annotation(line[0]);
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
		          if (word.equals(holder_word.split("-")[0])) {
		        	  	holder_ne = token.get(NamedEntityTagAnnotation.class);  
		        	  	System.out.println(holder_ne);
		          }
		                 
		        }
		    }
		    
			String new_path = holder_ne + path;
			
			if (holder_entity_counter.keySet().contains(new_path)) {
				holder_entity_counter.put(new_path, holder_entity_counter.get(new_path)+1);
			}
			else {
				holder_entity_counter.put(new_path, 1);
			}
			
		}
		
		br_h.close();
		
		BufferedWriter bw_h = new BufferedWriter(new FileWriter("dse_holders_entity.txt")) ;
		
		for (String key : holder_entity_counter.keySet()) {
			bw_h.write(key+"\t"+holder_entity_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();

	}
	
	
	public static void holder_pos_path() throws IOException{
		BufferedReader br_h = new BufferedReader(new FileReader(holder_file));
		String st;
		
		MaxentTagger tagger = new MaxentTagger("/Users/arzookatiyar/Desktop/workspace_Topic/stanford-postagger-full-2014-10-26/models/wsj-0-18-left3words-distsim.tagger");
		
	    //props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

	    
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String []line = st.split("\t");
			String holder = line[1];
			String holder_word = next_line.split("\t")[0];
			//String holder_ne = "NIL";
			String path = next_line.split("\t")[2];
			String new_path = holder_word.split("-")[0]+path;
			
			String[] tagged  =tagger.tagString(line[0]).split(" ");
			
			boolean changed = false;
			for (int i=0; i<tagged.length; i++) {
				//System.out.println(tagged[i].split("_")[0]+"\t"+holder_word);
				if ((holder_word.split("-")[0]).contains(tagged[i].split("_")[0])) {
					new_path = tagged[i].split("_")[1] + path;
					//System.out.println(new_path);
					changed = true;
					break;

				}
			}
			
			if (changed == false) {
				for (int i=0; i<tagged.length; i++) {
					//System.out.println(tagged[i].split("_")[0]+"\t"+holder_word);
					
					if ((holder_word.split("-")[0]).contains(tagged[i].split("_")[0]) || 
							(tagged[i].split("_")[0]).contains((holder_word.split("-")[0]))) {
						System.out.println(holder_word.split("-")[0]+"\t"+tagged[i].split("_")[0]);
						new_path = tagged[i].split("_")[1] + path;
						//System.out.println(new_path);
						break;
					}
				}
			}
			
			//System.out.println(tagged);
		    
			//String new_path = holder_ne + path;
			
			if (holder_pos_counter.keySet().contains(new_path)) {
				holder_pos_counter.put(new_path, holder_pos_counter.get(new_path)+1);
			}
			else {
				holder_pos_counter.put(new_path, 1);
			}
			
		}
		
		br_h.close();
		
		BufferedWriter bw_h = new BufferedWriter(new FileWriter("dse_holders_pos.txt")) ;
		
		for (String key : holder_pos_counter.keySet()) {
			bw_h.write(key+"\t"+holder_pos_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();

	}

	
	public static void target_pos_path() throws IOException{
		BufferedReader br_h = new BufferedReader(new FileReader(target_file));
		String st;
		
		MaxentTagger tagger = new MaxentTagger("/Users/arzookatiyar/Desktop/workspace_Topic/stanford-postagger-full-2014-10-26/models/wsj-0-18-left3words-distsim.tagger");
		
	    //props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

	    
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String []line = st.split("\t");
			String holder = line[1];
			String holder_word = next_line.split("\t")[0];
			//String holder_ne = "NIL";
			String path = next_line.split("\t")[2];
			String new_path = holder_word.split("-")[0]+path;
			
			String[] tagged  =tagger.tagString(line[0]).split(" ");
			
			boolean changed = false;
			for (int i=0; i<tagged.length; i++) {
				//System.out.println(tagged[i].split("_")[0]+"\t"+holder_word);
				if ((holder_word.split("-")[0]).contains(tagged[i].split("_")[0])) {
					new_path = tagged[i].split("_")[1] + path;
					//System.out.println(new_path);
					changed = true;
					break;

				}
			}
			
			if (changed == false) {
				for (int i=0; i<tagged.length; i++) {
					//System.out.println(tagged[i].split("_")[0]+"\t"+holder_word);
					
					if ((holder_word.split("-")[0]).contains(tagged[i].split("_")[0]) || 
							(tagged[i].split("_")[0]).contains((holder_word.split("-")[0]))) {
						System.out.println(holder_word.split("-")[0]+"\t"+tagged[i].split("_")[0]);
						new_path = tagged[i].split("_")[1] + path;
						//System.out.println(new_path);
						break;
					}
				}
			}
			//System.out.println(tagged);
		    
			//String new_path = holder_ne + path;
			
			if (target_pos_counter.keySet().contains(new_path)) {
				target_pos_counter.put(new_path, target_pos_counter.get(new_path)+1);
			}
			else {
				target_pos_counter.put(new_path, 1);
			}
			
		}
		
		br_h.close();
		
		BufferedWriter bw_h = new BufferedWriter(new FileWriter("dse_targets_pos.txt")) ;
		
		for (String key : target_pos_counter.keySet()) {
			bw_h.write(key+"\t"+target_pos_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();

	}

	
	
	public static void target_name_path() throws IOException {
		BufferedReader br_t = new BufferedReader(new FileReader(target_file));
		String st;
		
		while((st = br_t.readLine())!=null) {
			String next_line = br_t	.readLine();
			String []line = st.split("\t");
			String target = line[1];
			String target_word = next_line.split("\t")[0];
			String path = next_line.split("\t")[2];
			
			String new_path = path + "-" +target_word.split("-")[0];
			
			if (target_word_counter.keySet().contains(new_path)) {
				target_word_counter.put(new_path, target_word_counter.get(new_path)+1);
			}
			else {
				target_word_counter.put(new_path, 1);
			}
			
			String new_path_complete = path + "-" +target;
			
			if (target_name_counter.keySet().contains(new_path_complete)) {
				target_name_counter.put(new_path_complete, target_name_counter.get(new_path_complete) + 1);
			}
			else {
				target_name_counter.put(new_path_complete, 1);
			}
		}
		
		br_t.close();
		
		BufferedWriter bw_t = new BufferedWriter(new FileWriter("dse_targets_name.txt")) ;
		
		for (String key : target_name_counter.keySet()) {
			bw_t.write(key+"\t"+target_name_counter.get(key).toString()+"\n");
		}
		
		bw_t.close();
		
		bw_t = new BufferedWriter(new BufferedWriter(new FileWriter("dse_targets_word.txt")));
		
		for (String key : target_word_counter.keySet()) {
			bw_t.write(key+"\t"+target_word_counter.get(key).toString()+"\n");
		}
		
		bw_t.close();
	}
	
	/*
	 * TODO : Since we are using named-entity here. Then the path should include the path 
	 * between the named entity
	 * and the opinion expression!
	 */
	
	public static void target_entity_path() throws IOException{
		BufferedReader br_t = new BufferedReader(new FileReader(target_file));
		String st;
		
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    
		while((st = br_t.readLine())!=null) {
			String next_line = br_t.readLine();
			String []line = st.split("\t");
			String target = line[1];
			String target_word = next_line.split("\t")[0];
			String target_ne = "NIL";
			String path = next_line.split("\t")[2];
		    Annotation document = new Annotation(line[0]);
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
		          if (word.equals(target_word.split("-")[0])) {
		        	  	target_ne = token.get(NamedEntityTagAnnotation.class);  
		          }
		                 
		        }
		    }
		    
			String new_path = target_ne + path;
			
			if (target_entity_counter.keySet().contains(new_path)) {
				target_entity_counter.put(new_path, target_entity_counter.get(new_path)+1);
			}
			else {
				target_entity_counter.put(new_path, 1);
			}
			
		}
		
		br_t.close();
		
		BufferedWriter bw_t = new BufferedWriter(new FileWriter("dse_targets_entity.txt")) ;
		
		for (String key : target_entity_counter.keySet()) {
			bw_t.write(key+"\t"+target_entity_counter.get(key).toString()+"\n");
		}
		
		bw_t.close();

	}
	
	
	public static void new_holder_format() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("sentence_id_wfilename")) ;
		String st ;
		while ((st = br.readLine())!=null) {
			String sentence = st.split("\t")[1];
			String dir_id = st.split("\t")[2];
			sentence_document.put(sentence, dir_id);
		}
		
		BufferedReader br_h = new BufferedReader(new FileReader(holder_file));
		HashMap<String, LinkedList<String>> doc_paths = new HashMap<String, LinkedList<String>>();
		int count = 0;
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			if (next_line.split("\t").length < 3) {
				++count;
				continue;
			}
			//System.out.println("Next_line\t"+next_line);
			String []line = st.split("\t");
			String holder = line[1];
			String dir = sentence_document.get(line[0]); 
			System.out.println(dir);
			String holder_word = next_line.split("\t")[0];
			String path = next_line.split("\t")[2];
			
			String new_path = dir+"\t"+holder_word.split("-")[0] + path;
			String old_path = holder_word.split("-")[0] + path;
			
			if (holder_word_counter.keySet().contains(new_path)) {
				holder_word_counter.put(new_path, holder_word_counter.get(new_path)+1);
			}
			else {
				holder_word_counter.put(new_path, 1);
			}
			
			if (doc_paths.keySet().contains(dir)) {
				doc_paths.get(dir).add(old_path);
			}
			else {
				LinkedList new_list = new LinkedList();
				new_list.add(old_path);
				doc_paths.put(dir, new_list);
			}
			
			String new_path_complete = dir+"\t"+holder + path;
			
			if (holder_name_counter.keySet().contains(new_path_complete)) {
				holder_name_counter.put(new_path_complete, holder_name_counter.get(new_path_complete) + 1);
			}
			else {
				holder_name_counter.put(new_path_complete, 1);
			}
		}
		
		br_h.close();
		System.out.println("counter\t"+count);
		
		BufferedWriter bw_h = new BufferedWriter(new FileWriter("dse_holders_name_new.txt")) ;
		
		for (String key : holder_name_counter.keySet()) {
			bw_h.write(key+"\t"+holder_name_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();
		
		bw_h = new BufferedWriter(new BufferedWriter(new FileWriter("dse_holders_word_new.txt")));
		
		for (String key : holder_word_counter.keySet()) {
			bw_h.write(key+"\t"+holder_word_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();
		
		bw_h = new BufferedWriter(new BufferedWriter(new FileWriter("dse_holders_word_final.txt")));
		
		for (String key : doc_paths.keySet()) {
			String tobewritten = "";
			for (int k = 0; k < doc_paths.get(key).size(); k++) {
				tobewritten += doc_paths.get(key).get(k) + "\t";
			}
			bw_h.write(key+"\t"+tobewritten+"\n");
		}
		
		bw_h.close();

	}
	
	public static void new_target_format() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("sentence_id_wfilename")) ;
		String st ;
		while ((st = br.readLine())!=null) {
			String sentence = st.split("\t")[1];
			String dir_id = st.split("\t")[2];
			sentence_document.put(sentence, dir_id);
		}
		
		BufferedReader br_h = new BufferedReader(new FileReader(target_file));
		HashMap<String, LinkedList<String>> doc_paths = new HashMap<String, LinkedList<String>>();
		
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String []line = st.split("\t");
			String holder = line[1];
			String dir = sentence_document.get(line[0]); 
			System.out.println(dir);
			String holder_word = next_line.split("\t")[0];
			String path = next_line.split("\t")[2];
			
			String new_path = dir+"\t"+holder_word.split("-")[0] + path;
			String old_path = holder_word.split("-")[0] + path;
			
			if (holder_word_counter.keySet().contains(new_path)) {
				holder_word_counter.put(new_path, holder_word_counter.get(new_path)+1);
			}
			else {
				holder_word_counter.put(new_path, 1);
			}
			
			if (doc_paths.keySet().contains(dir)) {
				doc_paths.get(dir).add(old_path);
			}
			else {
				LinkedList new_list = new LinkedList();
				new_list.add(old_path);
				doc_paths.put(dir, new_list);
			}
			
			String new_path_complete = dir+"\t"+holder + path;
			
			if (holder_name_counter.keySet().contains(new_path_complete)) {
				holder_name_counter.put(new_path_complete, holder_name_counter.get(new_path_complete) + 1);
			}
			else {
				holder_name_counter.put(new_path_complete, 1);
			}
		}
		
		br_h.close();
		
		BufferedWriter bw_h = new BufferedWriter(new FileWriter("dse_targets_name_new.txt")) ;
		
		for (String key : holder_name_counter.keySet()) {
			bw_h.write(key+"\t"+holder_name_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();
		
		bw_h = new BufferedWriter(new BufferedWriter(new FileWriter("dse_targets_word_new.txt")));
		
		for (String key : holder_word_counter.keySet()) {
			bw_h.write(key+"\t"+holder_word_counter.get(key).toString()+"\n");
		}
		
		bw_h.close();
		
		bw_h = new BufferedWriter(new BufferedWriter(new FileWriter("dse_targets_word_final.txt")));
		
		for (String key : doc_paths.keySet()) {
			String tobewritten = "";
			for (int k = 0; k < doc_paths.get(key).size(); k++) {
				tobewritten += doc_paths.get(key).get(k) + "\t";
			}
			bw_h.write(key+"\t"+tobewritten+"\n");
		}
		
		bw_h.close();

	}

	
	public static void main(String args[]) throws IOException{
		//holder_name_path();
		//target_name_path();
		//holder_entity_path();
		//holder_pos_path();
		//target_pos_path();
		//target_entity_path();
		//expression_word_path();
		new_target_format();
		//new_holder_format();
	}
}