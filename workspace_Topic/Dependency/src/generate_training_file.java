import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.HashMap;

class generate_training_file {
	
	/*Read all the files to find out the paths corresponding to all the sentences in the CRF output and 
	 * then store all these paths in a bif file corresponding to each sentence.
	 * 
	 * Another file filename --> candidate paths! (Check if my candidate paths match this
	 */
	
	static String file_path = "/Users/arzookatiyar/Desktop/TopicModeling/ILPExtractor/data/";
	static Hashtable<String, LinkedList<Annotations>>annotated_sentences = new Hashtable<String, LinkedList<Annotations>>();
	static String train_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Training_data/";
	
	static String train_gold = "/Users/arzookatiyar/Desktop/TopicModeling/ILPExtractor/input/";
	static Hashtable<String, gold_Annotations> gold_annotated_sentences = 
			new Hashtable<String, gold_Annotations>();
	
	
	//will read all the paths from the training gold standard and then for every sentence it will store the 
	//holder-exp pairs and target-exp pairs! 
	//In the next step it will create gold for every fold...for holder and target
	public static void create_gold_standard(String filename0) throws IOException{
		//String filename0 = train_gold+"adt_crf_train.crf";
		BufferedReader br = new BufferedReader(new FileReader(filename0));
		String st;
		String new_sentence = "";
		
		LinkedList<Pairs> holders = new LinkedList<Pairs>();
		LinkedList<Pairs> targets = new LinkedList<Pairs>();
		LinkedList<Pairs> dse = new LinkedList<Pairs>();
		
		int last_holder_index = -1;
		int last_target_index = -1;
		int last_dse_index = -1;
		int current_index = -1;
		int last_imp_index = -1;
		String last_annotation = "";
		String current_holder = "";
		String current_target = "";
		String current_dse = "";
		int token_number = 0;

		
		boolean indicator = false;
		while((st = br.readLine())!=null) {
			do {
				//System.out.println("st\t"+st);
				if (st != "" && st!=null && (st.split("\t").length == 3)) {
					new_sentence += st.split("\t")[0]+" ";
					token_number ++;
					current_index += 1;
					//do all the analysis here
					//System.out.println(st.split("\t")[0]+"\t"+path);
					if (st.split("\t").length-1 < 2 ) {
						continue;
					}
					if (st.split("\t")[2].contains("B_AGENT")) {
						if (last_imp_index >=0 && last_imp_index == current_index-1) {
							//this means i need to put everything back!
							if(last_holder_index == last_imp_index) {
								//add the holder string
								holders.add(new Pairs(current_holder, last_annotation));
								last_holder_index = -1;
								current_holder = "";
								last_annotation = "";
							}
							if (last_target_index == last_imp_index) {
								//add the target string
								targets.add(new Pairs(current_target, last_annotation));
								last_target_index = -1;
								current_target = "";
								last_annotation = "";
							}
							
							if (last_dse_index == last_imp_index) {
								//add the target string
								dse.add(new Pairs(current_dse, last_annotation));
								last_dse_index = -1;
								current_dse = "";
								last_annotation = "";
							}
						}
						
						last_holder_index = current_index;
						last_imp_index = current_index;
						last_annotation = st.split("\t")[2];
						if (last_annotation.split("_")[0].equals("B")) {
							last_annotation = last_annotation.substring(last_annotation.indexOf("_")+1);
						}

						current_holder += st.split("\t")[0] + "-"+token_number +" ";			
					}
					else {
						if (st.split("\t")[2].contains("AGENT")) {
							//has to be followed by a B_AGENT. Makes life simple!
							
							if (last_imp_index >=0 && last_imp_index == current_index-1) {
								//this means i need to put everything back!
								if (last_target_index == last_imp_index) {
									//add the target string
									targets.add(new Pairs(current_target, last_annotation));
									last_target_index = -1;
									current_target = "";
									last_annotation = "";
								}
								
								if (last_dse_index == last_imp_index) {
									//add the target string
									dse.add(new Pairs(current_dse, last_annotation));
									last_dse_index = -1;
									current_dse = "";
									last_annotation = "";
								}
								
								if (last_holder_index == last_imp_index 
										&& !(last_annotation.equals(st.split("\t")[2]))) {
									holders.add(new Pairs(current_holder, last_annotation));
									last_holder_index = -1;
									current_holder = "";
									last_annotation = "";
								}
							}

							
							last_holder_index = current_index;
							last_imp_index = current_index;
							current_holder += st.split("\t")[0] + "-"+token_number+ " ";
							last_annotation = st.split("\t")[2];
						}
						else {
							if (st.split("\t")[2].contains("B_DSE")) {
								if (last_imp_index >=0 && last_imp_index == current_index-1) {
									//this means i need to put everything back!
									if(last_holder_index == last_imp_index) {
										//add the holder string
										holders.add(new Pairs(current_holder, last_annotation));
										last_holder_index = -1;
										current_holder = "";
										last_annotation = "";
									}
									if (last_target_index == last_imp_index) {
										//add the target string
										targets.add(new Pairs(current_target, last_annotation));
										last_target_index = -1;
										current_target = "";
										last_annotation = "";
									}
									
									if (last_dse_index == last_imp_index) {
										//add the target string
										dse.add(new Pairs(current_dse, last_annotation));
										last_dse_index = -1;
										current_dse = "";
										last_annotation = "";
									}
								}
								
								last_dse_index = current_index;
								last_imp_index = current_index;
								current_dse += st.split("\t")[0] + "-"+token_number+ " ";	
								last_annotation = st.split("\t")[2];
								if (last_annotation.split("_")[0].equals("B")) {
									last_annotation = last_annotation.substring(last_annotation.indexOf("_")+1);
								}


							}
							else {
								if (st.split("\t")[2].contains("DSE")) {
									
									if (last_imp_index >=0 && last_imp_index == current_index-1) {
										//this means i need to put everything back!
										if(last_holder_index == last_imp_index) {
											//add the holder string
											holders.add(new Pairs(current_holder, last_annotation));
											last_holder_index = -1;
											current_holder = "";
											last_annotation = "";
										}
										if (last_target_index == last_imp_index) {
											//add the target string
											targets.add(new Pairs(current_target, last_annotation));
											last_target_index = -1;
											current_target = "";
											last_annotation = "";
										}
										
										if (last_dse_index == last_imp_index 
												&& !(last_annotation.equals(st.split("\t")[2]))) {
											dse.add(new Pairs(current_dse, last_annotation));
											last_dse_index = -1;
											current_dse = "";
											last_annotation = "";
										}
										
									}

									
									last_dse_index = current_index;
									last_imp_index = current_index;
									current_dse += st.split("\t")[0] + "-"+token_number+ " ";
									last_annotation = st.split("\t")[2];

								}
								else {
									if (st.split("\t")[2].contains("B_TARGET")) {
										if (last_imp_index >=0 && last_imp_index == current_index-1) {
											//this means i need to put everything back!
											if(last_holder_index == last_imp_index) {
												//add the holder string
												holders.add(new Pairs(current_holder, last_annotation));
												last_holder_index = -1;
												current_holder = "";
												last_annotation = "";
											}
											if (last_target_index == last_imp_index) {
												//add the target string
												targets.add(new Pairs(current_target, last_annotation));
												last_target_index = -1;
												current_target = "";
												last_annotation = "";
											}
											
											if (last_dse_index == last_imp_index) {
												//add the target string
												dse.add(new Pairs(current_dse, last_annotation));
												last_dse_index = -1;
												current_dse = "";
												last_annotation = "";
											}
										}
										
										last_target_index = current_index;
										last_imp_index = current_index;
										current_target += st.split("\t")[0] + "-"+token_number+ " ";			
										//System.out.println(current_target);
										last_annotation = st.split("\t")[2];
										if (last_annotation.split("_")[0].equals("B")) {
											last_annotation = last_annotation.substring(last_annotation.indexOf("_")+1);
										}


									}
									else {
										if ((st.split("\t")[2].contains("TARGET"))) {
											
											if (last_imp_index >=0 && last_imp_index == current_index-1) {
												//this means i need to put everything back!
												if(last_holder_index == last_imp_index) {
													//add the holder string
													holders.add(new Pairs(current_holder, last_annotation));
													last_holder_index = -1;
													current_holder = "";
													last_annotation = "";
												}
												
												if (last_dse_index == last_imp_index) {
													//add the target string
													dse.add(new Pairs(current_dse, last_annotation));
													last_dse_index = -1;
													current_dse = "";
													last_annotation = "";
												}
												
												if (last_target_index == last_imp_index &&
														!(last_annotation.equals(st.split("\t")[2]))) {
													targets.add(new Pairs(current_target, last_annotation));
													last_target_index = -1;
													current_target = "";
													last_annotation = "";
												}
											}

											
											last_target_index = current_index;
											last_imp_index = current_index;
											current_target += st.split("\t")[0] + "-"+token_number+" ";
											//System.out.println(current_target);
											last_annotation = st.split("\t")[2];

										}
										else {
											//O's 
											//System.out.println("Enters0s\t"+last_imp_index+"\t"+current_index);
											if (last_imp_index >=0 && last_imp_index == current_index-1) {
												//this means i need to put everything back!
												if(last_holder_index == last_imp_index) {
													//add the holder string
													//System.out.println(current_holder);
													holders.add(new Pairs(current_holder, last_annotation));
													last_holder_index = -1;
													current_holder = "";
													last_annotation = "";
												}
												if (last_target_index == last_imp_index) {
													//add the target string
													targets.add(new Pairs(current_target, last_annotation));
													last_target_index = -1;
													current_target = "";
													last_annotation = "";
												}
												
												if (last_dse_index == last_imp_index) {
													//add the target string
													dse.add(new Pairs(current_dse, last_annotation));
													last_dse_index = -1;
													current_dse = "";
													last_annotation = "";
												}
											}

										}
									}
								}
							}
						}
					}

				}
				else {
					new_sentence += "";
					indicator = true;

					break; //indication that we reach before a full stop
					//if (st == null) System.exit(0);
					
				}
			}while (!((st = br.readLine()).split("\t")[0].equals(".")));
			
			//System.exit(0);
			if (st.split("\t")[0].equals("."))
				new_sentence += ".";
			current_index++;
			
			System.out.println(new_sentence);
			
			//System.out.println(last_imp_index+"\t"+current_index);
			
			if (last_imp_index >0 && last_imp_index == current_index-1) {
				//this means i need to put everything back!
				if(last_holder_index == last_imp_index) {
					//add the holder string
					holders.add(new Pairs(current_holder, last_annotation));
					last_holder_index = -1;
					current_holder = "";
					last_annotation = "";
				}
				if (last_target_index == last_imp_index) {
					//add the target string
					targets.add(new Pairs(current_target, last_annotation));
					last_target_index = -1;
					current_target = "";
					last_annotation = "";
				}
				
				if (last_dse_index == last_imp_index) {
					//add the target string
					dse.add(new Pairs(current_dse, last_annotation));
					last_dse_index = -1;
					current_dse = "";
					last_annotation = "";
				}
			}
			
			System.out.println();
			
			for (int l=0; l<holders.size(); l++) {
				System.out.println("Holders\t"+holders.get(l).entity+"\t"+holders.get(l).type);
			}
			
			for (int l=0; l<targets.size(); l++) {
				System.out.println("Targets\t"+targets.get(l).entity+"\t"+targets.get(l).type);
			}

			for (int l=0; l<dse.size(); l++) {
				System.out.println("DSE\t"+dse.get(l).entity+"\t"+dse.get(l).type);
			}
			
			//System.out.println();
			if (gold_annotated_sentences.get(new_sentence)!=null) {
				gold_Annotations new_ann = new gold_Annotations(holders, targets, dse);
				//LinkedList new_list = gold_annotated_sentences.get(new_sentence);
				//new_list.add(new_ann);
				gold_annotated_sentences.put(new_sentence, new_ann);
				
			}
			else {
				//System.out.println(st.split("\t")[path]);
				gold_Annotations new_ann = new gold_Annotations(holders, targets, dse);
				//LinkedList new_list = new LinkedList();
				//new_list.add(new_ann);
				gold_annotated_sentences.put(new_sentence, new_ann);
			}

			//store them!
			holders = new LinkedList<Pairs>();
			targets = new LinkedList<Pairs>();
			dse = new LinkedList<Pairs>();
			
			last_holder_index = -1;
			last_target_index = -1;
			last_dse_index = -1;
			current_index = -1;
			last_imp_index = -1;
			last_annotation = "";
			current_holder = "";
			current_target = "";
			current_dse = "";
			token_number = 0;
			if (indicator == false)
				st = br.readLine(); //assuming that this reads the next space
			new_sentence = "";
						
		}
		
		br.close();
	}
	
	
	public static void gold_find_paths_store() throws IOException{
		String file_holder = train_path + "gold_holders";
		String file_target = train_path + "gold_targets";
		
		BufferedWriter bw_holder = new BufferedWriter(new FileWriter(file_holder));
		BufferedWriter bw_target = new BufferedWriter(new FileWriter(file_target));
		
		System.out.println(gold_annotated_sentences.size());
		for (String sentence : gold_annotated_sentences.keySet()) {
			gold_Annotations entries = gold_annotated_sentences.get(sentence);

			bw_holder.write(sentence+"\n");
			bw_target.write(sentence+"\n");
			//for every pair and for every crf path
			//maybe create a hash where we store all the computed entity, expr pairs
			LinkedList<Triples> cache = new LinkedList<Triples>();
			//for (int j=0; j<entries.size(); j++) {  //this will correspond to all 50 crf paths
				LinkedList<Pairs> holders = entries.holders;
				LinkedList<Pairs> dse = entries.dse;
				
				System.out.println("Holders\t"+holders.size()+"\t"+dse.size());
				
				//bw_holder.write(Integer.toString(j));
				//bw_target.write(Integer.toString(j));
				//bw_holder.write("\t"+Double.toString(entries.get(j).prob));
				//bw_target.write("\t"+Double.toString(entries.get(j).prob));
				for (int hold = 0; hold < holders.size(); hold++) {
					for (int ds = 0; ds < dse.size(); ds++) {
						
						//find the distance only when they are same type
						String type_holder = holders.get(hold).type;
						String type_exp = dse.get(ds).type;
						
						//System.out.println(type_holder);
						//System.out.println(type_exp);
												
						boolean match = false;
						
						for (int h = 1; h<type_holder.split("_").length; h++) {
							for (int e = 1; e < type_exp.split("_").length; e++) {
								//String cand1 = type_holder.split("_")[0]+"_"+type_holder.split("_")[h];
								//String cand2 = type_exp.split("_")[0]+"_"+type_exp.split("_")[e];
								String cand1 = type_holder.split("_")[h];
								String cand2 = type_exp.split("_")[e];
								if (cand1.equals(cand2)) {
									match = true;
								}
							}
						}
						
						if (match == true) {
						
							LinkedList<String> holder_query = new LinkedList<String>(); 
									//(LinkedList)Arrays.asList(holders.get(hold).split(" "));
							String []temp_array = holders.get(hold).entity.split(" ");
							for (int pp=0; pp<temp_array.length; pp++) {
								holder_query.add(temp_array[pp]);
							}
							LinkedList<String> exp_query = new LinkedList<String>();
									//(LinkedList)Arrays.asList(dse.get(ds).split(" "));
							temp_array = dse.get(ds).entity.split(" ");
							for (int pp=0; pp<temp_array.length; pp++) {
								exp_query.add(temp_array[pp]);
							}
							
							LinkedList<String> result = isPresent(cache, holders.get(hold).entity, dse.get(ds).entity);
							
							if (result == null) {	
								find_paths p = new find_paths();
								System.out.println(p.getDependencyStringFromSentence(sentence));

								LinkedList relation_holder = Mpqa_dependencypaths.shortestPath(exp_query, holder_query, p);
								if (relation_holder.size() > 0) {
									System.out.println(relation_holder.get(0)+"\t"+relation_holder.get(1)+"\t"+relation_holder.get(2));
									//Triples(String word, String exp, String path, String entity, String dse)
									cache.add(new Triples((String)(relation_holder.get(0)), (String)(relation_holder.get(1)),
											(String)(relation_holder.get(2)),
											holders.get(hold).entity, dse.get(ds).entity));
									bw_holder.write("\t"+(String)(relation_holder.get(0))+"\t"+(String)(relation_holder.get(1))
											+"\t"+(String)(relation_holder.get(2))+
											"\t"+holders.get(hold).entity+"\t"+ dse.get(ds).entity);
								}
							}
							else {
								System.out.println(result.get(0)+"\t"+result.get(1)+"\t"+result.get(2));
								bw_holder.write("\t"+result.get(0)+"\t"+result.get(1)+"\t"+result.get(2)+
										"\t"+holders.get(hold).entity+"\t"+ dse.get(ds).entity);
							}
						}
					}
				}
				
				LinkedList<Pairs> targets = entries.targets;
				
				System.out.println("Targets\t"+targets.size()+"\t"+dse.size());

				
				for (int targ = 0; targ < targets.size(); targ++) {
					for (int ds = 0; ds < dse.size(); ds++) {
						
						String type_target = targets.get(targ).type;
						String type_exp = dse.get(ds).type;
						
						boolean match = false;
						
						for (int h = 1; h<type_target.split("_").length; h++) {
							for (int e = 1; e < type_exp.split("_").length; e++) {
								//String cand1 = type_target.split("_")[0]+"_"+type_target.split("_")[h];
								String cand1 = type_target.split("_")[h];
								//String cand2 = type_exp.split("_")[0]+"_"+type_exp.split("_")[e];
								String cand2 = type_exp.split("_")[e];
								if (cand1.equals(cand2)) {
									match = true;
								}
							}
						}
						
						if (match == true) {
							LinkedList<String> target_query = new LinkedList<String>(); 
							//(LinkedList)Arrays.asList(holders.get(hold).split(" "));
							String []temp_array = targets.get(targ).entity.split(" ");
							for (int pp=0; pp<temp_array.length; pp++) {
								target_query.add(temp_array[pp]);
							}
							LinkedList<String> exp_query = new LinkedList<String>();
									//(LinkedList)Arrays.asList(dse.get(ds).split(" "));
							temp_array = dse.get(ds).entity.split(" ");
							for (int pp=0; pp<temp_array.length; pp++) {
								exp_query.add(temp_array[pp]);
							}
							
							LinkedList<String> result = isPresent(cache, targets.get(targ).entity, dse.get(ds).entity);
	
							
							if (result == null) {	
								find_paths p = new find_paths();
								System.out.println(p.getDependencyStringFromSentence(sentence));

								LinkedList relation_target = Mpqa_dependencypaths.shortestPath(exp_query, target_query, p);
								if (relation_target.size() > 0) {
									System.out.println(relation_target.get(0)+"\t"+relation_target.get(1)+"\t"+relation_target.get(2));
									cache.add(new Triples((String)(relation_target.get(0)), (String)(relation_target.get(1)),
											(String)(relation_target.get(2)),
											targets.get(targ).entity, dse.get(ds).entity));
									bw_target.write("\t"+relation_target.get(0)+"\t"+relation_target.get(1)+"\t"+relation_target.get(2)
											+"\t"+targets.get(targ).entity+"\t"+ dse.get(ds).entity);
								}
							}
							else {
								System.out.println(result.get(0)+"\t"+result.get(1)+"\t"+result.get(2));
								bw_target.write("\t"+result.get(0)+"\t"+result.get(1)+"\t"+result.get(2)+"\t"+
								targets.get(targ).entity+"\t"+ dse.get(ds).entity);
							}
						}
					}
				}
				bw_holder.write("\n");
				bw_target.write("\n");
			bw_holder.write("\n");
			bw_target.write("\n");
		}
		bw_holder.close();
		bw_target.close();
	}


	
	public static void create_training_file(int fold) throws IOException{
		//for (int fold = 0; fold < 1; fold++) { //change to 10
			String filename = file_path+"agent_dse_target_results_50_"+fold;
			if (fold == 0) 
				filename = file_path+"agent_dse_target_results_50_0_"+fold+".txt";
						
			for (int path = 0; path < 50 ;path++) { //change to 50
				String new_sentence = "";
				
				BufferedReader br = new BufferedReader(new FileReader(filename));
				String st;


				System.out.println("Path number\t"+path);
				int line_number = 0;
				LinkedList<String> holders = new LinkedList<String>();
				LinkedList<String> targets = new LinkedList<String>();
				LinkedList<String> dse = new LinkedList<String>();
				
				int last_holder_index = -1;
				int last_target_index = -1;
				int last_dse_index = -1;
				int current_index = -1;
				int last_imp_index = -1;
				String last_annotation = "";
				String current_holder = "";
				String current_target = "";
				String current_dse = "";
				int token_number = 0;

			
				while((st = br.readLine())!=null) {
					//System.out.println(st);
					line_number++;
					
					do {
						//System.out.println("st\t"+st);
						if (st.split("\t").length == 50) {
							//expecting probabilities here
							//System.out.println(st);
							//System.out.println(st.split("\t")[path]);
							double prob = Double.parseDouble(st.split("\t")[path]);
							line_number++;
							
							System.out.println(new_sentence);
							
							//System.out.println("Enters\t"+last_imp_index+"\t"+current_index);

							if (last_imp_index >0 && last_imp_index == current_index-1) {
								//this means i need to put everything back!
								if(last_holder_index == last_imp_index) {
									//add the holder string
									holders.add(current_holder);
									last_holder_index = -1;
									current_holder = "";
								}
								if (last_target_index == last_imp_index) {
									//add the target string
									targets.add(current_target);
									last_target_index = -1;
									current_target = "";
								}
								
								if (last_dse_index == last_imp_index) {
									//add the target string
									dse.add(current_dse);
									last_dse_index = -1;
									current_dse = "";
								}
							}
							
							for (int l=0; l<holders.size(); l++) {
								System.out.println("Holders\t"+holders.get(l));
							}
							
							for (int l=0; l<targets.size(); l++) {
								System.out.println("Targets\t"+targets.get(l));
							}

							for (int l=0; l<dse.size(); l++) {
								System.out.println("DSE\t"+dse.get(l));
							}
							
							if (annotated_sentences.get(new_sentence)!=null) {
								Annotations new_ann = new Annotations(holders, targets, dse,
										Double.parseDouble(st.split("\t")[path]));
								LinkedList new_list = annotated_sentences.get(new_sentence);
								new_list.add(new_ann);
								annotated_sentences.put(new_sentence, new_list);
								
							}
							else {
								//System.out.println(st.split("\t")[path]);
								Annotations new_ann = new Annotations(holders, targets, dse,
										Double.parseDouble(st.split("\t")[path]));
								LinkedList new_list = new LinkedList();
								new_list.add(new_ann);
								annotated_sentences.put(new_sentence, new_list);
							}
							
							new_sentence = "";
							st = br.readLine(); //Assuming that the next line is empty

							holders = new LinkedList<String>();
							targets = new LinkedList<String>();
							dse = new LinkedList<String>();
							
							last_holder_index = -1;
							last_target_index = -1;
							last_dse_index = -1;
							current_index = -1;
							last_imp_index = -1;
							last_annotation = "";
							current_holder = "";
							current_target = "";
							current_dse = "";
							token_number = 0;
							//System.exit(0);
							break;
							
							//Put everything to the designated places!
							
							
						}
						else {
							//System.out.println(st);
						}

						if (st != "" && st!=null && (st.split("\t").length > 50)) {
							token_number ++;
							new_sentence += st.split("\t")[0]+" ";
							current_index += 1;
							//do all the analysis here
							//System.out.println(st.split("\t")[0]+"\t"+path);
							if (st.split("\t").length-1 < path + 2 ) {
								continue;
							}
							if (st.split("\t")[path + 2].equals("B_AGENT")) {
								if (last_imp_index >=0 && last_imp_index == current_index-1) {
									//this means i need to put everything back!
									if(last_holder_index == last_imp_index) {
										//add the holder string
										holders.add(current_holder);
										last_holder_index = -1;
										current_holder = "";
									}
									if (last_target_index == last_imp_index) {
										//add the target string
										targets.add(current_target);
										last_target_index = -1;
										current_target = "";
									}
									
									if (last_dse_index == last_imp_index) {
										//add the target string
										dse.add(current_dse);
										last_dse_index = -1;
										current_dse = "";
									}
								}
								
								last_holder_index = current_index;
								last_imp_index = current_index;
								current_holder += st.split("\t")[0] + "-"+token_number +" ";			
							}
							else {
								if (st.split("\t")[path + 2].equals("AGENT")) {
									//has to be followed by a B_AGENT. Makes life simple!
									
									if (last_imp_index >=0 && last_imp_index == current_index-1) {
										//this means i need to put everything back!
										if (last_target_index == last_imp_index) {
											//add the target string
											targets.add(current_target);
											last_target_index = -1;
											current_target = "";
										}
										
										if (last_dse_index == last_imp_index) {
											//add the target string
											dse.add(current_dse);
											last_dse_index = -1;
											current_dse = "";
										}
									}

									
									last_holder_index = current_index;
									last_imp_index = current_index;
									current_holder += st.split("\t")[0] + "-"+token_number+ " ";
								}
								else {
									if (st.split("\t")[path + 2].equals("B_DSE")) {
										if (last_imp_index >=0 && last_imp_index == current_index-1) {
											//this means i need to put everything back!
											if(last_holder_index == last_imp_index) {
												//add the holder string
												holders.add(current_holder);
												last_holder_index = -1;
												current_holder = "";
											}
											if (last_target_index == last_imp_index) {
												//add the target string
												targets.add(current_target);
												last_target_index = -1;
												current_target = "";
											}
											
											if (last_dse_index == last_imp_index) {
												//add the target string
												dse.add(current_dse);
												last_dse_index = -1;
												current_dse = "";
											}
										}
										
										last_dse_index = current_index;
										last_imp_index = current_index;
										current_dse += st.split("\t")[0] + "-"+token_number+ " ";			

									}
									else {
										if (st.split("\t")[path + 2].equals("DSE")) {
											
											if (last_imp_index >=0 && last_imp_index == current_index-1) {
												//this means i need to put everything back!
												if(last_holder_index == last_imp_index) {
													//add the holder string
													holders.add(current_holder);
													last_holder_index = -1;
													current_holder = "";
												}
												if (last_target_index == last_imp_index) {
													//add the target string
													targets.add(current_target);
													last_target_index = -1;
													current_target = "";
												}
												
											}

											
											last_dse_index = current_index;
											last_imp_index = current_index;
											current_dse += st.split("\t")[0] + "-"+token_number+ " ";

										}
										else {
											if (st.split("\t")[path + 2].equals("B_TARGET")) {
												if (last_imp_index >=0 && last_imp_index == current_index-1) {
													//this means i need to put everything back!
													if(last_holder_index == last_imp_index) {
														//add the holder string
														holders.add(current_holder);
														last_holder_index = -1;
														current_holder = "";
													}
													if (last_target_index == last_imp_index) {
														//add the target string
														targets.add(current_target);
														last_target_index = -1;
														current_target = "";
													}
													
													if (last_dse_index == last_imp_index) {
														//add the target string
														dse.add(current_dse);
														last_dse_index = -1;
														current_dse = "";
													}
												}
												
												last_target_index = current_index;
												last_imp_index = current_index;
												current_target += st.split("\t")[0] + "-"+token_number+ " ";			
												//System.out.println(current_target);

											}
											else {
												if ((st.split("\t")[path + 2].equals("TARGET"))) {
													
													if (last_imp_index >=0 && last_imp_index == current_index-1) {
														//this means i need to put everything back!
														if(last_holder_index == last_imp_index) {
															//add the holder string
															holders.add(current_holder);
															last_holder_index = -1;
															current_holder = "";
														}
														
														if (last_dse_index == last_imp_index) {
															//add the target string
															dse.add(current_dse);
															last_dse_index = -1;
															current_dse = "";
														}
													}

													
													last_target_index = current_index;
													last_imp_index = current_index;
													current_target += st.split("\t")[0] + "-"+token_number+" ";
													//System.out.println(current_target);

												}
												else {
													//O's 
													//System.out.println("Enters0s\t"+last_imp_index+"\t"+current_index);
													if (last_imp_index >=0 && last_imp_index == current_index-1) {
														//this means i need to put everything back!
														if(last_holder_index == last_imp_index) {
															//add the holder string
															//System.out.println(current_holder);
															holders.add(current_holder);
															last_holder_index = -1;
															current_holder = "";
														}
														if (last_target_index == last_imp_index) {
															//add the target string
															targets.add(current_target);
															last_target_index = -1;
															current_target = "";
														}
														
														if (last_dse_index == last_imp_index) {
															//add the target string
															dse.add(current_dse);
															last_dse_index = -1;
															current_dse = "";
														}
													}

												}
											}
										}
									}
								}
							}
						}
						else {
							new_sentence += st+" ";
							//if (st == null) System.exit(0);
						}
						line_number++;
						//System.out.println(st.split("\t")[0]);
					}while (!((st = br.readLine()).split("\t")[0].equals(".")));
					//st = br.readLine(); //assuming that this reads the next space
					//System.exit(0);
					if (new_sentence!="")
						new_sentence += ". ";
					//System.out.println(new_sentence);
					current_index ++;
					//System.out.println(line_number);
				}	
				br.close();
			}
			//return fold;
		//}
	}
	
	
	public static LinkedList<String> isPresent(LinkedList<Triples> entries, String entity, String dse) {
		for (int k=0; k<entries.size(); k++) {
			if (entries.get(k).entity.equals(entity) && entries.get(k).dse.equals(dse)) {
				LinkedList path = new LinkedList();
				path.add(entries.get(k).word);
				path.add(entries.get(k).exp);
				path.add(entries.get(k).path);
				return path;
			}
		}
		return null;
	}
	
