import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

public class FirstOrderLogic {

	public static HashMap<String, ArrayList<Integer>> KBmap;
	public static ArrayList<String> KBlist;
	public static int NKB;
	
	public static class sentenceKB{
		String st;
		ArrayList<Integer> path;		
		sentenceKB(){
			st=null;
			path=new ArrayList<Integer>();
		}
	}
	
	public static HashMap<String, ArrayList<Integer>> createKBmap()
	{
		KBmap = new HashMap<String, ArrayList<Integer>>();
		int index = 0;
		String[] tmp;	
		boolean dup=false;
		while (index<KBlist.size()){
			tmp = KBlist.get(index).split("[(|]");
			int i = 0;
			String str;
			while(i<tmp.length){
				str = tmp[i];
				if (KBmap.containsKey(str)){
					dup=false;
					for (int j=0;j<KBmap.get(str).size();j++){
						if(KBmap.get(str).get(j)==index){
							dup=true;
						}
					}
					if(!dup){
						KBmap.get(str).add(index);
					}
				}
				else{
					ArrayList<Integer> indexlist = new ArrayList<Integer>();
					indexlist.add(index);
					KBmap.put(str, indexlist);
					
				}
				i+=2;
			}// end while(i<tmp.length)
			index++;
		}
		
		return KBmap;
	}
	
	public static HashMap<String, ArrayList<Integer>> copy(HashMap<String, ArrayList<Integer>> original)
		{
		    HashMap<String, ArrayList<Integer>> copy = new HashMap<String, ArrayList<Integer>>();
		    for (Entry<String, ArrayList<Integer>> entry : original.entrySet())
		    {
		    	//if(entry.)
		    	
		        copy.put(entry.getKey(),new ArrayList<Integer>(entry.getValue()));
		    }
		    return copy;
		}
	
