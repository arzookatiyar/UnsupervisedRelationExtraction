/**
 * Experiment to find if only considering noun phrases is enough for being able to get all 
 * the holders and targets from the data.
 */

/**
 * In the current implementation we only take the lowest level subtree into account and misses on 
 * the possibilities like 
 *  Osama bin Laden 's Al Qaeda followers
 *  Detect : Osama bin Laden 's (since we remove Osama bin Laden 's Al Qaeda followers)
 *  Ideal : Osama bin Laden 's Al Qaeda followers
 */

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;

class noun_phrases {
	static String file_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Training_data/";
	
	public static List<String> find_noun_phrase(LexicalizedParser model, 
			MaxentTagger tagger, String sentence1) {
	   // String modelPath = "edu/stanford/nlp/models/srparser/englishSR.ser.gz";

	    DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(sentence1));
	    for (List<HasWord> sentence : tokenizer) {
	      List<TaggedWord> tagged = tagger.tagSentence(sentence);
	      //System.out.println(tagged+"\tAND--"+model);
	      Tree tree = model.apply(tagged);
	    		  //model.apply(tagged);
	      //System.err.println(tree);
	      
		    List<String> phraseList = new ArrayList<String>();
		    List<String> final_phraseList = new ArrayList<String>();
		    String remove = "";
		    String add = "";
		    
		    for (Tree subtree : tree) {
		    	if (subtree.label().value().equals("NP")) {
		    		//check if a substring is annotated, then remove the bigger annotation
		    		if (phraseList.size() == 0) {
			    		phraseList.add(Sentence.listToString(subtree.yield()));
			    		
		    		}

		    		for (String present : final_phraseList) {
		    			if (present.contains(Sentence.listToString(subtree.yield()))) {
		    				//remove present only when the children are other than NNP
		    				phraseList.remove(present);
		    				if (!phraseList.contains(Sentence.listToString(subtree.yield()))) {
					    		phraseList.add(Sentence.listToString(subtree.yield()));
					    		//System.out.println("Adding at pos1\t"+Sentence.listToString(subtree.yield()));
		    				}
		    			}
		    			else if((Sentence.listToString(subtree.yield())).contains(present) || 
		    					phraseList.contains(Sentence.listToString(subtree.yield()))) {
		    				// do not add this
		    			}
		    			else {
				    		phraseList.add(Sentence.listToString(subtree.yield()));
				    		//System.out.println("Adding\t"+Sentence.listToString(subtree.yield()));
		    			}
		    		}
		    		
		    		
		    		//System.out.println(Sentence.listToString(subtree.yield()));
		    		
		    		final_phraseList = new ArrayList<String>();  		
		    		final_phraseList.addAll(phraseList);
		    	}
		    }
		    
		   /* for (String final_p : final_phraseList) {
		    	System.out.println(final_p);
		    }*/
			return final_phraseList;
	    }
	    return null;
	}
	
	public static Triple find_TP(List<String> candidates, String holder_line, String target_line,
			Triple current){
		HashSet<String>truth = new HashSet<String>();
		//holder
		//System.out.println("holder_line\t"+holder_line);
		//System.out.println("holder_line\t"+holder_line.split("\t")[0]);
		String[]holder = holder_line.split("\t");
		if (holder.length >1) {
			for (int i=1; i<holder.length; i=i+5) {
				String crude = holder[i+3];
				String parts[] = crude.split(" ");
				String holder_final = parts[0].split("-")[0];
				for (int j=1; j<parts.length; j++) {
					holder_final += " "+parts[j].split("-")[0];
				}
				truth.add(holder_final);
			}
		}
		
		//target
		String[]target = target_line.split("\t");
		if (target.length > 1) {
			for (int i=1; i<target.length; i=i+5) {
				String crude = target[i+3];
				String parts[] = crude.split(" ");
				String target_final = parts[0].split("-")[0];
				for (int j=1; j<parts.length; j++) {
					target_final += " "+parts[j].split("-")[0];
				}
				truth.add(target_final);
			}			
		}
		
		//System.out.println(candidates.toString());
		
		//Now calculate the matched and unmatched
		int TP = 0;
		int FN = 0;
		int FP = 0;
		Iterator it = truth.iterator();
		while(it.hasNext()) {
			String true_m = (String)it.next();
			//System.out.println(true_m);
			if (candidates.contains(true_m)) {
				TP++;
			}
			else {
				FN++;
			}
		}
		
		it = candidates.iterator();
		while(it.hasNext()) {
			String cand_m = (String)it.next();
			if (truth.contains(cand_m)) {
				
			}
			else {
				FP++;
			}
		}
		current.add_Count(TP, FN, FP);
		return current;
	}
	
	
	
	public static void main(String args[]) throws IOException{
	    String modelPath = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	    String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

	    //String text = "My \'dog likes to shake\" his stuffed chickadee toy.";
	    String text = "George Baker , director of Mexico Energy Intelligence , expressed this opinion : `` In the event that the Mexican Government gives priority to its relationship with the United States , it will not be a game without risks .";

	    MaxentTagger tagger = new MaxentTagger(taggerPath);
	    //ShiftReduceParser model = ShiftReduceParser.loadModel(modelPath);
	    LexicalizedParser model = LexicalizedParser.loadModel(modelPath); 
	    
	    Triple current = new Triple(0, 0, 0);
	    
	    for (int i=0; i<10; i++) {
	    	System.out.println(i);
	    	BufferedReader br_h = new BufferedReader(new FileReader(file_path+"holders_gold_"+i));
	    	BufferedReader br_t = new BufferedReader(new FileReader(file_path+"targets_gold_"+i));
	    	String st_h, st_t;
	    	while((st_h = br_h.readLine())!=null && (st_t = br_t.readLine())!=null) {
	    		//any line should be same
	    		if (!st_h.equals(st_t))
	    			System.out.println(st_h.equals(st_t)+"\tnot");
	    		br_h.readLine();
	    		current = find_TP(find_noun_phrase(model, tagger, st_h), "", br_t.readLine(), current);
	    		//System.out.println(current.TP+"\t"+current.FP+"\t"+current.FN);
	    	}
	    	System.out.println(current.TP+"\t"+current.FP+"\t"+current.FN);
	    }
	    
	    double precision = ((double)(current.TP)/(current.TP+current.FP)) ;
	    double recall = ((double)(current.TP)/(current.TP+current.FN));
	    
	    System.out.println("Precision\t"+precision 
	    		+"\tRecall\t"+recall);
		//find_noun_phrase(model, tagger, text);
	}
}

class Triple {
	int TP = 0;
	int FN = 0;
	int FP = 0;
	
	public Triple(int TP, int FN, int FP) {
		this.TP = TP;
		this.FN = FN;
		this.FP = FP;
	}
	
	public void add_Count(int TP, int FN, int FP) {
		this.TP += TP;
		this.FN += FN;
		this.FP += FP;
	}
}