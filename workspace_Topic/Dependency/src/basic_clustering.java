import java.io.*;
import java.util.*;

class Pair {
	String start;
	String end;
	
	Pair(String start, String end) {
		this.start = start;
		this.end = end;
	}
		
	public boolean equals(Object o) {
		if (this.start.equals(((Pair)o).start) && this.end.equals(((Pair) o).end)) {
			return true;
		}
		else
			return false;
	}
	
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + start.hashCode();
		hash = hash * 31 + end.hashCode();
		return hash;
	}
	
}

class Paths { 
	int count;
	LinkedList<String> paths;
	
	Paths() {
		this.count = 0;
		this.paths = new LinkedList<String>();
	}
	
	Paths(int count, LinkedList<String> paths) {
		this.count = count;
		this.paths = paths;
	}
	
	public void increment() {
		this.count = this.count + 1;
	}
	
	public boolean equals(Object o) {
		if (this.count == ((Paths)o).count && this.paths.equals(((Paths)o).paths)) {
			return true;
		}
		return false;
	}
	
	public void addPath(String path) {
		this.paths.add(path);
	}
	
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + count;
		hash = hash*31 + paths.hashCode();
		return hash;
	}
}

class basic_clustering {
	static String dse_holder_file = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
			+ "/Run_dep_paths/dse_holders.txt";
	static String ese_holder_file;
	static String dse_target_file = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
			+ "/Run_dep_paths/dse_targets.txt";
	
	static HashMap<String, Integer>dse_holder_cluster = new HashMap<String, Integer>();
	static HashMap<String, Integer>dse_target_cluster = new HashMap<String, Integer>();
	
	static HashMap<Pair, Paths>dse_holder_cluster2 = new HashMap<Pair, Paths>();
	static HashMap<Pair, Paths>dse_target_cluster2 = new HashMap<Pair, Paths>();
	
	public static void counts() throws IOException{
		BufferedReader br_dh = new BufferedReader(new FileReader(dse_holder_file));
		String st_dh;
		int counter_id = 0;
		while ((st_dh = br_dh.readLine())!=null) {
			String next_line = br_dh.readLine();
			String path = next_line.split("\t")[2];
			//System.out.println(path);
			if (dse_holder_cluster.containsKey(path)) {
				dse_holder_cluster.put(path, dse_holder_cluster.get(path)+1);
			}
			else {
				dse_holder_cluster.put(path, 1);
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
				+ "/Run_dep_paths/dse_holders_count.txt"));
		
		for (String key : dse_holder_cluster.keySet()) { 
			//System.out.println(key+"\t"+dse_holder_cluster.get(key));
			bw.write(key+"\t"+dse_holder_cluster.get(key).toString()+"\n");
		}
		
		bw.close();
		br_dh.close();
		
		BufferedReader br_dt = new BufferedReader(new FileReader(dse_target_file));
		String st_dt;
		int counter_id_t = 0;
		while ((st_dt = br_dt.readLine())!=null) {
			String next_line = br_dt.readLine();
			String path = next_line.split("\t")[2];
			//System.out.println(path);
			if (dse_target_cluster.containsKey(path)) {
				dse_target_cluster.put(path, dse_target_cluster.get(path)+1);
			}
			else {
				dse_target_cluster.put(path, 1);
			}
		}
		
		BufferedWriter bw_t = new BufferedWriter(new FileWriter("/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
				+ "/Run_dep_paths/dse_targets_count.txt"));
		
		for (String key : dse_target_cluster.keySet()) { 
			System.out.println(key+"\t"+dse_target_cluster.get(key));
			bw_t.write(key+"\t"+dse_target_cluster.get(key).toString()+"\n");
		}
		
		bw_t.close();
		br_dt.close();
		
	}
	
	public static void start_and_end() throws IOException{
		BufferedReader br_dh = new BufferedReader(new FileReader(dse_holder_file));
		String st_dh;
		int counter_id = 0;
		while ((st_dh = br_dh.readLine())!=null) {
			String next_line = br_dh.readLine();
			String path = next_line.split("\t")[2];
			Pair pair = new Pair(path.split("-")[1], path.split("-")[path.split("-").length-1]);
			//System.out.println(path);
			if (dse_holder_cluster2.containsKey(pair)) {
				//dse_holder_cluster.put(pair, dse_holder_cluster.get(pair).);
				dse_holder_cluster2.get(pair).addPath(path);
				dse_holder_cluster2.get(pair).increment();
			}
			else {
				Paths path1 = new Paths(1, new LinkedList());
				path1.addPath(path);
				dse_holder_cluster2.put(pair, path1);
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
				+ "/Run_dep_paths/dse_holders_start_end.txt"));
		
		for (Pair key : dse_holder_cluster2.keySet()) { 
			//System.out.println(key+"\t"+dse_holder_cluster.get(key));
			bw.write(key.start+"\t"+key.end+"\t"+dse_holder_cluster2.get(key).count+"\n");
		}
		
		bw.close();
		br_dh.close();
		
		BufferedReader br_dt = new BufferedReader(new FileReader(dse_target_file));
		String st_dt;
		//int counter_id = 0;
		while ((st_dt = br_dt.readLine())!=null) {
			String next_line = br_dt.readLine();
			String path = next_line.split("\t")[2];
			Pair pair = new Pair(path.split("-")[1], path.split("-")[path.split("-").length-1]);
			//System.out.println(path);
			if (dse_target_cluster2.containsKey(pair)) {
				//dse_holder_cluster.put(pair, dse_holder_cluster.get(pair).);
				dse_target_cluster2.get(pair).addPath(path);
				dse_target_cluster2.get(pair).increment();
			}
			else {
				Paths path1 = new Paths(1, new LinkedList());
				path1.addPath(path);
				dse_target_cluster2.put(pair, path1);
			}
		}
		
		BufferedWriter bw_t = new BufferedWriter(new FileWriter("/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
				+ "/Run_dep_paths/dse_targets_start_end.txt"));
		
		for (Pair key : dse_target_cluster2.keySet()) { 
			//System.out.println(key+"\t"+dse_holder_cluster.get(key));
			bw_t.write(key.start+"\t"+key.end+"\t"+dse_target_cluster2.get(key).count+"\n");
		}
		
		bw_t.close();
		br_dh.close();
		
	}
	
