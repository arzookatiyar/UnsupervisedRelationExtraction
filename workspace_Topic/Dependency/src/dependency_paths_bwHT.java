/*
 * Find the shortest dependency paths and write dse_holder and dse_target file. 
 */

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;
import java.util.HashSet;
import java.lang.Character;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

class dependency_paths_bwHT {
	
	static HashMap<Integer, String> sentence_id = new HashMap<Integer, String>();
	static HashSet<String> stop_words = new HashSet<String>();
	
	public static LinkedList<String> create_query_string(int begin, int end, String sentence) {
		
		//String query_string = "";
		int new_begin = begin;
		
		//System.out.println(sentence+"\t"+begin+"\t"+end+"\t"+sentence.length());
		//System.out.println(sentence.substring(begin, end) );
		String query_string = sentence.substring(begin, end);
		
		/*if (sentence.length() > end+1 && (sentence.charAt(end+1)==' '|| sentence.charAt(end+1)=='.') {
			query_string = sentence.substring(begin, end);
		}
		else if (sentence.length() > end+1 && sentence.charAt(end+1)!=' ') { //character at that position is not space so extend the string to
			//find the next space
			query_string = sentence.substring(begin, sentence.indexOf(" ", end));
		}
		else if (sentence.length() == end+1) {
			//do nothing
		}*/
		
		if (begin-1 >=0 && (Character.isLetter(sentence.charAt(begin-1)))) {
			int prev_index = -1;
			for (int j = 0; j< sentence.length(); j++) {
				if (sentence.charAt(j)==' ') {
					if (begin > j) {
						prev_index = j;
					}
				}
			}
			
			if (prev_index != -1) {
				begin = prev_index+1;
			}
			else {
				begin = 0; //probably the start of line? Check this!
			}
		}
		
		//System.out.println("Begin_changed\t"+begin);
		
		//System.out.println(end+"\t"+ sentence.length()+ "\t");
		
		if (sentence.length() > end+1 && (Character.isLetter(sentence.charAt(end)))){
			//System.out.println("prev\t"+end);
			if (sentence.indexOf(" ", end) == -1)
				end = sentence.indexOf(".", end);
			else
				end = sentence.indexOf(" ", end);
			//System.out.println("end_changed\t"+query_string+"\t"+end);
			
			if (end == -1 && sentence.indexOf(" ") == -1) {
				end = sentence.length()-1;
			}
		}
		
		//System.out.println(begin+"\t"+end);
		
		query_string = sentence.substring(begin, end);
		
		/*for (int i=0; i<begin; i++) {
			if (sentence.charAt(i) == ' ') {
				new_begin --;
			}
		}*/

		
		//System.out.println(query_string);
		
		LinkedList <String> query_tokens = new LinkedList<String>();
		
		LinkedList <String> sentence_tokens = new LinkedList<String>();
		LinkedList<Integer> indices = new LinkedList<Integer>();
		
		PTBTokenizer ptbt = new PTBTokenizer(new StringReader(query_string),
				new CoreLabelTokenFactory(), "");
		for (CoreLabel label; ptbt.hasNext(); ) {
			label = (CoreLabel)ptbt.next();
			//System.out.println(label+"-----");
			query_tokens.add(label.toString());
		}
		
		PTBTokenizer ptbt1 = new PTBTokenizer(new StringReader(sentence),
				new CoreLabelTokenFactory(), "");
		for (CoreLabel label1; ptbt1.hasNext(); ) {
			label1 = (CoreLabel)ptbt1.next();
			//System.out.println(label1+"|||");
			sentence_tokens.add(label1.toString());
			if (indices.size() > 0) {
				indices.add(sentence.indexOf(label1.toString(), indices.get(indices.size()-1)));
				//System.out.println(sentence.indexOf(label1.toString(), (indices.get(indices.size()-1))));
				//System.out.println((indices.get(indices.size()-1)));
			}
			else {
				indices.add(sentence.indexOf(label1.toString()));
			}
		}
		
		/*LinkedList<String> query_tokens = new LinkedList<String>(); 
		for (int i=0; i< query_string.split(" ").length; i++) {
			query_tokens.add(query_string.split(" ")[i]);
		}
						
		LinkedList<String> sentence_tokens = new LinkedList<String>(); 
		for (int j=0; j < sentence.split(" ").length; j++) {
			sentence_tokens.add(sentence.split(" ")[j]);
		}*/
		
		LinkedList<String> final_query = new LinkedList<String>();
		//for the two linkedlist find out the overlapping region!
		
		String partial_sentence = "";
		
		loop1 : for (int k=0 ; k<sentence_tokens.size(); k++) {
			//partial_sentence = partial_sentence +sentence_tokens.get(k).toString()+" ";
			partial_sentence = sentence.substring
					(0, indices.get(k) + sentence_tokens.get(k).length());
			//System.out.println("Indices!\t"+indices.get(k));
			//System.out.println("---");
			//System.out.println(sentence_tokens.get(k));
			//System.out.println(query_tokens.get(0));
			//System.out.println("---"+sentence_tokens.get(k).toString().equals(query_tokens.get(0).toString()));
			//String compare_sentence = sentence.replaceAll("\\s+", "");
			//compare_sentence = compare_sentence.replaceAll("\"", "\'\'");
			String compare_sentence = sentence;
			//System.out.println(partial_sentence);
			//System.out.println(compare_sentence);
			

			//System.out.println(partial_sentence.indexOf(query_tokens.get(0).toString()));
			//System.out.println(compare_sentence.indexOf(query_tokens.get(0).toString()));
			
			/*int tradeoff = (partial_sentence.indexOf(query_tokens.get(0).toString()) - 
					compare_sentence.indexOf(query_tokens.get(0).toString()));
			int another_tradeoff = compare_sentence.indexOf(query_tokens.get(0).toString()) - new_begin;
			
			System.out.println(another_tradeoff +"\t"+new_begin);
			
			int ind = partial_sentence.indexOf(query_tokens.get(0).toString());
			int last_ind = ind;
			//System.out.println(last_ind);

			if(another_tradeoff < 0 && partial_sentence.indexOf(query_tokens.get(0).toString()) >= 0) {
				while (ind >= 0) {
					
					if (query_tokens.get(0).length() > 0) {
						ind = partial_sentence.indexOf(query_tokens.get(0).toString(), ind + query_tokens.get(0).length());
						if (ind >= 0) {
							//System.out.println(query_tokens.get(0).length()+"\t"+query_tokens.get(0).toString());
							//System.out.println("Changed!\t"+last_ind);
							//System.out.println(another_tradeoff+"\t"+last_ind+"\t"+new_begin);
	
							if (another_tradeoff < 0) {
								last_ind = ind;
								another_tradeoff = last_ind - new_begin;
							}
							else {
								break;
							}
						}
					}
					else 
						break;
				}
			}*/
			
			
			
			//another_tradeoff = last_ind - new_begin;
			//System.out.println(another_tradeoff+"\t"+last_ind+"\t"+new_begin);
			
			if (sentence_tokens.size() == 0 || query_tokens.size() == 0)
				return new LinkedList();
			
			if (sentence_tokens.get(k).toString().equals(query_tokens.get(0).toString())
					&& (new_begin == indices.get(k))){
					 //tradeoff >= -1 && tradeoff <= 1 && (another_tradeoff >= -1 && another_tradeoff <=1)) {
				//first element matches
				boolean matches = true;
				for (int l=1; l<query_tokens.size(); l++) {
					//System.out.println((k+l)+"\t"+sentence_tokens.get(k+l).toString());
					if (sentence_tokens.size() <= (k+l)) {
						continue loop1;
					}
					if (sentence_tokens.get(k+l).toString().equals(query_tokens.get(l).toString())) {
						//do nothing. Good!
						//System.out.println("==="+sentence_tokens.get(k+l).toString()+"\t"+(query_tokens.get(l).toString()));
					}
					else {
						matches = false;
					}
				}
				
				if (matches == false)
					continue;
				else {
					for (int p=0; p<query_tokens.size(); p++) {
						final_query.add(sentence_tokens.get(k+p)+"-"+(k+p+1));
					}
					//System.out.println(final_query);
					
					break;
				}
			}
		}
		
		
		/*String []final_query = new String[queries.length];
		
		for (int i=0; i<queries.length; i++) {
			String[] sentence_tokens = sentence.split(" ");
			for (int j=0; j<sentence_tokens.length; j++) {
				if (queries[i].equals(sentence_tokens[j])) {
					final_query[i] = queries[i]+"-"+j;
				}
			}
		}			
		 */
		
		//System.out.println(final_query.toString());
		return final_query;
		
	}
	
	
	public static LinkedList<String> shortestPath(LinkedList<String> exp, LinkedList<String> entity,
			find_paths p) throws IOException {
		//System.out.println(entity.toString());
		//System.out.println(exp.toString());
		
		String old_path = "";
		String old_arg0 = "";
		String old_arg1 = "";
		
		if (exp.size() > 0 && entity.size() > 0) {
			//changed
			old_path = p.getTheRelationBetween_words(entity.get(0), exp.get(0));
			old_arg0 = entity.get(0);
			old_arg1 = exp.get(0);
			if (stop_words.contains(entity.get(0).split("-")[0].toLowerCase()) || 
					stop_words.contains(exp.get(0).split("-")[0].toLowerCase()) || 
					(exp.contains(entity.get(0)) && entity.contains(exp.get(0)))) {
				old_path = "";
				old_arg0 = "";
				old_arg1 = "";
			}

		}
		else {
			return new LinkedList();
		}
		for (int i=0; i<exp.size(); i++) {
			for (int j=0; j<entity.size(); j++) {
				//System.out.println(entity.get(j)+"\t"+exp.get(i));
				if (stop_words.contains(entity.get(j).split("-")[0].toLowerCase()) || 
						stop_words.contains(exp.get(i).split("-")[0].toLowerCase())) {
					//System.out.println("True");
					continue;
				}
				//changed
				String new_path = p.getTheRelationBetween_words(entity.get(j), exp.get(i));
				//System.out.println(new_path);
				
				if (old_path == "") {
					old_path = new_path;
					old_arg0 = entity.get(j);
					old_arg1 = exp.get(i);
				}
				if (new_path.split("-").length < old_path.split("-").length && new_path!=""
						&&
						!(exp.contains(entity.get(0)) && entity.contains(exp.get(0)))) {
					old_path = new_path;
					old_arg0 = entity.get(j);
					old_arg1 = exp.get(i);
				}
			}
		}
		
		if (exp.size() > 0 && entity.size() > 0 && old_path == "" && old_arg0 == "" && old_arg1 == "") {
			old_path = p.getTheRelationBetween(entity.get(0), exp.get(0));
			old_arg0 = entity.get(0);
			old_arg1 = exp.get(0);
			
			for (int i=0; i<exp.size(); i++) {
				for (int j=0; j<entity.size(); j++) {
					//System.out.println(entity.get(j)+"\t"+exp.get(i));
					//changed
					String new_path = p.getTheRelationBetween_words(entity.get(j), exp.get(i));
					//System.out.println(new_path);
					
					if (old_path == "") {
						old_path = new_path;
						old_arg0 = entity.get(j);
						old_arg1 = exp.get(i);
					}
					if (new_path.split("-").length < old_path.split("-").length && new_path!=""
							&& 
							!(exp.contains(entity.get(0)) && entity.contains(exp.get(0)))) {
						old_path = new_path;
						old_arg0 = entity.get(j);
						old_arg1 = exp.get(i);
					}
				}
			}
		}

		
		System.out.println("---->\t"+old_arg0 + "\t" + old_arg1);
		//if (old_arg0.contains("The") && sentence.contains("Lusaka")) {
		/*if (sentence.contains("The exclusion of China")){
			System.exit(0);
		}*/
		LinkedList<String> final_answer = new LinkedList<String>();
		final_answer.add(old_arg0);
		final_answer.add(old_arg1);
		final_answer.add(old_path);
		return final_answer;
	}
	
