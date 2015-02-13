import java.io.*;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

class create_10fold_train_and_test {
	
	static String file_path = "/Users/arzookatiyar/Desktop/TopicModeling/ILPExtractor/data/";
	static HashMap<String_modified, String> sentence_file = new HashMap<String_modified, String>();
	
	
	public static void generate_train_test()throws IOException {
		
		HashMap<String, LinkedList<String>> file_holders = new HashMap<String, LinkedList<String>>();
		
		BufferedReader br_0 = new BufferedReader(new FileReader("dse_holders_word_final.txt"));
		
		String st_0;
		while((st_0 = br_0.readLine())!=null) {
			String[] line = st_0.split("\t");
			LinkedList<String> entries = new LinkedList<String>();
			for (int k=1; k<line.length; k++) {
				entries.add(line[k]);
			}
			
			file_holders.put(line[0], entries);
		}
		
		br_0.close();
		
		HashMap<String, LinkedList<String>> file_targets = new HashMap<String, LinkedList<String>>();
		
		br_0 = new BufferedReader(new FileReader("dse_targets_word_final.txt"));
		
		while((st_0 = br_0.readLine())!=null) {
			String[] line = st_0.split("\t");
			LinkedList<String> entries = new LinkedList<String>();
			for (int k=1; k<line.length; k++) {
				entries.add(line[k]);
			}
			
			file_targets.put(line[0], entries);
		}
		
		br_0.close();
		for (int i=0; i<10; i++) {
			System.out.println("Fold\t"+i);
			int count = 0;
			String train_file = "10-fold_evaluation/train_"+i;
			String test_file = "10-fold_evaluation/test_"+i;
			
			String train_file_write_h = "10-fold_data/train_holders_"+i;
			String test_file_write_h = "10-fold_data/test_holders_"+i;
			
			String train_file_write_t = "10-fold_data/train_targets_"+i;
			String test_file_write_t = "10-fold_data/test_targets_"+i;


			BufferedReader br_1 = new BufferedReader(new FileReader(train_file));
			String st_1;
			
			BufferedWriter bw_1 = new BufferedWriter(new FileWriter(train_file_write_h));
			
			while((st_1 = br_1.readLine())!=null) {
				LinkedList<String> line = file_holders.get(st_1);
				bw_1.write(st_1);
				if (line == null) {
					//System.out.println(st_1);
					continue;
				}
				else {
					//++count;

					//System.out.println("Found\t"+st_1);
				}
				for (int j=0; j<line.size(); j++) {
					bw_1.write("\t"+line.get(j));
				}
				bw_1.write("\n");
			}
			
			System.out.println("Counts\t"+count);
			
			bw_1.close();
			br_1.close();
			
			br_1 = new BufferedReader(new FileReader(train_file));

			
			bw_1 = new BufferedWriter(new FileWriter(train_file_write_t));
			
			while((st_1 = br_1.readLine())!=null) {
				LinkedList<String> line = file_targets.get(st_1);
				if (line == null) {
					//System.out.println(st_1);
					continue;
				}
				else {
					//++count;

					//System.out.println("Found\t"+st_1);
				}

				bw_1.write(st_1);
				for (int j=0; j<line.size(); j++) {
					bw_1.write("\t"+line.get(j));
				}
				bw_1.write("\n");
			}
			
			bw_1.close();
			
			br_1.close();
			
			br_1 = new BufferedReader(new FileReader(test_file));

			
			bw_1 = new BufferedWriter(new FileWriter(test_file_write_h));
			
			while((st_1 = br_1.readLine())!=null) {
				LinkedList<String> line = file_holders.get(st_1);
				if (line == null) {
					//System.out.println(st_1);
					continue;
				}
				else {
					++count;

					System.out.println("Found\t"+st_1);
				}

				bw_1.write(st_1);
				for (int j=0; j<line.size(); j++) {
					bw_1.write("\t"+line.get(j));
				}
				bw_1.write("\n");
			}
			
			bw_1.close();
			
			br_1.close();
			
			br_1 = new BufferedReader(new FileReader(test_file));


			bw_1 = new BufferedWriter(new FileWriter(test_file_write_t));
			
			while((st_1 = br_1.readLine())!=null) {
				LinkedList<String> line = file_targets.get(st_1);
				bw_1.write(st_1);
				if (line == null) {
					//System.out.println(st_1);
					continue;
				}
				else {
					//++count;

					//System.out.println("Found\t"+st_1);
				}

				for (int j=0; j<line.size(); j++) {
					bw_1.write("\t"+line.get(j));
				}
				bw_1.write("\n");
			}
			
			bw_1.close();

		}
	}
	
	
	public static void split_data() throws IOException{
		
		BufferedReader br_0 = new BufferedReader(new FileReader("sentence_id_wfilename"));
		String st_0;
		
		while((st_0 = br_0.readLine())!=null) {
			String filename = st_0.split("\t")[2];
			String sentence = st_0.split("\t")[1];
			String new_sentence = "";
			PTBTokenizer ptbt = new PTBTokenizer(new StringReader(sentence),
					new CoreLabelTokenFactory(), "");
			//LinkedList<String> sentence_tokens = new LinkedList<String>();
			for (CoreLabel label; ptbt.hasNext(); ) {
				label = (CoreLabel)ptbt.next();
				//System.out.println(label+"-----");
				new_sentence += label.toString()+" ";
				//sentence_tokens.add(label.toString());
			}
			sentence_file.put(new String_modified(new_sentence), filename);
		}
		
		System.out.println(sentence_file.keySet().toString());
		
		for (int i=0; i<10; i++) {
			System.out.println("Fold\t"+i);
			String train_filename = file_path + "agent_dse_target_train_crf_"+i;
			
			HashSet<String> training_set = new HashSet<String>();
			
			BufferedReader br = new BufferedReader(new FileReader(train_filename));
			String st;
			String new_sentence = "";
			while((st = br.readLine())!=null) {
				//System.out.println(st);
				do {
					//System.out.println("st\t"+st);
					if (st != "" && st!=null && (st.split("\t").length == 3)) {
						new_sentence += st.split("\t")[0]+" ";
					}
					else {
						new_sentence += st+" ";
						//if (st == null) System.exit(0);
					}
				}while (!((st = br.readLine()).split("\t")[0].equals(".")));
				st = br.readLine(); //assuming that this reads the next space
				//System.exit(0);
				new_sentence += ". ";
				//System.out.println("ends\t"+new_sentence);
				String filename_0 = sentence_file.get(new String_modified(new_sentence));
				if (filename_0 == null) {
					//System.out.println(filename_0);
					//System.out.println(new_sentence);
					//none is matching
					/*String_modified match = null;
					int score = 100000;

					for (String_modified key : sentence_file.keySet()) {
						if (String_modified.minDistance(key.string, new_sentence) < score) {
							match = key;
							score = String_modified.minDistance(key.string, new_sentence);
						}
						if ((key.string.contains(new_sentence)) || 
								(new_sentence.contains(key.string)&& key.string.split(" ").length > 5)) {
							match = key;
							score = 0;
							break;
						}
						//System.out.println(match+"\t"+score);
					}
					System.out.println("Final score\t"+score);
					System.out.println("Final match\t"+match.string);

					if ((double)score/ match.string.length() < 0.1) {
						filename_0 = sentence_file.get(match);
						System.out.println("Changed\t"+filename_0);
					}*/
					
				}
				else {
					//System.out.println("test\t"+filename_0);
					//System.out.println("test\t"+new_sentence);
				}
				
				new_sentence = "";
				if (filename_0!=null)
					training_set.add(filename_0);
			}			
			
			String test_filename = file_path + "agent_dse_target_test_crf_"+i;
			
			HashSet<String> test_set = new HashSet<String>();
			
			br = new BufferedReader(new FileReader(test_filename));
			new_sentence = "";
			while((st = br.readLine())!=null) {
				do {
					//System.out.println("st\t"+st);
					if (st != "" && st!=null && (st.split("\t").length == 3)) {
						new_sentence += st.split("\t")[0]+" ";
					}
					else {
						new_sentence += st+" ";
						//if (st == null) System.exit(0);
					}
				}while (!((st = br.readLine()).split("\t")[0].equals(".")));
				st = br.readLine(); //assuming that this reads the next space
				//System.exit(0);
				new_sentence += ". ";
				String filename_0 = sentence_file.get(new String_modified(new_sentence));
				
				if (filename_0 == null) {
					//System.out.println("test\t"+filename_0);
					//System.out.println("test\t"+new_sentence);
					
					//none is matching
					/*String_modified match = null;
					int score = 100000;

					for (String_modified key : sentence_file.keySet()) {
						if (String_modified.minDistance(key.string, new_sentence) < score) {
							match = key;
							score = String_modified.minDistance(key.string, new_sentence);
						}
						if (key.string.contains(new_sentence) || 
								(new_sentence.contains(key.string)&& key.string.split(" ").length > 5)) {
							match = key;
							score = 0;
							break;
						}
						//System.out.println(match+"\t"+score);
					}
					System.out.println("Final score\t"+score);
					System.out.println("Final match\t"+match.string);

					if ((double)score/ match.string.length() < 0.1) {
						filename_0 = sentence_file.get(match);
						System.out.println("Changed\t"+filename_0);
					}*/
					
				}
				else {
					//System.out.println("test\t"+filename_0);
					//System.out.println("test\t"+new_sentence);
				}

				new_sentence = "";
				if (filename_0!=null)
					test_set.add(filename_0);
			}
			
			
			//Check that there are no duplicates in the train set and the test set
			Iterator it_1 = training_set.iterator();
			HashSet<String> remove = new HashSet<String>();
			while(it_1.hasNext()) {
				String train_file = (String)it_1.next();
				if (test_set.contains(train_file)) {
					remove.add(train_file);
				}
			}
			System.out.println("Removed\t"+remove.size());
			training_set.removeAll(remove);
			
			
			String write_train_file = "10-fold_evaluation/train_"+i;
			String write_test_file = "10-fold_evaluation/test_"+i;
			
			BufferedWriter bw_train = new BufferedWriter(new FileWriter(write_train_file));
			BufferedWriter bw_test = new BufferedWriter(new FileWriter(write_test_file));
			
			Iterator<String> it = training_set.iterator();
			while(it.hasNext()) {
				bw_train.write(it.next()+"\n");
			}
			
			it = test_set.iterator();
			while(it.hasNext()) {
				bw_test.write(it.next()+"\n");
			}
			
			bw_train.close();
			bw_test.close();
			
			System.out.println(training_set.size());
			System.out.println(test_set.size());
			System.out.println(training_set.size() + test_set.size());
			
			//System.exit(0);
			
		}

	}
	
	public static void main(String args[]) throws IOException{
		split_data();
		generate_train_test();
	}
}


class String_modified {
	String string;
	
	String_modified(String string) {
		this.string = string;
	}
	
	public static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
	
	@Override
	
	public boolean equals(Object o) {
		//System.out.println(minDistance(this.string, ((String_modified)o).string)+"\t"+(this.string.length()));
		if ((double)minDistance(this.string, ((String_modified)o).string)/(this.string.length()) < 0.1 ) {
			return true;
		}
		else 
			return false;
	}
	
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + this.string.hashCode();
		return hash;
	}
	
	
}