package cc.mallet.examples;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class TopicModel {
	
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

	public static void topic_distribution(String holder, LinkedList<LinkedList<String>> topicWords, String filename, int numTopics) throws IOException{
		//String filename = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/dse_holders_word.txt";
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		String st;
		int not_assigned = 0;
		HashMap<String, Integer> not_assigned_words = new HashMap<String, Integer>();
		
		int []topic_assignment_probability = new int[numTopics];
		
		while((st = br.readLine())!=null) {
			String candidate = "";
			if (holder.equals("holder")) {
				candidate = st.split("-")[0].toLowerCase().replace(".", "").replace(",", "");
			}
			else {
				candidate = st.split("\t")[0].split("-")[st.split("\t")[0].split("-").length-1]
						.toLowerCase().replace(".", "").replace(",", "");
			}
			int freq = Integer.parseInt(st.split("\t")[1]);
			// Now check where is this candidate present
			boolean assigned = false;
			loop1 : for (int i=0; i<topicWords.size(); i++) {
				LinkedList ranktopicWords = topicWords.get(i);
				//System.out.print("Topicnumber\t"+i+"\t");
				for (int j=0; j<ranktopicWords.size(); j++) {
					//|| minDistance(candidate, (String)ranktopicWords.get(j)) == 1 
					if (candidate.equals((String)ranktopicWords.get(j)) 
							|| 
							(((candidate.contains((String)ranktopicWords.get(j)) && 
									candidate.indexOf((String)ranktopicWords.get(j)) == 0)||
								(((String)ranktopicWords.get(j)).contains(candidate) && 
										((String)ranktopicWords.get(j)).indexOf(candidate) == 0)) && 
									(minDistance(candidate, (String)ranktopicWords.get(j)) <=2 && 
									candidate.length() > 2))) {
						assigned = true;
						topic_assignment_probability[i] += freq;
						//System.out.println(candidate+"\t"+ (String)ranktopicWords.get(j));
						break loop1;
					}
					//System.out.print(ranktopicWords.get(j)+"\t");
				}
				
				//System.out.println();
			}
			if (assigned == false) {
				not_assigned += freq;
				//System.out.println(candidate);
				
				if (not_assigned_words.keySet().contains(candidate)) {
					not_assigned_words.put(candidate, not_assigned_words.get(candidate)+freq);
				}
				else {
					not_assigned_words.put(candidate, freq);
				}
				
				
				/*System.out.println(candidate+"========");
				if (candidate == "executive") {
				for (int i=0; i<topicWords.size(); i++) {
				LinkedList ranktopicWords = topicWords.get(i);
				//System.out.print("Topicnumber\t"+i+"\t");
				for (int j=0; j<ranktopicWords.size(); j++) {
					System.out.print(ranktopicWords.get(j)+"\t");
				}
				System.out.println();
				}
			}*/

				
			}
		}
		
		int assigned_words_count = 0;
		
		for (int k=0; k<topic_assignment_probability.length; k++) {
			System.out.println("Topic\t"+k+"\t"+topic_assignment_probability[k]);
			assigned_words_count += topic_assignment_probability[k];
		}
		
		System.out.println(not_assigned);
		System.out.println(assigned_words_count);
		
		for (String key : not_assigned_words.keySet()) {
			System.out.println(key+"\t"+not_assigned_words.get(key));
		}
		
		/*Iterator it = not_assigned_words.iterator();
		
		while(it.hasNext()) {
			System.out.println(it.next());
		}*/
		
		System.out.println("Left_words\t"+not_assigned_words.size());
		
		System.out.println("==============");
		/*for (int i=0; i<topicWords.size(); i++) {
			LinkedList ranktopicWords = topicWords.get(i);
			System.out.print("Topicnumber\t"+i+"\t");
			for (int j=0; j<ranktopicWords.size(); j++) {
				System.out.print(ranktopicWords.get(j)+"\t");
			}
			System.out.println();
		}*/

	}

	public static void main(String[] args) throws Exception {

		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));

		Reader fileReader = new InputStreamReader(new FileInputStream(new File("/Users/arzookatiyar/Desktop/TopicModeling/documents")), "UTF-8");
		instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
											   3, 2, 1)); // data, label, name fields

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is 
		int numTopics = 200;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only, 
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(2000);
		model.estimate();

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;
		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);
		
		// Estimate the topic distribution of the first instance, 
		//  given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		
		LinkedList<LinkedList<String>> topicWords = new LinkedList<LinkedList<String>>();
		
		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
			
			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			LinkedList rank_topicWords = new LinkedList();
			while (iterator.hasNext() && rank < 20) {
				IDSorter idCountPair = iterator.next();
				//if (idCountPair.getWeight() > 10)  {
					rank_topicWords.add(dataAlphabet.lookupObject(idCountPair.getID()));
				//}
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
			topicWords.add(rank_topicWords);
			System.out.println(out);
		}
		
		//for holder_file
		
		String filename = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/dse_holders_word.txt";
		
		topic_distribution("holder", topicWords, filename, numTopics);
		
		//for targetfile
		filename = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/dse_targets_word.txt";

		topic_distribution("target", topicWords, filename, numTopics);
	
		// Create a new instance with high probability of topic 0
		/*StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
			rank++;
		}

		// Create a new instance named "test instance" with empty target and source fields.
		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

		TopicInferencer inferencer = model.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		System.out.println("0\t" + testProbabilities[0]);*/
	}

}