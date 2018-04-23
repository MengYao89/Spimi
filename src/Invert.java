


public class Invert{
	Dictionarypart element;
	String term;
	
	
	
	public Invert(){
		element=new Dictionarypart();
		term="";
	}
	
	
	// constructor for one instance of dictionary
	
	public Invert(String termpass, Dictionarypart dictionarypart){
		
		   term=termpass;
			element=dictionarypart;
			
			
		}
		
	
	
	//toString method
	public String toString(){
		String NEW_LINE = System.getProperty("line.separator");
		return (NEW_LINE+term+""+ element);
	}
	
	
	
	//add to existing term
	
	
	
	
	public Dictionarypart getElement() {
		return element;
	}


	public void setElement(Dictionarypart element) {
		this.element = element;
	}

	// append two list of inverted indexs

 
	public String getTerm() {
		return term;
	}
    

	public void setTerm(String term) {
		this.term = term;
	}
	
	
}