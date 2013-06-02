package sei.buaa.program.analyzer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sei.buaa.program.cluster.StringUtility;


public class Parser {

	private Set<Integer> faults = null;
	private int totalFailedTestCaseCnt = 0;
	private int totalPassedTestCaseCnt = 0;
	private int totalExecutableCodeCnt = 0;

	public List<TestCase> parser(String gcovDir) {
		File dir = new File(gcovDir);

		List<TestCase> tests = new ArrayList<TestCase>();
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				tests.add(gcovParser(file.getAbsolutePath()));
			}
		} else {
			System.err.println(gcovDir + " is not a directory!");
		}
		
		return tests;
	}

	private TestCase gcovParser(String gcovFile) {

		String fileName = StringUtility.getBaseName(gcovFile);
		int passed = StringUtility.getDigit(fileName, fileName.length() - 1);
		int testCaseId = StringUtility.getDigit(fileName, fileName.length()-3);

		if (passed == 1)
			totalFailedTestCaseCnt++;
		else
			totalPassedTestCaseCnt++;

		TestCase testCase = new TestCase();
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
					if (times != 0)
						testCase.incrementExecutedStatements();
					
//					addStatementSum(st, passed == 1 ? false : true);
					
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
		
		return testCase;
	}

	
//	private void addStatementSum(Statement s, boolean testCaseResult) {
//		
//		
//		StatementSum eSum = map.get(s.getLineNumber());
//		if (eSum == null) {
//			eSum = new StatementSum(s.getLineNumber());
//			map.put(s.getLineNumber(), eSum);
//		}
//		if (s.isExecuted()) {
//			if (testCaseResult)
//				eSum.incrementA10();
//			else
//				eSum.incrementA11();
//		}
//
//	}

	public static void main(String[] args) {
		Parser parser = new Parser();
		parser.faults = new HashSet<Integer>();
		parser.faults.add(215);
		parser.parser("/Users/csea/Documents/Experiment/Siemens/tot_info/outputs/v23");
		// parser.gcovParser("/Users/csea/Documents/Experiment/Siemens/schedule/outputs/v2/schedule.c.gcov_1797_0");
		// parser.gcovParser("/Users/csea/Documents/Experiment/Siemens/schedule/outputs/v2/schedule.c.gcov_2002_1");
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

	public void setFaults(Set<Integer> faults) {
		this.faults = faults;
	}

}
