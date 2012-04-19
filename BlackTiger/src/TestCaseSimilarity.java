import java.util.List;


public class TestCaseSimilarity implements Comparable<TestCaseSimilarity> {
	
	private String name;
	private int similarity;
	private boolean isCC;
	
	private int sim2;
	
	List<Stmt> stmts = null;
	
	public TestCaseSimilarity() {
		super();
		isCC = false;
	}

	public TestCaseSimilarity(String name, Integer similarity) {
		this.name = name;
		this.similarity = similarity;
		isCC = false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getSim2(){
		return sim2;
	}
	
	public void setSim2(int sim2)
	{
		this.sim2 = sim2;
	}
	
	public int getSimilarity() {
		return similarity;
	}
	public void setSimilarity(Integer similarity) {
		this.similarity = similarity;
	}

	@Override
	public int compareTo(TestCaseSimilarity o) {
//		if (o.getSimilarity() == this.similarity)
//			return compare(o);
//		else
			return -o.getSimilarity()+this.similarity;
	}

	public int compare(TestCaseSimilarity o)
	{
		List<Stmt> list1 = o.getStmts();
		int ret = 0;
		
		int i = 0, j = 0;
		while (i < stmts.size() && j < list1.size())
		{
			if (stmts.get(i).getLineNumber() == list1.get(j).getLineNumber())
			{
				ret += distance(stmts.get(i).getTimes(), list1.get(i).getTimes());
				i++;
				j++;
			}
			else if (stmts.get(i).getLineNumber() < list1.get(j).getLineNumber())
				i++;
			else j++;
		}

		
		return -ret;
	}
	
	public boolean isCC() {
		return isCC;
	}

	public void setCC(boolean isCC) {
		this.isCC = isCC;
	}

	public List<Stmt> getStmts() {
		return stmts;
	}

	public void setStmts(List<Stmt> stmts) {
		this.stmts = stmts;
	}
	
	private int distance(int a,int b)
	{
		return a-b;
	}
	
	public void showInfo()
	{
		System.out.print("name:"+name);
		System.out.println(" sim:"+similarity);
	}
	

}