	public static boolean getAnswer(String query){
		//0.create updatedKBmap
		HashMap<String, ArrayList<Integer>> cur_KBmap = copy(KBmap);
		String[] q=query.split("[(|]");
		ArrayList<String> additionalKBlist = new ArrayList<String>();
		additionalKBlist.add(query);
		boolean dup=false;
		for(int qi=0;qi<q.length;qi+=2){
			String str=q[qi];
			if (cur_KBmap.containsKey(str)){
				for (int j=0;j<cur_KBmap.get(str).size();j++){
					if(cur_KBmap.get(str).get(j)==NKB){
						dup=true;
					}
				}
				if(!dup){
					dup=false;
					cur_KBmap.get(str).add(NKB);
				}
			}
			else{
				ArrayList<Integer> indexlist = new ArrayList<Integer>();
				indexlist.add(NKB);
				cur_KBmap.put(str, indexlist);
			}
		}	
		
		//others
		Stack<sentenceKB> queryStack = new Stack<sentenceKB>();
		sentenceKB querySt = new sentenceKB();
		querySt.st = query;
		queryStack.push(querySt);
		StringBuilder restQuery = new StringBuilder();
		HashMap<String, String> allVar;
		HashMap<String, ArrayList<String>> equalVar;
		ArrayList<String> qsVar, comVar=new ArrayList<String>();
		
		//1.queryStack
		while (!queryStack.isEmpty()) {
			//2.get query sentence
			querySt=queryStack.pop();
			
			//3.split the query:qs[1...]
			String[] qs=querySt.st.split("[|]");
			
			for(int qi=0;qi<qs.length;qi++){	
				allVar=new HashMap<String, String>();
				equalVar=new HashMap<String, ArrayList<String>>();
				
				//3-1.get rest of query
				boolean qiFirst=false;
				restQuery = new StringBuilder();
				if(qs.length!=1){ // except qs[qi]
					if (qi==0){
						qiFirst=true;
					}
					for(int x=0;x<qs.length;x++){
						if(qi!=x){
							if(x==0 || qiFirst){
								restQuery.append(qs[x]);
								qiFirst = false;
							}
							else{
								restQuery.append("|"+qs[x]);
							}
						}
					}
				}

				//4.get Predication:qsPre & Variables:qsVar in qs[qi]
				String[] qsEach = qs[qi].split("[(,)]");				
				String qsPre = qsEach[0]; //query predicate
				qsVar = new ArrayList<String>();				
				for (int i=1;i<qsEach.length;i++){
					qsVar.add(qsEach[i]); //query variables
					if(!allVar.containsKey(qsEach[i]) && !Character.isUpperCase(qsEach[i].charAt(0))){
						allVar.put(qsEach[i],null);
					}
				}
				
				//5.Put ~qsPre
				String notPre = "";
				if (qsPre.charAt(0) == '~'){
					StringBuilder sb = new StringBuilder(qsPre);
					sb.deleteCharAt(0);
					notPre = sb.toString();
				}
				else{
					notPre = "~";
					notPre += qsPre;
				}
				
				//6.find notPre in cur_KBmap
				String combineWith="";
				StringBuilder comSb = new StringBuilder();
				
					if (cur_KBmap.containsKey(notPre)){
						ArrayList<Integer> tmplist = new ArrayList<Integer>();
						for (int x=0;x<cur_KBmap.get(notPre).size();x++){
							tmplist.add(cur_KBmap.get(notPre).get(x));
						}
						nestedLoop:
						for (int m=0;m<tmplist.size();m++){
							allVar=new HashMap<String, String>();
							equalVar=new HashMap<String, ArrayList<String>>();
							comSb = new StringBuilder();
							//(1)add Path: check the path
							int indexOfPre=tmplist.get(m);
							for(int len=0;len<querySt.path.size();len++){
								if(indexOfPre==querySt.path.get(len)){
									continue nestedLoop;
								}
							}
							sentenceKB newSentenceKB = new sentenceKB();
							for(int l=0;l<querySt.path.size();l++){
								newSentenceKB.path.add(querySt.path.get(l));
							}
							newSentenceKB.path.add(indexOfPre);

							//(2)Find the sentence in KB: combineWith	
							if(indexOfPre<NKB){
								combineWith=KBlist.get(indexOfPre);
							}
							else{
								combineWith=additionalKBlist.get(indexOfPre-NKB);
							}
							
							//(3)split OR in combineWith
							String[] comEach = combineWith.split("[|]"); 
							boolean fakeConstant=false;
							boolean fake=false;
							boolean equal=false;
							for (int i=0;i<comEach.length;i++){
								
								String[] comSplit = comEach[i].split("[(,)]");
								comVar = new ArrayList<String>();
								if (comSplit[0].equals(notPre)){

									//(4)don't add this predicate, Add comVals
									for (int k=1;k<comSplit.length;k++){
										comVar.add(comSplit[k]);
										if(Character.isUpperCase(comVar.get(k-1).charAt(0))){//comVal: Constant
											if(!Character.isUpperCase(qsVar.get(k-1).charAt(0))){//qsVal: variable
												allVar.put(qsVar.get(k-1),comVar.get(k-1)); // replace null to comvalConstant
											}
											else{//qsVal: Constant
												if (!qsVar.get(k-1).equals(comVar.get(k-1))){// have different Constant => PASS
											
													fakeConstant=true;
													fake=true;
													equal=false;
													break;
												}
												else{//both: equal Constant
													equal=true;
												}
											}
										}
										else{//comVal: variable
											if(Character.isUpperCase(qsVar.get(k-1).charAt(0))){//qsVal: Constant
												allVar.put(comVar.get(k-1), qsVar.get(k-1));
											}
											else{//both are variable
												if (qsVar.get(k-1)!=comVar.get(k-1)){
													boolean include=false;
													for (HashMap.Entry<String, ArrayList<String>> entry : equalVar.entrySet()){
														if(entry.getKey().equals(qsVar.get(k-1))){//if qsVar exists
															equalVar.get(entry.getValue().add(comVar.get(k-1)));
															include=true;
															break;
														}
														if(entry.getKey().equals(comVar.get(k-1))){//if comVar exists
															equalVar.get(entry.getValue().add(qsVar.get(k-1)));
															include=true;
															break;
														}
														
														for(int y=0;y<entry.getValue().size();y++){
															if(qsVar.get(k-1).equals(entry.getValue().get(y))){
																include=true;
																equalVar.get(entry.getValue().add(comVar.get(k-1)));
																break;
															}	
															if(comVar.get(k-1).equals(entry.getValue().get(y))){
																include=true;
																equalVar.get(entry.getValue().add(qsVar.get(k-1)));
																break;
															}
														}
													}
													
													if(!include){
														ArrayList<String> list = new ArrayList<String>();
														list.add(comVar.get(k-1));
														equalVar.put(qsVar.get(k-1), list);
													}
												}
											}
										}//end else
									}//end for(k)
									
									if(fakeConstant){
										fakeConstant=false;
										if (i == 0 || comSb.length()==0){
											comSb.append(comEach[i]);
										}
										else{
											comSb.append("|"+comEach[i]);
										}
									}
								}//end if(comEach.equals(notPre))
								else{
									//(5)create new combination with variables: comSb
									if (i == 0 || comSb.length()==0 ){
										comSb.append(comEach[i]);
									}
									else{
										comSb.append("|"+comEach[i]);
									}
								}
							}//end for(i)
							
							// check 
							if(fake){
								if(!equal){
									continue nestedLoop;
								}
							}
							
							//(6) append rest of query and comSb
							if(restQuery.toString().length()!=0) {
								if (comSb.length()==0){//comSb is nothing
									comSb.append(restQuery);
								}
								else{// both has something to connect
									comSb.append("|"+restQuery);
								}
							}
							else {//restQuery has nothing
								if(comSb.length()==0){
									//both are nothing
									// check the terminate
								}
								
							}
													
							//(7) substitute variables to constants
							boolean keepVariable = true;
							StringBuilder finalSb = new StringBuilder();
							String[] finalComEach = comSb.toString().split("[|]"); 

							for (int i=0;i<finalComEach.length;i++){
								String[] finalSplit = finalComEach[i].split("[(,)]");
								if (comSb.length()!=0){
									finalSb.append(finalSplit[0]+"(");
								}
								
								for (int k=1;k<finalSplit.length;k++){
									keepVariable = true;
									if(allVar.containsKey(finalSplit[k]) && allVar.get(finalSplit[k])!=null){
										if (k==1){
											finalSb.append(allVar.get(finalSplit[k]));
										}
										else{
											finalSb.append(","+allVar.get(finalSplit[k]));
										}
										keepVariable = false;
									}
									if (keepVariable){
										// check variables
										for (HashMap.Entry<String, ArrayList<String>> entry : equalVar.entrySet()){

											for(int j=0;j<entry.getValue().size();j++){
												if(entry.getValue().get(j).equals(finalSplit[k])){
													finalSplit[k]=entry.getKey();
												}
											}
										}				
										
										if (k==1){
											finalSb.append(finalSplit[k]);
										}
										else{
											finalSb.append(","+finalSplit[k]);
										}
									}
								}//end for(k)
								
								if (comSb.length()!=0){
									finalSb.append(")");
								}
								if (i!=finalComEach.length-1){
									finalSb.append("|");
								}
							}//end for(i:finalComEach)
								
							// CHECK "Empty"
							boolean ansFalse = false;
							if(finalSb.toString().length()==0){
								Iterator it = allVar.entrySet().iterator();
								for(HashMap.Entry<String, String> entry:allVar.entrySet())
								{
									if(entry.getValue()==null){
										ansFalse = true;
										break;
									}
								}
								if(!ansFalse){
									return true;
								}
							}
							
							//(8)add a new sentence and nexKBindex to KBlsit 
							String newKB = finalSb.toString();
							if (newKB.length()==0){
								return true;
							}
							
							// check redundent predicates
							String ansKB="";
							boolean redun=false;
							String[] newEach = newKB.split("[|]"); 
							for (int se=0;se<newEach.length;se++){
								redun=false;
								for(int j=se+1;j<newEach.length;j++){
									if(newEach[se].equals(newEach[j])){
										redun=true;
										break;
									}
								}
								if(!redun){
									if(ansKB.length()==0){
										ansKB+=(newEach[se]);
									}
									else{
										ansKB+=("|"+newEach[se]);
									}
								}
							}
							
							// check redundent sentences
							boolean redunSen=false;
							for(int se=0;se<KBlist.size();se++){
								if(KBlist.get(se).equals(ansKB)){
									redunSen=true;
									break;
								}
							}
							if(!redunSen){
								for(int se=0;se<additionalKBlist.size();se++){
									if(additionalKBlist.get(se).equals(ansKB)){
										redunSen=true;
										break;
									}
								}
							}
							
							// add
							if(!redunSen){
								additionalKBlist.add(ansKB);
								int newKBIndex =(NKB)+additionalKBlist.indexOf(ansKB);
			
								//(9)add a new sentence to cur_KBmap
								String[] tmp = ansKB.split("[(|]");
								String str;
								for(int i=0;i<tmp.length;i+=2){
									str = tmp[i];
									if (cur_KBmap.containsKey(str)){
										cur_KBmap.get(str).add(newKBIndex);
									}
									else{
										ArrayList<Integer> indexlist = new ArrayList<Integer>();
										indexlist.add(newKBIndex);
										cur_KBmap.put(str, indexlist);
									}
								}// end for(i)
								
								//(10)add a new query to queryStack
								newSentenceKB.st=ansKB;	
								queryStack.add(newSentenceKB);
							}
							
						}//end for(m:cur_KBmap)
				}//end if(containsKey in cur_KBmap)
			
			}//end for(qi:qs)
		}//end while
		
		return false;
	}
	
