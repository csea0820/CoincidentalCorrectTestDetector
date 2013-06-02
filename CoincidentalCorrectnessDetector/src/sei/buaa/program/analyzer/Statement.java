package sei.buaa.program.analyzer;


public class Statement {
	
	private int lineNumber;
	private int count;
	private boolean block;
	
	public Statement(int lineNumber,int count)
	{
		this.lineNumber = lineNumber;
		this.count = count;
	}
	
	
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public boolean isExecuted()
	{
		return count != 0;
	}


	public boolean isBlock() {
		return block;
	}


	public void setBlock(boolean block) {
		this.block = block;
	}	
}
