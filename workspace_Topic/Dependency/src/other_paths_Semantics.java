import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

class Pair_paths_Semantics {
	String holder_path;
	String target_path;
	
	Pair_paths_Semantics(String holder_path, String target_path) {
		this.holder_path = holder_path;
		this.target_path = target_path;
	}
}

class other_paths_Semantics {
	
	//static String dse_holder_file = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
	//		+ "/Run_dep_paths_Semantics/dse_holders.txt";
	static String dse_holder_file = "dse_holders.txt";
	static String ese_holder_file;
	static String dse_target_file = "dse_targets.txt";
	//static String dse_target_file = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
	//		+ "/Run_dep_paths_Semantics/dse_targets.txt";
	
	
	public static void analysis(HashMap<String, Pair_paths> final_100, int option) throws IOException {
		
		HashMap<String, Integer> all_other_freq = new HashMap<String, Integer>();
		HashMap<String, Integer> relevant_freq = new HashMap<String, Integer>();
		
		Set<String> all_other_paths = new HashSet<String>();
		Set<String> relevant_paths = new HashSet<String>();
		
		for (String key : final_100.keySet()) {
			//Let us now find path!
			String sentence = key;
			//String []words = sentence.split("\t");
			find_paths p = new find_paths();
			System.out.println(p.getDependencyStringFromSentence(sentence));
			
			/*
			 * Adding both of them to the same set!
			 */
			relevant_paths.add(final_100.get(key).holder_path);
			relevant_paths.add(final_100.get(key).target_path);
			
			if (relevant_freq.keySet().contains(final_100.get(key).holder_path)) {
				relevant_freq.put(final_100.get(key).holder_path, relevant_freq.get(final_100.get(key).holder_path)+1);
			}
			else {
				relevant_freq.put(final_100.get(key).holder_path, 1);
			}
			
			if (relevant_freq.keySet().contains(final_100.get(key).target_path)) {
				relevant_freq.put(final_100.get(key).target_path, relevant_freq.get(final_100.get(key).target_path)+1);
			}
			else {
				relevant_freq.put(final_100.get(key).target_path, 1);
			}
			

			/*LinkedList<String> words = new LinkedList<String>();
			PTBTokenizer ptbt = new PTBTokenizer(new StringReader(sentence),
					new CoreLabelTokenFactory(), "");
			int count = 1;
			for (CoreLabel label; ptbt.hasNext(); ) {
				label = (CoreLabel)ptbt.next();
				//System.out.println(label+"-----");
				words.add(label+"-"+count);
				System.out.println(label+"-"+count);
				++count;
			}*/
			
			String[]words = sentence.split(" ");
			for (int i=0; i< words.length; i++) {
				//System.out.println(words[i]+"-"+(i+1));
				//for (int j=i+1; j<words.length; j++) {
				for (int j=0; j<words.length; j++) {
					String new_path = p.getTheRelationBetween(words[i]+"-"+(i+1), words[j]+"-"+(j+1));
					
					if (option == 1) {
						//onlyholderadded
						new_path = words[i]+new_path;
					}
					
					if (all_other_freq.keySet().contains(new_path)) {
						all_other_freq.put(new_path, all_other_freq.get(new_path)+1);
					}
					else {
						all_other_freq.put(new_path, 1);
					}

					all_other_paths.add(new_path);
					
					//System.out.println(p.getTheRelationBetween(words[i]+"-"+(i+1), words[j]+"-"+(j+1)));
				}
			}	
		}
		
		/*Check the common paths in the all_other_paths_list. The next option can be to 
		 * check all other paths for just the start and end!*/
		int count = 0;
		int not_present = 0;
		Iterator it = all_other_paths.iterator();
		while(it.hasNext()) {
			String path = (String)it.next();
			if (relevant_paths.contains(path)) {
				System.out.println(path +"\t"+relevant_freq.get(path)+"\t"+all_other_freq.get(path));
				++count;
			}
			else {
				++not_present;
			}
		}
		
		System.out.println(count+"\t"+relevant_paths.size()+"\t"+all_other_paths.size());
	}
	
