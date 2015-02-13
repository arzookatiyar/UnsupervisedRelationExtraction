import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashSet;

class training_file_svm_only_topics_all {
	
	static String folder_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Training_data/";
	static String folder_path_svm = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/svm_format_only_topic_all/";
	
	static String folder_topic = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Features/";
	
	static HashMap<String, Entries> gold_holders = new HashMap<String, Entries>();
	static HashMap<String, Entries> gold_targets = new HashMap<String, Entries>();
		
	static HashMap<String, Topics> word_topic_assignment = new HashMap<String, Topics>();
	static HashMap<String, Topics> path_topic_assignment = new HashMap<String, Topics>();

	public static boolean Isoverlap(Triples2 crf_entry, Triples2 gold) {
		//either the holder entity is overlapping or the expression is overlapping
		String []holder_words = crf_entry.entity.split(" ");
		String []gold_words = gold.entity.split(" ");
		boolean match = false;
		for (int i=0; i<holder_words.length; i++) {
			for (int j=0; j<gold_words.length; j++) {
				if (holder_words[i].equals(gold_words[j])) {
					//System.out.println("True");
					return true;
				}
			}
		}
		holder_words = crf_entry.exp.split(" ");
		gold_words = gold.exp.split(" ");
		match = false;
		for (int i=0; i<holder_words.length; i++) {
			for (int j=0; j<gold_words.length; j++) {
				if (holder_words[i].equals(gold_words[j])) {
					//System.out.println("True");
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean Bothoverlap(Triples2 crf_entry, Triples2 gold) {
		//either the holder entity is overlapping or the expression is overlapping
		String []holder_words = crf_entry.entity.split(" ");
		String []gold_words = gold.entity.split(" ");
		boolean match_holder = false;
		for (int i=0; i<holder_words.length; i++) {
			for (int j=0; j<gold_words.length; j++) {
				if (holder_words[i].equals(gold_words[j])) {
					//System.out.println("True");
					match_holder = true;
				}
			}
		}
		holder_words = crf_entry.exp.split(" ");
		gold_words = gold.exp.split(" ");
		boolean match_exp = false;
		for (int i=0; i<holder_words.length; i++) {
			for (int j=0; j<gold_words.length; j++) {
				if (holder_words[i].equals(gold_words[j])) {
					//System.out.println("True");
					match_exp = true;
				}
			}
		}
		
		if (match_holder == true && match_exp == true) {
			return true;
		}
		return false;
	}

	
	//except which file, everything goes to the training set
	public static void create_file_holders () throws IOException{
		for (int fold=0; fold<10; fold++) {
			//if (fold == 1) {System.exit(0);}
			String file_name = folder_path+"holders_gold_"+fold;
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
			
			String train_name = folder_path+"holders_"+fold;
			BufferedReader br1 = new BufferedReader(new FileReader(train_name));
			String filename = folder_path_svm+"holders_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			String filename_full = folder_path_svm+"holders_full_"+fold;
			BufferedWriter bw_full = new BufferedWriter(new FileWriter(filename_full));

			LinkedList<String> entries = new LinkedList<String>();
			LinkedList<String> entries_full = new LinkedList<String>();
			HashMap<String, Double> entries_prob = new HashMap<String, Double>();
			HashMap<String, Double> entries_full_prob = new HashMap<String, Double>();

			
			loop1 : while((st = br1.readLine())!=null) {
				st = st.substring(0, st.length()-1);
				String sentence = st;
				//System.out.println(sentence);
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
				do {
					st = br1.readLine();
					if (st.split("\t").length > 2) {
						int crf_path = Integer.parseInt(st.split("\t")[0]);
						double crf_prob = Double.parseDouble(st.split("\t")[1]);
						for (int ind = 2; ind < st.split("\t").length; ind=ind+5) {
							Triples2 crf_entry = new Triples2(st.split("\t")[ind],
									st.split("\t")[ind+1], st.split("\t")[ind+2], st.split("\t")[ind+3],
									st.split("\t")[ind+4]);
							
							Iterator<Triples2> it = gold_holders.get(sentence).unit.iterator();
							boolean matched = false;
							boolean overlapping = false;
							while (it.hasNext()) {
								Triples2 gold = (Triples2) it.next();
								if (crf_entry.word_path_equals(gold)) { //instead of word_path_relation_equals
									//the final label is 1
									//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
									//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
									String to_write = "+1\t"+st.split("\t")[ind]+"\t"
													+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2];

									if (!entries.contains(to_write)) {
										bw.write(to_write+"\n");
										entries.add(to_write);
										entries_prob.put(to_write, crf_prob);
										System.out.println("written\t"+to_write);
									}
									else {
										entries_prob.put(to_write, entries_prob.get(to_write)+crf_prob);
									}
									
									if(!entries_full.contains(to_write)) {
										bw_full.write(to_write+"\n");
										entries_full.add(to_write);
										entries_full_prob.put(to_write, crf_prob);
									}
									else {
										entries_full_prob.put(to_write, entries_full_prob.get(to_write)+crf_prob);

									}
									matched = true;
								}
								
								if (Isoverlap(crf_entry, gold)) {
								//if (Bothoverlap(crf_entry, gold)) {
									overlapping  = true;
								}
							}
														
							if (matched == false) {
									//negative example
								//System.out.println("matched false");
								//String new_write = "-1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
								//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								String new_write = "-1\t"+st.split("\t")[ind]+"\t"
										+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2];

								if (!entries.contains(new_write) && overlapping == true) {//there is some overlap
									entries.add(new_write);
									bw.write(new_write+"\n");
									entries_prob.put(new_write, crf_prob);
									System.out.println("written2\t"+new_write);
								}
								if (entries.contains(new_write) && overlapping == true) {
									entries_prob.put(new_write, entries_prob.get(new_write)+crf_prob);
								}
								if (!entries_full.contains(new_write)) {
									bw_full.write(new_write+"\n");
									entries_full.add(new_write);
									entries_full_prob.put(new_write, crf_prob);
								}
								else {
									entries_full_prob.put(new_write, entries_full_prob.get(new_write)+crf_prob);
								}
							}
						}
					}
					//System.out.println(sentence);
				}while(!st.equals(""));
				//bw.close();
				//System.exit(0);

			}
			
			//write_entries and entries_full in separate file and then try on that
			String filename_t1 = folder_path_svm+"holders_t_"+fold;
			
			BufferedWriter bw_t1 = new BufferedWriter(new FileWriter(filename_t1));
			Iterator it1 = entries.iterator();
			while(it1.hasNext()) {
				String line = (String)it1.next();
				line = line + "\t" +String.valueOf(entries_prob.get(line)) + "\n";
				bw_t1.write(line);
			}
			bw_t1.close();
			
			String filename_t2 = folder_path_svm+"holders_full_t_"+fold;
			
			BufferedWriter bw_t2 = new BufferedWriter(new FileWriter(filename_t2));
			it1 = entries_full.iterator();
			while(it1.hasNext()) {
				String line = (String)it1.next();
				line = line + "\t" +String.valueOf(entries_full_prob.get(line)) + "\n";
				bw_t2.write(line);
			}

			bw_t2.close();
			
			br1.close();
			bw.close();
			bw_full.close();
			gold_holders = new HashMap<String, Entries>();
		}
	}
	
	public static void create_file_targets () throws IOException{
		for (int fold=0; fold<10; fold++) {
			//if (fold == 1) {System.exit(0);}
			String file_name = folder_path+"targets_gold_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(file_name));
			String st;
			while((st = br.readLine())!=null) {
				String entries = br.readLine();
				String split[] = entries.split("\t");
				LinkedList<Triples2> units = new LinkedList(); 
				for (int index=1; index<split.length; index= index+5) {
					units.add(new Triples2(split[index], split[index+1], split[index+2], split[index+3], split[index+4]));
				}
				gold_targets.put(st, new Entries(fold, units, 1.0));
			}
			br.close();
			
			String train_name = folder_path+"targets_"+fold;
			BufferedReader br1 = new BufferedReader(new FileReader(train_name));
			String filename = folder_path_svm+"targets_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			String filename_full = folder_path_svm+"targets_full_"+fold;
			BufferedWriter bw_full = new BufferedWriter(new FileWriter(filename_full));

			LinkedList<String> entries = new LinkedList<String>();
			LinkedList<String> entries_full = new LinkedList<String>();
			HashMap<String, Double> entries_prob = new HashMap<String, Double>();
			HashMap<String, Double> entries_full_prob = new HashMap<String, Double>();

			
			loop1 : while((st = br1.readLine())!=null) {
				st = st.substring(0, st.length()-1);
				String sentence = st;
				//System.out.println(sentence);
				//System.out.println(gold_holders.containsKey(sentence));
				//System.out.println(gold_holders.get(st));

				if (!gold_targets.containsKey(st)) {
					do {
						st = br1.readLine();
					}while(!st.equals(""));
					continue loop1;
				}
				
				//System.out.println(st);
				
				do {
					st = br1.readLine();
					if (st.split("\t").length > 2) {
						int crf_path = Integer.parseInt(st.split("\t")[0]);
						double crf_prob = Double.parseDouble(st.split("\t")[1]);
						for (int ind = 2; ind < st.split("\t").length; ind=ind+5) {
							Triples2 crf_entry = new Triples2(st.split("\t")[ind],
									st.split("\t")[ind+1], st.split("\t")[ind+2], st.split("\t")[ind+3],
									st.split("\t")[ind+4]);
							
							Iterator<Triples2> it = gold_targets.get(sentence).unit.iterator();
							boolean matched = false;
							boolean overlapping = false;
							while (it.hasNext()) {
								Triples2 gold = (Triples2) it.next();
								if (crf_entry.word_path_equals(gold)) { //instead of word_path_relation_equals
									//the final label is 1
									//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
									//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
									String to_write = "+1\t"+st.split("\t")[ind]+"\t"
													+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2];

									if (!entries.contains(to_write)) {
										bw.write(to_write+"\n");
										entries.add(to_write);
										entries_prob.put(to_write, crf_prob);
										System.out.println("written\t"+to_write);
									}
									else {
										entries_prob.put(to_write, entries_prob.get(to_write)+crf_prob);
									}
									
									if(!entries_full.contains(to_write)) {
										bw_full.write(to_write+"\n");
										entries_full.add(to_write);
										entries_full_prob.put(to_write, crf_prob);
									}
									else {
										entries_full_prob.put(to_write, entries_full_prob.get(to_write)+crf_prob);

									}
									matched = true;
								}
								
								if (Bothoverlap(crf_entry, gold)) {
									overlapping  = true;
								}
							}
														
							if (matched == false) {
									//negative example
								//System.out.println("matched false");
								//String new_write = "-1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
								//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								String new_write = "-1\t"+st.split("\t")[ind]+"\t"
										+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2];

								if (!entries.contains(new_write) && overlapping == true) {//there is some overlap
									entries.add(new_write);
									bw.write(new_write+"\n");
									entries_prob.put(new_write, crf_prob);
									System.out.println("written2\t"+new_write);
								}
								if (entries.contains(new_write) && overlapping == true) {
									entries_prob.put(new_write, entries_prob.get(new_write)+crf_prob);
								}
								if (!entries_full.contains(new_write)) {
									bw_full.write(new_write+"\n");
									entries_full.add(new_write);
									entries_full_prob.put(new_write, crf_prob);
								}
								else {
									entries_full_prob.put(new_write, entries_full_prob.get(new_write)+crf_prob);
								}
							}
						}
					}
					//System.out.println(sentence);
				}while(!st.equals(""));
				//bw.close();
				//System.exit(0);

			}
			
			//write_entries and entries_full in separate file and then try on that
			String filename_t1 = folder_path_svm+"targets_t_"+fold;
			
			BufferedWriter bw_t1 = new BufferedWriter(new FileWriter(filename_t1));
			Iterator it1 = entries.iterator();
			while(it1.hasNext()) {
				String line = (String)it1.next();
				line = line + "\t" +String.valueOf(entries_prob.get(line)) + "\n";
				bw_t1.write(line);
			}
			bw_t1.close();
			
			String filename_t2 = folder_path_svm+"targets_full_t_"+fold;
			
			BufferedWriter bw_t2 = new BufferedWriter(new FileWriter(filename_t2));
			it1 = entries_full.iterator();
			while(it1.hasNext()) {
				String line = (String)it1.next();
				line = line + "\t" +String.valueOf(entries_full_prob.get(line)) + "\n";
				bw_t2.write(line);
			}

			bw_t2.close();
			
			br1.close();
			bw.close();
			bw_full.close();
			gold_targets = new HashMap<String, Entries>();
		}
	}


	
	//except which file, everything goes to the training set
	/*public static void create_file_holders () throws IOException{
		for (int fold=0; fold<10; fold++) {
			//if (fold == 1) {System.exit(0);}
			String file_name = folder_path+"holders_gold_"+fold;
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
			
			String train_name = folder_path+"holders_"+fold;
			BufferedReader br1 = new BufferedReader(new FileReader(train_name));
			String filename = folder_path_svm+"holders_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			String filename_full = folder_path_svm+"holders_full_"+fold;
			BufferedWriter bw_full = new BufferedWriter(new FileWriter(filename_full));

			HashSet<String> entries = new HashSet<String>();
			HashSet<String> entries_full = new HashSet<String>();

			
			loop1 : while((st = br1.readLine())!=null) {
				st = st.substring(0, st.length()-1);
				String sentence = st;
				//System.out.println(sentence);
				//System.out.println(gold_holders.containsKey(sentence));
				//System.out.println(gold_holders.get(st));

				if (!gold_holders.containsKey(st)) {
					do {
						st = br1.readLine();
					}while(!st.equals(""));
					continue loop1;
				}
				
				//System.out.println(st);
				
				do {
					st = br1.readLine();
					if (st.split("\t").length > 2) {
						int crf_path = Integer.parseInt(st.split("\t")[0]);
						double crf_prob = Double.parseDouble(st.split("\t")[1]);
						for (int ind = 2; ind < st.split("\t").length; ind=ind+5) {
							Triples2 crf_entry = new Triples2(st.split("\t")[ind],
									st.split("\t")[ind+1], st.split("\t")[ind+2], st.split("\t")[ind+3],
									st.split("\t")[ind+4]);
							
							Iterator<Triples2> it = gold_holders.get(sentence).unit.iterator();
							boolean matched = false;
							boolean overlapping = false;
							while (it.hasNext()) {
								Triples2 gold = (Triples2) it.next();
								if (crf_entry.word_path_equals(gold)) { //instead of soft_equals have word_equals
									//the final label is 1
									//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
									//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
									String to_write = "+1\t"+st.split("\t")[ind]+"\t"
													+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";

									if (!entries.contains(to_write)) {
										bw.write(to_write);
										entries.add(to_write);
										System.out.println("written\t"+to_write);
									}
									
									if(!entries_full.contains(to_write)) {
										bw_full.write(to_write);
										entries_full.add(to_write);
									}
									matched = true;
								}
								
								if (Isoverlap(crf_entry, gold)) {
									overlapping  = true;
								}
							}
														
							if (matched == false) {
									//negative example
								//System.out.println("matched false");
								//String new_write = "-1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
								//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								String new_write = "-1\t"+st.split("\t")[ind]+"\t"
										+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";

								if (!entries.contains(new_write) && overlapping == true) {//there is some overlap
									entries.add(new_write);
									bw.write(new_write);
									System.out.println("written2\t"+new_write);
								}
								if (!entries_full.contains(new_write)) {
									bw_full.write(new_write);
									entries_full.add(new_write);
								}
							}
						}
					}
					//System.out.println(sentence);
				}while(!st.equals(""));
				//bw.close();
				//System.exit(0);

			}
			br1.close();
			bw.close();
			bw_full.close();
			gold_holders = new HashMap<String, Entries>();
		}
	}*/
	
