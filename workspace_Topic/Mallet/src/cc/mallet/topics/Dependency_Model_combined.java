package cc.mallet.topics;

import java.util.*;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import java.io.*;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Dirichlet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.Randoms;

import java.text.NumberFormat;


public class Dependency_Model_combined implements Serializable{
	
	private static Logger logger = MalletLogger.getLogger(Dependency_Model_combined.class.getName());

	
	protected ArrayList<RelationAssignment> data;
	
	//the alphabet for the input data
	protected Alphabet lexical_alphabet;
	
	//the alphabet for the relations
	protected LabelAlphabet relationAlphabet;
	
	//the number of relations requested
	protected int numRelations;
	
	//the size of vocabulary
	protected int numlexical_Types; //What are these types?!!
	
	protected double alpha;
	protected double alphaSum;
	protected double beta_lexical; //prior on the holder words
	protected double beta_lexicalSum; 
	public static final double DEFAULT_BETA = 0.01;
	
	//An array to put the topic counts for the current document.
	//Initialized locally below. Defined here to avoid garbage collection overhead.
	protected int[] oneDocRelationCounts; //indexed by <document index, relation index>
	
	//Statistics needed for sampling
	protected int[][] type_lexicalRelationCounts; //indexed by <feature index, relation index>
	//need to define feature!!
	protected int[] tokens_lexicalPerRelation; //indexed by <relation index>
	//tokens are the tuples. Differentiate between the tuples and tokens
		
	
	public int showRelationsInterval = 50;
	public int wordsPerRelation = 10; //Change them.
	
	protected Randoms random;
	protected NumberFormat formatter;
	protected boolean printLogLikelihood = false;
	
	public Dependency_Model_combined(int numberOfRelations) {
		this (numberOfRelations, numberOfRelations, DEFAULT_BETA);
	}
	
	//There are more number of beta parameters! need to include them.
	
	public Dependency_Model_combined(int numberOfRelations, double alphaSum, double beta_lexical) {
		this (numberOfRelations, alphaSum, beta_lexical, new Randoms());
	}
	
	//check what is meant by LabelAlphabet!!
	
	private static LabelAlphabet newLabelAlphabet (int numRelations) {
		LabelAlphabet ret = new LabelAlphabet();
		for (int i=0; i<numRelations; i++) {
			ret.lookupIndex("relation"+i);
		}
		return ret;
	}
	
	public Dependency_Model_combined(int numberOfRelations, double alphaSum, double beta_lexical, Randoms random) {
		this (newLabelAlphabet (numberOfRelations), alphaSum, beta_lexical, random);
	}
	
	public Dependency_Model_combined(LabelAlphabet relationAlphabet, double alphaSum, double beta_lexical, Randoms random) {
		this.data = new ArrayList<RelationAssignment>();
		this.relationAlphabet = relationAlphabet;
		this.numRelations = relationAlphabet.size();
		
		this.alphaSum = alphaSum;
		this.alpha = alphaSum/numRelations;
		this.beta_lexical = beta_lexical;
		this.random = random;
		
		oneDocRelationCounts = new int[numRelations];
		tokens_lexicalPerRelation = new int[numRelations];
		
		formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(5);
		
		logger.info("Dependency Model combined: " + numRelations + " relations");
	}
	
	public Alphabet get_leixcalAlphabet() {
		return lexical_alphabet;
	}
		
	public LabelAlphabet getRelationAlphabet() {
		return relationAlphabet;
	}
	
	public int getNumRelation() {
		return numRelations;
	}
	
	public ArrayList<RelationAssignment> getData() {
		return data;
	}
	
	public void setRelationDisplay(int interval, int n) {
		this.showRelationsInterval = interval;
		this.wordsPerRelation = n;
	}
	
	public void setRandomSeed(int seed) {
		random = new Randoms(seed);
	}
	
	public int[][] getType_lexicalRelationCounts() {
		return type_lexicalRelationCounts;
	}
		
	public int[] getRelation_lexicalTotals() {
		return tokens_lexicalPerRelation;
	}
	