	public static void main(String[] args) {
		
		// 1.read input.txt
		try { 
			String fileName = "input_0.txt"; 
            String line, tmpline;
            BufferedReader br = new BufferedReader(new FileReader(fileName)); 
               
            int NQ = 0;
            NKB = 0;
            KBlist = new ArrayList<String>();
            ArrayList<String> Qlist = new ArrayList<>();
            
            
            // (1). Number of query: (int) NQ
            line = br.readLine();
            NQ = Integer.parseInt(line);
            // (2). get (String) queries : insert ~ to original queries
            int num = 0;
            while(num < NQ){
                tmpline = br.readLine().replaceAll("\\s","");
                if (tmpline.charAt(0) == '~')
                {
                	StringBuilder sb = new StringBuilder(tmpline);
                	sb.deleteCharAt(0);
                	line = sb.toString();
                }
                else
                {
                	line = "~";
                	line += tmpline;
                }
                Qlist.add(line);
                num++;
            }
            // (3). Number of KB: (int) NKB
            line = br.readLine();
            NKB = Integer.parseInt(line);
            // (4). get (String) KBs
            num = 0;
            while(num < NKB) 
            {
            	line = br.readLine().replaceAll("\\s","");
            	KBlist.add(line);
            	num++;
            } 
            br.close(); 
            
            // 2.create a map of KB
            KBmap = createKBmap();
                     
    		// 3.Write output.txt
	    	fileName = "output.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            
            for(int i = 0; i < NQ; i++)
            {
                // 4. Get answer to query
            	boolean res;
            	res = getAnswer(Qlist.get(i));
                
            	if (res)
            	{
            		bw.write("TRUE");
            	}
            	else
            	{
            		bw.write("FALSE");
            	}
                bw.newLine(); //Enter
                
                System.out.println("Result: " + res );
            }
            
            bw.close();
            
         } catch (IOException e){ 
               e.printStackTrace(); 
         }
	}

}