	/*public static void create_file_targets () throws IOException{
		for (int fold=0; fold<10; fold++) {
			String file_name = folder_path+"targets_gold_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(file_name));
			String st;
			while((st = br.readLine())!=null) {
				String entries = br.readLine();
				String split[] = entries.split("\t");
				LinkedList<Triples2> units = new LinkedList(); 
				for (int index=1; index<split.length; index= index+5) {
					units.add(new Triples2(split[index], split[index+1], split[index+2], split[index+3], split[index+4]));
				}
				gold_targets.put(st, new Entries(fold, units, 1.0));
			}
			br.close();
			
			String train_name = folder_path+"targets_"+fold;
			BufferedReader br1 = new BufferedReader(new FileReader(train_name));
			String filename = folder_path_svm+"targets_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			String filename_full = folder_path_svm+"targets_full_"+fold;
			BufferedWriter bw_full = new BufferedWriter(new FileWriter(filename_full));


			HashSet<String> entries = new HashSet<String>();
			HashSet<String> entries_full = new HashSet<String>();


			
			loop1 : while((st = br1.readLine())!=null) {
				st = st.substring(0, st.length()-1);
				String sentence = st;
				//System.out.println(sentence);
				//System.out.println(gold_holders.containsKey(sentence));
				//System.out.println(gold_holders.get(st));

				if (!gold_targets.containsKey(st)) {
					do {
						st = br1.readLine();
					}while(!st.equals(""));
					continue loop1;
				}
				
				//System.out.println(st);
				

				do {
					st = br1.readLine();
					if (st.split("\t").length > 2) {
						int crf_path = Integer.parseInt(st.split("\t")[0]);
						double crf_prob = Double.parseDouble(st.split("\t")[1]);
						for (int ind = 2; ind < st.split("\t").length; ind=ind+5) {
							Triples2 crf_entry = new Triples2(st.split("\t")[ind],
									st.split("\t")[ind+1], st.split("\t")[ind+2], st.split("\t")[ind+3],
									st.split("\t")[ind+4]);
							
							Iterator it = gold_targets.get(sentence).unit.iterator();
							boolean matched = false;
							boolean overlapping = false;
							while (it.hasNext()) {
								Triples2 gold = (Triples2) it.next();
								if (crf_entry.word_path_equals(gold)) { //instead of soft_equals
									//the final label is 1
									//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
									//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
									String to_write = "+1\t"+st.split("\t")[ind]+"\t"
											+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";

									if (!entries.contains(to_write)) {
										bw.write(to_write);
										entries.add(to_write);
									}
									
									if(!entries_full.contains(to_write)) {
										bw_full.write(to_write);
										entries_full.add(to_write);
									}

									matched = true;
								}
								if (Isoverlap(crf_entry, gold)) {
									overlapping  = true;
								}

							}
														
							if (matched == false ) {
									//negative example
								//System.out.println("matched false");
								//String new_write = "-1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
										//+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								String new_write = "-1\t"+st.split("\t")[ind]+"\t"
												+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";

								if (!entries.contains(new_write) && overlapping == true ) {
									bw.write(new_write);
									entries.add(new_write);
								}
								
								if (!entries_full.contains(new_write)) {
									bw_full.write(new_write);
									entries_full.add(new_write);
								}

							}
							
						}
					}
					//System.out.println(sentence);
				}while(!st.equals(""));
				//bw.close();
				//System.exit(0);

			}
			br1.close();
			bw.close();
			bw_full.close();
			gold_targets = new HashMap<String, Entries>();
		}
	}*/
	
