import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

class evaluation {
	
	static String folder_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Training_data/";
	static String output_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Final_output/";

	
	public static Statistics exact_holders(String file, String gold_file) throws IOException{
		String filename = output_path+file;
		String gold_filename = folder_path+gold_file;
		
		BufferedReader br_1 = new BufferedReader(new FileReader(filename));
		BufferedReader br_2 = new BufferedReader(new FileReader(gold_filename));
		
		String st1, st2;
		
		int num_gold_hold = 0;
		int num_hold = 0;
		int match = 0;
		int match1 = 0;
		int num_gold1 = 0;
		int TP = 0;
		int FP = 0;
		int FN = 0;
		
		while(((st1 = br_1.readLine())!=null) && ((st2 = br_2.readLine())!=null)) {
			if (st1.equals(st2)) { //should always hold true
				String[] next_line1 = br_1.readLine().split("\t");
				String[] next_line2 = br_2.readLine().split("\t");
				
				//System.out.println("New sentence");
				HashSet<String> holders = new HashSet<String>();
				for (int i=1; i<next_line1.length; i= i+5) {
					holders.add(next_line1[i+3]);
					//System.out.println("entry\t"+next_line1[i+3]);
				}
				
				HashSet<String> gold_holders = new HashSet<String>();
				for (int j=1; j<next_line2.length; j=j+5) {
					gold_holders.add(next_line2[j+3]);
				}

				//Here compare the two hashsets!
				
				num_gold_hold += gold_holders.size();
				num_hold += holders.size();

				
				Iterator it = gold_holders.iterator();
				while(it.hasNext()) {
					num_gold1++;
					String gold_holder = (String)it.next();
					if (holders.contains(gold_holder)) {
						match++;
						match1++;
						TP++;
						//System.out.println("Contains\t"+gold_holder);
					}
					else {
						//System.out.println("Does not contain\t"+gold_holder);
					}
				}
				
				/*if (gold_holders.size() == 0) {
					num_gold_hold += 1;
					if (holders.size() == 0) {
						num_hold += 1;
						match++;
					}
				}*/
			}
			else {
				System.out.println("Condition whiich should be always true is false");
			}
		}

		System.out.println("TP:\t"+TP+"\t(TP+FP):\t"+num_hold+"\t"+"total_relevant\t"+num_gold_hold);
		double Precision = ((double)TP/num_hold);
		double Recall = ((double)TP/num_gold_hold);
		
		System.out.println("Precision:\t"+Precision+"\tRecall:\t"+Recall);

		
		double Fscore = 2*((Precision*Recall)/(Precision+Recall));
		System.out.println(Fscore);
		
		br_1.close();
		br_2.close();
		
		return new Statistics(Precision, Recall, Fscore);

	}
	
	public static boolean overlaps(HashSet<String> holders, String gold_holder) {
		Iterator it = holders.iterator();
		while(it.hasNext()) {
			String holder = (String)it.next();
			//System.out.println(holder+"\t"+gold_holder);
			String []holder_words = holder.split(" ");
			String []gold_words = gold_holder.split(" ");
			boolean match = false;
			for (int i=0; i<holder_words.length; i++) {
				for (int j=0; j<gold_words.length; j++) {
					if (holder_words[i].equals(gold_words[j])) {
						//System.out.println("True");
						return true;
					}
				}
			}
		}
		//System.out.println("False");
		return false;
	}
	