	public FeatureSequence getFeatureSequence(String st, Alphabet alphabet) {
		LinkedList<Integer> indices = new LinkedList<Integer>();
		int []indices_new =  new int[st.split("\t").length-1];
		String line[] = st.split("\t");
		String doc_id = line[0];
		for (int i=1; i<line.length; i++) {
			String tuple = line[i];
			String path = tuple;
				//get the index in the alphabet
			indices.add(alphabet.lookupIndex(path));
			indices_new[i-1] = alphabet.lookupIndex(path);
			
			//lexical_set.add(tuple.split("-")[0]);
			//path_set.add(tuple.substring(tuple.indexOf("-"), tuple.length()));
			//System.out.println(tuple.split("-")[0]);
			//System.out.println(tuple.substring(tuple.indexOf("-"), tuple.length()));
		}
		return new FeatureSequence(alphabet, indices_new);
	}
	
	public void addInstances(LinkedList<String> training, Alphabet lexical_alphabets) {
		//lexical_alphabet = training.getlexical_Alphabet(); //check this!!
		lexical_alphabet = lexical_alphabets;
		numlexical_Types = lexical_alphabet.size(); //maybe needed to be defined for different beta's differently
		//seems like true!!
		
		//path_alphabet = training.getpath_Alphabet();
		
		//System.out.println(path_alphabet.toString());
		
		beta_lexicalSum = beta_lexical * numlexical_Types;  //Find out why!
		
		type_lexicalRelationCounts = new int[numlexical_Types][numRelations];
		
		int doc= 0;
		
		for(String instance : training) {
			doc++;
			//Each instance of the training set will have a holder and the path tuple
			
			//FeatureSequence lexical_tokens = (FeatureSequence) instance.getlexical_Data(); //here tokens will contribute to the tuple
			//FeatureSequence path_tokens = (FeatureSequence) instance.getpath_Data(); //here tokens will contribute to the tuple
			
			FeatureSequence lexical_tokens = getFeatureSequence(instance, lexical_alphabet);
			
			//System.out.println(path_tokens.toString());
			
			//lexical_tokens.size() == path_tokens.size()

			LabelSequence relationSequence = new LabelSequence(relationAlphabet, new int[lexical_tokens.size()]);			
			
			int []relations = relationSequence.getFeatures();
			//lexical_tokens.size() == path_tokens.size()
			for (int position= 0; position < lexical_tokens.size(); position++) {
				int relation = random.nextInt(numRelations);
				relations[position] = relation;
				tokens_lexicalPerRelation[relation]++;
				
				int lexical_type = lexical_tokens.getIndexAtPosition(position);
				//System.out.println("Path_type\t"+path_type+"\tRelation\t"+relation);
				type_lexicalRelationCounts[lexical_type][relation]++;
				//print_matrix(type_pathRelationCounts);
				//System.out.println("==========");
			}
			RelationAssignment t = new RelationAssignment(
					new Instance_new(instance, lexical_tokens, null), relationSequence);
			data.add(t);
		}
	}
	
