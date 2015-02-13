import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

class final_output {
	
	static String folder_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Training_data/";
	static String output_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Final_output/";

	static String folder_path_svm = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/svm_format_word/";
	static LinkedList<Triples> holder_triples = new LinkedList<Triples>();
	static LinkedList<Double> holder_prob = new LinkedList<Double>();
	
	static HashMap<String, Entries> gold_holders = new HashMap<String, Entries>();

	
	//static HashMap<Triples, Double> holder_result = new HashMap<Triples, Double>();
	/*
	 * With every sentence, get pairs of the holder, expression and targets alongwith a
	 * score (possibly the sum of probs for all the parts!)
	 */
	public static void triples_result(String file_name, String result_file) throws IOException{
		String file = folder_path_svm+file_name;
		String file1 = folder_path_svm+result_file;
		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedReader br1 = new BufferedReader(new FileReader(file1));
		
		String st;
		String st1;
		while((st = br.readLine())!=null && (st1 = br1.readLine())!=null) {
			String line[] = st.split("\t");
			//String word, String exp, String path, String entity, String dse
			Triples t;
			if (line.length == 5) {
				t = new Triples(line[1], line[2], line[3]);
			}
			else {
				t = new Triples(line[1], line[2], "");
			}
			double prob = Double.parseDouble(st1);
			if (prob > 0)
				System.out.println(t.word+"\t"+t.exp+"\t"+t.path+"\t"+prob);
			
			holder_triples.add(t);
			holder_prob.add(prob);
		}
		
		br.close();
		br1.close();
	}
	