	public static Statistics soft_holders(String file, String gold_file) throws IOException{
		String filename = output_path+file;
		String gold_filename = folder_path+gold_file;
		
		BufferedReader br_1 = new BufferedReader(new FileReader(filename));
		BufferedReader br_2 = new BufferedReader(new FileReader(gold_filename));
		
		String st1, st2;
		
		int num_gold_hold = 0;
		int num_hold = 0;
		int match = 0;
		int match1 = 0;
		int num_gold1 = 0;
		int TP = 0;
		int FP = 0;
		int FN = 0;
		
		while(((st1 = br_1.readLine())!=null) && ((st2 = br_2.readLine())!=null)) {
			if (st1.equals(st2)) { //should always hold true
				String[] next_line1 = br_1.readLine().split("\t");
				String[] next_line2 = br_2.readLine().split("\t");
				
				HashSet<String> holders = new HashSet<String>();
				for (int i=1; i<next_line1.length; i= i+5) {
					holders.add(next_line1[i+3]);
				}
				
				HashSet<String> gold_holders = new HashSet<String>();
				for (int j=1; j<next_line2.length; j=j+5) {
					gold_holders.add(next_line2[j+3]);
				}

				//Here compare the two hashsets!
				
				num_gold_hold += gold_holders.size();
				num_hold += holders.size();

				
				Iterator it = gold_holders.iterator();
				while(it.hasNext()) {
					num_gold1++;
					String gold_holder = (String)it.next();
					if (overlaps(holders, gold_holder)) {
						match++;
						match1++;
						TP++;
					}
				}
				
				/*if (gold_holders.size() == 0) {
					num_gold_hold += 1;
					if (holders.size() == 0) {
						num_hold += 1;
						match++;
					}
				}*/
			}
			else {
				System.out.println("Condition whiich should be always true is false");
			}
		}

		System.out.println("TP:\t"+TP+"\t(TP+FP):\t"+num_hold+"\t"+"total_relevant\t"+num_gold_hold);
		double Precision = ((double)TP/num_hold);
		double Recall = ((double)TP/num_gold_hold);
		
		System.out.println("Precision:\t"+Precision+"\tRecall:\t"+Recall);

		
		double Fscore = 2*((Precision*Recall)/(Precision+Recall));
		System.out.println(Fscore);
		
		br_1.close();
		br_2.close();
		return new Statistics(Precision, Recall, Fscore);

	}
	
	public static void main(String args[]) throws IOException{		
		
		double avg_precision = 0;
		double avg_recall = 0;
		double avg_f_measure = 0;
		
		double soft_avg_precision = 0;
		double soft_avg_recall = 0;
		double soft_avg_f_measure = 0;
		
		for (int fold=0; fold<10; fold++) {
			Statistics exact = exact_holders("holders_t_"+fold, "holders_gold_"+fold);
			Statistics soft = soft_holders("holders_t_"+fold, "holders_gold_"+fold);
			avg_precision += exact.precision;
			avg_recall += exact.recall;
			avg_f_measure += exact.f_measure;
			
			soft_avg_precision += soft.precision;
			soft_avg_recall += soft.recall;
			soft_avg_f_measure += soft.f_measure;

		}
		
		System.out.println("For Holders!!------->");
		System.out.println("Exact\t"+(avg_precision/10)+"\t"+(avg_recall/10)+"\t"+(avg_f_measure/10));
		System.out.println("Soft\t"+(soft_avg_precision/10)+"\t"+(soft_avg_recall/10)
				+"\t"+(soft_avg_f_measure/10));
		
		System.out.println();
		System.out.println("===============");
		System.out.println();
		
		avg_precision = 0;
		avg_recall = 0;
		avg_f_measure = 0;
		
		soft_avg_precision = 0;
		soft_avg_recall = 0;
		soft_avg_f_measure = 0;

		//exact_holders("holders_0", "holders_gold_0");
		//soft_holders("holders_0", "holders_gold_0");

		
		for (int fold=0; fold<10; fold++) {
			Statistics exact = exact_holders("targets_t_"+fold, "targets_gold_"+fold);
			Statistics soft = soft_holders("targets_t_"+fold, "targets_gold_"+fold);
			avg_precision += exact.precision;
			avg_recall += exact.recall;
			avg_f_measure += exact.f_measure;
			
			soft_avg_precision += soft.precision;
			soft_avg_recall += soft.recall;
			soft_avg_f_measure += soft.f_measure;

		}
		
		System.out.println("For Targets!!------->");
		System.out.println("Exact\t"+(avg_precision/10)+"\t"+(avg_recall/10)+"\t"+(avg_f_measure/10));
		System.out.println("Soft\t"+(soft_avg_precision/10)+"\t"+(soft_avg_recall/10)
				+"\t"+(soft_avg_f_measure/10));

		
		//exact_holders("holders_1", "holders_gold_1");
		//soft_holders("holders_1", "holders_gold_1");

		
		//exact_holders("targets_0", "targets_gold_0");
		//soft_holders("targets_0", "targets_gold_0");

		
		//exact_holders("targets_1", "targets_gold_1");
		//soft_holders("targets_1", "targets_gold_1");
	}
}