	public static void analysis_expression(HashMap<String, common_Fields> final_100, int option) throws IOException{
		HashMap<String, Integer> all_other_freq = new HashMap<String, Integer>();
		HashMap<String, Integer> relevant_freq = new HashMap<String, Integer>();
		
		Set<String> all_other_paths = new HashSet<String>();
		Set<String> relevant_paths = new HashSet<String>();
		
		for (String key : final_100.keySet()) {
			//Let us now find path!
			String sentence = key;
			//String []words = sentence.split("\t");
			find_paths p = new find_paths();
			System.out.println(p.getDependencyStringFromSentence(sentence));
			
			/*
			 * Adding both of them to the same set!
			 */
			relevant_paths.add(final_100.get(key).holder_path);
			relevant_paths.add(final_100.get(key).target_path);
			
			if (relevant_freq.keySet().contains(final_100.get(key).holder_path)) {
				relevant_freq.put(final_100.get(key).holder_path, relevant_freq.get(final_100.get(key).holder_path)+1);
			}
			else {
				relevant_freq.put(final_100.get(key).holder_path, 1);
			}
			
			if (relevant_freq.keySet().contains(final_100.get(key).target_path)) {
				relevant_freq.put(final_100.get(key).target_path, relevant_freq.get(final_100.get(key).target_path)+1);
			}
			else {
				relevant_freq.put(final_100.get(key).target_path, 1);
			}
			

			/*LinkedList<String> words = new LinkedList<String>();
			PTBTokenizer ptbt = new PTBTokenizer(new StringReader(sentence),
					new CoreLabelTokenFactory(), "");
			int count = 1;
			for (CoreLabel label; ptbt.hasNext(); ) {
				label = (CoreLabel)ptbt.next();
				//System.out.println(label+"-----");
				words.add(label+"-"+count);
				System.out.println(label+"-"+count);
				++count;
			}*/
			
			String[]words = sentence.split(" ");
			
			int begin = sentence.indexOf(final_100.get(key).expression);
			int end  = sentence.indexOf(final_100.get(key).expression) + final_100.get(key).expression.length();
			
			LinkedList<String> expression_list = Mpqa_dependencypaths.create_query_string(begin, end, sentence);
			
			for (int i=0; i< words.length; i++) {
				//System.out.println(words[i]+"-"+(i+1));
				if (!final_100.get(key).holder_word.equals(words[i]) && !(final_100.get(key).target_word.equals(words[i]))) {
				
					//LinkedList<String> temp = new LinkedList<String>();
					//temp.add(words[i]+"-"+(i+1));
					//LinkedList<String> shortest_paths = Mpqa_dependencypaths.shortestPath
					//		(expression_list, temp, sentence);
					//distance between the expression and the holder
					//distance between the expression and the target
					String shortest_path = p.getTheRelationBetween( expression_list.get(0), words[i]+"-"+(i+1));
					int index = 0;
					for (int k=1; k<expression_list.size(); k++) {
						String path = p.getTheRelationBetween(expression_list.get(k), words[i]+"-"+(i+1));
						if (path.split("-").length < shortest_path.split("-").length) {
							shortest_path = path;
							index = k;
						}
					}
					
					String new_path = shortest_path;
					
					if (option == 1 || option == 3) {
						//onlyholder and target added
						new_path = words[i]+new_path;
					}
					
					if (option == 2 || option == 3) {
						new_path = new_path + "-"+words[index];
					}
					
					//String new_path = p.getTheRelationBetween(words[i]+"-"+(i+1), expression_list.get(j));
					
					if (all_other_freq.keySet().contains(new_path)) {
						all_other_freq.put(new_path, all_other_freq.get(new_path)+1);
					}
					else {
						all_other_freq.put(new_path, 1);
					}

					all_other_paths.add(new_path);
					
					//System.out.println(p.getTheRelationBetween(words[i]+"-"+(i+1), words[j]+"-"+(j+1)));
				}
			}	
		}
		
		/*Check the common paths in the all_other_paths_list. The next option can be to 
		 * check all other paths for just the start and end!*/
		int count = 0;
		int not_present = 0;
		Iterator it = all_other_paths.iterator();
		while(it.hasNext()) {
			String path = (String)it.next();
			if (relevant_paths.contains(path)) {
				System.out.println(path +"\t"+relevant_freq.get(path)+"\t"+all_other_freq.get(path));
				++count;
			}
			else {
				++not_present;
			}
		}
		
		System.out.println(count+"\t"+relevant_paths.size()+"\t"+all_other_paths.size());

	}

	
	/*public static void analysis_expression(HashMap<String, common_Fields> final_100) throws IOException{
		HashMap<String, Integer> all_other_freq = new HashMap<String, Integer>();
		HashMap<String, Integer> relevant_freq = new HashMap<String, Integer>();
		
		Set<String> all_other_paths = new HashSet<String>();
		Set<String> relevant_paths = new HashSet<String>();
		
		for (String key : final_100.keySet()) {
			//Let us now find path!
			String sentence = key;
			//String []words = sentence.split("\t");
			find_paths p = new find_paths();
			System.out.println(p.getDependencyStringFromSentence(sentence));
			
			
			 //Adding both of them to the same set!
			 
			relevant_paths.add(final_100.get(key).holder_path);
			relevant_paths.add(final_100.get(key).target_path);
			
			if (relevant_freq.keySet().contains(final_100.get(key).holder_path)) {
				relevant_freq.put(final_100.get(key).holder_path, relevant_freq.get(final_100.get(key).holder_path)+1);
			}
			else {
				relevant_freq.put(final_100.get(key).holder_path, 1);
			}
			
			if (relevant_freq.keySet().contains(final_100.get(key).target_path)) {
				relevant_freq.put(final_100.get(key).target_path, relevant_freq.get(final_100.get(key).target_path)+1);
			}
			else {
				relevant_freq.put(final_100.get(key).target_path, 1);
			}
			

			/*LinkedList<String> words = new LinkedList<String>();
			PTBTokenizer ptbt = new PTBTokenizer(new StringReader(sentence),
					new CoreLabelTokenFactory(), "");
			int count = 1;
			for (CoreLabel label; ptbt.hasNext(); ) {
				label = (CoreLabel)ptbt.next();
				//System.out.println(label+"-----");
				words.add(label+"-"+count);
				System.out.println(label+"-"+count);
				++count;
			//}  //** end of the comment here!
			
			String[]words = sentence.split(" ");
			
			int begin = sentence.indexOf(final_100.get(key).expression);
			int end  = sentence.indexOf(final_100.get(key).expression) + final_100.get(key).expression.length();
			
			LinkedList<String> expression_list = Mpqa_dependencypaths.create_query_string(begin, end, sentence);
			
			for (int i=0; i< words.length; i++) {
				//System.out.println(words[i]+"-"+(i+1));
				if (!final_100.get(key).holder_word.equals(words[i]) && !(final_100.get(key).target_word.equals(words[i]))) {
				
					LinkedList<String> temp = new LinkedList<String>();
					temp.add(words[i]+"-"+(i+1));
					LinkedList<String> shortest_paths = Mpqa_dependencypaths.shortestPath
							(expression_list, temp, sentence);
					
					String new_path = shortest_paths.get(2);
					
					//String new_path = p.getTheRelationBetween(words[i]+"-"+(i+1), expression_list.get(j));
					
					if (all_other_freq.keySet().contains(new_path)) {
						all_other_freq.put(new_path, all_other_freq.get(new_path)+1);
					}
					else {
						all_other_freq.put(new_path, 1);
					}

					all_other_paths.add(new_path);
					
					//System.out.println(p.getTheRelationBetween(words[i]+"-"+(i+1), words[j]+"-"+(j+1)));
				}
			}	
		}
		
		//Check the common paths in the all_other_paths_list. The next option can be to 
		 //check all other paths for just the start and end!
		int count = 0;
		int not_present = 0;
		Iterator it = all_other_paths.iterator();
		while(it.hasNext()) {
			String path = (String)it.next();
			if (relevant_paths.contains(path)) {
				System.out.println(path +"\t"+relevant_freq.get(path)+"\t"+all_other_freq.get(path));
				++count;
			}
			else {
				++not_present;
			}
		}
		
		System.out.println(count+"\t"+relevant_paths.size()+"\t"+all_other_paths.size());

	}*/
	
	
	/**
	 * Analysis need to be modified!! Does not take into account the semantics
	 * @param final_100
	 * @throws IOException
	 */
	