	public static void find_paths_store(int fold) throws IOException{
		String file_holder = train_path + "holders_"+fold;
		String file_target = train_path + "targets_"+fold;
		
		BufferedWriter bw_holder = new BufferedWriter(new FileWriter(file_holder));
		BufferedWriter bw_target = new BufferedWriter(new FileWriter(file_target));
		
		System.out.println(annotated_sentences.size());
		for (String sentence : annotated_sentences.keySet()) {
			LinkedList<Annotations> entries = annotated_sentences.get(sentence);
			find_paths p = new find_paths();
			System.out.println(p.getDependencyStringFromSentence(sentence));

			bw_holder.write(sentence+"\n");
			bw_target.write(sentence+"\n");
			//for every pair and for every crf path
			//maybe create a hash where we store all the computed entity, expr pairs
			LinkedList<Triples> cache = new LinkedList<Triples>();
			for (int j=0; j<entries.size(); j++) {  //this will correspond to all 50 crf paths
				LinkedList<String> holders = entries.get(j).holders;
				LinkedList<String> dse = entries.get(j).dse;
				bw_holder.write(Integer.toString(j));
				bw_target.write(Integer.toString(j));
				bw_holder.write("\t"+Double.toString(entries.get(j).prob));
				bw_target.write("\t"+Double.toString(entries.get(j).prob));
				for (int hold = 0; hold < holders.size(); hold++) {
					for (int ds = 0; ds < dse.size(); ds++) {
						LinkedList<String> holder_query = new LinkedList<String>(); 
								//(LinkedList)Arrays.asList(holders.get(hold).split(" "));
						String []temp_array = holders.get(hold).split(" ");
						for (int pp=0; pp<temp_array.length; pp++) {
							holder_query.add(temp_array[pp]);
						}
						LinkedList<String> exp_query = new LinkedList<String>();
								//(LinkedList)Arrays.asList(dse.get(ds).split(" "));
						temp_array = dse.get(ds).split(" ");
						for (int pp=0; pp<temp_array.length; pp++) {
							exp_query.add(temp_array[pp]);
						}
						
						LinkedList<String> result = isPresent(cache, holders.get(hold), dse.get(ds));
						
						if (result == null) {						
							LinkedList relation_holder = Mpqa_dependencypaths.shortestPath(exp_query, holder_query, p);
							if (relation_holder.size() > 0) {
								System.out.println(relation_holder.get(0)+"\t"+relation_holder.get(1)+"\t"+relation_holder.get(2));
								//Triples(String word, String exp, String path, String entity, String dse)
								cache.add(new Triples((String)(relation_holder.get(0)), (String)(relation_holder.get(1)),
										(String)(relation_holder.get(2)),
										holders.get(hold), dse.get(ds)));
								bw_holder.write("\t"+(String)(relation_holder.get(0))+"\t"+(String)(relation_holder.get(1))
										+"\t"+(String)(relation_holder.get(2))+
										"\t"+holders.get(hold)+"\t"+ dse.get(ds));
							}
						}
						else {
							System.out.println(result.get(0)+"\t"+result.get(1)+"\t"+result.get(2));
							bw_holder.write("\t"+result.get(0)+"\t"+result.get(1)+"\t"+result.get(2)+
									"\t"+holders.get(hold)+"\t"+ dse.get(ds));
						}
					}
				}
				
				LinkedList<String> targets = entries.get(j).targets;
				
				for (int targ = 0; targ < targets.size(); targ++) {
					for (int ds = 0; ds < dse.size(); ds++) {
						LinkedList<String> target_query = new LinkedList<String>(); 
						//(LinkedList)Arrays.asList(holders.get(hold).split(" "));
						String []temp_array = targets.get(targ).split(" ");
						for (int pp=0; pp<temp_array.length; pp++) {
							target_query.add(temp_array[pp]);
						}
						LinkedList<String> exp_query = new LinkedList<String>();
								//(LinkedList)Arrays.asList(dse.get(ds).split(" "));
						temp_array = dse.get(ds).split(" ");
						for (int pp=0; pp<temp_array.length; pp++) {
							exp_query.add(temp_array[pp]);
						}
						
						LinkedList<String> result = isPresent(cache, targets.get(targ), dse.get(ds));

						
						if (result == null) {						
							LinkedList relation_target = Mpqa_dependencypaths.shortestPath(exp_query, target_query, p);
							if (relation_target.size() > 0) {
								System.out.println(relation_target.get(0)+"\t"+relation_target.get(1)+"\t"+relation_target.get(2));
								cache.add(new Triples((String)(relation_target.get(0)), (String)(relation_target.get(1)),
										(String)(relation_target.get(2)),
										targets.get(targ), dse.get(ds)));
								bw_target.write("\t"+relation_target.get(0)+"\t"+relation_target.get(1)+"\t"+relation_target.get(2)
										+"\t"+targets.get(targ)+"\t"+ dse.get(ds));
							}
						}
						else {
							System.out.println(result.get(0)+"\t"+result.get(1)+"\t"+result.get(2));
							bw_target.write("\t"+result.get(0)+"\t"+result.get(1)+"\t"+result.get(2)+"\t"+targets.get(targ)+"\t"+ dse.get(ds));
						}

					}
				}
				bw_holder.write("\n");
				bw_target.write("\n");
			}
			bw_holder.write("\n");
			bw_target.write("\n");
		}
		bw_holder.close();
		bw_target.close();
	}
	
