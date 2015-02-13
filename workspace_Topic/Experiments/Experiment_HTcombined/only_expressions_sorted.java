/**
 * Experiment to find out the ditribution of the opinion words in these shortest 
 * dependency paths obtained earlier.
 */

import java.io.*;
import java.util.Hashtable;

class only_expressions_sorted {
	
	static Hashtable<String, Integer> expr_table = new Hashtable<String, Integer>();
	
	public static void main(String args[]) throws IOException{
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("only_expr_word"));
		
		BufferedReader br = new BufferedReader(new FileReader("dse_holders_expr_sorted.txt"));
		String st;
		while((st = br.readLine())!=null) {
			int index = st.lastIndexOf("-");
			String substring = st.substring(index+1).split("\t")[0];
			if (expr_table.containsKey(substring)) {
				expr_table.put(substring, expr_table.get(substring)+1);
			}
			else {
				expr_table.put(substring, 1);
			}
		}
		
		for (String key : expr_table.keySet()) {
			bw.write(key+"\t"+(expr_table.get(key).toString())+"\n");
		}
		br.close();
		bw.close();
	}
}