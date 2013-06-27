package sei.buaa.program.cluster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author csea
 * @Date 2013-6-1 下午3:29:54
 */

public class Version {

	
	private String programName = null;
	private String programDIR = null;
	private int versionID;
	
	Set<Integer> failedTestCaseIDS = null;
	Set<Integer> passedTestCaseIDS = null;
	
	Set<Integer> coincidentCorrectIDS = null;
	
	int m_returned_relevant_tests =0;
	int m_all_relevant_test = 0;
	int m_returned_tests = 0;
	
	double false_positive = 0.0;
	double false_negative = 0.0;
	
	ISampleStrategy  sampleStrategy = null;	
	
	public Version(String programDIR,int versinID,ISampleStrategy sampleStrategy)
	{
		this.programDIR = programDIR;
		this.versionID = versinID;
		this.programName = StringUtility.getBaseName(programDIR);
		this.sampleStrategy = sampleStrategy;
		
		failedTestCaseIDS = new HashSet<Integer>();
		passedTestCaseIDS = new HashSet<Integer>();
		coincidentCorrectIDS = new HashSet<Integer>();
	}
	
	//使用聚类算法进行聚类，通过采样算法选择聚类产生的簇作为结果并返回。同时计算false positive及false negative。
	public Set<Integer> analyzeCoincidentalCorrectness(List<sei.buaa.program.analyzer.TestCase> tests)
	{
		readTestCaseResult(programDIR+"/outputs_predicate/v"+versionID);
		readCoincidentalCorrectTests(programDIR+"/coincidentalCorrectness/coincidentalCorrectness_v"+versionID);
		ClusteringAnalysis ca = new ClusteringAnalysis();
		Map<Integer,Set<Integer>> cluster = ca.cluster(programDIR+"/output_statement_arff/"+programName+"_v"+versionID+".arff");	
		Set<Integer> result = sampleStrategy.sample(cluster,tests);
		m_returned_tests = result.size();
		m_all_relevant_test = coincidentCorrectIDS.size();
		
		m_returned_relevant_tests = 0;
		for (Integer key : coincidentCorrectIDS)
		{
			if (result.contains(key))
				m_returned_relevant_tests++;
		}
		
//		System.out.println(failedTestCaseIDS);
//		System.out.println(result);
//		System.out.println(coincidentCorrectIDS);
		false_negative = (m_all_relevant_test-m_returned_relevant_tests)*1.0/m_all_relevant_test;
		false_positive = (m_returned_tests-m_returned_relevant_tests)*1.0/(passedTestCaseIDS.size()-m_all_relevant_test);
//		System.out.println("false negatives = " + false_negative);
//		System.out.println("false positives = " + false_positive);
		return result;
	}
	
	public String getResultInfo()
	{
		return programName+"_v"+versionID+","+false_positive+","+false_negative;
	}
	
		
	
	private void readCoincidentalCorrectTests(String resultDir)
	{
		
		FileInputStream fr = null;
		BufferedReader br = null;
		String str;
		
		try {
			fr = new FileInputStream(resultDir);
			br = new BufferedReader(new InputStreamReader(fr));

			str = br.readLine();

			while (str != null)
			{
				coincidentCorrectIDS.add(Integer.parseInt(str));
				str = br.readLine();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void readTestCaseResult(String resultDir)
	{
		
		File dir = new File(resultDir);

		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				String fileName = file.getName();
				
				int passed = StringUtility.getDigit(fileName, fileName.length() - 1);
				int testCaseId = StringUtility.getDigit(fileName, fileName.length()-3);
				
				if (passed == 0)
					passedTestCaseIDS.add(testCaseId);
				else 
					failedTestCaseIDS.add(testCaseId);
			}
		} else {
			System.err.println(resultDir + " is not a directory!");
		}
		
	}	
	public Set<Integer> getCoincidentCorrectIDS() {
		if (coincidentCorrectIDS == null)
			readCoincidentalCorrectTests(programDIR+"/coincidentalCorrectness/coincidentalCorrectness_v"+versionID);
		return coincidentCorrectIDS;
	}
		
	
	public static void main(String[] args)
	{
		Version v = new Version("/Users/csea/Documents/Experiment/Siemens/print_tokens",7, new SampleByFailedTests());
//		v.analyzeCoincidentalCorrectness();
	}
	
}
