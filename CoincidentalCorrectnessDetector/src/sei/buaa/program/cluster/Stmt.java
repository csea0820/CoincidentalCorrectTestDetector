package sei.buaa.program.cluster;
import java.util.List;


public class Stmt {
	private String fileName;
	private int lineNumber;
	private int times;
	
	boolean isPredicate = false;
	
	private List<Integer> branchExecutionCount = null;
	
	
	
	
//	public int getTrueBranchCnt() {
//		return trueBranchCnt;
//	}
//
//	public void setTrueBranchCnt(int trueBranchCnt) {
//		this.trueBranchCnt = trueBranchCnt;
//	}
//
//	public int getFalseBranchCnt() {
//		return falseBranchCnt;
//	}
//
//	public void setFalseBranchCnt(int falseBranchCnt) {
//		this.falseBranchCnt = falseBranchCnt;
//	}

	public Stmt()
	{
		times = 0;
	}
	
	public Stmt(int ln,int times)
	{
		lineNumber = ln;
		this.times = times;
	}
	
	public Stmt(String fn,int ln)
	{
		fileName = fn;
		lineNumber = ln;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
	
	
	public void showInfo()
	{
		System.out.println("Line Number:"+lineNumber);
		System.out.println("Times:"+times);
		System.out.println("Predicate:"+isPredicate);
		if (branchExecutionCount != null)
			for (Integer i:branchExecutionCount)
				System.out.print(i+" ");
		System.out.println("");
		
	}

	public boolean isPredicate() {
		return isPredicate;
	}

	public void setPredicate(boolean isPredicate) {
		this.isPredicate = isPredicate;
	}

	public List<Integer> getBranchExecutionCount() {
		return branchExecutionCount;
	}

	public void setBranchExecutionCount(List<Integer> branchExecutionCount) {
		this.branchExecutionCount = branchExecutionCount;
	}
	
}
