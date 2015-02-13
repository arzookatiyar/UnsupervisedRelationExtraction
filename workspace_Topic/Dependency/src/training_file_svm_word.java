import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashSet;

class training_file_svm_word {
	
	static String folder_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Training_data/";
	static String folder_path_svm = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/svm_format_word/";
	
	static HashMap<String, Entries> gold_holders = new HashMap<String, Entries>();
	static HashMap<String, Entries> gold_targets = new HashMap<String, Entries>();
	
	
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
			HashSet<String> entries = new HashSet<String>();

			
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
							
							Iterator it = gold_holders.get(sentence).unit.iterator();
							boolean matched = false;
							while (it.hasNext()) {
								if (crf_entry.word_equals(it.next())) { //instead of soft_equals have word_equals
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
									matched = true;
								}
							}
														
							if (matched == false) {
									//negative example
								//System.out.println("matched false");
								//String new_write = "-1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
								//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								String new_write = "-1\t"+st.split("\t")[ind]+"\t"
										+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";

								if (!entries.contains(new_write)) {
									entries.add(new_write);
									bw.write(new_write);
									System.out.println("written2\t"+new_write);
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
			gold_holders = new HashMap<String, Entries>();
		}
	}
	
	public static void create_file_targets () throws IOException{
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
			HashSet<String> entries = new HashSet<String>();

			
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
							
							Iterator it = gold_targets.get(sentence).unit.iterator();
							boolean matched = false;
							while (it.hasNext()) {
								if (crf_entry.word_equals(it.next())) { //instead of soft_equals
									//the final label is 1
									//String to_write = "+1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
									//		+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
									String to_write = "+1\t"+st.split("\t")[ind]+"\t"
											+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";

									if (!entries.contains(to_write)) {
										bw.write(to_write);
										entries.add(to_write);
									}
									matched = true;
								}
							}
														
							if (matched == false) {
									//negative example
								//System.out.println("matched false");
								//String new_write = "-1\t"+st.split("\t")[1]+"\t"+st.split("\t")[ind]+"\t"
										//+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";
								String new_write = "-1\t"+st.split("\t")[ind]+"\t"
												+st.split("\t")[ind+1]+"\t"+st.split("\t")[ind+2]+"\n";

								if (!entries.contains(new_write)) {
									bw.write(new_write);
									entries.add(new_write);
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
			gold_targets = new HashMap<String, Entries>();
		}
	}
	
	
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
				//+"\t"+String.valueOf(feature_path)+":1"
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
		BufferedReader br = new BufferedReader(new FileReader(folder_path_svm+"holders_"+except));
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
			/*if (feature_path!=-1 && features_path1.indexOf(st.split("\t")[3]) != -1) {
				output = output+"\t"+String.valueOf(feature_path)+":1";
			}
			if (feature_word_path!=-1 && features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
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
						//+"\t"+String.valueOf(feature_path)+":1"
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
			/*if (feature_path!=-1 && features_path1.indexOf(st.split("\t")[3]) != -1) {
				output = output+"\t"+String.valueOf(feature_path)+":1";
			}
			if (feature_word_path!=-1 && features_entity_word_path1.indexOf(st.split("\t")[1].split("-")[0]+
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
		training_file_holder(1);
		/*training_file_holder(2);
		training_file_holder(3);
		training_file_holder(4);
		training_file_holder(5);
		training_file_holder(6);
		training_file_holder(7);
		training_file_holder(8);*/
		//training_file_holder_bayes(9);
		//training_file_holder(9);
		//training_file_target(1);
	}
}