	public static void analysis_start_end(HashMap<String, Pair_paths> final_100) throws IOException {
		
		HashMap<Pair, Integer> all_other_freq = new HashMap<Pair, Integer>();
		HashMap<Pair, Integer> relevant_freq = new HashMap<Pair, Integer>();
		
		Set<Pair> all_other_paths = new HashSet<Pair>();
		Set<Pair> relevant_paths = new HashSet<Pair>();
		
		for (String key : final_100.keySet()) {
			//Let us now find path!
			String sentence = key;
			//String []words = sentence.split("\t");
			find_paths p = new find_paths();
			System.out.println(p.getDependencyStringFromSentence(sentence));
			
			/*
			 * Adding both of them to the same set!
			 */
			Pair p1 = new Pair(final_100.get(key).holder_path.split("-")[1], final_100.get(key).holder_path.split("-")[final_100.get(key).holder_path.split("-").length-1]);
			Pair p2 = new Pair(final_100.get(key).target_path.split("-")[1], final_100.get(key).target_path.split("-")[final_100.get(key).target_path.split("-").length-1]);


			relevant_paths.add(p1);
			relevant_paths.add(p2);
			
			
			if (relevant_freq.keySet().contains(p1)) {
				relevant_freq.put(p1, relevant_freq.get(p1)+1);
			}
			else {
				relevant_freq.put(p1, 1);
			}
			
			
			if (relevant_freq.keySet().contains(p2)) {
				relevant_freq.put(p2, relevant_freq.get(p2)+1);
			}
			else {
				relevant_freq.put(p2, 1);
			}
			

			/*LinkedList<String> words = new LinkedList<String>();
			PTBTokenizer ptbt = new PTBTokenizer(new StringReader(sentence),
					new CoreLabelTokenFactory(), "");
			int count = 1;
			for (CoreLabel label; ptbt.hasNext(); ) {
				label = (CoreLabel)ptbt.next();
				//System.out.println(label+"-----");
				words.add(label+"-"+count);
				System.out.println(label+"-"+count);
				++count;
			}*/
			
			String[]words = sentence.split(" ");
			for (int i=0; i< words.length; i++) {
				//System.out.println(words[i]+"-"+(i+1));
				for (int j=i+1; j<words.length; j++) {
					String new_path = p.getTheRelationBetween(words[i]+"-"+(i+1), words[j]+"-"+(j+1));
					
					if (new_path.split("-").length > 1) {
					Pair p3 = new Pair(new_path.split("-")[1], new_path.split("-")[new_path.split("-").length-1]);
					
					if (all_other_freq.keySet().contains(p3)) {
						all_other_freq.put(p3, all_other_freq.get(p3)+1);
					}
					else {
						all_other_freq.put(p3, 1);
					}

					all_other_paths.add(p3);
					}
					//System.out.println(p.getTheRelationBetween(words[i]+"-"+(i+1), words[j]+"-"+(j+1)));
				}
			}	
		}
		
		/*Check the common paths in the all_other_paths_list. The next option can be to 
		 * check all other paths for just the start and end!*/
		int count = 0;
		int not_present = 0;
		Iterator it = all_other_paths.iterator();
		while(it.hasNext()) {
			Pair path = (Pair)it.next();
			if (relevant_paths.contains(path)) {
				System.out.println(path.start+"\t"+path.end +"\t"+relevant_freq.get(path)+"\t"+all_other_freq.get(path));
				++count;
			}
			else {
				++not_present;
			}
		}
		
		System.out.println(count+"\t"+relevant_paths.size()+"\t"+all_other_paths.size());
	}
	