	public static void main(String args[]) throws IOException{
		
		//read the stop words list
		BufferedReader brr = new BufferedReader(new FileReader("stop_words.txt"));
		String stt;
		while ((stt = brr.readLine())!=null) {
			System.out.println(stt);
			stop_words.add(stt);
		}
		
		brr.close();
		
		
		String sentence_file = "sentence_id";
		String dse_file = "dse";
		String ese_file = "ese";
		
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("dse_holder_target.txt"));
		int count1 = 0;
		int count1_1 = 0;
		int count2 = 0;
		BufferedReader br = new BufferedReader(new FileReader(sentence_file));
		String st;
		
		while((st = br.readLine())!=null) {
			String line[] = st.split("\t");
			
			sentence_id.put(Integer.parseInt(line[0]), line[1]);
		}
		
		BufferedReader br_dse = new BufferedReader(new FileReader (dse_file));
		String st_dse;
		
		while((st_dse = br_dse.readLine())!=null) {
			String line[] = st_dse.split("\t");
			int sentenceid = Integer.parseInt(line[0]);
			//System.out.println(sentenceid);
			
			String sentence = sentence_id.get(sentenceid);
			
			find_paths p = new find_paths();
			System.out.println(p.getDependencyStringFromSentence(sentence));

			
			String expression_offset = line[1].split(":")[0];
			//System.out.println(expression_offset);
			//Pattern pattern = Pattern.compile("\\(([0-9]+)\\,([0-9]+)\\)");					
			//Matcher matcher_exp = pattern.matcher(expression_offset);
			//System.out.println(matcher_exp.pattern());
			//int exp_start = Integer.parseInt(matcher_exp.group(0));
			//int exp_end = Integer.parseInt(matcher_exp.group(1));
			
			//int exp_start = Integer.parseInt(expression_offset.split(",")[0].substring(1));
			//int exp_end = Integer.parseInt(expression_offset.split(",")[1].substring(0, expression_offset.split(",")[1].length()-1));
			////System.out.println(exp_start+"\t"+exp_end);
			
			//LinkedList<String> exp_query = create_query_string(exp_start, exp_end, sentence);
			//for (int p=0; p<exp_query.size(); p++)
			//System.out.println(exp_query.get(p));

			
			//System.out.println(tmp.split(":").length);
			if (line[1].split(":").length > 1) {
				String holders = line[1].split(":")[1];
				if (!holders.equals("")) {
					//I decided that the last holder is the one to be taken care of.
					String holder = holders.split(";")[holders.split(";").length-1];
				
					//System.out.println(holder);
					//check if it is the author!!
					int holder_start = Integer.parseInt(holder.split(",")[0].substring(1));
					int holder_end = Integer.parseInt(holder.split(",")[1].substring(0, holder.split(",")[1].length()-1));
				
					if (holder_start == 0 && holder_end == 0) {
						//do nothing
					}
					else {
						// find the paths here?
						LinkedList<String> holder_query = create_query_string(holder_start, holder_end, sentence);
						if (line[1].split(":").length > 2) {
							String[] targets = line[1].split(":")[2].split(";");
							//check why there are not more than one target in this dse file?!
							System.out.println("Targets\t"+targets.length);
							for (int i=0 ; i< targets.length; i++) {
								String target = targets[i];

								if (!target.equals("")) {
									int target_start = Integer.parseInt(target.split(",")[0].substring(1));
									int target_end = Integer.parseInt(target.split(",")[1].substring(0, target.split(",")[1].length()-1));
		
									LinkedList<String> target_query = create_query_string(target_start, target_end, sentence);
									LinkedList relation_holder = shortestPath(holder_query, target_query, p);
									
									if (relation_holder.size()>0) {
										System.out.println((++count1)+"\tholder\t"+relation_holder.get(2));
										bw1.write(sentence);
										bw1.write("\t"+sentence.substring(holder_start, holder_end)+"\t"+
										sentence.substring(target_start, target_end)+"\t"+line[1].split(":")[3]);
										bw1.write("\n");
										bw1.write(relation_holder.get(0).toString());
										bw1.write("\t");
										bw1.write(relation_holder.get(1).toString());
										bw1.write("\t");
										bw1.write(relation_holder.get(2).toString());
										bw1.write("\n");
									}

								}
							}
						}
					
						//for (int p=0; p<holder_query.size(); p++)
						//	System.out.println(holder_query.get(p));
						//System.out.println(shortestPath(exp_query, holder_query, sentence));

						//LinkedList relation_holder = shortestPath(exp_query, holder_query, p);
					}
				}
				
				
			}
			
		}
		
		
		br.close();
		//br_dse.close();
		bw1.close();
		
	}
}

