

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;

public class main {

	/**
	 * @param args
	 */
	static int docID=1;
	final static long memory=80000000;
	static int block=0;
	public static void main(String[] args) {
		
		
		
		
		  Hashtable<String, Dictionarypart> h = new Hashtable<String, Dictionarypart>();
		
	

		  ArrayList<String> sw=new ArrayList<String>();
		  sw=stopWords();
		/* making the file names to read them from reuters*/
		  String[] fnames =new String[22];
		 for (int i=0;i<fnames.length;i++){
			if(i<=9)
			 fnames[i]="reut2-00"+i+".sgm";
			else
				 fnames[i]="reut2-0"+i+".sgm";
		 }
		 
		//building the indexes and make the blocks
		 for (int i=0;i<fnames.length;i++){
			
			 buildIndex(fnames[i],sw,h,memory);
		     System.out.println(fnames[i]+ "file has been made");
		 } 
	

		System.out.println(block);
		
		// make the objects to be able to merge all files
		  ArrayList<blocks> blocks=new ArrayList<blocks>();
		  blocks=makeblocks();
		  System.out.println(blocks);
		  //actual merge
		Merg(blocks);
		  //System.out.println(blocks);
		 

			// query processing
        ArrayList<Invert> general=getquery();
		  
	 intersectthem(general);
		 
		
	        }
	
	// make a arrraylist of stopwords
			public static ArrayList<String> stopWords(){
				ArrayList<String> stopwords=new ArrayList<String>();
				Scanner sc1=null;
				String temp;
				try{
					sc1 = new Scanner(new File("F:/workspace/Spimi/stopwords/common-english-words.txt"));
				}
				catch(FileNotFoundException e){
					System.out.println("Could not open input file for reading." + " Please check if the file exists.");
					System.out.print("Program will terminate.");
					System.exit(0);
				}

				while(sc1.hasNext()){ // in a file
					temp = sc1.next();
					stopwords.add(temp);
					//	System.out.println(temp);
				}

				return stopwords;
			}	
			
			//building the indexes
			public static void buildIndex(String fileName, ArrayList<String> stopwords, Hashtable<String, Dictionarypart> h,long memory){
				String str="";
				Scanner sc=null;

				try{
					sc = new Scanner(new File("F:/workspace/Spimi/corpus/"+fileName));
				}
				catch(FileNotFoundException e){
					System.out.println("Could not open input file for reading." + " Please check if the file exists.");
					System.out.print("Program will terminate.");
					System.exit(0);
				}

				while(sc.hasNext()){ // in a file
					str = sc.next();

					if(str.equals("</REUTERS>")){
						// System.out.println("Finish reading the document, docID:"+docID);
						docID++;
					}

					//normalize
					str = str.replaceAll("<[^>]*>|&lt;[^>]*>", " "); // no tags
					str = str.replaceAll("[\\p{Punct}]", " "); // no punctions

					//using lossy compression
					str = str.replaceAll("[\\d]", " "); // no numbers
					str = str.toLowerCase(); // case folding

					StringTokenizer st = new StringTokenizer(str);
					while(st.hasMoreTokens()){ // in a word
						String term=st.nextToken();
						//check whether or not a token is one of stop words
						if(isStop(stopwords,term)){
							continue;
						}
						else
							addtohash(memory,term,h);
					}///
					//
				}///
				savetodisk(h);
			}

			// check whether string is a stop word
			public static boolean isStop(ArrayList<String> list, String str){
				Iterator<String> itr = list.iterator();
				while (itr.hasNext()) {
					String element = itr.next();
					if(element.equals(str))
						return true;

				}
				return false;
			}


			// to check the memory space
			public static boolean  Fullmemory(){

				long freeMemory = Runtime.getRuntime().freeMemory();
				// 	 Runtime q = Runtime.getRuntime();
				long totalMemory = Runtime.getRuntime().totalMemory();
				//	 System.out.println(totalMemory-freeMemory+":total - free>=memory:"+memory);
				return  ((totalMemory-freeMemory)>= memory);

			}

