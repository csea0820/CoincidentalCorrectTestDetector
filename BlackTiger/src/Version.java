import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import weka.core.Instances;

public class Version {

	int versionID;
	String program;
	//final String testCaseResultDir = "./testCaseExecutedStmts/v"
	
	List<TestCase> passTestCases = null;
	List<TestCase> failedTestCases = null;
	
	List<TestCaseSimilarity> tcs = null;
	
	int numberOfPredicates = 0;
	int numberOfLine = 0;
	
	int coincidentalCorrectnessFound = 0;
	int totalClusterSize = 0;
	
	final static String xp_path = "E:\\workspace_eclipse\\BlackTiger";
	
	int cnt = 0;
	
	int coincidnetCorrectnessCnt = 0;
	
	
	public int getCoincidentalCorrectnessFound() {
		return coincidentalCorrectnessFound;
	}

	public int getTotalClusterSize() {
		return totalClusterSize;
	}

	public int getVersionID() {
		return versionID;
	}

	public void setVersionID(int versionID) {
		this.versionID = versionID;
	}

	public Version()
	{
		passTestCases = new ArrayList<TestCase>();
		failedTestCases = new ArrayList<TestCase>();
		tcs = new ArrayList<TestCaseSimilarity>();
	}
	
	public void addTestCase(TestCase tc)
	{
		if (tc.isResult() == true)
			passTestCases.add(tc);
		else failedTestCases.add(tc);
	}
	
	private void versionInfoAboutTestSet()
	{
		System.out.println("--------------------------------------");
		calNumberOfLine(passTestCases);
		calNumberOfLine(failedTestCases);
		System.out.println( "\nProgram name:" + program+"\n"+
							"Version:" + versionID +"\n"+
							"Number of line:"+numberOfLine+"\n"+
							"Total test cases:" +(passTestCases.size()+failedTestCases.size())+"\n"+
							"Failed test cases:"+failedTestCases.size()+"\n"+
							"Coincidental Corret test cases:"+coincidnetCorrectnessCnt+"\n");
	}
	
	public void go(String testCaseResultDir,String CCResultDir)
	{
//		int lastV = testCaseResultDir.lastIndexOf("v");
//		versionID = Integer.parseInt(testCaseResultDir.substring(lastV+1));
		
//		System.out.println("program:"+program);
//		System.out.println("version:"+versionID);
//		
//		System.out.println("Begining read testResult");
		
		File file = new File(testCaseResultDir);
		String[] files = file.list();
		
		for (int i = 0; i < files.length; i++)
		{
			readTestResult(file+"/"+files[i]);
		}
		
		//System.out.println("Begining read CoincidentalCorrectness");
		
		readCoincidentalCorrectness(CCResultDir);

		
		versionInfoAboutTestSet();
		//generateArff(passTestCases);
		//rankMethod();
		//wekaSimpleKMeans();
		clusterMethod();
	}
	
