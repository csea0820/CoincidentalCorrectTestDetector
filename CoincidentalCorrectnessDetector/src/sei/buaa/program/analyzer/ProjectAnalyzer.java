package sei.buaa.program.analyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sei.buaa.program.cluster.StringUtility;

public class ProjectAnalyzer {

	Map<Integer, List<Integer>> faults = null;
	List<Integer> versions = null;
	String programName;
	String programDir;

	private int totalVersions;
	private int singleFaultVersionsCnt;
	private int multiFaultsVersionsCnt;
	private int nonFaultVersionsCnt;
	private int analyzeVersionsCnt;
	int versionsOfIndividualProgramsCnt = 0;
	int totalTestsCnt = 0;
	private StringBuilder diagnosisContent = new StringBuilder(10000);

	private StringBuilder expenseSummary = null;

	private StringBuilder tscoreReductionInfo = new StringBuilder();
	
	SiemensAnalyzer sa;

	public ProjectAnalyzer(SiemensAnalyzer sa, String programDir,
			boolean coincidentalCorrectnessEnable,
			boolean coincidentalCorrectnessAbandon) {
		this.programDir = programDir;
		this.sa = sa;
		this.expenseSummary = sa.getExpenseSummary();
		totalVersions = 0;
		singleFaultVersionsCnt = 0;
		nonFaultVersionsCnt = 0;
		analyzeVersionsCnt = 0;
		programName = StringUtility.getBaseName(programDir);
		faults = new HashMap<Integer, List<Integer>>();
		versions = new ArrayList<Integer>();
		init();
	}

	private void init() {
		getVersionsInfo(programDir + Constant.VERSIONS_LIST);
		getFaultLocation(programDir + Constant.FAULTS_LIST);
	}

	public void analyze() {
		versionsOfIndividualProgramsCnt = 0;
		totalTestsCnt = 0;
		tscoreReductionInfo.append(programName).append(",RELABEL").append(",DISCARD")
		.append(",IDEAL_REALBEL").append(",IDEAL_DISCARD").append("\n");	
		for (int vid : versions) {
			Parser parser = new Parser();
			Version v = new Version();
			v.setName(programName);
			v.setVersionId(vid);
			v.addFaults(faults);
			parser.setFaults(v.getFaults());
			totalVersions++;
			// System.out.println("version:"+vid);

			diagnosisContent.append("\n");
			if (v.getFaultNumber() > 1) {
				multiFaultsVersionsCnt++;
				diagnosisContent.append(
						"program=" + programName + ",version=" + vid
								+ " is a mutil-faults version").append("\n");
				continue;
			} else if (v.getFaultNumber() < 1) {
				nonFaultVersionsCnt++;
				diagnosisContent.append(
						"program=" + programName + ",version=" + vid
								+ " is a non-fault version").append("\n");
				continue;
			} else
				singleFaultVersionsCnt++;

			List<TestCase> tests = parser.parser(programDir + "/"
					+ Constant.OUT_PUT_DIR + "/v" + vid);

			List<TestCase> ideal_CCTests = new ArrayList<TestCase>();
			for (TestCase t: tests)
			{
				ideal_CCTests.add((TestCase) t.clone());
			}
			
			sei.buaa.program.cluster.Version cv = new sei.buaa.program.cluster.Version(
					programDir, vid);
			Set<Integer> cc = cv.analyzeCoincidentalCorrectness();

			addCoincidentalCorrectInfo(tests, cc);
			addCoincidentalCorrectInfo(ideal_CCTests, cv.getCoincidentCorrectIDS());

			v.setTotalFailedCount(parser.getTotalFailedTestCaseCnt());
			v.setTotalPassedCount(parser.getTotalPassedTestCaseCnt());
			v.setTotalExecutableCode(parser.getTotalExecutableCodeCnt());

			analyzeVersion(v,tests,ideal_CCTests);	
		}

		System.out.println("Program_Name:" + programName + ",analyzeVersions:"
				+ versionsOfIndividualProgramsCnt + ",totalTests:"
				+ totalTestsCnt);
	}

	private void addCoincidentalCorrectInfo(List<TestCase> tests,
			Set<Integer> cc) {
		for (TestCase t : tests) {
			if (t.isPassed() && cc.contains(t.getId()))
				t.setCoincidentalCorrectness(true);
		}
	}

	private Map<Integer, StatementSum> generateStatementSum(
			List<TestCase> tests, int strategy) {

		Map<Integer, StatementSum> map = new HashMap<Integer, StatementSum>();
		for (TestCase test : tests) {
			for (Statement s : test.getStatements()) {
				StatementSum eSum = map.get(s.getLineNumber());
				if (eSum == null) {
					eSum = new StatementSum(s.getLineNumber());
					map.put(s.getLineNumber(), eSum);
				}
				if (s.isExecuted()) {
					if (test.isPassed()) {
						if (strategy == Constant.NO_ACTION
								|| test.isCoincidentalCorrectness() == false)
							eSum.incrementA10();
						else {
							switch (strategy) {
							case Constant.RELABLE:
								eSum.incrementA11();
								break;
							case Constant.DISCARD:
								break;
							}
						}
					} else {
						eSum.incrementA11();
					}
				} else {
					if (test.isPassed()) {
						if (strategy == Constant.NO_ACTION
								|| test.isCoincidentalCorrectness() == false)
							eSum.incrementA00();
						else {
							switch (strategy) {
							case Constant.RELABLE:
								eSum.incrementA01();
								break;
							case Constant.DISCARD:
								break;
							}
						}
					} else {
						eSum.incrementA01();
					}
				}
			}
		}

		return map;

	}

