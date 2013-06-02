package sei.buaa.program.cluster;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestCase implements Comparator<TestCase> {

	// private static int id = 0;
	private String name;
	private boolean result;
	private boolean isCoincidentCorretness = false;

	protected List<Stmt> stmts = null;

	public TestCase() {
		stmts = new ArrayList<Stmt>();
	}

	// public int getId() {
	// return id;
	// }
	//
	// public void setId(int id) {
	// this.id = id;
	// }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public void addStmt(Stmt stmt) {
		stmts.add(stmt);
	}

	public boolean equals(Object object) {
		TestCase tc = (TestCase) object;
		return tc.getName().equals(name);
	}

	public void writeToFile(String id) {
		
		if (result == false)return;
		
		String file = name + ".res";

		StringBuffer sb = new StringBuffer();
//		sb.append("Test Case:" + name);
//		sb.append("\n");
//		sb.append("Result:" + (result == true ? "true" : "false"));
//		sb.append("\n");

		for (Stmt st : stmts) {
			if (st.isPredicate) {
				sb.append(st.getLineNumber());
				sb.append(" ");
				//sb.append(st.getTimes() + " ");
				//sb.append(st.isPredicate == true ? "1" : "0");
				//if (st.isPredicate) {
					//sb.append(" " + st.getBranchExecutionCount().size());
					for (Integer cnt : st.getBranchExecutionCount()) {
						sb.append(" " + cnt);
					//}
				}
				sb.append("\n");
			}
		}

		PrintWriter pw = null;
		try {
			//System.out.println(sb.toString());
			pw = new PrintWriter(file);
			pw.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}

	}

	public void showInfo() {
		System.out.println("Test Case:" + name);
		System.out.println("Result:" + (result == true ? "true" : "false"));

		for (Stmt st : stmts) {
			st.showInfo();
		}
	}

	public boolean isCoincidentCorretness() {
		return isCoincidentCorretness;
	}

	public void setCoincidentCorretness(boolean isCoincidentCorretness) {
		this.isCoincidentCorretness = isCoincidentCorretness;
	}

	public List<Stmt> getStmts() {
		return stmts;
	}

	public void setStmts(List<Stmt> stmts) {
		this.stmts = stmts;
	}

	@Override
	public int compare(TestCase o1, TestCase o2) {
		return o1.getName().compareTo(o2.getName());
	}

	public int calSimilarityOfTestCases(TestCase tc) {
		List<Stmt> passList = null;
		List<Stmt> failedList = null;

		int i = 0;
		int tempSimilarity = 0;
		passList = tc.getStmts();
		failedList = stmts;

		// while (i < passList.size() && j < failedList.size()) {
		// int lineNumber0 = passList.get(i).getLineNumber();
		// int iineNumber1 = failedList.get(j).getLineNumber();
		//
		// if (lineNumber0 == iineNumber1
		// && passList.get(i).isPredicate == true) {
		// tempSimilarity +=
		// calDistance(passList.get(i).getTimes(),failedList.get(j).getTimes());

		while (i < passList.size()) {
			if (passList.get(i).isPredicate) {
				tempSimilarity += calPredicatesSimilarity(passList.get(i)
						.getBranchExecutionCount(), failedList.get(i)
						.getBranchExecutionCount());
			}
			i++;
		}
		// } else if (lineNumber0 < iineNumber1)
		// i++;
		// else
		// j++;
		// }
		// System.out.println("Similarity:"+tempSimilarity);
		if (tempSimilarity < 0)
			System.out.println("Similarity is less than 0");
		return tempSimilarity;
	}

	private int calPredicatesSimilarity(List<Integer> list0, List<Integer> list1) {
		int ret = 0;
		for (int i = 0; i < list0.size(); i += 2)
			ret += calDistance(list0.get(i), list1.get(i));
		return ret;
	}

	private int calDistance(int times1, int times2) {
		return (times1 - times2) * (times1 - times2);
		// return euclideanDistanceBySquare(times1,times2);
	}

	private int euclideanDistanceBySquare(int a, int b) {
		return (int) Math.sqrt((a - b) * (a - b));
	}

	private int absoluteDistance(int times1, int times2) {
		return Math.abs(times1 - times2);
	}

	private int hamingDistance(int times1, int times2) {
		// if (times1 == times2)return 1;
		// else return 0;
		// if ((times1 != 0 && times2 != 0) || (times2 == 0 && times1 ==
		// 0))return 1;
		// else return 0;
		if (times1 != 0)
			times1 = 1;
		if (times2 != 0)
			times2 = 1;

		return times1 ^ times2;
	}

}