	/*Assuming that we want to form k clusters!!*/
	
	public static void longest_sequence(int number_cluster) throws IOException{
		BufferedReader br_dh = new BufferedReader(new FileReader(dse_holder_file));
		String st_dh;
		int counter_id = 0;
		while ((st_dh = br_dh.readLine())!=null) {
			String next_line = br_dh.readLine();
			String path = next_line.split("\t")[2];
			
			/* Implement some form to find the clusters in the data!!*/
			
			Pair pair = new Pair(path.split("-")[1], path.split("-")[path.split("-").length-1]);
			//System.out.println(path);
			if (dse_holder_cluster2.containsKey(pair)) {
				//dse_holder_cluster.put(pair, dse_holder_cluster.get(pair).);
				dse_holder_cluster2.get(pair).addPath(path);
				dse_holder_cluster2.get(pair).increment();
			}
			else {
				Paths path1 = new Paths(1, new LinkedList());
				path1.addPath(path);
				dse_holder_cluster2.put(pair, path1);
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
				+ "/Run_dep_paths/dse_holders_start_end.txt"));
		
		for (Pair key : dse_holder_cluster2.keySet()) { 
			//System.out.println(key+"\t"+dse_holder_cluster.get(key));
			bw.write(key.start+"\t"+key.end+"\t"+dse_holder_cluster2.get(key).count+"\n");
		}
		
		bw.close();
		br_dh.close();
		
		BufferedReader br_dt = new BufferedReader(new FileReader(dse_target_file));
		String st_dt;
		//int counter_id = 0;
		while ((st_dt = br_dt.readLine())!=null) {
			String next_line = br_dt.readLine();
			String path = next_line.split("\t")[2];
			Pair pair = new Pair(path.split("-")[1], path.split("-")[path.split("-").length-1]);
			//System.out.println(path);
			if (dse_target_cluster2.containsKey(pair)) {
				//dse_holder_cluster.put(pair, dse_holder_cluster.get(pair).);
				dse_target_cluster2.get(pair).addPath(path);
				dse_target_cluster2.get(pair).increment();
			}
			else {
				Paths path1 = new Paths(1, new LinkedList());
				path1.addPath(path);
				dse_target_cluster2.put(pair, path1);
			}
		}
		
		BufferedWriter bw_t = new BufferedWriter(new FileWriter("/Users/arzookatiyar/Desktop/workspace_Topic/Dependency"
				+ "/Run_dep_paths/dse_targets_start_end.txt"));
		
		for (Pair key : dse_target_cluster2.keySet()) { 
			//System.out.println(key+"\t"+dse_holder_cluster.get(key));
			bw_t.write(key.start+"\t"+key.end+"\t"+dse_target_cluster2.get(key).count+"\n");
		}
		
		bw_t.close();
		br_dh.close();
		
	}
	
	public static void compare_holder_target_paths() throws IOException{
		
		int pair_annotations = 0;
		HashMap<String, String> holder_paths = new HashMap<String, String>();
		HashMap<String, String> holder_exp = new HashMap<String, String>();
		HashMap<String, String> holder_ent = new HashMap<String, String>();
		
		BufferedReader br_h = new BufferedReader(new FileReader(dse_holder_file));
		String st;
		
		while((st = br_h.readLine())!=null) {
			String sentence = st.split("\t")[0];
			String next_line = br_h.readLine();
			String path = next_line.split("\t")[2];
			holder_paths.put(sentence, path);
			//holder_exp.put(sentence, next_line.split("\t")[1]);
			//holder_ent.put(sentence, next_line.split("\t")[0]);
			holder_exp.put(sentence, st.split("\t")[2]);
			holder_ent.put(sentence, st.split("\t")[1]);
		}
		
		br_h.close();
		
		BufferedReader br_t = new BufferedReader(new FileReader(dse_target_file));
		int bad_news = 0;
		while ((st = br_t.readLine())!=null) {
			String sentence = st.split("\t")[0];
			String next_line = br_t.readLine();
			String path = next_line.split("\t")[2];
			
			if (holder_paths.containsKey(sentence)) {
				pair_annotations++;
				if (holder_paths.get(sentence).equals(path)) {
					bad_news = bad_news + 1;
					System.out.println(sentence);
					System.out.println(holder_ent.get(sentence)+"\t"+holder_exp.get(sentence));
					//System.out.println(next_line.split("\t")[0] + "\t"+ next_line.split("\t")[1]);
					System.out.println(st.split("\t")[1]+"\t"+st.split("\t")[2]);
					System.out.println(holder_paths.get(sentence)+"\t"+path);
				}
			}
		}
		
		System.out.println(bad_news);
		System.out.println(pair_annotations);
		br_t.close();
	}
	
	public static void main(String args[]) throws IOException {
		//counts();
		//start_and_end();
		compare_holder_target_paths();
	}
}