	public void print_matrix(int [][]a) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[0].length; j++) {
				System.out.print(a[i][j]+"\t");
			}
			System.out.println();
		}
	}
	
	public void sample(int iterations) throws IOException {
		
		//iterations = 1;
		for (int iteration = 0; iteration < iterations; iteration++) {
			
			//System.out.println(iteration);
			
			long iterationStart = System.currentTimeMillis();

			//loop over every document in the corpus
			for (int doc=0; doc < data.size(); doc++) {
				FeatureSequence lexical_tokenSequence =
						(FeatureSequence) data.get(doc).instance.getlexical_Data(); //FeatureSequence needs to be changed!

				LabelSequence relationSequence =
						(LabelSequence) data.get(doc).relationSequence;
				
				//System.out.println(lexical_tokenSequence.toString());
				//System.out.println(path_tokenSequence.toString());
				
				//System.out.println("Document\t"+doc);
				sampleRelationForOneDoc(lexical_tokenSequence, relationSequence);
			}
							
            long elapsedMillis = System.currentTimeMillis() - iterationStart;
			//System.out.println("Outside for loop\t"+elapsedMillis);

			logger.fine(iteration + "\t" + elapsedMillis + "ms\t");

			// Occasionally print more information
			if (showRelationsInterval != 0 && iteration % showRelationsInterval == 0) {
				logger.info("<" + iteration + "> Log Likelihood: " + modelLogLikelihood() + "\n" +
							topWords (wordsPerRelation));
			}

		}
	}
	
	protected void sampleRelationForOneDoc(FeatureSequence lexical_tokenSequence,
			FeatureSequence relationSequence) {
		
		int[] oneDocRelation = relationSequence.getFeatures();
		
		int[] currentType_lexicalRelationCounts;
		int[] currentType_pathRelationCounts;
		int lexical_type, path_type, oldRelation, newRelation;
		double relationWeightsSum; 
		int docLength = lexical_tokenSequence.getLength();
		//lexical_tokenSequence == path_tokenSequence.length
		//System.out.println(lexical_tokenSequence.getLength()+"\t"+path_tokenSequence.getLength());
		
		int[] localRelationCounts = new int[numRelations];
		
		//populate relation counts
		for (int position = 0; position < docLength; position++) {
			localRelationCounts[oneDocRelation[position]]++;
		}
		
		double score, sum; 
		double []relationTermScores = new double[numRelations];
		
		//Iterate over the positions (words) in the document
		for(int position = 0; position < docLength; position++) {
			lexical_type = lexical_tokenSequence.getIndexAtPosition(position);
			//System.out.println("Path_type\t"+path_type+"\t"+lexical_type);
			oldRelation = oneDocRelation[position];
			
			//Grab the relevant row from our two-dimensional array
			currentType_lexicalRelationCounts = type_lexicalRelationCounts[lexical_type];
			
			//Remove this token from all counts
			localRelationCounts[oldRelation]--;
			tokens_lexicalPerRelation[oldRelation]--;
			assert(tokens_lexicalPerRelation[oldRelation] >= 0) : "old Relation " + oldRelation + " below 0";
			currentType_lexicalRelationCounts[oldRelation]--;
			
			//Now calculate and add up the scores for each relation for this word
			sum = 0.0;
			// Here's where the math happens! Note that overall performance is 
			//  dominated by what you do in this loop.
			for (int relation= 0; relation < numRelations; relation++) {
				score = ((alpha + localRelationCounts[relation]) *
						((beta_lexical + currentType_lexicalRelationCounts[relation])/
						(beta_lexicalSum + tokens_lexicalPerRelation[relation])));
				/*System.out.println("Score\t"+score+"\t"+alpha+"\t"+localRelationCounts[relation]
						+"\t"+currentType_lexicalRelationCounts[relation] + "\t"+
						tokens_lexicalPerRelation[relation]+"\t"+currentType_pathRelationCounts[relation]
								+"\t"+ tokens_pathPerRelation[relation]);
				System.out.println((alpha + localRelationCounts[relation])+"\t"+((beta_lexical + currentType_lexicalRelationCounts[relation])/
						beta_lexicalSum + tokens_lexicalPerRelation[relation])+"\t"+
						((beta_path + currentType_pathRelationCounts[relation])/
						beta_pathSum + tokens_pathPerRelation[relation]));*/
				sum += score;
				relationTermScores[relation] = score;
			}
			
			
			// Choose a random point between 0 and the sum of all topic scores
			//System.out.println("Sum\t"+sum);
			double sample = random.nextUniform()*sum;
			//System.out.println("Sample\t"+sample);
			
			//Figure out which relation contains that point
			newRelation = -1;
			while (sample > 0.0) {
				newRelation++;
				sample -= relationTermScores[newRelation];
				//System.out.println(relationTermScores[newRelation]+"\t"+newRelation+"\t"+sample);
			}
			
			// Make sure we actually sampled a relation
			if (newRelation == -1) {
				throw new IllegalStateException ("SimpleLDA: New topic not sampled.");
			}
			//System.out.println(newRelation);

			//Put that relation into the counts
			oneDocRelation[position] = newRelation;
			localRelationCounts[newRelation]++;
			tokens_lexicalPerRelation[newRelation]++;
			currentType_lexicalRelationCounts[newRelation]++;
		}
	}
	
	public double modelLogLikelihood() {
		double logLikelihood = 0.0;
		
		// The likelihood of the model is a combination of a 
		// Dirichlet-multinomial for the tuples(path, lexical term) in each relation
		// and a Dirichlet-multinomial for the relations in each
		// document.

		// The likelihood function of a dirichlet multinomial is
		//	 Gamma( sum_i alpha_i )	 prod_i Gamma( alpha_i + N_i )
		//	prod_i Gamma( alpha_i )	  Gamma( sum_i (alpha_i + N_i) )

		// So the log likelihood is 
		//	logGamma ( sum_i alpha_i ) - logGamma ( sum_i (alpha_i + N_i) ) + 
		//	 sum_i [ logGamma( alpha_i + N_i) - logGamma( alpha_i ) ]

		// Do the documents first
		
		int []relationCounts = new int[numRelations];
		double[] relationLogGammas = new double[numRelations];
		
		int[] docRelations;
		
		for (int relation = 0; relation < numRelations; relation++) {
			relationLogGammas[relation] = Dirichlet.logGamma(alpha);
		}
		
		for(int doc = 0; doc < data.size(); doc++) {
			LabelSequence relationSequence = (LabelSequence) data.get(doc).relationSequence;
			
			docRelations = relationSequence.getFeatures();
			
			for (int token = 0; token < docRelations.length; token++) {
				relationCounts[docRelations[token]]++; 
			}
			
			for (int relation = 0; relation < numRelations; relation++) {
				if (relationCounts[relation] > 0) {
					logLikelihood += (Dirichlet.logGamma(alpha + relationCounts[relation]) 
							- relationLogGammas[relation]);
				}
			}
			
			logLikelihood -= Dirichlet.logGamma(alphaSum + docRelations.length);
			Arrays.fill(relationCounts, 0);
		}
		
		//add the parameter sum term
		logLikelihood += data.size() * Dirichlet.logGamma(alphaSum);
		
		//And the relations!
		//System.out.println("At 1.");
		
		double logGammaBeta_lexical = Dirichlet.logGamma(beta_lexical);
		
		for (int type=0; type < numlexical_Types; type++) {
			//reuse this array as a pointer
			relationCounts = type_lexicalRelationCounts[type];
			
			for (int relation = 0; relation < numRelations; relation++) {
				if (relationCounts[relation]==0) {continue;}
				
				logLikelihood += Dirichlet.logGamma(beta_lexical + relationCounts[relation]) 
						- logGammaBeta_lexical;
				
				if (Double.isNaN(logLikelihood)) {
					System.out.println(relationCounts[relation]);
					System.exit(1);
				}
			}
		}
		
		for (int relation = 0; relation < numRelations; relation++) {
			logLikelihood -= Dirichlet.logGamma((beta_lexical * numlexical_Types) 
					+ tokens_lexicalPerRelation[relation]);
			
			if (Double.isNaN(logLikelihood)) {
				System.out.println("after relation "+relation + " "+ tokens_lexicalPerRelation[relation] );
				System.exit(1);
			}
		}
		
		//System.out.println("At 2.");

		
		logLikelihood += numRelations * Dirichlet.logGamma(beta_lexical * numlexical_Types);
		
		if (Double.isNaN(logLikelihood)) {
			System.out.println("after the lexical part");
			System.exit(1);
		}
		
		//System.out.println("At 3.");
		//System.out.println("at11\t"+logLikelihood);
		
		//System.out.println(logLikelihood);
		
		return logLikelihood;
	}
	
	// 
	// Methods for displaying and saving results
	//
	
	public String topWords(int numWords) {
		StringBuilder output = new StringBuilder();
		
		IDSorter[] sortedWords = new IDSorter[numlexical_Types];
		
		for (int relation=0; relation < numRelations; relation++) {
			for (int type = 0; type < numlexical_Types; type++) {
				sortedWords[type] = new IDSorter(type, type_lexicalRelationCounts[type][relation]);
			}
			
			Arrays.sort(sortedWords);
			
			
			
			//output.append(relation+"\t"+tokens_lexicalPerRelation[relation]+"\t");
			output.append(relation+"& ");
			output.append("pbox-replace\t");			

			for (int i=0; i < numWords; i++) {
				output.append(lexical_alphabet.lookupObject(sortedWords[i].getID()) + ", ");
				//add the path part as well to the answer!
			}
			output.append("\t} hline-replace");

			output.append('\n');
			

		}
		
		return output.toString();
	}
	
	/**
	 *  @param file        The filename to print to
	 *  @param threshold   Only print topics with proportion greater than this number
	 *  @param max         Print no more than this many topics
	 */
	
	public void printDocumentRelations(File file, double threshold, int max) throws IOException{
		PrintWriter out = new PrintWriter(file);
		
		out.print ("#doc source relation proportion ...\n");
		int docLen;
		int []relationCounts = new int[numRelations];
		
		IDSorter[]sortedRelations = new IDSorter[numRelations];
		for (int relation=0; relation < numRelations; relation++) {
			sortedRelations[relation] = new IDSorter(relation, relation);
		}
		
		if (max < 0 || max > numRelations) {
			max = numRelations;
		}
		
		for (int doc=0; doc < data.size(); doc++) {
			LabelSequence relationSequence = (LabelSequence)data.get(doc).relationSequence;
			int []currentDocRelations = relationSequence.getFeatures();
			
			out.print(doc); out.print(' ');
			
			/*if (data.get(doc).instance.getSource() != null) {
				out.print(data.get(doc).instance.getSource());
			}
			else {
				out.print("null-source");
			}*/
			
			out.print(' ');
			docLen = currentDocRelations.length;
			
			// Count up the tokens
			for (int token=0; token < docLen; token++) {
				relationCounts[ currentDocRelations[token] ]++;
			}
			
			//And normalize
			for (int relation = 0; relation < numRelations; relation++) {
				sortedRelations[relation].set(relation, (float) relationCounts[relation] / docLen);
			}
			
			Arrays.sort(sortedRelations);
			
			for (int i = 0; i<max; i++) {
				if (sortedRelations[i].getWeight() < threshold) {
					break;
				}
				out.print(sortedRelations[i].getID() + " " 
				+sortedRelations[i].getWeight() + " ");
			}
			
			out.print(" \n");
			
			Arrays.fill(relationCounts, 0);
		}
	}
	
	public void printState (File f) throws IOException {
		PrintStream out =
			new PrintStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(f))));
		printState(out);
		out.close();
	}
	
	public void printState (PrintStream out) {

		out.println ("#doc source pos typeindex type relation");

		for (int doc = 0; doc < data.size(); doc++) {
			FeatureSequence lexical_tokenSequence =	(FeatureSequence) data.get(doc).instance.getlexical_Data();
			FeatureSequence path_tokenSequence =	(FeatureSequence) data.get(doc).instance.getpath_Data();
			LabelSequence relationSequence =	(LabelSequence) data.get(doc).relationSequence;

			/*String source = "NA";
			if (data.get(doc).instance.getSource() != null) {
				source = data.get(doc).instance.getSource().toString();
			}*/

			for (int position = 0; position < relationSequence.getLength(); position++) {
				int lexical_type = lexical_tokenSequence.getIndexAtPosition(position);
				int path_type = path_tokenSequence.getIndexAtPosition(position);
				int relation = relationSequence.getIndexAtPosition(position);
				out.print(doc); out.print(' ');
				//out.print(source); out.print(' '); 
				out.print(position); out.print(' ');
				out.print(lexical_type); out.print(' ');
				out.print(path_type); out.print(' ');
				out.print(lexical_alphabet.lookupObject(lexical_type)); out.print(' ');
				out.print(relation); out.println();
			}
		}
	}
	
	// Serialization
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	private static final int NULL_INTEGER = -1;
	
	public void write (File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream(f));
			oos.writeObject(this);
			oos.close();
		}
		catch (IOException e) {
			System.err.println("Exception writing file " + f + ": " + e);
		}
	}
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);

		// Instance lists
		out.writeObject (data);
		out.writeObject (lexical_alphabet);
		out.writeObject (relationAlphabet);

		out.writeInt (numRelations);
		out.writeObject (alpha);
		out.writeDouble (beta_lexical);
		out.writeDouble (beta_lexicalSum);

		out.writeInt(showRelationsInterval);
		out.writeInt(wordsPerRelation);

		out.writeObject(random);
		out.writeObject(formatter);
		out.writeBoolean(printLogLikelihood);

		out.writeObject (type_lexicalRelationCounts);

		for (int ti = 0; ti < numRelations; ti++) {
			out.writeInt (tokens_lexicalPerRelation[ti]);
		}
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int featuresLength;
		int version = in.readInt ();

		data = (ArrayList<RelationAssignment>) in.readObject ();
		lexical_alphabet = (Alphabet) in.readObject();
		relationAlphabet = (LabelAlphabet) in.readObject();

		numRelations = in.readInt();
		alpha = in.readDouble();
		alphaSum = alpha * numRelations;
		beta_lexical = in.readDouble();
		beta_lexicalSum = in.readDouble();

		showRelationsInterval = in.readInt();
		wordsPerRelation = in.readInt();

		random = (Randoms) in.readObject();
		formatter = (NumberFormat) in.readObject();
		printLogLikelihood = in.readBoolean();
		
		int numDocs = data.size();
		this.numlexical_Types = lexical_alphabet.size();

		type_lexicalRelationCounts = (int[][]) in.readObject();
		tokens_lexicalPerRelation = new int[numRelations];
		for (int ti = 0; ti < numRelations; ti++) {
			tokens_lexicalPerRelation[ti] = in.readInt();
		}
		
		
	}
	
	public String printDocumentRelations(double threshold, int max) throws IOException{
		StringBuilder output = new StringBuilder();
		
		output.append("#doc source relation proportion ...\n");
		//output.toString();
		int docLen;
		int []relationCounts = new int[numRelations];
		
		IDSorter[]sortedRelations = new IDSorter[numRelations];
		for (int relation=0; relation < numRelations; relation++) {
			sortedRelations[relation] = new IDSorter(relation, relation);
		}
		
		if (max < 0 || max > numRelations) {
			max = numRelations;
		}
		
		for (int doc=0; doc < data.size(); doc++) {
			LabelSequence relationSequence = (LabelSequence)data.get(doc).relationSequence;
			int []currentDocRelations = relationSequence.getFeatures();
			
			output.append(doc); output.append(' ');
			//output.toString();
			
			/*if (data.get(doc).instance.getSource() != null) {
				out.print(data.get(doc).instance.getSource());
			}
			else {
				out.print("null-source");
			}*/
			
			//output.print(' ');
			docLen = currentDocRelations.length;
			
			// Count up the tokens
			for (int token=0; token < docLen; token++) {
				relationCounts[ currentDocRelations[token] ]++;
			}
			
			//And normalize
			for (int relation = 0; relation < numRelations; relation++) {
				sortedRelations[relation].set(relation, (float) relationCounts[relation] / docLen);
			}
			
			Arrays.sort(sortedRelations);
			
			for (int i = 0; i<max; i++) {
				//System.out.println(sortedRelations[i].getWeight());
				if (sortedRelations[i].getWeight() < threshold) {
					break;
				}
				output.append(sortedRelations[i].getID() + " " 
				+sortedRelations[i].getWeight() + " ");
				//System.out.println("After\t"+sortedRelations[i].getWeight());

			}
			
			output.append(" \n");
			
			Arrays.fill(relationCounts, 0);
		}
		//System.out.println(output.toString());
		return output.toString();
	}


	public static void main (String[] args) throws IOException {
		
		LinkedList training = new LinkedList();
		
		String filename = 
				"/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/dse_targets_word_final.txt";
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		//Alphabet is more like a dictionary
		
		
		String st;
		HashSet<String> lexical_set = new HashSet<String>();
		HashSet<String> path_set = new HashSet<String>();
		while ((st = br.readLine())!=null) {
			String line[] = st.split("\t");
			training.add(st);
			String doc_id = line[0];
			for (int i=1; i<line.length; i++) {
				String tuple = line[i];
				//lexical_set.add(tuple.split("-")[0]);
				lexical_set.add(tuple);
				//path_set.add(tuple.substring(tuple.indexOf("-"), tuple.length()));
				//System.out.println(tuple.split("-")[0]);
				//System.out.println(tuple.substring(tuple.indexOf("-"), tuple.length()));
			}
		}
		
		Alphabet lexical_alphabets = new Alphabet(lexical_set.toArray());
		Alphabet path_alphabets = new Alphabet(path_set.toArray());
				
		
		//InstanceList training = InstanceList.load (new File(args[0]));

		int numRelations = args.length > 1 ? Integer.parseInt(args[1]) : 20;

		Dependency_Model_combined lda = new Dependency_Model_combined (numRelations, 0.01, 0.01);
		lda.addInstances(training, lexical_alphabets);
		lda.sample(2000);
		System.out.println(lda.printDocumentRelations(0.01, 2000));
	}
	
}