	public static void topic_features(String filename) throws IOException{
				
		//clear old topic assignments
		word_topic_assignment = new HashMap<String, Topics>();
		path_topic_assignment = new HashMap<String, Topics>();
		
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		String st;
		while((st = br.readLine())!=null) {
			String word_line[] = st.split("\t");
			String path_line[] = (st=br.readLine()).split("\t");
			
			for (int i=1; i<word_line.length; i++) {
				int classname = Integer.parseInt(word_line[0]);
				String word = word_line[i].split(",")[0];
				int freq = (int)Double.parseDouble(word_line[i].split(",")[1]);
				if (word_topic_assignment.containsKey(word)) {
					Topics to_change = word_topic_assignment.get(word);
					//assuming that the same pair will not appear again
					t_Pairs tt = new t_Pairs(classname, freq);
					to_change.class_freq.add(tt);
					word_topic_assignment.put(word, to_change);
				}
				else {
					t_Pairs tt = new t_Pairs(classname, freq);
					LinkedList<t_Pairs> temp = new LinkedList<t_Pairs>();
					temp.add(tt);
					System.out.println(temp.get(0).classname+"\t"+temp.get(0).freq);
					word_topic_assignment.put(word, new Topics(temp));
				}
			}
			
			
			for (int i=1; i<path_line.length; i++) {
				int classname = Integer.parseInt(path_line[0]);
				String path = path_line[i].split(",")[0];
				int freq = (int)Double.parseDouble(path_line[i].split(",")[1]);
				if (path_topic_assignment.containsKey(path)) {
					Topics to_change = path_topic_assignment.get(path);
					//assuming that the same pair will not appear again
					t_Pairs tt = new t_Pairs(classname, freq);
					to_change.class_freq.add(tt);
					path_topic_assignment.put(path, to_change);
				}
				else {
					t_Pairs tt = new t_Pairs(classname, freq);
					LinkedList<t_Pairs> temp = new LinkedList<t_Pairs>();
					temp.add(tt);
					path_topic_assignment.put(path, new Topics(temp));
				}
			}
		}	
		br.close();
	}
	
