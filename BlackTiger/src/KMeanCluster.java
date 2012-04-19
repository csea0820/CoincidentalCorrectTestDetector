import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class KMeanCluster {
	
	List<TestCase> testcases = null;
	List<TestCase> failedTestCases = null;
	
	Map<Integer,Integer> map = null;
	
	int[] ownerCluster = null;
	int K = 0;
	Cluster[] clusters = null;
	
	int cntIter = 0;
	
	int numberOfLine;
	
	public KMeanCluster(List<TestCase> tcs,int K)
	{
		this.K = K;
		this.testcases = tcs;
		this.numberOfLine = tcs.get(0).getStmts().size();
		initCluster();
	}

	public List<TestCase> getTestcases() {
		return testcases;
	}

	public void setTestcases(List<TestCase> testcases) {
		this.testcases = testcases;
	}
	
	
	private void initCluster()
	{
		int size = testcases.size();
		
		clusters = new Cluster[K];
		ownerCluster = new int[size];
		
		Arrays.fill(ownerCluster, -1);
		
		Random random = new Random();
		for (int i = 0; i < K; i++)
		{
			int indx = 0;
			TestCase tc = testcases.get(indx = random.nextInt(size));
			//System.out.println(indx+"th testcase selected");
			clusters[i] = new Cluster(tc,indx);
		}
	}
	
	public List<List<TestCase> > getCluster()
	{
		List<List<TestCase> > result = new ArrayList<List<TestCase> >();
		for (int i = 0; i < K; i++)
		{
			result.add(clusters[i].getClusterElements());
		}
		return result;
	}
	
	public List<SumCluster> cluster()
	{	
		boolean change = true;
		int iter = 0;
		
		while (change)
		{
			change = false;
			//if ((++iter) >= 50)break;
			//System.out.println("Iteration "+ (iter));
			for (int i = 0; i < testcases.size(); i++)
			{
				int minSimi = Integer.MAX_VALUE;
				int index = -1;
				
				//对于点I，计算最近簇
				for (int j = 0; j < K; j++)
				{
					int sim = clusters[j].calSimilarity(testcases.get(i));
					
					if (minSimi > sim)
					{
						minSimi = sim;
						index = j;						
					}
				}
				
				//如果最近簇与原先簇不一样，则重新分配点i到新簇
				if (index != -1 && ownerCluster[i] != index)
				{
					change = true;
					ownerCluster[i] = index;
					clusters[index].addElement(i);
					for (int j = 0; j < K; j++)
					{
						if (j != index)
						{
							clusters[j].removeElement(i);
						}
					}
				}
			}
			
			//更新簇质心
			for (int i = 0; i < K; i++)
				clusters[i].updateCentroid();
			
		}
		
		
		List<SumCluster> ret = new ArrayList<SumCluster>();
		for (int i = 0; i < K; i++)
		{
			//result.add(clusters[i].getClusterElements());
			
			List<TestCase> tcs = clusters[i].getClusterElements();
			int cc = 0;
			for (TestCase tc:tcs)
			{
				if (tc.isCoincidentCorretness())cc++;
				
			}
			ret.add(new SumCluster(tcs.size(),cc,clusters[i].calVariance(),clusters[i].
					calDistanceFromFailedTestCases(),clusters[i].calSuspiciousDistance()));
			
//			int sse = 0;
//			for (TestCase tc:clusters[i].getClusterElements())
//			{
//				sse += clusters[i].calSimilarity(tc);
//			}
//			System.out.println("SSE:"+sse);
		}
		
		//debug();
		
		return ret;
	}
	
//	private List<SumCluster> getSumCluster(List<List<TestCase> > result)
//	{
//		List<SumCluster> ret = new ArrayList<SumCluster>();
//		
//		for (List<TestCase> tcs:result)
//		{
//			int size = tcs.size();
//			int cc = 0;
//			for (TestCase tc:tcs)
//				if (tc.isCoincidentCorretness())cc++;
//			ret.add(new SumCluster(size,cc));
//		}
//		
//		Collections.sort(ret);
//		
//		//for (SumCluster sc:ret)System.out.println(sc.getSize());
//		
//		return ret;
//	}
	
	private void debug()
	{
		int index = -1;
		if (clusters[0].size() < clusters[1].size())
			index = 0;
		else index = 1;
		
		List<TestCaseSimilarity> list = new ArrayList<TestCaseSimilarity>();
		
		for (TestCase tc: clusters[1-index].getClusterElements())
		{
			int sim = clusters[index].calSimilarity(tc);
			
			
			TestCaseSimilarity tcs = new TestCaseSimilarity();
			tcs.setCC(tc.isCoincidentCorretness());
			tcs.setSimilarity(sim);
			tcs.setName(tc.getName());
			tcs.setSim2(clusters[1-index].calSimilarity(tc));
			
			list.add(tcs);
		}
		
		Collections.sort(list);
		
		int K = 20;
		int num = 0;
		for (int i = 0; i < K; i++)
		{
			if (list.get(i).isCC())
			{
				num++;
				list.get(i).showInfo();
				System.out.println(list.get(i).getSim2());
			}
		}
		System.out.println("Ratio: " + num*1.0/K);
	}
	
	
	class Cluster {
		
		Set<Integer> elements = null;
		
		Centroid centroid = null;
		
		public Cluster(TestCase tc, int indx)
		{
			elements = new HashSet<Integer>();
			elements.add(indx);

			centroid = new Centroid(numberOfLine);
			centroid.accumulatePredicate(tc);
//			centroid.clear();
//			for (Stmt st:tc.getStmts())
//			{
//				if (st.isPredicate)
//				{
//					centroid.accumulatePredicate(st.getLineNumber(), st.getTimes(), 
//							st.getBranchExecutionCount());
//					
//				}
//			}
			//centroid.setStmts(stmts);
		}
		
		public List<TestCase> getClusterElements()
		{
			List<TestCase> list = new ArrayList<TestCase>();
			
			for (Integer integer:elements)
			{
				list.add(testcases.get(integer));
			}
			
//			if (list.size() < 200)
//				for (TestCase tc:list)
//					System.out.println(tc.getName());
			
			return list;
		}
		
		public float calVariance()
		{
			int res = 0;
			
			for (Integer integer:elements)
			{
				res += centroid.calVarianceOnEachTestCase(testcases.get(integer));
			}
			
			return (res*1.0f)/elements.size();
		}
		
		public float calDistanceFromFailedTestCases()
		{
			long res = 0;
			for (Integer integer:elements)
			{
				for (TestCase tc : failedTestCases)
				{
					res += tc.calSimilarityOfTestCases(testcases.get(integer));
					if (res < 0)System.err.println("long overflow in calDistanceFromFailedTestCases");
				}
			}
			return (res*1.0f)/elements.size();
		}
		
		public float calSuspiciousDistance()
		{
			int res = 0;
			for (Integer integer:elements)
			{
				TestCase t = testcases.get(integer);
				List<Stmt> stmts = t.getStmts();
				
				for (Stmt st:stmts)
				{
					if (st.getTimes()!= 0)
						if (map.containsKey(st.getLineNumber()))
							res += map.get(st.getLineNumber());
				}
			}
			
			return (res*1.0f)/elements.size();
		}
		
		
		public int calSimilarity(TestCase tc)
		{
			return centroid.calSimilarityOfTestCases(tc);
		}
			
		public boolean contains(Integer integer)
		{
			return elements.contains(integer);
		}
		
		public void addElement(Integer integer)
		{
			if (!elements.contains(integer))
				elements.add(integer);
		}
		
		public void removeElement(Integer integer)
		{
			elements.remove(integer);
		}
			
		public void updateCentroid()
		{
			centroid.clear();
			
			for (Integer index : elements)
			{
				TestCase tc = testcases.get(index);
				centroid.accumulatePredicate(tc);
//				for (Stmt st: tc.getStmts())
//				{
//					if (st.isPredicate)
//						centroid.accumulatePredicate(st.getLineNumber(), st.getTimes(), 
//								st.getBranchExecutionCount());
//						
//				}
			}
			
			centroid.updateCentroid(elements.size());
		}
		
		public int size()
		{
			return elements.size();
		}
	}


	public List<TestCase> getFailedTestCases() {
		return failedTestCases;
	}

	public void setFailedTestCases(List<TestCase> failedTestCases) {
		this.failedTestCases = failedTestCases;
		
		map = new HashMap<Integer,Integer>();
		int line = -1;
		int value = -1;
		for (TestCase tc:failedTestCases)
		{
			List<Stmt> stmts = tc.getStmts();
			for (Stmt st:stmts)
			{
				if (st.getTimes() != 0)
				{
				line = st.getLineNumber();
				if (!map.containsKey(line))
					map.put(line, 1);
				else 
				{
					value = map.get(line);
					map.remove(line);
					value++;
					map.put(line, value);
				}
				}
			}
		}
		
//		for (Integer it:map.keySet())
//			System.out.println("Key = " + it + ",Value = " + map.get(it));
	}
}
