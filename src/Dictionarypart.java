import java.util.LinkedList;




public class Dictionarypart{
	LinkedList<DocId> element;
	//String term;
	
	
	
	public Dictionarypart(){
		element=new LinkedList<DocId>();
	//	term="";
	}
	
	
	// constructor for one instance of dictionary
	public Dictionarypart(/*String termpass,*/ int docId){
		
	//	term=termpass;
		DocId make=new DocId(docId);
		element=new LinkedList<DocId>();
		element.add(make);
		
	}
	
	
	//toString method
	public String toString(){
	//	String NEW_LINE = System.getProperty("line.separator");
		return (/*NEW_LINE+term+*/" -> "+ element);
	}
	
	
	
	//add to existing term
	
	public void addExisting(int docId){
		//DocId make1=new DocId(docId);
				for(int i=0;i<this.getElement().size();i++){
	             // if we have such a term in same document, we increase the frequency
			if(this.getElement().get(i).getDocId()==docId){
				this.getElement().get(i).setFreq(this.getElement().get(i).getFreq()+1);
			  return;
			
			}//end of if statement
			
			
		}// end of loop
		
		// if there is not duplication of a term in document, we add it at the end of postings
		DocId make=new DocId(docId);
		this.getElement().addLast(make);
	}

	
	public void addfromMerge(int DOC,int FR){
		
		for(int i=0;i<this.getElement().size();i++){
           
		if(this.getElement().get(i).getDocId()==DOC){
			
			this.getElement().get(i).setFreq(this.getElement().get(i).getFreq()+FR);
			
		  return;
		
		}//end of if statement
		
		
	}// end of loop
	
	// if there is not duplication of a term in document, we add it at the end of postings
		
	DocId make=new DocId(DOC,FR);
	this.getElement().addLast(make);
	}
	

	
	
	public LinkedList<DocId> getElement() {
		return element;
	}


	public void setElement(LinkedList<DocId> element) {
		this.element = element;
	}

	
public  Dictionarypart appendlist(Dictionarypart passy) {
		
		for(int i=0;i<this.getElement().size();i++){
		
			for(int j=0;j<( passy.getElement()).size();j++){
				
				if(this.getElement().get(i).getDocId()==passy.getElement().get(j).getDocId()){
					this.getElement().get(i).setFreq(this.getElement().get(i).getFreq()+passy.getElement().get(j).getFreq());
					passy.getElement().remove(j);
			}
				
		}
		
		
	//	return term;
	}// end of outer loop
		
		
		for(int i=0;i<this.getElement().size();i++){
			
			for(int j=0;j<( passy.getElement()).size();j++){
				if(passy.getElement().get(j).getDocId()<this.getElement().get(i).getDocId()){
				//	System.out.println(passy.getElement().get(j).getDocId());
					this.getElement().add(i,passy.getElement().get(j));
					passy.getElement().remove(j);
					//this.getElement().get(i).setFreq(this.getElement().get(i).getFreq()+passy.getElement().get(j).getFreq());
				//	passy.getElement().remove(j);
			}
			}
			
		}
		///////////////////////////////////////////////////////////////////////////////////////////////
		this.getElement().addAll(passy.getElement());
		
	//	System.out.println(this);
		return this;
	}
	
	
	
}