	public static void training_file_holder_prob(int except) throws IOException{
		//HashSet<String> features_entity_word = new HashSet<String>();
		//HashSet<String> features_path = new HashSet<String>();
		//HashSet<String> features_entity_word_path = new HashSet<String>();
		LinkedList<String> features_topic_words = new LinkedList<String>();
		LinkedList<String> features_topic_paths = new LinkedList<String>();
		//HashSet<String> features_entity_ = new HashSet<String>();
		
		for (int fold = 0; fold < 10; fold++) {
			//three kinds of features! entity_word, path, entity_word-path
			if (fold == except)
				continue;
			
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"holders_t_"+fold));
			String st;
			while((st = br.readLine())!=null) {
				//features_entity_word.add(st.split("\t")[1].split("-")[0]);
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 5) {
					//System.out.println("Should not continue1\t"+st);
					continue;
				}
				//features_path.add(st.split("\t")[3]);
				//System.out.println(st.split("\t")[3]);
				//features_entity_word_path.add(st.split("\t")[1].split("-")[0]+st.split("\t")[3]);
			}
			br.close();
		}
		
		//Adding the topic features here!
		
		String filename = folder_topic+"train_holders_"+except;
		topic_features(filename);
		for (String word_features : word_topic_assignment.keySet()) {
			features_topic_words.add(word_features);
		}
		
		for (String path_features : path_topic_assignment.keySet()) {
			features_topic_words.add(path_features);
		}
		
		
		
		//System.out.println(features_entity_word.size()+"\t"+features_path.size());
				//+"\t"+features_entity_word_path.size()); //Not too bad! 3321, 5730, 17083
		
		//LinkedList<String> features_entity_word1 = new LinkedList<String>();
		//LinkedList<String> features_path1 = new LinkedList<String>();
		//LinkedList<String> features_entity_word_path1 = new LinkedList<String>();
		
		/*Iterator it = features_entity_word.iterator();
		while(it.hasNext()) {
			features_entity_word1.add((String)it.next());
		}
		
		it = features_path.iterator();
		while(it.hasNext()) {
			String path1 = (String)it.next();
			features_path1.add(path1);
			if ( path1.equals("-nsubj")) {
				System.out.println(path1);
			}
			
			
		}*/
		
		/*it = features_entity_word_path.iterator();
		while(it.hasNext()) {
			features_entity_word_path1.add((String)it.next());
		}*/
		
		//int size = features_entity_word1.size()+features_path1.size();
				//+features_entity_word_path1.size();
		//System.out.println(except+"\t"+size);

		//features_entity_word1.indexOf(o)
		BufferedWriter bw = new BufferedWriter(new FileWriter(folder_path_svm+"training_holders_t_"+except));
		for (int fold = 0; fold < 10; fold++) {
			if (fold == except)
				continue;
			System.out.println(fold);
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"holders_t_"+fold));
			String st;
			String output = "";
			//HashSet<String> training_data = new HashSet<String>();
			while((st = br.readLine())!=null) {
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 5) {
					//System.out.println("Should not continue\t"+st);
					continue;
				}
				output = st.split("\t")[0];
				//output = output+"\t1:"+ st.split("\t")[1];
				//int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
				//int feature_path = features_path1.indexOf(st.split("\t")[3])+features_entity_word1.size()+2;
				//int feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
				//		(st.split("\t")[3])) + features_entity_word1.size() + features_path.size() +3;
				
				//topic features : 
				//		//HashSet<String> features_topic_words = new HashSet<String>();
				//HashSet<String> features_topic_paths = new HashSet<String>();
				
				
				LinkedList<Integer> feature_word_topic = new LinkedList<Integer>();
				
				if (word_topic_assignment.
						get(st.split("\t")[1].split("-")[0])!=null) {
					LinkedList<t_Pairs> ttt = word_topic_assignment.
							get(st.split("\t")[1].split("-")[0]).class_freq;
					for (int k = 0; k<ttt.size(); k++) {
						feature_word_topic.add(word_topic_assignment.
								get(st.split("\t")[1].split("-")[0]).class_freq.get(k).classname
								//+ features_entity_word1.size() + features_path.size()+4);
								+1);
					}
							//+ features_entity_word_path1.size()+4;					
				}
				
				LinkedList<Integer> feature_path_topic = new LinkedList<Integer>();
				
				if (path_topic_assignment.
						get(st.split("\t")[3])!=null) {
					LinkedList<t_Pairs> ttt = path_topic_assignment.
							get(st.split("\t")[3]).class_freq;
					for (int k = 0; k<ttt.size(); k++) {
						feature_path_topic.add(path_topic_assignment.
								get(st.split("\t")[3]).class_freq.get(k).classname
								//+ features_entity_word1.size() + features_path.size()+20+5);
								+20+2);
					}
				}
				

				
				//output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[4]
				//+"\t"+String.valueOf(feature_path)+":"+st.split("\t")[4];
				if (feature_word_topic.size()!=0 && Topics.max_value(word_topic_assignment.
						get(st.split("\t")[1].split("-")[0]).class_freq).classname >= 0) {
					for (int k=0; k < feature_word_topic.size(); k++) {
						output+= "\t"+String.valueOf(feature_word_topic.get(k))+":"+st.split("\t")[4];
					}
				}
				if (feature_path_topic.size()!=0 && Topics.max_value(path_topic_assignment.
						get(st.split("\t")[3]).class_freq).classname >= 0) {
					
					for (int k=0; k < feature_path_topic.size(); k++) {
						output+= "\t"+String.valueOf(feature_path_topic.get(k))+":"+st.split("\t")[4];
					}
				}
						//+"\t"+ String.valueOf(feature_word_path)+":1"
				output += "\n";
				//training_data.add(output);
				bw.write(output);;
			}
			//System.out.println(training_data.size());

			br.close();			
		}
		bw.close();
		
		//Now the test file
		//HashSet<String> training_data = new HashSet<String>();
		bw = new BufferedWriter(new FileWriter(folder_path_svm+"test_holders_t_"+except));
		BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"holders_full_t_"+except));
		String st;
		String output = "";
		int count = 0;
		while((st = br.readLine())!=null) {
			//System.out.println(st);
			//System.out.println(st+"\t"+st.split("\t").length);
			//if (st.split("\t").length < 4)
			//	continue;
			output = st.split("\t")[0];
			//output = output+"\t1:"+ st.split("\t")[1];
			//int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
			
			LinkedList<Integer> feature_word_topic = new LinkedList<Integer>();
			if (word_topic_assignment.
					get(st.split("\t")[1].split("-")[0])!=null) {
				//feature_word_topic = Topics.max_value(word_topic_assignment.
				//		get(st.split("\t")[1].split("-")[0]).class_freq).classname
				//		+  features_entity_word1.size() + features_path.size() + 4;
				
				LinkedList<t_Pairs> ttt = word_topic_assignment.
						get(st.split("\t")[1].split("-")[0]).class_freq;
				for (int k = 0; k<ttt.size(); k++) {
					feature_word_topic.add(word_topic_assignment.
							get(st.split("\t")[1].split("-")[0]).class_freq.get(k).classname
							//+ features_entity_word1.size() + features_path.size()+4);
							+4);
				}

						//+ features_entity_word_path1.size()+4;
				
			}

			//int feature_path = -1;
			LinkedList<Integer> feature_path_topic = new LinkedList<Integer>();
			if (st.split("\t").length < 5) {
				//feature_path = -1;
			}
			else {
				//feature_path = features_path1.indexOf(st.split("\t")[3]) + features_entity_word1.size()+2;
				
				if (path_topic_assignment.
						get(st.split("\t")[3])!=null) {
				/*	feature_path_topic = Topics.max_value(path_topic_assignment.
							get(st.split("\t")[3]).class_freq).classname
							+ features_entity_word1.size() + features_path.size() + 20 + 5;
							//+ features_entity_word_path1.size()+20+5;*/
					LinkedList<t_Pairs> ttt = path_topic_assignment.
							get(st.split("\t")[3]).class_freq;
					for (int k = 0; k<ttt.size(); k++) {
						feature_path_topic.add(path_topic_assignment.
								get(st.split("\t")[3]).class_freq.get(k).classname
							//	+ features_entity_word1.size() + features_path.size()+20+5);
								+20+5);
					}

				}

			}
			/*int feature_word_path = -1;
			if (st.split("\t").length < 5) {
				feature_word_path = -1;
			}
			else {
				feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path1.size() + 3;
			}*/
					
			/*if (feature_word!=-1 && features_entity_word1.indexOf(st.split("\t")[1].split("-")[0]) != -1) {
				if (st.split("\t").length == 5)
					output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[4];
				else {
					output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[3];
				}
			}
			if (feature_path!=-1 && features_path1.indexOf(st.split("\t")[3]) != -1) {
				output = output+"\t"+String.valueOf(feature_path)+":"+st.split("\t")[4];
			}*/
			
			if (feature_word_topic.size()!=0 && Topics.max_value(word_topic_assignment.
					get(st.split("\t")[1].split("-")[0]).class_freq).classname >= 0) {
					//output+= "\t"+String.valueOf(feature_word_topic)+":"+st.split("\t")[4];
				for (int k=0; k < feature_word_topic.size(); k++) {
					output+= "\t"+String.valueOf(feature_word_topic.get(k))+":"+st.split("\t")[4];
				}

			}
			
			if (st.split("\t").length > 4) {
				if (feature_path_topic.size()!=0 && Topics.max_value(path_topic_assignment.
						get(st.split("\t")[3]).class_freq).classname >= 0) {
					for (int k=0; k < feature_path_topic.size(); k++) {
						output+= "\t"+String.valueOf(feature_path_topic.get(k))+":"+st.split("\t")[4];
					}
				}
			}

			
			/*if (feature_word_path!=-1 && features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
					(st.split("\t")[3]))!=-1) {
				output = output+"\t"+ String.valueOf(feature_word_path)+":1";
			}*/
			output += "\n";
			
			//+"\t"+String.valueOf(feature_path)+":1\t"+ String.valueOf(feature_word_path)+":1\n";
			bw.write(output);
			//System.out.println((++count));
			//training_data.add(output);
		}
		//System.out.println(training_data.size());
		bw.close();
		br.close();
	}
	
	public static void training_file_target_prob(int except) throws IOException{
		//HashSet<String> features_entity_word = new HashSet<String>();
		//HashSet<String> features_path = new HashSet<String>();
		//HashSet<String> features_entity_word_path = new HashSet<String>();
		LinkedList<String> features_topic_words = new LinkedList<String>();
		LinkedList<String> features_topic_paths = new LinkedList<String>();
		//HashSet<String> features_entity_ = new HashSet<String>();
		
		for (int fold = 0; fold < 10; fold++) {
			//three kinds of features! entity_word, path, entity_word-path
			if (fold == except)
				continue;
			
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_t_"+fold));
			String st;
			while((st = br.readLine())!=null) {
				//features_entity_word.add(st.split("\t")[1].split("-")[0]);
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 5) {
					//System.out.println("Should not continue1\t"+st);
					continue;
				}
				//features_path.add(st.split("\t")[3]);
				//System.out.println(st.split("\t")[3]);
				//features_entity_word_path.add(st.split("\t")[1].split("-")[0]+st.split("\t")[3]);
			}
			br.close();
		}
		
		//Adding the topic features here!
		
		String filename = folder_topic+"train_targets_"+except;
		topic_features(filename);
		for (String word_features : word_topic_assignment.keySet()) {
			features_topic_words.add(word_features);
		}
		
		for (String path_features : path_topic_assignment.keySet()) {
			features_topic_words.add(path_features);
		}
		
		
		
		//System.out.println(features_entity_word.size()+"\t"+features_path.size());
				//+"\t"+features_entity_word_path.size()); //Not too bad! 3321, 5730, 17083
		
		//LinkedList<String> features_entity_word1 = new LinkedList<String>();
		//LinkedList<String> features_path1 = new LinkedList<String>();
		//LinkedList<String> features_entity_word_path1 = new LinkedList<String>();
		
		/*Iterator it = features_entity_word.iterator();
		while(it.hasNext()) {
			features_entity_word1.add((String)it.next());
		}
		
		it = features_path.iterator();
		while(it.hasNext()) {
			String path1 = (String)it.next();
			features_path1.add(path1);
			if ( path1.equals("-nsubj")) {
				System.out.println(path1);
			}
			
			
		}*/
		
		/*it = features_entity_word_path.iterator();
		while(it.hasNext()) {
			features_entity_word_path1.add((String)it.next());
		}*/
		
		//int size = features_entity_word1.size()+features_path1.size();
				//+features_entity_word_path1.size();
		//System.out.println(except+"\t"+size);

		//features_entity_word1.indexOf(o)
		BufferedWriter bw = new BufferedWriter(new FileWriter(folder_path_svm+"training_targets_t_"+except));
		for (int fold = 0; fold < 10; fold++) {
			if (fold == except)
				continue;
			System.out.println(fold);
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_t_"+fold));
			String st;
			String output = "";
			//HashSet<String> training_data = new HashSet<String>();
			while((st = br.readLine())!=null) {
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 5) {
					//System.out.println("Should not continue\t"+st);
					continue;
				}
				output = st.split("\t")[0];
				//output = output+"\t1:"+ st.split("\t")[1];
				//int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
				//int feature_path = features_path1.indexOf(st.split("\t")[3])+features_entity_word1.size()+2;
				//int feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
				//		(st.split("\t")[3])) + features_entity_word1.size() + features_path.size() +3;
				
				//topic features : 
				//		//HashSet<String> features_topic_words = new HashSet<String>();
				//HashSet<String> features_topic_paths = new HashSet<String>();
				
				
				LinkedList<Integer> feature_word_topic = new LinkedList<Integer>();
				
				if (word_topic_assignment.
						get(st.split("\t")[1].split("-")[0])!=null) {
					LinkedList<t_Pairs> ttt = word_topic_assignment.
							get(st.split("\t")[1].split("-")[0]).class_freq;
					for (int k = 0; k<ttt.size(); k++) {
						feature_word_topic.add(word_topic_assignment.
								get(st.split("\t")[1].split("-")[0]).class_freq.get(k).classname
								//+ features_entity_word1.size() + features_path.size()+4);
								+1);
					}
							//+ features_entity_word_path1.size()+4;					
				}
				
				LinkedList<Integer> feature_path_topic = new LinkedList<Integer>();
				
				if (path_topic_assignment.
						get(st.split("\t")[3])!=null) {
					LinkedList<t_Pairs> ttt = path_topic_assignment.
							get(st.split("\t")[3]).class_freq;
					for (int k = 0; k<ttt.size(); k++) {
						feature_path_topic.add(path_topic_assignment.
								get(st.split("\t")[3]).class_freq.get(k).classname
								//+ features_entity_word1.size() + features_path.size()+20+5);
								+20+2);
					}
				}
				

				
				//output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[4]
				//+"\t"+String.valueOf(feature_path)+":"+st.split("\t")[4];
				if (feature_word_topic.size()!=0 && Topics.max_value(word_topic_assignment.
						get(st.split("\t")[1].split("-")[0]).class_freq).classname >= 0) {
					for (int k=0; k < feature_word_topic.size(); k++) {
						output+= "\t"+String.valueOf(feature_word_topic.get(k))+":"+st.split("\t")[4];
					}
				}
				if (feature_path_topic.size()!=0 && Topics.max_value(path_topic_assignment.
						get(st.split("\t")[3]).class_freq).classname >= 0) {
					
					for (int k=0; k < feature_path_topic.size(); k++) {
						output+= "\t"+String.valueOf(feature_path_topic.get(k))+":"+st.split("\t")[4];
					}
				}
						//+"\t"+ String.valueOf(feature_word_path)+":1"
				output += "\n";
				//training_data.add(output);
				bw.write(output);;
			}
			//System.out.println(training_data.size());

			br.close();			
		}
		bw.close();
		
		//Now the test file
		//HashSet<String> training_data = new HashSet<String>();
		bw = new BufferedWriter(new FileWriter(folder_path_svm+"test_targets_t_"+except));
		BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_full_t_"+except));
		String st;
		String output = "";
		int count = 0;
		while((st = br.readLine())!=null) {
			//System.out.println(st);
			//System.out.println(st+"\t"+st.split("\t").length);
			//if (st.split("\t").length < 4)
			//	continue;
			output = st.split("\t")[0];
			//output = output+"\t1:"+ st.split("\t")[1];
			//int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
			
			LinkedList<Integer> feature_word_topic = new LinkedList<Integer>();
			if (word_topic_assignment.
					get(st.split("\t")[1].split("-")[0])!=null) {
				//feature_word_topic = Topics.max_value(word_topic_assignment.
				//		get(st.split("\t")[1].split("-")[0]).class_freq).classname
				//		+  features_entity_word1.size() + features_path.size() + 4;
				
				LinkedList<t_Pairs> ttt = word_topic_assignment.
						get(st.split("\t")[1].split("-")[0]).class_freq;
				for (int k = 0; k<ttt.size(); k++) {
					feature_word_topic.add(word_topic_assignment.
							get(st.split("\t")[1].split("-")[0]).class_freq.get(k).classname
							//+ features_entity_word1.size() + features_path.size()+4);
							+4);
				}

						//+ features_entity_word_path1.size()+4;
				
			}

			//int feature_path = -1;
			LinkedList<Integer> feature_path_topic = new LinkedList<Integer>();
			if (st.split("\t").length < 5) {
				//feature_path = -1;
			}
			else {
				//feature_path = features_path1.indexOf(st.split("\t")[3]) + features_entity_word1.size()+2;
				
				if (path_topic_assignment.
						get(st.split("\t")[3])!=null) {
				/*	feature_path_topic = Topics.max_value(path_topic_assignment.
							get(st.split("\t")[3]).class_freq).classname
							+ features_entity_word1.size() + features_path.size() + 20 + 5;
							//+ features_entity_word_path1.size()+20+5;*/
					LinkedList<t_Pairs> ttt = path_topic_assignment.
							get(st.split("\t")[3]).class_freq;
					for (int k = 0; k<ttt.size(); k++) {
						feature_path_topic.add(path_topic_assignment.
								get(st.split("\t")[3]).class_freq.get(k).classname
							//	+ features_entity_word1.size() + features_path.size()+20+5);
								+20+5);
					}

				}

			}
			/*int feature_word_path = -1;
			if (st.split("\t").length < 5) {
				feature_word_path = -1;
			}
			else {
				feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path1.size() + 3;
			}*/
					
			/*if (feature_word!=-1 && features_entity_word1.indexOf(st.split("\t")[1].split("-")[0]) != -1) {
				if (st.split("\t").length == 5)
					output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[4];
				else {
					output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[3];
				}
			}
			if (feature_path!=-1 && features_path1.indexOf(st.split("\t")[3]) != -1) {
				output = output+"\t"+String.valueOf(feature_path)+":"+st.split("\t")[4];
			}*/
			
			if (feature_word_topic.size()!=0 && Topics.max_value(word_topic_assignment.
					get(st.split("\t")[1].split("-")[0]).class_freq).classname >= 0) {
					//output+= "\t"+String.valueOf(feature_word_topic)+":"+st.split("\t")[4];
				for (int k=0; k < feature_word_topic.size(); k++) {
					output+= "\t"+String.valueOf(feature_word_topic.get(k))+":"+st.split("\t")[4];
				}

			}
			
			if (st.split("\t").length > 4) {
				if (feature_path_topic.size()!=0 && Topics.max_value(path_topic_assignment.
						get(st.split("\t")[3]).class_freq).classname >= 0) {
					for (int k=0; k < feature_path_topic.size(); k++) {
						output+= "\t"+String.valueOf(feature_path_topic.get(k))+":"+st.split("\t")[4];
					}
				}
			}

			
			/*if (feature_word_path!=-1 && features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
					(st.split("\t")[3]))!=-1) {
				output = output+"\t"+ String.valueOf(feature_word_path)+":1";
			}*/
			output += "\n";
			
			//+"\t"+String.valueOf(feature_path)+":1\t"+ String.valueOf(feature_word_path)+":1\n";
			bw.write(output);
			//System.out.println((++count));
			//training_data.add(output);
		}
		//System.out.println(training_data.size());
		bw.close();
		br.close();
	}

	
	
	/*public static void training_file_target_prob(int except) throws IOException{
		HashSet<String> features_entity_word = new HashSet<String>();
		HashSet<String> features_path = new HashSet<String>();
		HashSet<String> features_entity_word_path = new HashSet<String>();
		//HashSet<String> features_entity_ = new HashSet<String>();
		
		for (int fold = 0; fold < 10; fold++) {
			//three kinds of features! entity_word, path, entity_word-path
			if (fold == except)
				continue;
			
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_t_"+fold));
			String st;
			while((st = br.readLine())!=null) {
				features_entity_word.add(st.split("\t")[1].split("-")[0]);
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 5) {
					//System.out.println("Should not continue1\t"+st);
					continue;
				}
				features_path.add(st.split("\t")[3]);
				System.out.println(st.split("\t")[3]);
				features_entity_word_path.add(st.split("\t")[1].split("-")[0]+st.split("\t")[3]);
			}
			br.close();
		}
		
		System.out.println(features_entity_word.size()+"\t"+features_path.size()
				+"\t"+features_entity_word_path.size()); //Not too bad! 3321, 5730, 17083
		
		LinkedList<String> features_entity_word1 = new LinkedList<String>();
		LinkedList<String> features_path1 = new LinkedList<String>();
		LinkedList<String> features_entity_word_path1 = new LinkedList<String>();
		
		Iterator it = features_entity_word.iterator();
		while(it.hasNext()) {
			features_entity_word1.add((String)it.next());
		}
		
		it = features_path.iterator();
		while(it.hasNext()) {
			String path1 = (String)it.next();
			features_path1.add(path1);
			if ( path1.equals("-nsubj")) {
				System.out.println(path1);
			}
			
			
		}
		
		it = features_entity_word_path.iterator();
		while(it.hasNext()) {
			features_entity_word_path1.add((String)it.next());
		}
		
		int size = features_entity_word1.size()+features_path1.size()+features_entity_word_path1.size();
		System.out.println(except+"\t"+size);

		//features_entity_word1.indexOf(o)
		BufferedWriter bw = new BufferedWriter(new FileWriter(folder_path_svm+"training_targets_t_"+except));
		for (int fold = 0; fold < 10; fold++) {
			if (fold == except)
				continue;
			System.out.println(fold);
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_t_"+fold));
			String st;
			String output = "";
			//HashSet<String> training_data = new HashSet<String>();
			while((st = br.readLine())!=null) {
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 5) {
					//System.out.println("Should not continue\t"+st);
					continue;
				}
				output = st.split("\t")[0];
				//output = output+"\t1:"+ st.split("\t")[1];
				int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
				int feature_path = features_path1.indexOf(st.split("\t")[3])+features_entity_word1.size()+2;
				int feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path.size() +3;
				output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[4]
				+"\t"+String.valueOf(feature_path)+":"+st.split("\t")[4]
						//+"\t"+ String.valueOf(feature_word_path)+":1"
						+"\n";
				//training_data.add(output);
				bw.write(output);;
			}
			//System.out.println(training_data.size());

			br.close();			
		}
		bw.close();
		
		//Now the test file
		//HashSet<String> training_data = new HashSet<String>();
		bw = new BufferedWriter(new FileWriter(folder_path_svm+"test_targets_t_"+except));
		BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_full_t_"+except));
		String st;
		String output = "";
		int count = 0;
		while((st = br.readLine())!=null) {
			//System.out.println(st);
			//System.out.println(st+"\t"+st.split("\t").length);
			//if (st.split("\t").length < 4)
			//	continue;
			output = st.split("\t")[0];
			//output = output+"\t1:"+ st.split("\t")[1];
			int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
			int feature_path = -1;
			if (st.split("\t").length < 5) {
				feature_path = -1;
			}
			else {
				feature_path = features_path1.indexOf(st.split("\t")[3]) + features_entity_word1.size()+2;
			}
			int feature_word_path = -1;
			if (st.split("\t").length < 5) {
				feature_word_path = -1;
			}
			else {
				feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path1.size() + 3;
			}
					
			if (feature_word!=-1 && features_entity_word1.indexOf(st.split("\t")[1].split("-")[0]) != -1) {
				if (st.split("\t").length == 5)
					output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[4];
				else {
					output = output+"\t"+String.valueOf(feature_word)+":"+st.split("\t")[3];
				}
			}
			if (feature_path!=-1 && features_path1.indexOf(st.split("\t")[3]) != -1) {
				output = output+"\t"+String.valueOf(feature_path)+":"+st.split("\t")[4];
			}
			output += "\n";
			
			//+"\t"+String.valueOf(feature_path)+":1\t"+ String.valueOf(feature_word_path)+":1\n";
			bw.write(output);
			//System.out.println((++count));
			//training_data.add(output);
		}
		//System.out.println(training_data.size());
		bw.close();
		br.close();
	}*/


	public static void training_file_holder(int except) throws IOException{
		HashSet<String> features_entity_word = new HashSet<String>();
		HashSet<String> features_path = new HashSet<String>();
		HashSet<String> features_entity_word_path = new HashSet<String>();
		//HashSet<String> features_entity_ = new HashSet<String>();
		
		for (int fold = 0; fold < 10; fold++) {
			//three kinds of features! entity_word, path, entity_word-path
			if (fold == except)
				continue;
			
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"holders_"+fold));
			String st;
			while((st = br.readLine())!=null) {
				features_entity_word.add(st.split("\t")[1].split("-")[0]);
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 4) {
					//System.out.println("Should not continue1\t"+st);
					continue;
				}
				features_path.add(st.split("\t")[3]);
				features_entity_word_path.add(st.split("\t")[1].split("-")[0]+st.split("\t")[3]);
			}
			br.close();
		}
		
		//System.out.println(features_entity_word.size()+"\t"+features_path.size()
		//		+"\t"+features_entity_word_path.size()); //Not too bad! 3321, 5730, 17083
		
		LinkedList<String> features_entity_word1 = new LinkedList<String>();
		LinkedList<String> features_path1 = new LinkedList<String>();
		LinkedList<String> features_entity_word_path1 = new LinkedList<String>();
		
		Iterator it = features_entity_word.iterator();
		while(it.hasNext()) {
			features_entity_word1.add((String)it.next());
		}
		
		it = features_path.iterator();
		while(it.hasNext()) {
			features_path1.add((String)it.next());
		}
		
		it = features_entity_word_path.iterator();
		while(it.hasNext()) {
			features_entity_word_path1.add((String)it.next());
		}
		
		int size = features_entity_word1.size()+features_path1.size()+features_entity_word_path1.size();
		System.out.println(except+"\t"+size);

		//features_entity_word1.indexOf(o)
		BufferedWriter bw = new BufferedWriter(new FileWriter(folder_path_svm+"training_holders_"+except));
		for (int fold = 0; fold < 10; fold++) {
			if (fold == except)
				continue;
			System.out.println(fold);
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"holders_"+fold));
			String st;
			String output = "";
			//HashSet<String> training_data = new HashSet<String>();
			while((st = br.readLine())!=null) {
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 4) {
					//System.out.println("Should not continue\t"+st);
					continue;
				}
				output = st.split("\t")[0];
				//output = output+"\t1:"+ st.split("\t")[1];
				int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
				int feature_path = features_path1.indexOf(st.split("\t")[3])+features_entity_word1.size()+2;
				int feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path.size() +3;
				output = output+"\t"+String.valueOf(feature_word)+":1"
				+"\t"+String.valueOf(feature_path)+":1"
						//+"\t"+ String.valueOf(feature_word_path)+":1"
						+"\n";
				//training_data.add(output);
				bw.write(output);;
			}
			//System.out.println(training_data.size());

			br.close();			
		}
		bw.close();
		
		//Now the test file
		//HashSet<String> training_data = new HashSet<String>();
		bw = new BufferedWriter(new FileWriter(folder_path_svm+"test_holders_"+except));
		BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"holders_full_"+except));
		String st;
		String output = "";
		int count = 0;
		while((st = br.readLine())!=null) {
			//System.out.println(st);
			//System.out.println(st+"\t"+st.split("\t").length);
			//if (st.split("\t").length < 4)
			//	continue;
			output = st.split("\t")[0];
			//output = output+"\t1:"+ st.split("\t")[1];
			int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
			int feature_path = -1;
			if (st.split("\t").length < 4) {
				feature_path = -1;
			}
			else {
				feature_path = features_path1.indexOf(st.split("\t")[3]) + features_entity_word1.size()+2;
			}
			int feature_word_path = -1;
			if (st.split("\t").length < 4) {
				feature_word_path = -1;
			}
			else {
				feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path1.size() + 3;
			}
					
			if (feature_word!=-1 && features_entity_word1.indexOf(st.split("\t")[1].split("-")[0]) != -1) {
				output = output+"\t"+String.valueOf(feature_word)+":1";
			}
			if (feature_path!=-1 && features_path1.indexOf(st.split("\t")[3]) != -1) {
				output = output+"\t"+String.valueOf(feature_path)+":1";
			}
			/*if (feature_word_path!=-1 && features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
					(st.split("\t")[3]))!=-1) {
				output = output+"\t"+ String.valueOf(feature_word_path)+":1";
			}*/
			output += "\n";
			
			//+"\t"+String.valueOf(feature_path)+":1\t"+ String.valueOf(feature_word_path)+":1\n";
			bw.write(output);
			//System.out.println((++count));
			//training_data.add(output);
		}
		//System.out.println(training_data.size());
		bw.close();
		br.close();
	}
	
	public static void training_file_target(int except) throws IOException{
		HashSet<String> features_entity_word = new HashSet<String>();
		HashSet<String> features_path = new HashSet<String>();
		HashSet<String> features_entity_word_path = new HashSet<String>();
		//HashSet<String> features_entity_ = new HashSet<String>();
		
		for (int fold = 0; fold < 10; fold++) {
			//three kinds of features! entity_word, path, entity_word-path
			if (fold == except)
				continue;
			
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_"+fold));
			String st;
			while((st = br.readLine())!=null) {
				features_entity_word.add(st.split("\t")[1].split("-")[0]);
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 4)
					continue;
				features_path.add(st.split("\t")[3]);
				features_entity_word_path.add(st.split("\t")[1].split("-")[0]+st.split("\t")[3]);
			}
			br.close();
		}
		
		//System.out.println(features_entity_word.size()+"\t"+features_path.size()
		//		+"\t"+features_entity_word_path.size()); //Not too bad! 3321, 5730, 17083
		
		LinkedList<String> features_entity_word1 = new LinkedList<String>();
		LinkedList<String> features_path1 = new LinkedList<String>();
		LinkedList<String> features_entity_word_path1 = new LinkedList<String>();
		
		Iterator it = features_entity_word.iterator();
		while(it.hasNext()) {
			features_entity_word1.add((String)it.next());
		}
		
		it = features_path.iterator();
		while(it.hasNext()) {
			features_path1.add((String)it.next());
		}
		
		it = features_entity_word_path.iterator();
		while(it.hasNext()) {
			features_entity_word_path1.add((String)it.next());
		}
		
		System.out.println(except+"\t"+
		features_entity_word1.size()+features_path1.size()+features_entity_word_path1.size());
		
		
		//features_entity_word1.indexOf(o)
		BufferedWriter bw = new BufferedWriter(new FileWriter(folder_path_svm+"training_targets_"+except));
		for (int fold = 0; fold < 10; fold++) {
			if (fold == except)
				continue;
			System.out.println(fold);
			BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_"+fold));
			String st;
			String output = "";
			//HashSet<String> training_data = new HashSet<String>();
			while((st = br.readLine())!=null) {
				//System.out.println(st+"\t"+st.split("\t").length);
				if (st.split("\t").length < 4)
					continue;
				output = st.split("\t")[0];
				//output = output+"\t1:"+ st.split("\t")[1];
				int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
				int feature_path = features_path1.indexOf(st.split("\t")[3])+features_entity_word1.size()+2;
				int feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path.size() +3;
				output = output+"\t"+String.valueOf(feature_word)+":1"
						+"\t"+String.valueOf(feature_path)+":1"
						//+ "\t"+ String.valueOf(feature_word_path)+":1\n";
						+"\n";
				//training_data.add(output);
				bw.write(output);;
			}
			//System.out.println(training_data.size());

			br.close();			
		}
		bw.close();
		
		//Now the test file
		//HashSet<String> training_data = new HashSet<String>();
		bw = new BufferedWriter(new FileWriter(folder_path_svm+"test_targets_"+except));
		BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"targets_"+except));
		String st;
		String output = "";
		int count = 0;
		while((st = br.readLine())!=null) {
			//System.out.println(st);
			//System.out.println(st+"\t"+st.split("\t").length);
			//if (st.split("\t").length < 4)
			//	continue;
			output = st.split("\t")[0];
			//output = output+"\t1:"+ st.split("\t")[1];
			int feature_word = features_entity_word1.indexOf(st.split("\t")[1].split("-")[0])+1;
			int feature_path = -1;
			if (st.split("\t").length < 4) {
				feature_path = -1;
			}
			else {
				feature_path = features_path1.indexOf(st.split("\t")[3]) + features_entity_word1.size()+2;
			}
			int feature_word_path = -1;
			if (st.split("\t").length < 4) {
				feature_word_path = -1;
			}
			else {
				feature_word_path = features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
						(st.split("\t")[3])) + features_entity_word1.size() + features_path1.size() + 3;
			}
					
			if (feature_word!=-1 && features_entity_word1.indexOf(st.split("\t")[1].split("-")[0]) != -1) {
				output = output+"\t"+String.valueOf(feature_word)+":1";
			}
			if (feature_path!=-1 && features_path1.indexOf(st.split("\t")[3]) != -1) {
				output = output+"\t"+String.valueOf(feature_path)+":1";
			}
			/*if (feature_word_path!=-1 && features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
					(st.split("\t")[3]))!=-1) {
				output = output+"\t"+ String.valueOf(feature_word_path)+":1";
			}*/
			output += "\n";
			
			//+"\t"+String.valueOf(feature_path)+":1\t"+ String.valueOf(feature_word_path)+":1\n";
			bw.write(output);
			//System.out.println((++count));
			//training_data.add(output);
		}
		//System.out.println(training_data.size());
		bw.close();
		br.close();
	}

	public static void main(String args[]) throws IOException{
		create_file_holders();
		create_file_targets();
		//training_file_holder(0);
		//training_file_holder(1);
		/*training_file_holder(2);
		training_file_holder(3);
		training_file_holder(4);
		training_file_holder(5);
		training_file_holder(6);
		training_file_holder(7);
		training_file_holder(8);*/
		//training_file_holder_bayes(9);
		//training_file_holder(9);
		//training_file_target(0);
		
		training_file_holder_prob(0);
		training_file_target_prob(0);
		
		training_file_holder_prob(1);
		training_file_target_prob(1);
		
		training_file_holder_prob(2);
		training_file_target_prob(2);
		
		training_file_holder_prob(3);
		training_file_target_prob(3);
		
		training_file_holder_prob(4);
		training_file_target_prob(4);
		
		training_file_holder_prob(5);
		training_file_target_prob(5);
		
		training_file_holder_prob(6);
		training_file_target_prob(6);
		
		training_file_holder_prob(7);
		training_file_target_prob(7);
		
		training_file_holder_prob(8);
		training_file_target_prob(8);
		
		training_file_holder_prob(9);
		training_file_target_prob(9);
	}
}

