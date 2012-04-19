import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Centroid extends TestCase{
	
	public Centroid(int numberOfLine)
	{
		super();
		//stmts = new ArrayList<Stmt>();
		for (int i = 0; i < numberOfLine; i++)
			stmts.add(new Stmt());
		
	}
	
	public int calVarianceOnEachTestCase(TestCase tc)
	{
		int dist = 0;
		
		List<Stmt> stmts = tc.getStmts();
		
		for (int i = 0; i < stmts.size(); i++)
		{
			Stmt st = tc.getStmts().get(i);
			Stmt st2 = getStmts().get(i);
			if (st.isPredicate == false)System.out.println("not predicate");
			if (st.getLineNumber() != st2.getLineNumber()){
				System.out.println("match error!");
				System.out.println("Line:"+st.getLineNumber());
				System.out.println("Line:"+st2.getLineNumber());
			}
			
			List<Integer> count = st.getBranchExecutionCount();
			List<Integer> count2 = st2.getBranchExecutionCount();
			
			for (int j = 0; j < count.size(); j++)
				dist += (count.get(j)-count2.get(j))*(count.get(j)-count2.get(j));
			
		}
		
		if (dist < 0)System.out.println("dist is negative in calVarianceOnEachTestCase");
		
		return dist;
	}
	
	public void clear()
	{
//		stmts = new ArrayList<Stmt>(numberOfLine);
		for (Stmt st:stmts)
		{
			if (st.isPredicate)
			{
				st.setTimes(0);
				List<Integer> list = st.getBranchExecutionCount();
				Collections.fill(list,0);
//				for (int i = 0; i < list.size(); i++)
//				{
//					list.set(i, 0);
//				}
			}
		}
	}
	
	
	public void accumulatePredicate(TestCase tc)
	{
		List<Stmt> tcStmt = tc.getStmts();
		if (tcStmt.size() != stmts.size()){
			System.out.println("Error,Predicates Size Different!!");
		}
		for (int i = 0; i < stmts.size(); i++)
		{
			Stmt st = stmts.get(i);
			st.setLineNumber(tcStmt.get(i).getLineNumber());
			st.setPredicate(true);
			st.setTimes(tcStmt.get(i).getTimes()+st.getTimes());
			
			
			List<Integer> branches = tcStmt.get(i).getBranchExecutionCount();
			List<Integer> sum = new ArrayList<Integer>();
			List<Integer> origin = st.getBranchExecutionCount();
			for (int j = 0; j < branches.size(); j++)
			{
				//sum.add(branches.get(i)+)
				int a = branches.get(j);
				int b = 0;
				if (origin != null && j < origin.size())
					b = origin.get(j);
				
				sum.add(a+b);
			}	
			st.setBranchExecutionCount(sum);
		}
	}
	
	public void accumulatePredicate(int lineNumber,int times,List<Integer> branches)
	{
		//System.out.println(stmts.size());
		Stmt st = stmts.get(lineNumber);
		st.setLineNumber(lineNumber);
		st.setPredicate(true);
		st.setTimes(times+st.getTimes());
		
		List<Integer> sum = new ArrayList<Integer>();
		List<Integer> origin = st.getBranchExecutionCount();
		for (int i = 0; i < branches.size(); i++)
		{
			//sum.add(branches.get(i)+)
			int a = branches.get(i);
			int b = 0;
			if (origin != null && i < origin.size())
				b = origin.get(i);
			
			sum.add(a+b);
		}	
		st.setBranchExecutionCount(sum);
	}
	
	public void updateCentroid(int n)
	{
		if (n ==0 )return;
		
		for (Stmt st:stmts)
		{
			if (st.isPredicate)
			{
				st.setTimes(st.getTimes()/n);
				List<Integer> list = st.getBranchExecutionCount();
				for (int i = 0; i < list.size(); i++)
				{
					list.set(i, list.get(i)/n);
				}
			}
		}
	}

}