	public static void create_gold_files()throws IOException {
		String file_holder = train_path + "gold_holders";
		String file_target = train_path + "gold_targets";
		HashMap<String, String> holder_part = new HashMap<String, String>();
		HashMap<String, String> target_part = new HashMap<String, String>();

		BufferedReader br_h = new BufferedReader(new FileReader(file_holder));
		String st;
		while((st=br_h.readLine())!=null) {
			holder_part.put(st, br_h.readLine());
			br_h.readLine();
		}
		
		br_h.close();
		
		BufferedReader br_t = new BufferedReader(new FileReader(file_target));
		while((st=br_t.readLine())!=null) {
			target_part.put(st, br_t.readLine());
			br_t.readLine();
		}

		br_t.close();
		
		
		//Now read the test_files fold by fold and form gold 
		for (int fold = 0; fold < 10; fold++) {
			String hold_file = train_path + "holders_"+fold;
			String hold_file_gold = train_path + "holders_gold_"+fold;
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(hold_file_gold));
			BufferedReader br1 = new BufferedReader(new FileReader(hold_file));
			
			String st1;
			while ((st1 = br1.readLine())!=null) {
				if (st1.length() > 0) {
					String sentence = st1.substring(0, st1.length()-1);
					if (holder_part.containsKey(sentence)) {
						bw1.write(sentence+"\n");
						bw1.write(holder_part.get(sentence)+"\n");
					}
					else {
						//System.out.println(sentence);
					}
				}
			}
			
			bw1.close();
			br1.close();
			
			String targ_file = train_path + "targets_"+fold;
			String targ_file_gold = train_path + "targets_gold_"+fold;
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(targ_file_gold));
			BufferedReader br2 = new BufferedReader(new FileReader(targ_file));
			