	private void wekaSimpleKMeans()
	{
		try {
			Instances instances = readInstances();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Instances readInstances() throws FileNotFoundException, IOException
	{
		Instances instances = new Instances(new FileReader("C:\\Program Files\\Weka-3-6\\data\\tmp.arff"));
		
		return instances;
	}
	
	private int getAttrSize(TestCase tc)
	{
		int size = 0;
		for (int i = 0; i < tc.getStmts().size(); i++)
		{
			size += tc.getStmts().get(i).getBranchExecutionCount().size();
		}
		return size;
	}
	
	private void generateArff(List<TestCase> tcs)
	{
		
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("@relation "+program+versionID+"\n");
		
		int attrCnt = getAttrSize(tcs.get(0));
		for (int i = 0; i < attrCnt; i++)
		buffer.append("@attribute A"+i+" real\n");
		
		buffer.append("@data\n");
		for (int i = 0; i < tcs.size(); i++)
		{
			for (int j = 0; j < tcs.get(i).getStmts().size(); j++)
			{
				for (int k = 0; k < tcs.get(i).getStmts().get(j).getBranchExecutionCount().size();k++)
				{
					if (j == 0 && k ==0)
						buffer.append(tcs.get(i).getStmts().get(j).getBranchExecutionCount().get(k));
					else buffer.append(","+tcs.get(i).getStmts().get(j).getBranchExecutionCount().get(k));
				}
			}
			buffer.append("\n");
		}
		writeBufferToFile(program+versionID,buffer);
	}
	
	public void writeBufferToFile(String file,StringBuffer buffer)
	{
		try {
			FileWriter fileWriter = new FileWriter(file,false);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write(buffer.toString());
			
			bufferWriter.close();
			fileWriter.close();		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void clusterMethod()
	{
//		calNumberOfLine(passTestCases);
//		calNumberOfLine(failedTestCases);
//		System.out.println("Total Line:"+numberOfLine);
		
		int K = 10;
		if (program.equals("tot_info"))K = 7;
		else if (program.equals("schedule"))K = 17;
		else K = 18;
		
		
		System.out.println("Value of K:"+K);
		KMeanCluster KMean = new KMeanCluster(passTestCases,K);
		KMean.setFailedTestCases(failedTestCases);
//		List<List<TestCase> > result = KMean.cluster();
//		List<SumCluster> ret = getSumCluster(result);
		List<SumCluster> ret = KMean.cluster();
		evaluateSumCluster(ret);
		suspiciousSample(ret);
		//distanceSample(ret);
		//nPerClusterSample(ret,(int)(passTestCases.size()*0.3));
		//adaptiveSample(KMean.getCluster());
		
//		System.out.println("Passing Tests:"+passTestCases.size());
		
//		KMeanCluster KMean = new KMeanCluster(passTestCases,2);
//		List<List<TestCase> > result = KMean.cluster();
//		analysisCluster(result.get(0));
//		analysisCluster(result.get(1));
		//analysisCluster(result.get(2));
	}
	
	private void evaluateSumCluster(List<SumCluster> ret)
	{
		for (SumCluster sc:ret)
		{
			System.out.println("Cluster Size:"+sc.getSize());
			System.out.println("CC contained:"+sc.getCcCount());
			System.out.println("Variance:"+sc.getVariance());
			System.out.println("Distance:"+sc.getDistanceFromFailedTestCases());
			System.out.println("Suspicious Distance:" + sc.getSuspiciousDistance());
			System.out.println("Percetage:"+sc.getCcCount()*1.0/sc.getSize()+"\n\n");
		}
		System.out.println("--------------------------------------");
	}
	
	private void distanceSample(List<SumCluster> ret)
	{
		int minIndex = 0;
		for (int i  = 1; i < ret.size(); i++)
		{
			if (ret.get(i).getDistanceFromFailedTestCases() < ret.get(minIndex).getDistanceFromFailedTestCases())
				minIndex = i;
		}
		
		totalClusterSize += ret.get(minIndex).getSize();
		coincidentalCorrectnessFound += ret.get(minIndex).getCcCount();
	}
	
	private void suspiciousSample(List<SumCluster> ret)
	{
//		int maxIndex = 0;
//		float maxValue = ret.get(0).getSuspiciousDistance();
//		
//		for (int i = 1; i < ret.size(); i++)
//		{
//			if (ret.get(i).getSuspiciousDistance() > maxValue)
//			{
//				maxIndex = i;
//				maxValue = ret.get(i).getSuspiciousDistance();
//			}
//		}
//		
//		totalClusterSize += ret.get(maxIndex).getSize();
//		coincidentalCorrectnessFound += ret.get(maxIndex).getCcCount();
		Collections.sort(ret);
		totalClusterSize += ret.get(0).getSize();
		coincidentalCorrectnessFound += ret.get(0).getCcCount();
		totalClusterSize += ret.get(1).getSize();
		coincidentalCorrectnessFound += ret.get(1).getCcCount();
	}
	
	private void nPerClusterSample(List<SumCluster> ret,int N)
	{
		int totalSize = 0,cc = 0;
		
		for (SumCluster sc:ret)
		{
			if (totalSize+sc.getSize() <= N)
			{
				totalSize += sc.getSize();
				cc += sc.getCcCount();
			}
		}
		
		totalClusterSize += totalSize;
		coincidentalCorrectnessFound += cc;
		
		System.out.println("Choose Size:"+totalSize);
		System.out.println("CC in Cluster:"+cc);
		System.out.println("Percentage of CC in cluster:"+cc*1.0/totalSize);
		System.out.println("False Negative:"+(coincidnetCorrectnessCnt-cc)*1.0/coincidnetCorrectnessCnt);
		System.out.println("False Postive:"+(totalSize-cc)*1.0/(passTestCases.size()-coincidnetCorrectnessCnt));
		System.out.println("\n");
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
	
	
	private void adaptiveSample(List<List<TestCase> > result)
	{
		System.out.println("\nFollowing is result using Adaptive Sample Stratege to select Coincidental Correctness");
		int totalSize = 0;
		int ccSize = 0;
		Random r = new Random();
		int listSize = 0;
		
		for (List<TestCase> list:result)
		{
			listSize = list.size();
			if (listSize == 0)continue;
			int selection = Math.abs(r.nextInt()%listSize);
			//int selection = 0;
			if (list.get(selection).isCoincidentCorretness())
			{
				totalSize += listSize;
				for (TestCase tc:list)
					if (tc.isCoincidentCorretness())
						ccSize++;
			}
			else totalSize++;
		}
		totalClusterSize += totalSize;
		coincidentalCorrectnessFound += ccSize;
		System.out.println("Found CC in cluster:"+ccSize);
		System.out.println("Percentage of CC in cluster:"+ccSize*1.0/totalSize);
		System.out.println("False Negative:"+(coincidnetCorrectnessCnt-ccSize)*1.0/coincidnetCorrectnessCnt);
		System.out.println("False Postive:"+(totalSize-ccSize)*1.0/(passTestCases.size()-coincidnetCorrectnessCnt));
		System.out.println("\n");
	}
	
	
	private void analysisCluster(List<TestCase> list)
	{
		int baseNumber =  list.size();
		System.out.println("Cluster Size:"+baseNumber);
		int cnt = 0;
		for (int i = 0; i < baseNumber; i++)
		{
			if (list.get(i).isCoincidentCorretness())cnt++;
		}
		
		//System.out.println(baseNumber);
		System.out.println("Found CC in cluster:"+cnt);
		System.out.println("False Negative:"+(coincidnetCorrectnessCnt-cnt)*1.0/coincidnetCorrectnessCnt);
		System.out.println("False Postive:"+(baseNumber-cnt)*1.0/(passTestCases.size()-coincidnetCorrectnessCnt));
		System.out.println("\n");
	}
	
	private void rankMethod()
	{
		System.out.println("Begining rank testcase");
		rankTestCase();
		
		for (TestCaseSimilarity tc:tcs)
			System.out.println(tc.getName()+" "+tc.getSimilarity()+" "+tc.isCC());
		
		System.out.println("Analysis");
		System.out.println("This test suite cointains "+ coincidnetCorrectnessCnt + " CC");
		analysis(coincidnetCorrectnessCnt);
		
		analysis((int)(coincidnetCorrectnessCnt*1.5));
		analysis(coincidnetCorrectnessCnt*4);
		analysis(coincidnetCorrectnessCnt*6);
	}
	
	
	private void calSimilarity(TestCase tc)
	{
		int similarity = Integer.MAX_VALUE;
		
//		List<Stmt> passList = null;
//		List<Stmt> failedList = null;
		for (TestCase tt:failedTestCases)
		{
//			int i = 0,j = 0;
//			int tempSimilarity = 0;
//			passList = tc.getStmts();
//			failedList = tt.getStmts();
//			
//			while (i < passList.size() && j < failedList.size())
//			{
//				int lineNumber0 = passList.get(i).getLineNumber();
//				int iineNumber1 = failedList.get(j).getLineNumber();
//				
//				if (lineNumber0 == iineNumber1 && passList.get(i).isPredicate == true)
//				{
////					tempSimilarity += calDistance(passList.get(i).getTimes(),failedList.get(j).getTimes());
//					tempSimilarity += calPredicatesSimilarity(passList.get(i).getBranchExecutionCount(),
//							failedList.get(i).getBranchExecutionCount());
//					i++;
//					j++;
//				}
//				else if (lineNumber0 < iineNumber1)
//					i++;
//				else j++;
//			}
			int tempSimilarity = tt.calSimilarityOfTestCases(tc);
			similarity = Math.min(similarity, Math.abs(tempSimilarity));
			//similarity += tempSimilarity;
		}
		
		TestCaseSimilarity tcsTmp = new TestCaseSimilarity(tc.getName(),similarity);
		tcsTmp.setStmts(tc.getStmts());
		tcsTmp.setCC(tc.isCoincidentCorretness());
		tcs.add(tcsTmp);
	}
	
//	private int calSimilarityOfTestCases(TestCase tt, TestCase tc) {
//		List<Stmt> passList = null;
//		List<Stmt> failedList = null;
//
//		int i = 0, j = 0;
//		int tempSimilarity = 0;
//		passList = tc.getStmts();
//		failedList = tt.getStmts();
//
//		while (i < passList.size() && j < failedList.size()) {
//			int lineNumber0 = passList.get(i).getLineNumber();
//			int iineNumber1 = failedList.get(j).getLineNumber();
//
//			if (lineNumber0 == iineNumber1
//					&& passList.get(i).isPredicate == true) {
//				// tempSimilarity +=
//				// calDistance(passList.get(i).getTimes(),failedList.get(j).getTimes());
//				tempSimilarity += calPredicatesSimilarity(passList.get(i)
//						.getBranchExecutionCount(), failedList.get(i)
//						.getBranchExecutionCount());
//				i++;
//				j++;
//			} else if (lineNumber0 < iineNumber1)
//				i++;
//			else
//				j++;
//		}
//		
//		return tempSimilarity;
//	}
	

	
	private void rankTestCase()
	{
		//System.out.println("Calulate Similarity");
		for (TestCase tc:passTestCases)
		{
			calSimilarity(tc);
		}
		
		Collections.sort(tcs);
		
//		for (TestCaseSimilarity tcsy:tcs)
//			System.out.println(tcsy.getSimilarity());
		
	}
	
		
	private int calDistance(int times1,int times2)
	{
		return euclideanDistanceBySquare(times1,times2);
	}
//	
	private int euclideanDistanceBySquare(int a,int b)
	{
		return (a-b)*(a-b);
	}
//	
//	private int absoluteDistance(int times1,int times2)
//	{
//		return Math.abs(times1-times2);
//	}
//	
//	private int hamingDistance(int times1,int times2)
//	{
////		if (times1 == times2)return 1;
////		else return 0;
//		if ((times1 != 0 && times2 != 0) || (times2 == 0 && times1 == 0))return 1;
//		else return 0;
//	}
	
	private void analysis(int baseNumber)
	{
		baseNumber = Math.min(baseNumber, passTestCases.size());
		int cnt = 0;
		for (int i = 0; i < baseNumber; i++)
		{
			if (tcs.get(i).isCC())cnt++;
		}
		
		System.out.println(baseNumber);
		System.out.println("False Negative:"+(coincidnetCorrectnessCnt-cnt)*1.0/coincidnetCorrectnessCnt);
		System.out.println("False Postive:"+(baseNumber-cnt)*1.0/(passTestCases.size()-coincidnetCorrectnessCnt));
		System.out.println("\n");
	}
	
	private void readTestResult(String file)
	{
		BufferedReader br = null;
		FileReader fr = null;
		String str = null;
		
		TestCase tc = new TestCase();
		
		//System.out.println(++cnt);
		//System.out.println("file name:"+file);
		
		try {
			
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			str = br.readLine();
			tc.setName(str.split(":")[1]);
			
			str = br.readLine();
			tc.setResult(str.split(":")[1].equals("true"));
			
			str = br.readLine();
			Stmt stmt = null;
			while (str != null)
			{
				String[]  temp = str.split(" ");
				boolean isPredicate = Integer.parseInt(temp[2])==1;
				if (isPredicate)
				{
					tc.addStmt(stmt = new Stmt(Integer.parseInt(temp[0]),
							Integer.parseInt(temp[1])));
					
					stmt.setPredicate(true);
					int branchProfileCount = Integer.parseInt(temp[3]);
					int startIndex = 4;
					List<Integer> list  = new ArrayList<Integer>();
					while (branchProfileCount != 0)
					{
						list.add(Integer.parseInt(temp[startIndex++]));
						branchProfileCount--;
					}
					stmt.setBranchExecutionCount(list);
				}
				str = br.readLine();
			}
			
			if (tc.isResult())
				passTestCases.add(tc);
			else 
				{failedTestCases.add(tc);
				//System.out.println("Failed Test Case:" + tc.getName());
				}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println("finish reading");
		
	}
	
	private void readCoincidentalCorrectness(String file)
	{
		BufferedReader br = null;
		FileReader fr = null;
		String str = null;
		
		try {
			
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			str = br.readLine();
			while (str != null)
			{
				for (int i = 0; i < passTestCases.size(); i++)
				{
					if (passTestCases.get(i).getName().equals(str))
					{
						passTestCases.get(i).setCoincidentCorretness(true);
						coincidnetCorrectnessCnt++;
						break;
					}
				}
				str = br.readLine();
			}			
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void requireTestCase(String name)
	{
		for (TestCase tc: passTestCases)
			if (tc.getName().equals(name))
				tc.showInfo();
		for (TestCase tc:failedTestCases)
			if (tc.getName().equals(name))
				tc.showInfo();
	}
	
	//n0 for pass test case;n1 for failed test case
	public void compareTestCaseExecutionProfile(String n0,String n1)
	{
		TestCase tc0 = null;
		for (TestCase tc:passTestCases)
			if (tc.getName().equals(n0))
			{
				tc0 = tc;
				break;
			}
		
		TestCase tc1 = null;
		for (TestCase tc:passTestCases)
			if (tc.getName().equals(n1))
			{
				tc1 = tc;
				break;
			}
		
		int diff = 0;
		for (int i = 0; i < tc0.getStmts().size(); i++)
		{
			if (tc0.getStmts().get(i).isPredicate)
			{
//				System.out.println(tc0.getStmts().get(i).getTimes()+" "+
//						tc1.getStmts().get(i).getTimes());
				diff += calDistance(tc0.getStmts().get(i).getTimes(),
						tc1.getStmts().get(i).getTimes());
			}
		}
		System.out.println("Difference:"+diff);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		Version v = new Version();
		v.go(xp_path+args[0], xp_path+args[1]);
//		v.requireTestCase("tst31");
//		v.compareTestCaseExecutionProfile("tst31", "jk29");
//		v.compareTestCaseExecutionProfile("tst31", "jk29");
//		v.compareTestCaseExecutionProfile("tst31", "uslin.1283");
//		v.compareTestCaseExecutionProfile("tst31", "uslin.685");
//		v.compareTestCaseExecutionProfile("tst31", "newtst383.tst");
		
//		v.compareTestCaseExecutionProfile("uslin.378", "uslin.301");
//		v.compareTestCaseExecutionProfile("jk29", "ts696");
		
	}

	private void calNumberOfLine(List<TestCase> list)
	{
//		for (TestCase tc: list)
//		{
//			numberOfLine = Math.max(numberOfLine, tc.getStmts().size());
//		}
		numberOfLine =  list.get(0).getStmts().size();
	}
	
	
	private int calNumberOfPredicates(List<TestCase> list)
	{
		int ret = 0;
		
		for (TestCase tc : list)
		{
			int temp = 0;
			for (Stmt st:tc.getStmts())
				if (st.isPredicate)
					temp++;
			ret = Math.max(ret, temp);
		}
		return ret;
	}
	
	public int getNumberOfPredicates() {
		return numberOfPredicates=Math.max(calNumberOfPredicates(passTestCases),
				calNumberOfPredicates(failedTestCases));
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

}