	public static int indexOf(LinkedList<Triples2> list, Triples2 entry) {
		Iterator it = list.iterator();
		int index = 0;
		while(it.hasNext()) {
			Triples2 compare = (Triples2)it.next();
			if (compare.equals(entry)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public static int soft_indexOf(LinkedList<Triples2> list, Triples2 entry) {
		Iterator it = list.iterator();
		int index = 0;
		while(it.hasNext()) {
			Triples2 compare = (Triples2)it.next();
			if (compare.soft_equals(entry)) {
				return index;
			}
			index++;
		}
		return -1;

	}
	
	public static int soft_indexOf_word(LinkedList<Triples2> list, Triples2 entry) {
		Iterator it = list.iterator();
		int index = 0;
		while(it.hasNext()) {
			Triples2 compare = (Triples2)it.next();
			//if (compare.entity.equalssoft_equals(entry)) {
			String entity = compare.entity;
			String entity_compare = entry.entity;
			String []holder_words = entity.split(" ");
			String []gold_words = entity_compare.split(" ");
			boolean match = false;
			for (int i=0; i<holder_words.length; i++) {
				for (int j=0; j<gold_words.length; j++) {
					if (holder_words[i].equals(gold_words[j])) {
						return index;
					}
				}
			}
			//	return index;
			//}
			index++;
		}
		return -1;

	}

	
	public static void sentence_triples(String file, String gold_file, int fold, String output_file) throws IOException{
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(output_path+output_file));
		
		String file_name = folder_path+gold_file;
		BufferedReader br = new BufferedReader(new FileReader(file_name));
		String st;
		while((st = br.readLine())!=null) {
			String entries = br.readLine();
			String split[] = entries.split("\t");
			LinkedList<Triples2> units = new LinkedList(); 
			for (int index=1; index<split.length; index= index+5) {
				units.add(new Triples2(split[index], split[index+1], split[index+2], split[index+3], split[index+4]));
			}
			gold_holders.put(st, new Entries(fold, units, 1.0));
		}
		br.close();

		
		String filename = folder_path+file;
		BufferedReader br1 = new BufferedReader(new FileReader(filename));
		
		loop1 : while((st = br1.readLine())!=null) {
			st = st.substring(0, st.length()-1);
			String sentence = st;
			//System.out.println(gold_holders.containsKey(sentence));
			//System.out.println(gold_holders.get(st));

			if (!gold_holders.containsKey(st)) {
				do {
					st = br1.readLine();
				}while(!st.equals(""));
				continue loop1;
			}
			
			//System.out.println(st);
			
			/*if (gold_holders.get(st).unit.size() == 0) {
				do {
					st = br1.readLine();
				}while(!st.equals(""));
				continue loop1;
			}*/
			//System.out.println(sentence);
			LinkedList<Triples2> sentence_triples = new LinkedList<Triples2>();
			LinkedList<Double> sentence_prob = new LinkedList<Double>();
			do {
				st = br1.readLine();
				if (st.split("\t").length > 2) {
					int crf_path = Integer.parseInt(st.split("\t")[0]);
					double crf_prob = Double.parseDouble(st.split("\t")[1]);
					for (int ind = 2; ind < st.split("\t").length; ind=ind+5) {
						Triples2 crf_entry = new Triples2(st.split("\t")[ind],
								st.split("\t")[ind+1], st.split("\t")[ind+2], st.split("\t")[ind+3],
								st.split("\t")[ind+4]);
						Triples crf_compare = new Triples(st.split("\t")[ind],
								st.split("\t")[ind+1], st.split("\t")[ind+2]);
						
						if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
							System.out.println("Exists!!");
						}
						
						//Now we want to match it with the entries of the holder_triples
						Iterator it = holder_triples.iterator();
						int index_matched = -1;
						boolean matched = false;
						while (it.hasNext()) {
							++index_matched;

							if (crf_compare.soft_equals(it.next())) {
								/*if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
									System.out.println("Matches1!!");
								}*/

								//the final label is 1
								//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
								//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								matched = true;
								String to_write = "+1\t"+st.split("\t")[ind]+"\t"
												+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								if (indexOf(sentence_triples, crf_entry) == -1) { //does not contain
									sentence_triples.add(crf_entry);
									sentence_prob.add(crf_prob*holder_prob.get(index_matched));
									/*if (holder_prob.get(index_matched) > 0) {
										System.out.println("Positive added\t"+holder_prob.get(index_matched));
									}*/
									/*if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
										System.out.println("Matches12!!\t"+crf_entry.entity_word+"\t"+crf_entry.exp_word);
										System.out.println(holder_prob.get(index_matched));
									}*/

								}
								else {
									int temp_index = indexOf(sentence_triples, crf_entry);
									//need to just change the prob
									double prob = crf_prob*holder_prob.get(index_matched);
									/*if (holder_prob.get(index_matched) > 0) {
										System.out.println("Positive added\t"+holder_prob.get(index_matched));
									}*/
									double prob_stored = sentence_prob.get(temp_index);
									prob_stored += prob;
									sentence_prob.set(temp_index, prob_stored);
									
								}
								
							}
						}
						
						if(matched == false) {
								System.out.println("Does not find a match!");
								System.out.println(crf_compare.word+"\t"+crf_compare.exp+"\t"+crf_compare.path);
						}
					}
				}
				//System.out.println(sentence);
			}while(!st.equals(""));
			//bw.close();
			//System.exit(0);
			//here the complete sentence is read. Now for every sentence make decision on the triples2
			Iterator<Triples2> it1 = sentence_triples.iterator();
			Iterator<Double> it_prob = sentence_prob.iterator();
			
			LinkedList<Triples2> final_triples = new LinkedList<Triples2>();
			LinkedList<Double> final_probs = new LinkedList<Double>();
			
			while(it1.hasNext() && it_prob.hasNext()) {
				Triples2 tt = it1.next();
				double final_prob = it_prob.next();
				if (final_prob > 0) {
					System.out.println(tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
							+tt.entity+"\t"+tt.exp+"\t"
						+final_prob);
					if (soft_indexOf(final_triples, tt) == -1) {
						final_triples.add(tt);
						final_probs.add(final_prob);
					}
					else {
						int index = soft_indexOf(final_triples, tt);
						if (final_probs.get(index) < final_prob) {
							final_triples.set(index, tt);
							final_probs.set(index, final_prob);
						}
					}
				}
			}
			
			it1 = final_triples.iterator();
			it_prob = final_probs.iterator();
			
			System.out.println("Final\t");
			bw.write(sentence+"\n");

			while(it1.hasNext() && it_prob.hasNext()) {
				Triples2 tt = it1.next();
				double final_prob = it_prob.next();

				System.out.println(tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
						+tt.entity+"\t"+tt.exp+"\t"
					+final_prob);
				bw.write("\t"+tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
						+tt.entity+"\t"+tt.exp);
			}
			
			bw.write("\n");
		}

		br1.close();
		bw.close();
	}
	
	public static void sentence_triples_word(String file, String gold_file, int fold, String output_file) throws IOException{
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(output_path+output_file));
		
		String file_name = folder_path+gold_file;
		BufferedReader br = new BufferedReader(new FileReader(file_name));
		String st;
		while((st = br.readLine())!=null) {
			String entries = br.readLine();
			String split[] = entries.split("\t");
			LinkedList<Triples2> units = new LinkedList(); 
			for (int index=1; index<split.length; index= index+5) {
				units.add(new Triples2(split[index], split[index+1], split[index+2], split[index+3], split[index+4]));
			}
			gold_holders.put(st, new Entries(fold, units, 1.0));
		}
		br.close();

		
		String filename = folder_path+file;
		BufferedReader br1 = new BufferedReader(new FileReader(filename));
		
		loop1 : while((st = br1.readLine())!=null) {
			st = st.substring(0, st.length()-1);
			String sentence = st;
			//System.out.println(gold_holders.containsKey(sentence));
			//System.out.println(gold_holders.get(st));

			if (!gold_holders.containsKey(st)) {
				do {
					st = br1.readLine();
				}while(!st.equals(""));
				continue loop1;
			}
			
			//System.out.println(st);
			
			/*if (gold_holders.get(st).unit.size() == 0) {
				do {
					st = br1.readLine();
				}while(!st.equals(""));
				continue loop1;
			}*/
			//System.out.println(sentence);
			LinkedList<Triples2> sentence_triples = new LinkedList<Triples2>();
			LinkedList<Double> sentence_prob = new LinkedList<Double>();
			do {
				st = br1.readLine();
				if (st.split("\t").length > 2) {
					int crf_path = Integer.parseInt(st.split("\t")[0]);
					double crf_prob = Double.parseDouble(st.split("\t")[1]);
					for (int ind = 2; ind < st.split("\t").length; ind=ind+5) {
						Triples2 crf_entry = new Triples2(st.split("\t")[ind],
								st.split("\t")[ind+1], st.split("\t")[ind+2], st.split("\t")[ind+3],
								st.split("\t")[ind+4]);
						Triples crf_compare = new Triples(st.split("\t")[ind],
								st.split("\t")[ind+1], st.split("\t")[ind+2]);
						
						if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
							System.out.println("Exists!!");
						}
						
						//Now we want to match it with the entries of the holder_triples
						Iterator it = holder_triples.iterator();
						int index_matched = -1;
						boolean matched = false;
						while (it.hasNext()) {
							++index_matched;

							if (crf_compare.soft_equals(it.next())) {
								/*if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
									System.out.println("Matches1!!");
								}*/

								//the final label is 1
								//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
								//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								matched = true;
								String to_write = "+1\t"+st.split("\t")[ind]+"\t"
												+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								if (indexOf(sentence_triples, crf_entry) == -1) { //does not contain
									sentence_triples.add(crf_entry);
									sentence_prob.add(crf_prob*holder_prob.get(index_matched));
									/*if (holder_prob.get(index_matched) > 0) {
										System.out.println("Positive added\t"+holder_prob.get(index_matched));
									}*/
									/*if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
										System.out.println("Matches12!!\t"+crf_entry.entity_word+"\t"+crf_entry.exp_word);
										System.out.println(holder_prob.get(index_matched));
									}*/

								}
								else {
									int temp_index = indexOf(sentence_triples, crf_entry);
									//need to just change the prob
									double prob = crf_prob*holder_prob.get(index_matched);
									/*if (holder_prob.get(index_matched) > 0) {
										System.out.println("Positive added\t"+holder_prob.get(index_matched));
									}*/
									double prob_stored = sentence_prob.get(temp_index);
									prob_stored += prob;
									sentence_prob.set(temp_index, prob_stored);
									
								}
								
							}
						}
						
						if(matched == false) {
								System.out.println("Does not find a match!");
								System.out.println(crf_compare.word+"\t"+crf_compare.exp+"\t"+crf_compare.path);
						}
					}
				}
				//System.out.println(sentence);
			}while(!st.equals(""));
			//bw.close();
			//System.exit(0);
			//here the complete sentence is read. Now for every sentence make decision on the triples2
			Iterator<Triples2> it1 = sentence_triples.iterator();
			Iterator<Double> it_prob = sentence_prob.iterator();
			
			LinkedList<Triples2> final_triples = new LinkedList<Triples2>();
			LinkedList<Double> final_probs = new LinkedList<Double>();
			
			while(it1.hasNext() && it_prob.hasNext()) {
				Triples2 tt = it1.next();
				double final_prob = it_prob.next();
				if (final_prob > 0) {
					System.out.println(tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
							+tt.entity+"\t"+tt.exp+"\t"
						+final_prob);
					if (soft_indexOf_word(final_triples, tt) == -1) {
						final_triples.add(tt);
						final_probs.add(final_prob);
					}
					else {
						int index = soft_indexOf_word(final_triples, tt);
						if (final_probs.get(index) < final_prob) {
							final_triples.set(index, tt);
							final_probs.set(index, final_prob);
						}
					}
				}
			}
			
			it1 = final_triples.iterator();
			it_prob = final_probs.iterator();
			
			System.out.println("Final\t");
			bw.write(sentence+"\n");

			while(it1.hasNext() && it_prob.hasNext()) {
				Triples2 tt = it1.next();
				double final_prob = it_prob.next();

				System.out.println(tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
						+tt.entity+"\t"+tt.exp+"\t"
					+final_prob);
				bw.write("\t"+tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
						+tt.entity+"\t"+tt.exp);
			}
			
			bw.write("\n");
		}