			String st2;
			while ((st2 = br2.readLine())!=null) {
				if (st2.length() > 0) {
					String sentence = st2.substring(0, st2.length()-1);
					if (target_part.containsKey(sentence)) {
						bw2.write(sentence+"\n");
						bw2.write(target_part.get(sentence)+"\n");
					}
				}
			}
			
			bw2.close();
			br2.close();
			
		}
		
	}
	
	public static void main(String args[]) throws IOException{
		/*for (int fold = 0; fold<10; fold++) {
			if (fold == 1 || fold == 2)
				continue;
			create_training_file(fold);	
			find_paths_store(fold);
			annotated_sentences = new Hashtable<String, LinkedList<Annotations>>();
		}*/
		
		/*String filename0 = train_gold+"adt_crf_train.crf";
		create_gold_standard(filename0);
		int initial_size = gold_annotated_sentences.size();
		
		filename0 = train_gold+"adt_crf_test.crf";
		create_gold_standard(filename0);
		int final_size = gold_annotated_sentences.size();
		
		gold_find_paths_store();*/
		
		create_gold_files();
		
		//System.out.println(initial_size+"\t"+final_size);
		
	}
}


class Triples {
	//word, exp, path are the output
	//entity and dse are full!
	String word;
	String entity;
	String exp;
	String dse;
	String path;
	