	//this includes paths between all the words in the analysis!
	
	public static void all_other_paths(int option) throws IOException{
		HashMap<String, String> holder_map = new HashMap<String, String>();
		HashMap<String, Pair_paths> common_map = new HashMap<String, Pair_paths>();
		
		/*
		 * Note that the common_map does not contain the corresponding holder and target for a 
		 * dse_expression. To achieve that we need to compare the expression to be same from 
		 * the dse_holder_file and dse_target_file!
		 */
		
		BufferedReader br_h = new BufferedReader (new FileReader(dse_holder_file)); 
		String st;	
		
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String sentence = st.split("\t")[0];
			String path = next_line.split("\t")[2];
			if (option == 1) {
				//onlyholderadded
				path = next_line.split("-")[0]+path;
			}

			holder_map.put(sentence, path);
		}
		
		BufferedReader br_t = new BufferedReader(new FileReader(dse_target_file));
		
		while((st = br_t.readLine())!=null) {
			String next_line = br_t.readLine();
			String sentence = st.split("\t")[0];
			if (holder_map.containsKey(sentence)) {
				//common sentences are added!
				if (option == 1) { 
					common_map.put(sentence, new Pair_paths(holder_map.get(sentence), 
							next_line.split("-")[0]+next_line.split("\t")[2]));
				}
				else {
					common_map.put(sentence, new Pair_paths(holder_map.get(sentence), 
							next_line.split("\t")[2]));
				}
			}
		}
		
		
		//Now randomly select 100 keys from here!!
		LinkedList<String> keys = new LinkedList<String>();
		Set<String> keySet = common_map.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			keys.add(it.next());
		}
		
		LinkedList<Integer> generated_num = new LinkedList<Integer>();
		
		Random rand = new Random();
		
		HashMap<String, Pair_paths> final_100 = new HashMap<String, Pair_paths>();
		
		for (int i=0 ; i<100; i++) {
			int randomNum = -1;
			while (generated_num.contains(randomNum = rand.nextInt((keys.size())))) {
				//do nothing
			}
			String key = keys.get(randomNum);
			final_100.put(key, new Pair_paths(common_map.get(key).holder_path, common_map.get(key).target_path));			
		}
		
		System.out.println("Final_100");
		analysis(final_100, option);
		//analysis_start_end(final_100);
		
		br_h.close();
		br_t.close();
		
	}
	
	/*
	 * Now instead of finding paths between all the words, we find the path between all
	 * the words and the expression words
	 * option = 1 : only holder and target word added
	 * option = 2 : only expression word added
	 * option = 3 : both the words added
	 */
	
	public static void all_paths_wrt_expression(int option) throws IOException{
		
		HashMap<String, Fields> holder_map = new HashMap<String, Fields>();
		
		HashMap<String, common_Fields> common_map = new HashMap<String, common_Fields>();
		
		BufferedReader br_h = new BufferedReader (new FileReader(dse_holder_file)); 
		String st;	
		
		while((st = br_h.readLine())!=null) {
			String next_line = br_h.readLine();
			String sentence = st.split("\t")[0];
			String expression = st.split("\t")[2];
			String path = next_line.split("\t")[2];
			String holder_word = next_line.split("\t")[0];
			
			if (option == 1 || option == 3) {
				path = next_line.split("\t")[0].split("-")[0]+path;
			}
			
			if (option == 2 || option == 3) {
				path = path + "-"+next_line.split("\t")[1].split("-")[0];
			}
			
			holder_map.put(sentence, new Fields(expression, path, holder_word));
		}
		
		BufferedReader br_t = new BufferedReader(new FileReader(dse_target_file));
		
		while((st = br_t.readLine())!=null) {
			String next_line = br_t.readLine();
			String sentence = st.split("\t")[0];
			String expression = st.split("\t")[2];
			String path = next_line.split("\t")[2];
			String target_word = next_line.split("\t")[0];
			
			if (option == 1 || option == 3) {
				path = next_line.split("\t")[0].split("-")[0]+path;
			}

			if (option == 2 || option == 3) {
				path = path + "-"+next_line.split("\t")[1].split("-")[0];
			}

			
			if (holder_map.containsKey(sentence)) {
				if (holder_map.get(sentence).expression.equals(expression)) {
					common_map.put(sentence, new common_Fields(expression, holder_map.get(sentence).path, path, 
							holder_map.get(sentence).holder_word, target_word));
				}
			}
		}
		
		//Now randomly select 100 keys from here!!
		LinkedList<String> keys = new LinkedList<String>();
		Set<String> keySet = common_map.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			keys.add(it.next());
		}
		
		LinkedList<Integer> generated_num = new LinkedList<Integer>();
		
		Random rand = new Random();
		
		HashMap<String, common_Fields> final_100 = new HashMap<String, common_Fields>();
		
		for (int i=0 ; i<500; i++) {
			int randomNum = -1;
			while (generated_num.contains(randomNum = rand.nextInt((keys.size())))) {
				//do nothing
			}
			String key = keys.get(randomNum);
			
			final_100.put(key, new common_Fields(
					common_map.get(key).expression, common_map.get(key).holder_path, 
					common_map.get(key).target_path, common_map.get(key).holder_word,
					common_map.get(key).target_word));			
		}
		
		System.out.println("Final_100");
		//analysis(final_100);
		analysis_expression(final_100, option);
		
		br_h.close();
		br_t.close();
	}

	
	public static void main(String args[]) throws IOException {
		/*
		 *option = 1 : only holder and target word added
		 * option = 2 : only expression word added
		 * option = 3 : both the words added
		 */
		//all_other_paths(1);
		all_paths_wrt_expression(1);
	}
}

class Fields_Semantics {
	String expression;
	String path;
	String holder_word;
	
	public Fields_Semantics(String expression, String path, String holder_word) {
		this.expression = expression;
		this.path = path;
		this.holder_word = holder_word;
	}
}

class common_Fields_Semantics {
	String expression;
	String holder_path;
	String target_path;
	String holder_word;
	String target_word;
	
	public common_Fields_Semantics(String expression, String holder_path, String target_path, 
			String holder_word, String target_word) {
		this.expression = expression;
		this.holder_path = holder_path;
		this.target_path = target_path;
		this.holder_word = holder_word;
		this.target_word = target_word;
	}
}