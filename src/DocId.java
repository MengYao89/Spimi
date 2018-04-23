

public class DocId {

	private int  DocId;
	private int  Freq;
	
	
	
// Parameterized constructor	
	 public DocId(int a){
		 DocId=a;
		 Freq=1;
	 }
	
	 public DocId(int a,int b){
		 DocId=a;
		 Freq=b;
	 }
	
	
	
	public int getDocId() {
		return DocId;
	}
	public void setDocId(int docId) {
		DocId = docId;
	}
	
	public int getFreq() {
		return Freq;
	}
	
	public void setFreq(int freq) {
		Freq = freq;
	}
	
	 public String toString(){
		 return (DocId+"F"+Freq);
	 }
	
	
	
	
	
}