	public Triples(String word, String exp, String path, String entity, String dse) {
		this.word = word;
		this.exp = exp;
		this.path = path;
		this.entity = entity;
		this.dse = dse;
	}
	
	public Triples(String word, String exp, String path) {
		this.word = word;
		this.exp = exp;
		this.path = path;
		this.dse = "";
		this.entity = "";
	}
	
	public boolean soft_equals(Object o) {
		if (this.word.equals(((Triples)o).word) && this.exp.equals(((Triples)o).exp) 
				&& this.path.equals(((Triples)o).path)) {
			return true;
		}
		return false;
	}
}


class Annotations {
	LinkedList<String> holders = new LinkedList<String>();
	LinkedList<String> targets = new LinkedList<String>();
	LinkedList<String> dse = new LinkedList<String>();
	double prob;
	
	public Annotations(LinkedList<String> holders, LinkedList<String> targets, LinkedList<String> dse,
			double prob) {
		this.holders.addAll(holders);
		this.targets.addAll(targets);
		this.dse.addAll(dse);
		this.prob = prob;
	}
}

class gold_Annotations {
	LinkedList<Pairs> holders = new LinkedList<Pairs>();
	LinkedList<Pairs> targets = new LinkedList<Pairs>();
	LinkedList<Pairs> dse = new LinkedList<Pairs>();
	
	public gold_Annotations(LinkedList<Pairs> holders, LinkedList<Pairs> targets, LinkedList<Pairs> dse) {
		this.holders.addAll(holders);
		this.targets.addAll(targets);
		this.dse.addAll(dse);
	}

}

class Pairs {
	String entity;
	String type;
	
	public Pairs(String entity, String type) {
		this.entity = entity;
		this.type = type;
	}
}

/*class all_CRF_paths {
	Hashtable<Integer, String> paths = new Hashtable<Integer, String>();
	LinkedList <Double> prob_paths = new LinkedList<Double>(); 
	String sentence;
	String filename;
	
	all_CRF_paths(Hashtable<Integer, String> paths, String sentence) {
		this.paths = paths;
		this.sentence = sentence;
	}
	
	all_CRF_paths() {
		this.sentence = "";
		this.filename = "";
	}
	
	public void add_path(int index, String path) {
		this.paths.put(index, path);
	}
	
	public void add_sentence(String sentence) {
		this.sentence = sentence;
	}
	
	public void add_filename(String filename) {
		this.filename = filename;
	}
	
	public void add_prob_paths(double prob) {
		this.prob_paths.add(prob);
	}
	
}*/