		br1.close();
		bw.close();
	}

	public static void sentence_triples_hard_word(String file, String gold_file, int fold, String output_file) throws IOException{
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(output_path+output_file));
		
		String file_name = folder_path+gold_file;
		BufferedReader br = new BufferedReader(new FileReader(file_name));
		String st;
		while((st = br.readLine())!=null) {
			String entries = br.readLine();
			String split[] = entries.split("\t");
			LinkedList<Triples2> units = new LinkedList(); 
			for (int index=1; index<split.length; index= index+5) {
				units.add(new Triples2(split[index], split[index+1], split[index+2], split[index+3], split[index+4]));
			}
			gold_holders.put(st, new Entries(fold, units, 1.0));
		}
		br.close();
		
		//System.out.println(gold_holders.size());
		
		String filename = folder_path+file;
		BufferedReader br1 = new BufferedReader(new FileReader(filename));
		
		loop1 : while((st = br1.readLine())!=null) {
			st = st.substring(0, st.length()-1);
			String sentence = st;
			//System.out.println(gold_holders.containsKey(sentence));
			//System.out.println(gold_holders.get(st));

			if (!gold_holders.containsKey(st)) {
				do {
					st = br1.readLine();
				}while(!st.equals(""));
				continue loop1;
			}
			
			System.out.println(st);
			
			/*if (gold_holders.get(st).unit.size() == 0) {
				do {
					st = br1.readLine();
				}while(!st.equals(""));
				continue loop1;
			}*/
			//System.out.println(sentence);
			LinkedList<Triples2> sentence_triples = new LinkedList<Triples2>();
			LinkedList<Double> sentence_prob = new LinkedList<Double>();
			do {
				st = br1.readLine();
				if (st.split("\t").length > 2) {
					int crf_path = Integer.parseInt(st.split("\t")[0]);
					double crf_prob = Double.parseDouble(st.split("\t")[1]);
					for (int ind = 2; ind < st.split("\t").length; ind=ind+5) {
						Triples2 crf_entry = new Triples2(st.split("\t")[ind],
								st.split("\t")[ind+1], st.split("\t")[ind+2], st.split("\t")[ind+3],
								st.split("\t")[ind+4]);
						Triples crf_compare = new Triples(st.split("\t")[ind],
								st.split("\t")[ind+1], st.split("\t")[ind+2]);
												
						//Now we want to match it with the entries of the holder_triples
						Iterator it = holder_triples.iterator();
						int index_matched = -1;
						boolean matched = false;
						while (it.hasNext()) {
							++index_matched;
							//System.out.println(index_matched);

							if (crf_compare.soft_equals(it.next())) {//instead of 
								/*if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
									System.out.println("Matches1!!");
								}*/

								//the final label is 1
								//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
								//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								matched = true;
								if (indexOf(sentence_triples, crf_entry) == -1) { //does not contain
									sentence_triples.add(crf_entry);
									sentence_prob.add(holder_prob.get(index_matched));
									/*if (holder_prob.get(index_matched) > 0) {
										System.out.println("Positive added\t"+holder_prob.get(index_matched));
									}*/
									/*if (st.split("\t")[ind].equals("Pentagon-2") && st.split("\t")[ind+1].equals("provided-3")) {
										System.out.println("Matches12!!\t"+crf_entry.entity_word+"\t"+crf_entry.exp_word);
										System.out.println(holder_prob.get(index_matched));
									}*/
									if (st.split("\t")[ind].equals("Martens-4") && st.split("\t")[ind+1].equals("admitted-5")) {
										System.out.println("Matched!!\t"+crf_entry.entity+"\t"+crf_entry.exp+"\t"+holder_prob.get(index_matched)
												+"\t"+index_matched+"\t"+holder_triples.get(index_matched).word+"\t"+
												holder_triples.get(index_matched).exp);
									}

								}
								else {
									int temp_index = indexOf(sentence_triples, crf_entry);
									//need to just change the prob
									double prob = holder_prob.get(index_matched);
									/*if (holder_prob.get(index_matched) > 0) {
										System.out.println("Positive added\t"+holder_prob.get(index_matched));
									}*/
									double prob_stored = sentence_prob.get(temp_index);
									prob_stored += prob;
									sentence_prob.set(temp_index, prob_stored);
									
								}
								
							}
						}
						
						if(matched == false) {
								//System.out.println("Does not find a match!");
								//System.out.println(crf_compare.word+"\t"+crf_compare.exp+"\t"+crf_compare.path);
						}
					}
				}
				//System.out.println(sentence);
			}while(!st.equals(""));
			//bw.close();
			//System.exit(0);
			//here the complete sentence is read. Now for every sentence make decision on the triples2
			Iterator<Triples2> it1 = sentence_triples.iterator();
			Iterator<Double> it_prob = sentence_prob.iterator();
			
			LinkedList<Triples2> final_triples = new LinkedList<Triples2>();
			LinkedList<Double> final_probs = new LinkedList<Double>();
			
			while(it1.hasNext() && it_prob.hasNext()) {
				Triples2 tt = it1.next();

				double final_prob = it_prob.next();
				if (tt.entity_word.equals("Martens-4") && tt.exp_word.equals("admitted-5")) {
					System.out.println("Matched!!!!!\t"+final_prob);
				}

				//System.out.println(final_prob);
				if (final_prob > 0) {
					System.out.println(tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
							+tt.entity+"\t"+tt.exp+"\t"
						+final_prob);
					if (soft_indexOf_word(final_triples, tt) == -1) {
						final_triples.add(tt);
						final_probs.add(final_prob);
					}
					else {
						int index = soft_indexOf_word(final_triples, tt);
						if (final_probs.get(index) < final_prob) {
							final_triples.set(index, tt);
							final_probs.set(index, final_prob);
						}
					}
				}
			}
			
			it1 = final_triples.iterator();
			it_prob = final_probs.iterator();
			
			System.out.println("Final\t");
			bw.write(sentence+"\n");

			while(it1.hasNext() && it_prob.hasNext()) {
				Triples2 tt = it1.next();
				double final_prob = it_prob.next();

				System.out.println(tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
						+tt.entity+"\t"+tt.exp+"\t"
					+final_prob);
				bw.write("\t"+tt.entity_word+"\t"+tt.exp_word+"\t"+tt.path+"\t"
						+tt.entity+"\t"+tt.exp);
				//System.exit(0);
			}
			
			bw.write("\n");
			
		}

		br1.close();
		bw.close();
	}

		
	public static void main(String args[]) throws IOException{
		//triples_result("holders_0", "test_holders_0_result"); //file, result_file
		//sentence_triples_word("holders_0", "holders_gold_0", 0, "holders_0"); //crf_output
		
		
		for (int fold = 0; fold<10; fold++) {
			//triples_result("holders_"+fold, "test_holders_"+fold+"_result"); //file, result_file
			//sentence_triples_word("holders_"+fold, "holders_gold_"+fold, 0, "holders_"+fold); //crf_output
			
			triples_result("holders_full_t_"+fold, "test_holders_t_"+fold+"_result"); //file, result_file
			sentence_triples_hard_word("holders_"+fold, "holders_gold_"+fold, 0, "holders_t_"+fold); //crf_output

			
			holder_triples = new LinkedList<Triples>();
			holder_prob = new LinkedList<Double>();
			gold_holders = new HashMap<String, Entries>();

		}
		

		
		for (int fold = 0; fold<10; fold++) {
			triples_result("targets_full_t_"+fold, "test_targets_t_"+fold+"_result"); //file, result_file
			sentence_triples_hard_word("targets_"+fold, "targets_gold_"+fold, 0, "targets_t_"+fold); //crf_output
			
			holder_triples = new LinkedList<Triples>();
			holder_prob = new LinkedList<Double>();
			gold_holders = new HashMap<String, Entries>();

		}

		

		
		//triples_result("targets_0", "test_targets_0_result"); //file, result_file
		//sentence_triples_word("targets_0", "targets_gold_0", 0, "targets_0"); //crf_output
		
		//triples_result("targets_1", "test_targets_1_result"); //file, result_file
		//sentence_triples_word("targets_1", "targets_gold_1", 1, "targets_1"); //crf_output
	}
}