package sei.buaa.program.analyzer;


public class StatementSum {
	
	private int lineNumber;
	private int a00;   
	private int a10;
	private int a01;
	private int a11;
	
	
	public StatementSum(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}
	
	
	public void incrementA00()
	{
		a00++;
	}
	
	public void incrementA01()
	{
		a01++;
	}
	
	public void incrementA10()
	{
		a10++;
	}
	
	public void incrementA11()
	{
		a11++;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public int getA00() {
		return a00;
	}
	public void setA00(int a00) {
		this.a00 = a00;
	}
	public int getA10() {
		return a10;
	}
	public void setA10(int a10) {
		this.a10 = a10;
	}
	public int getA01() {
		return a01;
	}
	public void setA01(int a01) {
		this.a01 = a01;
	}
	public int getA11() {
		return a11;
	}
	public void setA11(int a11) {
		this.a11 = a11;
	}
	
	
	@Override
	public String toString() {
		return "StatementSum [lineNumber=" + lineNumber + ", a00=" + a00
				+ ", a10=" + a10 + ", a01=" + a01 + ", a11=" + a11 + "]";
	}


}