			// tokenize documents to arraylist of strings
			static ArrayList<String> token(String str){
				ArrayList<String> lst = new ArrayList<String>();
				String temp="";
				for(int i=0;i<str.length();++i){
					if(str.charAt(i)==' ')
					{ lst.add(temp);
					temp="";

					}
					else
						temp=temp+str.charAt(i);

				}
				return lst;
			} 
			// block maker(file names)
			public static ArrayList<blocks> makeblocks()
			{
				ArrayList<blocks> blocks=new ArrayList<blocks>();
				for(int i=0;i<block;i++){
					blocks block =new blocks();
					block.setBname(i+".txt");
					blocks.add(block);
				}

				return blocks;
			}
		
		// save the files to blocks
		public static void savetodisk(Hashtable<String, Dictionarypart> h){
			// write to file
			// blocks blocks=new blocks();
			// ArrayList<blocks> blist = new ArrayList<blocks>();
			ArrayList<String> list1 = new ArrayList<String>(h.keySet());
			Collections.sort(list1);
			Iterator<String> itr = list1.iterator();
			// System.out.println("save");
			try{
				// Create file 
				FileWriter fstream = new FileWriter("F:/workspace/Spimi/output/"+block+".txt");
				BufferedWriter out = new BufferedWriter(fstream);
				while (itr.hasNext()) {
					String element = itr.next();
					//  System.out.print(element + " ");

					Dictionarypart templist=h.get(element);
					//	 System.out.println(element+""+templist);
					out.write(element+""+templist+"\n");

				}
				//Close the output stream
				out.close();
				block++;
				//	  System.out.println(block);
			}catch (Exception e){                                     //Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

		}
		// make a posting from a string
			public static Dictionarypart getinvert(String str){

				Dictionarypart dic=new Dictionarypart();
				String[]  newstr=str.split(" ");

				for(int o=0;o<newstr.length;o++){


					if(newstr[o].matches("\\[.*F.*.\\]"))
					{
						// System.out.println(str+"----------");
						String DocId="";
						int id=0;
						String Freq="";
						int fr=0;
						int keeptrack=1;
						for(int i=1;i<newstr[o].length();i++){
							if(newstr[o].charAt(i)!='F')
							{ DocId=DocId+newstr[o].charAt(i);
							keeptrack++;
							}
							else
							{  
								keeptrack++;
								id = Integer.parseInt(DocId);
								break;
							}
						}	
						for(int i=keeptrack;i<newstr[o].length()-1;i++){
							Freq=Freq+newstr[o].charAt(i);
						}
						fr = Integer.parseInt(Freq);

						// System.out.println(id+"-"+fr);
						dic.addfromMerge(id,fr);
					}


					if(newstr[o].matches("\\[.*F.*\\,"))
					{

						String DocId="";
						int id=0;
						String Freq="";
						int fr=0;
						int keeptrack=1;
						for(int i=1;i<newstr[o].length();i++){
							if(newstr[o].charAt(i)!='F')
							{ DocId=DocId+newstr[o].charAt(i);
							keeptrack++;
							}
							else
							{  
								keeptrack++;
								id = Integer.parseInt(DocId);
								break;
							}
						}	
						for(int i=keeptrack;i<newstr[o].length()-1;i++){
							Freq=Freq+newstr[o].charAt(i);
						}
						fr = Integer.parseInt(Freq);

						// System.out.println(id+"-"+fr);
						dic.addfromMerge(id,fr);

					}

					if(newstr[o].matches("\\w*F.*.\\,"))
					{
						// System.out.println(str+"----------");
						String DocId="";
						int id=0;
						String Freq="";
						int fr=0;
						int keeptrack=0;
						for(int i=0;i<newstr[o].length();i++){
							if(newstr[o].charAt(i)!='F')
							{ DocId=DocId+newstr[o].charAt(i);
							keeptrack++;
							}
							else
							{  
								keeptrack++;
								id = Integer.parseInt(DocId);
								break;
							}
						}	
						for(int i=keeptrack;i<newstr[o].length()-1;i++){
							Freq=Freq+newstr[o].charAt(i);
						}
						fr = Integer.parseInt(Freq);

						// System.out.println(id+"-"+fr);
						dic.addfromMerge(id,fr);

					}
					if(newstr[o].matches("\\w*F.*.\\]"))
					{
						// System.out.println(str+"----------");
						String DocId="";
						int id=0;
						String Freq="";
						int fr=0;
						int keeptrack=0;
						for(int i=0;i<newstr[o].length();i++){
							if(newstr[o].charAt(i)!='F')
							{ DocId=DocId+newstr[o].charAt(i);
							keeptrack++;
							}
							else
							{  
								keeptrack++;
								id = Integer.parseInt(DocId);
								break;
							}
						}	
						for(int i=keeptrack;i<newstr[o].length()-1;i++){
							Freq=Freq+newstr[o].charAt(i);
						}
						fr = Integer.parseInt(Freq);

						// System.out.println(id+"-"+fr);
						dic.addfromMerge(id,fr);
					}


				}// end of for loop

				return dic;

			} 
			// split a line into a term and coresponding posting
			public static  String[] getterm(String str){
				//  ArrayList<String> list=new ArrayList<String>();
				String[] list= new String[2];
				String temp1="";
				String temp2="";
				int index=0;
				for(int i=0;i<str.length();i++){

					if (str.charAt(i)!=' ')
					{ temp1=temp1+ str.charAt(i); 
					index++;
					}
					else 
					{index++;
					break;
					}
				}

				// 	  System.out.println(temp1);
				temp2=str.substring(index);
				// 	  System.out.println(temp2);
				list[0]=temp1;
				list[1]=temp2;
				return list;

			}
			

	//begin of new merge
	public static void Merg(ArrayList<blocks> BB){
		 
		  Hashtable<String, Dictionarypart> ggg = new Hashtable<String, Dictionarypart>();

	  boolean key=true;
	//  int count=0;
	  for(int u=0;u<BB.size();u++){
		  key= (key && BB.get(u).isDone());
	  }
	  
	 
	  
		
		Scanner sc=null;
	
	  while (!key){
		  //
		  
		  
		  for(int u=0;u<BB.size() ;u++){
			  String str="";
			  Dictionarypart dic=new Dictionarypart();
			  Dictionarypart temp=new Dictionarypart();
			 
			  if(!BB.get(u).isDone())
			  {
			  try{
					sc = new Scanner(new File("F:/workspace/Spimi"+BB.get(u).getBname()));
					}
					catch(FileNotFoundException e){
					System.out.println("Could not open input file for reading." + " Please check if the file exists.");
					System.out.print("Program will terminate injam.");
					System.exit(0);
					}
	 
			  
		
		   int nlines=BB.get(u).getNlines();
	
		   int counter=0;
				while(sc.hasNextLine() && counter<nlines){ // in a file
				counter++;
					}// end of main while loop
		        if (sc.hasNextLine())
		        {
		        	str=sc.nextLine();
		        	BB.get(u).addline();
		        	}
		        else{
		        	System.out.println(BB.get(u).getBname()+" is Done");
		        	BB.get(u).setDone(true);
		        }
		  
		        String[] list= new String[2];
		        list=getterm(str);
		   String term=list[0];
		    dic= getinvert(list[1]);
		    // chech whether we hav already such a term in hashtable
		    if (ggg.containsKey(term))
		    { temp= ggg.remove(term);
		//    System.out.println(dic+"before");
		      dic.appendlist(temp);
		//      System.out.println(dic+"after");
		       ggg.put(term,dic);
		    }
		    else{
		    	ggg.put(term,dic);
		    }
		     if(Fullmemory())
		     { 
		    	// System.out.println("going to save now"+BB.get(u).getBname());
		    	 savetodiskmerge(ggg);
		    	 ggg.clear();
	   	          // g.gc();
	   	        System.gc();
		    	 ggg = new Hashtable<String, Dictionarypart>();
		     }
		     
		     
			  }//end of if
		  
		  }// end of for blocks reading
		  
		  
		  key=true;
		  for(int i=0;i<BB.size();i++)
		  {  key= (key && BB.get(i).isDone());
		//  System.out.println("in the loop end check key");
		  
		  }
		//   System.out.println("loop trough the blocks");
	  }//end of main while
	  savetodiskmerge(ggg);
	  
	}// end of new merge
	
	public static void mergeFiles(){
				
		 Hashtable<String, Dictionarypart> g = new Hashtable<String, Dictionarypart>();
		 Dictionarypart dic=new Dictionarypart();
			Dictionarypart temp=new Dictionarypart();
		String str="",str2="";
		Scanner sc=null;
	   
	    try{
		sc = new Scanner(new File("F:/workspace/Spimi/output/0.txt"));
		}
		catch(FileNotFoundException e){
		System.out.println("Could not open input file for reading." + " Please check if the file exists.");
		System.out.print("Program will terminate injam.");
		System.exit(0);
		}
        //int count=0;
		while(sc.hasNext()){ // in a file
		str = sc.next();
		
		//
		// take terms out of files 
		if (str.matches("\\w.*")&& !str.matches("\\w*F.*")){
		
                str2=str;
                if (g.containsKey(str)){
                	//System.out.println("7777");
                
                   temp=g.get(str);
          //         System.out.println(temp);
          //         temp=temp.appendlist(temp);
          //         System.out.println(temp);
                }
			 
		}
		
		// this pattern [*F*]
		 if(str.matches("\\[.*F.*.\\]"))
    	 {
    		// System.out.println(str+"----------");
    		 String DocId="";
    		 int id=0;
    		 String Freq="";
    		 int fr=0;
    		 int keeptrack=1;
    	 for(int i=1;i<str.length();i++){
    	    if(str.charAt(i)!='F')
    	    { DocId=DocId+str.charAt(i);
    	    keeptrack++;
    	    }
    	    else
    	    	{  
    	    	keeptrack++;
    	    	id = Integer.parseInt(DocId);
    	    	break;
    	    	}
    	    }	
    	 for(int i=keeptrack;i<str.length()-1;i++){
    		 Freq=Freq+str.charAt(i);
     	    }
    	    fr = Integer.parseInt(Freq);
    	 
    	// System.out.println(id+"-"+fr);
    	    dic.addfromMerge(id,fr);
       	 dic.appendlist(temp);
       	 temp=new Dictionarypart(); 
       	 g.put(str2,dic);
			 dic=new Dictionarypart();
    	 
    	 
    	 }
		
			
		// the case matches [*F,
		 if(str.matches("\\[.*F.*\\,"))
    	 {
			  
    		 String DocId="";
    		 int id=0;
    		 String Freq="";
    		 int fr=0;
    		 int keeptrack=1;
    	 for(int i=1;i<str.length();i++){
    	    if(str.charAt(i)!='F')
    	    { DocId=DocId+str.charAt(i);
    	    keeptrack++;
    	    }
    	    else
    	    	{  
    	    	keeptrack++;
    	    	id = Integer.parseInt(DocId);
    	    	break;
    	    	}
    	    }	
    	 for(int i=keeptrack;i<str.length()-1;i++){
    		 Freq=Freq+str.charAt(i);
     	    }
    	    fr = Integer.parseInt(Freq);
    	 
    	// System.out.println(id+"-"+fr);
    	 dic.addfromMerge(id,fr);
    	 
    	 }
			// matches \w*F.*.\]
        	// the case \w*F.*.\,
        	 if(str.matches("\\w*F.*.\\,"))
        	 {
        		// System.out.println(str+"----------");
        		 String DocId="";
        		 int id=0;
        		 String Freq="";
        		 int fr=0;
        		 int keeptrack=0;
        	 for(int i=0;i<str.length();i++){
        	    if(str.charAt(i)!='F')
        	    { DocId=DocId+str.charAt(i);
        	    keeptrack++;
        	    }
        	    else
        	    	{  
        	    	keeptrack++;
        	    	id = Integer.parseInt(DocId);
        	    	break;
        	    	}
        	    }	
        	 for(int i=keeptrack;i<str.length()-1;i++){
        		 Freq=Freq+str.charAt(i);
         	    }
        	    fr = Integer.parseInt(Freq);
        	 
        	// System.out.println(id+"-"+fr);
        	 dic.addfromMerge(id,fr);
        	 
        	 }
        	 
        	 // end up with *F*]
        	 if(str.matches("\\w*F.*.\\]"))
        	 {
        		// System.out.println(str+"----------");
        		 String DocId="";
        		 int id=0;
        		 String Freq="";
        		 int fr=0;
        		 int keeptrack=0;
        	 for(int i=0;i<str.length();i++){
        	    if(str.charAt(i)!='F')
        	    { DocId=DocId+str.charAt(i);
        	    keeptrack++;
        	    }
        	    else
        	    	{  
        	    	keeptrack++;
        	    	id = Integer.parseInt(DocId);
        	    	break;
        	    	}
        	    }	
        	 for(int i=keeptrack;i<str.length()-1;i++){
        		 Freq=Freq+str.charAt(i);
         	    }
        	    fr = Integer.parseInt(Freq);
        	 
        	// System.out.println(id+"-"+fr);
        	 dic.addfromMerge(id,fr);
        	 dic.appendlist(temp);
        	 temp=new Dictionarypart(); 
        	 g.put(str2,dic);
			 dic=new Dictionarypart();
        	 
        	 
        	 }
        	 
		}
   
		savetodiskmerge(g);
	
	}

	
	// add to hashtable
				public static void  addtohash(long memory,String str, Hashtable<String, Dictionarypart> h) {



					if(h.containsKey(str))
					{
						Dictionarypart templist=h.get(str);
						templist.addExisting(docID);

					}
					else
					{

						Dictionarypart lst2=new Dictionarypart(docID);
						h.put(str,lst2);

					}
					long freeMemory = Runtime.getRuntime().freeMemory();
					Runtime g = Runtime.getRuntime();
					long totalMemory = Runtime.getRuntime().totalMemory();
					//System.out.println("in hash");
					if (totalMemory-freeMemory>=memory)
					{ 


						savetodisk(h);

						//	 System.out.println("just called save to disk----------------------------------------------------->");
						//	 System.out.println("free memory"+freeMemory+"  total memory="+totalMemory+" memory assignedto program:"+(totalMemory-freeMemory));

						h.clear();
						g.gc();
						System.gc();

						h = new Hashtable<String, Dictionarypart>();
						freeMemory = Runtime.getRuntime().freeMemory();
						g = Runtime.getRuntime();
						totalMemory = Runtime.getRuntime().totalMemory();
						//       System.out.println("free memory"+freeMemory+"  total memory="+totalMemory+" memory assignedto program:"+(totalMemory-freeMemory));

						//System.gc().setPrioarity(1);
					}   

				}
				
	
	// sort and save to the Final.txt file
	public static void savetodiskmerge(Hashtable<String, Dictionarypart> g){
		
		// write to file
	    // System.out.println("sort the hash table and save to disk");

		ArrayList<String> list1 = new ArrayList<String>(g.keySet());
		Collections.sort(list1);
		Iterator<String> itr = list1.iterator();

		try{
			// Create file 
			FileWriter fstream = new FileWriter("F:/workspace/Spimi/final/Final.txt",true);
			BufferedWriter out = new BufferedWriter(fstream);
			while (itr.hasNext()) {
				String element = itr.next();
				//  System.out.print(element + " ");

				Dictionarypart templist=g.get(element);
			   	//  System.out.println(element+""+templist);
				out.write(element+""+templist+"\n");
			}
			
			//Close the output stream
			out.close();
			block++;
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}
		
	/////////////////////////////////////////////////////////////////////////////////////////
	// get the query from user
    public static ArrayList<Invert> getquery(){

		Scanner keyboard=new Scanner(System.in);
		
      System.out.println("Enter your desired query to search please:");
		String s=keyboard.nextLine();
		ArrayList<Invert> general=getAnd(s);
      keyboard.close();
          return general;
	}
    
    // split the query into array
	public static ArrayList<String> queryarray(String s){

		ArrayList<String> queries=new ArrayList<String>();

		String temp="";
		for(int i=0;i<s.length();i++){

			if(s.charAt(i)==' ')
			{
				queries.add(temp);
				temp="";

			}
			else
				temp=temp+s.charAt(i);


		}
		queries.add(temp);
		//	System.out.println(queries);
		return queries;
	}
	// intersect a set of DocId's
    public static Invert intersect(Invert list1, Invert list2){ 
  	  Dictionarypart dic=new Dictionarypart();
  	  Invert temp =new Invert();
	     temp.setElement(dic);
	     
	    int i=0;
  	  int j=0;
  	  while(i<list1.getElement().getElement().size()&&j<list2.getElement().getElement().size()){
  		  
  	  
  	  if(list1.getElement().getElement().get(i).getDocId()==list2.getElement().getElement().get(j).getDocId())
  	  
  	  { temp.getElement().getElement().add(list1.getElement().getElement().get(i));
  	  //temp.setTerm(list1.getTerm());
	    	i++;   
  	    j++;
  	  }
  	  else 
  		  if(list1.getElement().getElement().get(i).getDocId()<list2.getElement().getElement().get(j).getDocId())
  		  {      i++;    }
  		  else 
	    		  if(list1.getElement().getElement().get(i).getDocId()>list2.getElement().getElement().get(j).getDocId())
	    		  {      j++;    }
  	  
  	  }	  
  	  
  	  return temp;
	      }
    //  compress the query and find it in the file
	private static ArrayList<Invert>  getAnd(String s) {

	    	  ArrayList<String> sw=new ArrayList<String>();
			  sw=stopWords();
	    	  
	    	  
	    	  ArrayList<String> tttt=queryarray(s);
	    	  ArrayList<String> queries= new ArrayList<String>();
	    	  for(int i=0;i<tttt.size();i++){
	    		  if(isStop(sw,tttt.get(i)))
	    			  continue;
	    		  else
	    			  queries.add(tttt.get(i));
	    	  }

	    	  Scanner sc=null;
	    	  ArrayList<Dictionarypart> LIST=new ArrayList<Dictionarypart>();
	    	  ArrayList<String> terms=new ArrayList<String>();
	    	  try{
	    		  sc = new Scanner(new File("F:/workspace/Spimi/final/Final.txt"));
	    	  }
	    	  catch(FileNotFoundException e){
	    		  System.out.println("Could not open input file for reading." + " Please check if the file exists.");
	    		  System.out.print("Program will terminate injam.");
	    		  System.exit(0);
	    	  }
	    	  Dictionarypart dic=new Dictionarypart();
	    	  String str="";
	    	 // to check whether there is such a query in the dictionary or not 
	    	  boolean[] bools=new boolean[queries.size()];
	    	  for(int tt=0;tt<queries.size();tt++)
	    	  { bools[tt]=false;}
	    	  while(sc.hasNextLine()){
                   
	    		  str=sc.nextLine();
	    		  String[] list= new String[2];
	    		  list=getterm(str);

	    		  for(int i=0;i<queries.size();i++) {       
	    			  if(queries.get(i).equalsIgnoreCase(list[0]))
	    			  { dic= getinvert(list[1]);
	    			  bools[i]=true;
	    			  LIST.add(dic);
	    			  terms.add(list[0]);	

	    			  }

	    			  for(int g=0;g<terms.size()-1;g++){
	    				  for(int j=g+1;j<terms.size();j++){

	    					  if(terms.get(g).equalsIgnoreCase(terms.get(j)))
	    					  { // System.out.println(terms.get(g)+"<i  j>"+terms.get(j));
	    						  Dictionarypart temp=LIST.get(g);
	    						  LIST.get(j).appendlist(temp);
	    						  LIST.remove(g);
	    						  terms.remove(g);
	    					  }
	    				  }

	    			  }//outerloop

	    		  }//end of for loop of queries
	    	  }// end of while 
	    	  
	    	  
	    	  for(int i=0; i <bools.length;i++){
	    		  if (bools[i]==false)
	    		  {
	    			  ArrayList<Invert> general=new ArrayList<Invert>();
	    			  return general;
	    			  
	    		  }  
	    	  }

	    	  //Collections.sort(LIST);

	    	  ArrayList<Invert> general=new ArrayList<Invert>();

	    	  for(int i=0;i<LIST.size();i++){
	    		  //System.out.println(terms.get(i)+LIST.get(i));
	    		  Invert inv=new Invert(terms.get(i),LIST.get(i));
	    		  general.add(inv);
	    	  }
       
	    	  System.out.println(general);/**/
	    	  //	 
                return general;

	      }	  
	 
	      // show the result to user 
	      public static void intersectthem(ArrayList<Invert> general ){ 
	    	  Invert temp=new Invert();
	    	
	     if(general.size()==1)
	     { System.out.println(general.get(0));
	     
	     
	     }
	     else
	    	 if(general.size()>1)
	    	 {
	    		
	    	      temp=intersect(general.get(0),general.get(1));
	    	 }
	     if(general.size()>2){
	     for(int i=2;i<general.size();i++){
	    temp=intersect(temp,general.get(i));
	     }
			 
		 }
	     String terms="";
	     for(int i=0;i<general.size();i++){
	    	 terms=terms+general.get(i).getTerm();
	    	if(i!=general.size()-1)
	    	 terms=terms+" AND ";
	    	 	     }
	     System.out.println("The terms "+terms+" can be found in documents :");

         for(int i=0; i<temp.getElement().getElement().size();i++)       
	     System.out.print(" [ "+temp.getElement().getElement().get(i).getDocId()+" ] ");
	    	   
	      }// end of intersection
	     
	     	     	    	  
	      }