package sei.buaa.program.analyzer;

import java.util.ArrayList;
import java.util.List;

public class TestCase implements Cloneable{
	
	private int id;
	private boolean passed;
	private boolean isCoincidentalCorrectness = false;
	private int totalExecutedStatementCnt = 0;

	
	List<Statement> statements = null;
	

	public TestCase()
	{
		statements = new ArrayList<Statement>();
	}
	
	public TestCase(int id,boolean passed)
	{
		this.id = id;
		this.passed = passed;
		statements = new ArrayList<Statement>();

	}

	public void incrementExecutedStatements()
	{
		totalExecutedStatementCnt++;
	}
	
	public void addStatement(Statement e)
	{
		statements.add(e);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Test Case ID :" + id + "\n");
		sb.append("Test Case Result :" + (passed == true? "passed" : "failed") +"\n");
		sb.append("Test Case Execution Path:");
		for (Statement e:statements)
		{
			sb.append("["+e.getLineNumber()+","+e.getCount()+"] ");
		}
		
		return sb.toString();
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public boolean isCoincidentalCorrectness() {
		return isCoincidentalCorrectness;
	}

	public void setCoincidentalCorrectness(boolean isCoincidentalCorrectness) {
		this.isCoincidentalCorrectness = isCoincidentalCorrectness;
	}

	public int getTotalExecutedStatementCnt() {
		return totalExecutedStatementCnt;
	}
	
	public Object clone(){
		TestCase tc = null;
		
		try {
			tc = (TestCase) super.clone();
			tc.statements = new ArrayList<Statement>();
			for (Statement s : statements)
			{
				tc.statements.add((Statement) s.clone());
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return tc;
	}
	
	
	
	public static void main(String [] args)
	{
		TestCase t1 = new TestCase(1,true);
		t1.addStatement(new Statement(2,5));
		t1.addStatement(new Statement(3,6));
		t1.addStatement(new Statement(6,7));
		
		System.out.println(t1);
		
		TestCase t2 = (TestCase) t1.clone();
		t2.addStatement(new Statement(8,9));
		t2.setId(100);
		System.out.println(t1);
		System.out.println(t2);
	}
	
}
