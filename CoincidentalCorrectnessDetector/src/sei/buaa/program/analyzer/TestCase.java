package sei.buaa.program.analyzer;

import java.util.ArrayList;
import java.util.List;

public class TestCase {
	
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
	
}
