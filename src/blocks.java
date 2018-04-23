public class blocks {

	/**
	 * @param args
	 */
	int nlines;
	boolean done;
    String	bname;
    
    
    public blocks(){
    	
    	nlines=0;
    	done=true;
    	bname="";
    	
    	
    }
    public void addline(){
    	nlines=nlines+1;
    }
    
	public int getNlines() {
		return nlines;
	}
	public void setNlines(int nlines) {
		this.nlines = nlines;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public String getBname() {
		return bname;
	}
	public void setBname(String bname) {
		this.bname = bname;
	}
	@Override
	public String toString() {
		return "blocks [nlines=" + nlines + ", done=" + done + ", bname="
				+ bname + "]";
	}
    

}