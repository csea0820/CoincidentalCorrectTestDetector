package sei.buaa.program.analyzer;



public class AbstractSuspiciousness implements Comparable<AbstractSuspiciousness>{
	
	protected int lineNumber;
	protected double susp;
	
	ISuspsCalculator suspCalculator = null;
	
//	List<AbstractSuspiciousness> list = null;
	

	
	AbstractSuspiciousness()
	{
		
	}
	
	public AbstractSuspiciousness(ISuspsCalculator suspCalculator){
		this.suspCalculator = suspCalculator;
	}
	
	
	

	


	public int getLineNumber() {
		return lineNumber;
	}



	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}



	public double getSusp() {
		return susp;
	}

	/**
	 * @param a00 test case that don't execute the statement is classified as passes
	 * @param a01 test case that don't execute the statement is classified as fails
	 * @param a10 test case that execute the statement is classified as passes
	 * @param a11 test case that execute the statement is classified as fails
	 * 
	 */
	public void calcSups(StatementSum eSum){
		this.susp = suspCalculator.calcSups(eSum);
	}
	
	public void calcWeightedSups(double a00, double a01,double a10, double a11)
	{
		
	}

	public void setSusp(double susp) {
		this.susp = susp;
	}
	
	public int compareTo(AbstractSuspiciousness arg0) {
		
		return this.getSusp() > arg0.getSusp() ? 0: 1;
	}
	
	public void setSuspCalculator(ISuspsCalculator suspCalculator) {
		this.suspCalculator = suspCalculator;
	}
	
	
	public String toString()
	{
		return "[lineNumber="+lineNumber+",susp="+susp+"]";
	}

}
