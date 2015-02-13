import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

class find_stats {
	static HashMap<String, Integer> path_stats = new HashMap<String, Integer>();
	public static void main(String args[])throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("dse_holder_target.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("dse_paths.txt"));
		
		String st;
		while((st = br.readLine())!=null) {
			String next_line = br.readLine();
			System.out.println(next_line);
			if (next_line.split("\t").length < 3) {
			    continue;
			}
			String path = next_line.split("\t")[2];
			if (path_stats.containsKey(path)) {
				path_stats.put(path, path_stats.get(path)+1);
			}
			else {
				path_stats.put(path, 1);
			}
		}
		
		for (String key : path_stats.keySet()) {
			bw.write(key);
			bw.write("\t");
			bw.write(path_stats.get(key).toString());
			bw.write("\n");
		}
		bw.close();
		br.close();
	}
}