	public void analyzeVersion(Version v,List<TestCase> tests,List<TestCase> ideal_CCTests) {

		if (v.getTotalFailedCount() == 0) {
			diagnosisContent.append(
					"program=" + programName + ",version=" + v.getVersionId()
							+ " has no failed test cases").append("\n");
			return;
		}

		tscoreReductionInfo.append("v").append(v.getVersionId());
		
		analyzeVersionsCnt++;
		versionsOfIndividualProgramsCnt++;
		totalTestsCnt = v.getTotalFailedCount() + v.getTotalPassedCount();
		diagnosisContent.append(
				"\n\nanalyzing program " + programName + ",verions "
						+ v.getVersionId()).append("\n");
		diagnosisContent.append(v.getVersionInfo() + "\n");
		
		double t0 = calcSups(v,new TarantulaSusp(),tests,Constant.NO_ACTION,sa.getTarantulaExp());
		double t1 = calcSups(v,new TarantulaSusp(),tests,Constant.RELABLE,sa.getTarantulaRelabelExp());
		double t2 = calcSups(v,new TarantulaSusp(),tests,Constant.DISCARD,sa.getTarantulaDiscardExp());
		
		double t3 = calcSups(v,new TarantulaSusp(),ideal_CCTests,Constant.RELABLE,sa.getTarantulaRelabelExp_ideal());
		double t4 = calcSups(v,new TarantulaSusp(),ideal_CCTests,Constant.DISCARD,sa.getTarantulaDiscardExp_ideal());

		tscoreReductionInfo.append(",").append(t0-t1).append(",").append(t0-t2).append(",").append(t0-t3)
		.append(",").append(t0-t4).append("\n");
		
		
	}
	
	private double calcSups(Version v,ISuspsCalculator technique, List<TestCase> tests,int strategy,Expensive exp)
	{
		
		List<AbstractSuspiciousness> suspList = new ArrayList<AbstractSuspiciousness>();
		Map<Integer,StatementSum> map = generateStatementSum(tests, strategy);
		for (StatementSum eSum : map.values()) {
			addSuspToList(suspList, technique, eSum);
		}
		return rank(v, suspList, ISuspsCalculator.class.getName(),exp, map)*100;
	}

	private double rank(Version v, List<AbstractSuspiciousness> susp, String fl,
			Expensive exp, Map<Integer, StatementSum> map) {
		Collections.sort(susp);

		v.calcExamineEffort(susp);
		v.writeResultToFile(susp, programDir + "/FL", fl);
		exp.addExpensive(v.getExpensive());
		v.setTechnique(fl);
		diagnosisContent.append(v);
		StatementSum sum = map.get(susp.get(0).getLineNumber());
		if (sum != null)
			diagnosisContent
					.append("\nMostSuspStatement:" + sum.getLineNumber())
					.append("\n").append(sum).append("\n\n");

		return v.getExpensive();
	}

	public void getFaultLocation(String faultFile) {
		BufferedReader br = null;
		FileReader fr = null;
		String str;
		try {
			fr = new FileReader(faultFile);
			br = new BufferedReader(fr);

			str = br.readLine();
			while (str != null) {
				String[] nums = str.split("\\s+");
				int versionId = Integer.parseInt(nums[0]);
				List<Integer> fault = new ArrayList<Integer>();
				for (int i = 1; i < nums.length; i++) {
					fault.add(Integer.parseInt(nums[i]));
				}

				faults.put(versionId, fault);
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

		// for (Integer key : faults.keySet())
		// {
		// for (Integer value : faults.get(key))
		// {
		// System.out.println("version="+key+",fault="+value);
		// }
		// }
	}

	private void addSuspToList(List<AbstractSuspiciousness> list,
			ISuspsCalculator suspsCalc, StatementSum eSum) {
		AbstractSuspiciousness susp = new AbstractSuspiciousness(suspsCalc);
		susp.calcSups(eSum);
		susp.setLineNumber(eSum.getLineNumber());
		list.add(susp);
	}

	private void getVersionsInfo(String versionFile) {
		BufferedReader bufferReader = null;
		String str = null;

		try {
			bufferReader = new BufferedReader(new FileReader(versionFile));
			str = bufferReader.readLine();

			while (str != null) {
				versions.add(Integer.parseInt(str));
				str = bufferReader.readLine();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
	}

	public int getTotalVersions() {
		return totalVersions;
	}

	public int getSingleFaultVersions() {
		return singleFaultVersionsCnt;
	}

	public int getMultiFaultsVersions() {
		return multiFaultsVersionsCnt;
	}

	public int getNonFaultVersions() {
		return nonFaultVersionsCnt;
	}

	public int getAnalyzeVersions() {
		return analyzeVersionsCnt;
	}

	public StringBuilder getDiagnosisContent() {
		return diagnosisContent;
	}

	public StringBuilder getTscoreReductionInfo() {
		return tscoreReductionInfo;
	}

}
