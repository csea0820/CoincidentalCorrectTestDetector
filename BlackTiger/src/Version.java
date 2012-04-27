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
	
	List<TestCase> passTestCases = null;
	List<TestCase> failedTestCases = null;
	
	List<TestCaseSimilarity> tcs = null;
	
	int numberOfPredicates = 0;
	int numberOfLine = 0;
	
	int m_coincidentalCorrectnessTotalFound = 0;
	int m_coincidnetCorrectnessTotalCnt = 0;
	int m_totalChoosenSize = 0;
	double m_firstNClustersPercentage = 0.1;
	double m_suspiciousRange = 3;
		
	public int getm_coincidentalCorrectnessTotalFound() {
		return m_coincidentalCorrectnessTotalFound;
	}

	public int getTotalClusterSize() {
		return m_totalChoosenSize;
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
		//calNumberOfLine(failedTestCases);
		System.out.println( "\nProgram name:" + program+"\n"+
							"Version:" + versionID +"\n"+
							"Number of Predicates:"+numberOfLine+"\n"+
							"Total test cases:" +(passTestCases.size()+failedTestCases.size())+"\n"+
							"Failed test cases:"+failedTestCases.size()+"\n"+
							"Coincidental Corret test cases:"+m_coincidnetCorrectnessTotalCnt+"\n");
	}
	
	public void go(String testCaseResultDir,String CCResultDir)
	{	
		File file = new File(testCaseResultDir);
		String[] files = file.list();
		
		for (int i = 0; i < files.length; i++)
		{
			readTestResult(file+"/"+files[i]);
		}		
		readCoincidentalCorrectness(CCResultDir);		
		versionInfoAboutTestSet();
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
			e.printStackTrace();
		}
	}
	
	private void clusterMethod()
	{		
		int K = 10;
		if (program.equals("tot_info"))K = 10;
		else if (program.equals("schedule"))K = 17;
		else K = 18;
				
//		System.out.println("Value of K:"+K);
		KMeanCluster KMean = new KMeanCluster(passTestCases,K);
		KMean.setFailedTestCases(failedTestCases);
//		List<List<TestCase> > result = KMean.cluster();
//		List<SumCluster> ret = getSumCluster(result);
		List<SumCluster> ret = KMean.cluster();		
//		suspiciousSample(ret,(int)(Math.ceil(K*m_firstNClustersPercentage)));
		evaluateSumCluster(ret);
		suspiciousSample(ret);
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
		
		m_totalChoosenSize += ret.get(minIndex).getSize();
		m_coincidentalCorrectnessTotalFound += ret.get(minIndex).getCcCount();
	}
	
	private void suspiciousSample(List<SumCluster> ret,int n)
	{
		n = Math.max(1, n);
		n = Math.min(n, ret.size());

		Collections.sort(ret);
		//evaluateSumCluster(ret);
		resetArguments();
		for (int i = 0; i < n; i++)
		{
			m_totalChoosenSize += ret.get(i).getSize();
			m_coincidentalCorrectnessTotalFound += ret.get(i).getCcCount();
		}
	}
	
	private void suspiciousSample(List<SumCluster> ret)
	{
		resetArguments();
		Collections.sort(ret);
		m_totalChoosenSize += ret.get(0).getSize();
		m_coincidentalCorrectnessTotalFound += ret.get(0).getCcCount();
		float susp = ret.get(0).getSuspiciousDistance();
		m_suspiciousRange = susp*0.03;
		for (int i =  1; i < ret.size(); i++)
		{
			if (Math.abs(ret.get(ret.size()*2/3).getSuspiciousDistance() - susp) < m_suspiciousRange)
				continue;
			
			float s = ret.get(i).getSuspiciousDistance();
			if (Math.abs(susp-s) < m_suspiciousRange)
			{
				m_totalChoosenSize += ret.get(i).getSize();
				m_coincidentalCorrectnessTotalFound += ret.get(i).getCcCount();
			}
		}
	}
	
	private void resetArguments()
	{
		m_totalChoosenSize = 0;
		m_coincidentalCorrectnessTotalFound = 0;
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
		
		m_totalChoosenSize = totalSize;
		m_coincidentalCorrectnessTotalFound = cc;
	}
	
	public void showSampleResults()
	{
		Utility.calClusterResults(m_totalChoosenSize, m_coincidentalCorrectnessTotalFound, m_coincidnetCorrectnessTotalCnt, passTestCases.size());
	}
	
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
	}
	
	private void readTestResult(String file)
	{
		BufferedReader br = null;
		FileReader fr = null;
		String str = null;
		
		TestCase tc = new TestCase();
		
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
						m_coincidnetCorrectnessTotalCnt++;
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
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

	private void calNumberOfLine(List<TestCase> list)
	{
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

	public int getM_coincidentalCorrectnessTotalFound() {
		return m_coincidentalCorrectnessTotalFound;
	}

	public void setM_coincidentalCorrectnessTotalFound(
			int m_coincidentalCorrectnessTotalFound) {
		this.m_coincidentalCorrectnessTotalFound = m_coincidentalCorrectnessTotalFound;
	}

	public int getM_coincidnetCorrectnessTotalCnt() {
		return m_coincidnetCorrectnessTotalCnt;
	}

	public void setM_coincidnetCorrectnessTotalCnt(
			int m_coincidnetCorrectnessTotalCnt) {
		this.m_coincidnetCorrectnessTotalCnt = m_coincidnetCorrectnessTotalCnt;
	}

	public int getM_totalChoosenSize() {
		return m_totalChoosenSize;
	}

	public void setM_totalChoosenSize(int m_totalChoosenSize) {
		this.m_totalChoosenSize = m_totalChoosenSize;
	}
	
	public int getPassTestSize()
	{
		return passTestCases.size();
	}

}
