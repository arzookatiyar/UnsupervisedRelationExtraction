import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

class generate_features {
	
	static String folder_10_fold_data = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/10-fold_data/";
	static String folder_feature = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Features/";
	
	public static void features_holders_words() throws IOException{
		//store all the features in the corresponding folders!
		for (int fold = 0; fold<10; fold++) {
			
			HashSet<String> features = new HashSet<String>();
			
			String filename = folder_10_fold_data + "train_holders_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String st;
			while((st = br.readLine())!=null) {
				String []line = st.split("\t");
				for (int i=1; i<line.length; i++) {
					features.add(line[i].split("-")[0]);
				}
			}
			
			br.close();
			
			String filenamew = folder_feature+"words_holders_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filenamew));
			
			Iterator it = features.iterator();
			while(it.hasNext()) {
				bw.write(it.next()+"\n");
			}			
			bw.close();
		}
	}
	
	public static void features_holders_paths() throws IOException{
		//store all the features in the corresponding folders!
		for (int fold = 0; fold<10; fold++) {
			
			HashSet<String> features = new HashSet<String>();
			
			String filename = folder_10_fold_data + "train_holders_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String st;
			while((st = br.readLine())!=null) {
				String []line = st.split("\t");
				for (int i=1; i<line.length; i++) {
					features.add(line[i].substring(line[i].indexOf("-"), line[i].length()));
					System.out.println(line[i].substring(line[i].indexOf("-"), line[i].length()));
				}
			}
			
			br.close();
			
			String filenamew = folder_feature+"paths_holders_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filenamew));
			
			Iterator it = features.iterator();
			while(it.hasNext()) {
				bw.write(it.next()+"\n");
			}			
			bw.close();
		}
	}
	
	public static void features_holders_words_paths() throws IOException{
		//store all the features in the corresponding folders!
		for (int fold = 0; fold<10; fold++) {
			
			HashSet<String> features = new HashSet<String>();
			
			String filename = folder_10_fold_data + "train_holders_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String st;
			while((st = br.readLine())!=null) {
				String []line = st.split("\t");
				for (int i=1; i<line.length; i++) {
					features.add(line[i]);
				}
			}
			
			br.close();
			
			String filenamew = folder_feature+"words+paths_holders_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filenamew));
			
			Iterator it = features.iterator();
			while(it.hasNext()) {
				bw.write(it.next()+"\n");
			}			
			bw.close();
		}
	}
	
	
	public static void features_targets_words() throws IOException{
		//store all the features in the corresponding folders!
		for (int fold = 0; fold<10; fold++) {
			
			HashSet<String> features = new HashSet<String>();
			
			String filename = folder_10_fold_data + "train_targets_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String st;
			while((st = br.readLine())!=null) {
				String []line = st.split("\t");
				for (int i=1; i<line.length; i++) {
					features.add(line[i].split("-")[0]);
				}
			}
			
			br.close();
			
			String filenamew = folder_feature+"words_targets_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filenamew));
			
			Iterator it = features.iterator();
			while(it.hasNext()) {
				bw.write(it.next()+"\n");
			}			
			bw.close();
		}
	}
	
	public static void features_targets_paths() throws IOException{
		//store all the features in the corresponding folders!
		for (int fold = 0; fold<10; fold++) {
			
			HashSet<String> features = new HashSet<String>();
			
			String filename = folder_10_fold_data + "train_targets_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String st;
			while((st = br.readLine())!=null) {
				String []line = st.split("\t");
				for (int i=1; i<line.length; i++) {
					features.add(line[i].substring(line[i].indexOf("-"), line[i].length()));
					System.out.println(line[i].substring(line[i].indexOf("-"), line[i].length()));
				}
			}
			
			br.close();
			
			String filenamew = folder_feature+"paths_targets_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filenamew));
			
			Iterator it = features.iterator();
			while(it.hasNext()) {
				bw.write(it.next()+"\n");
			}			
			bw.close();
		}
	}
	
	public static void features_targets_words_paths() throws IOException{
		//store all the features in the corresponding folders!
		for (int fold = 0; fold<10; fold++) {
			
			HashSet<String> features = new HashSet<String>();
			
			String filename = folder_10_fold_data + "train_targets_"+fold;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String st;
			while((st = br.readLine())!=null) {
				String []line = st.split("\t");
				for (int i=1; i<line.length; i++) {
					features.add(line[i]);
				}
			}
			
			br.close();
			
			String filenamew = folder_feature+"words+paths_targets_"+fold;
			BufferedWriter bw = new BufferedWriter(new FileWriter(filenamew));
			
			Iterator it = features.iterator();
			while(it.hasNext()) {
				bw.write(it.next()+"\n");
			}			
			bw.close();
		}
	}
	
	public static void main(String args[]) throws IOException{
		features_holders_words();
		features_holders_paths();
		features_holders_words_paths();
		features_targets_words();
		features_targets_paths();
		features_targets_words_paths();
	}
}