package sei.buaa.program.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sei.buaa.program.analyzer.Statement;
import sei.buaa.program.utility.FileUtility;


public class ArffStatementGenerator {

	private int totalFailedTestCaseCnt = 0;
	private int totalPassedTestCaseCnt = 0;
	private int totalExecutableCodeCnt = 0;
	private String programDir;
	String program = null;
	int versionID;
	AttributeFilter af = null;
	private boolean pruneAttributes = false;
	private boolean failedTestsArffGenerate = true;
	private double K = 1;
	public List<TestCase> addCoincidentalCorrectnessInfo(List<TestCase> list,
			String ccFilePath) {
		Set<Integer> set = new HashSet<Integer>();
		File file = new File(ccFilePath);
		if (file.exists()) {
			BufferedReader br = null;
			FileReader fr = null;
			String str;

			try {
				fr = new FileReader(file);
				br = new BufferedReader(fr);

				str = br.readLine();

				while (!StringUtility.IsNullOrEmpty(str)) {
					int testCaseId = Integer.parseInt(str);
					testCaseId++;
					set.add(testCaseId);

					str = br.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("CCFile doesn't exist!");
		}


		return list;
	}

	public void parser(String gcovDir) {

		File dir = new File(gcovDir);

		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				parseTestCase(file.getAbsolutePath());
			}
		} else {
			System.err.println(gcovDir + " is not a directory!");
		}
	}

	private sei.buaa.program.analyzer.TestCase parseTestCase(String gcovFile)
	{
		String fileName = StringUtility.getBaseName(gcovFile);
		int passed = StringUtility.getDigit(fileName, fileName.length() - 1);
		int testCaseId = StringUtility.getDigit(fileName, fileName.length()-3);

		if (passed == 1)	
			totalFailedTestCaseCnt++;
		else
			totalPassedTestCaseCnt++;

		sei.buaa.program.analyzer.TestCase  testCase = new sei.buaa.program.analyzer.TestCase();
		testCase.setPassed(passed == 1 ? false : true);
		testCase.setId(testCaseId);

		BufferedReader br = null;
		FileReader fr = null;
		String str;
		totalExecutableCodeCnt = 0;

		try {
			fr = new FileReader(new File(gcovFile));
			br = new BufferedReader(fr);

			str = br.readLine();
			Statement st = null;
			while (str != null) {
				if (str.charAt(8) == '#') {
					str = str.replaceFirst("#####", "    0");
				}

				if (str.length() > 16 && str.charAt(15) == '-' && str.substring(16, 21).equals("block")) {
					st.setBlock(true);
				} else if (str.charAt(8) != '-' && str.charAt(9) == ':') {

					String[] strs = str.split(":");
					if (strs[2].trim().equals("{")) {
						str = br.readLine();
						continue;
					}
					totalExecutableCodeCnt++;
					int lineNumber = Integer.parseInt(strs[1].trim());
					int times = Integer.parseInt(strs[0].trim());

					st = new Statement(lineNumber, times);
					testCase.addStatement(st);
				}
				// read another line
				str = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (passed == 1 && pruneAttributes == true)
		{
			List<Integer> list = new ArrayList<Integer>();
			for (Statement s : testCase.getStatements())
				if (s.isExecuted())
					list.add(s.getLineNumber());
			af.count(list);
		}
		
		if (failedTestsArffGenerate == false && passed == 1)return null;
		return  testCase;
	}
	
	public void arffGenerator(String programDir)
	{
		this.programDir = programDir;
		program = StringUtility.getBaseName(programDir);
		File dir = new File(programDir+"/outputs");
		
		File[] files = dir.listFiles();
		for (File file : files) {
			af = new AttributeFilter();
			af.setK(K);
			versionID = Integer.valueOf(file.getName().substring(1));
			generator(file.getAbsolutePath());		
		}
	}
	
	public void generator(String programDir)
	{		
		File dir = new File(programDir);

		List<sei.buaa.program.analyzer.TestCase> tcs = new ArrayList<sei.buaa.program.analyzer.TestCase>();
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				sei.buaa.program.analyzer.TestCase tc = parseTestCase(file.getAbsolutePath());
					if (tc != null)
						tcs.add(tc);
			}
		} else {
			System.err.println(programDir + " is not a directory!");
		}
		
		generateArff(tcs);
	}

	private void generateArff(List<sei.buaa.program.analyzer.TestCase> tcs) {
		StringBuilder builder = new StringBuilder();
		builder.append("@relation "+program+versionID+"\n");
		
		int attrCnt = af.getAttributeCount();
		if (pruneAttributes == false)attrCnt = totalExecutableCodeCnt;
//		System.out.println("attribute size = " + attrCnt + ",totalFailedTests = " + totalFailedTestCaseCnt);
//		int attrCnt = totalExecutableCodeCnt;
		
		builder.append("@attribute ID integer\n");
		for (int i = 0; i < attrCnt; i++)
			builder.append("@attribute A"+i+" real\n");
		
		builder.append("@data\n");
		for (int i = 0; i < tcs.size(); i++)
		{
			builder.append(tcs.get(i).getId());
			for (int j = 0; j < tcs.get(i).getStatements().size(); j++)
			{
				if (af.filter(tcs.get(i).getStatements().get(j).getLineNumber()) == false && pruneAttributes == true)
					builder.append(","+tcs.get(i).getStatements().get(j).getCount());
				else if (pruneAttributes == false)
					builder.append(","+tcs.get(i).getStatements().get(j).getCount());
			}
			builder.append("\n");
		}
		FileUtility.writeContentToFile(builder.toString(),programDir+"/output_statement_arff/v"+versionID+".arff");
	}
	
	public void setPruneAttributes(boolean prune)
	{
		this.pruneAttributes = prune;
	}
	
	public int getTotalFailedTestCaseCnt() {
		return totalFailedTestCaseCnt;
	}

	public int getTotalPassedTestCaseCnt() {
		return totalPassedTestCaseCnt;
	}

	public int getTotalExecutableCodeCnt() {
		return totalExecutableCodeCnt;
	}
	
	public void setK(double k) {
		K = k;
	}
	
	public void setFailedTestsArffGenerate(boolean failedTestsArffGenerate) {
		this.failedTestsArffGenerate = failedTestsArffGenerate;
	}
	
	public static void main(String[] args) {
		
		if (args.length == 0)
		{
			System.out.println("Invalid Usage!");
			System.out.println("Usage: ArffGenerator [programDir1 programDir2 ...]");
		}
		else 
		{
			for (String s: args)
			{
				System.out.println("Analyzing Program " + s);
				ArffStatementGenerator asg = new ArffStatementGenerator();
				asg.setPruneAttributes(false);
				asg.setFailedTestsArffGenerate(false);
				asg.arffGenerator(s);
			}
		}
	}
}
