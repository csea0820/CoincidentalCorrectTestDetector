package sei.buaa.program.analyzer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sei.buaa.program.utility.FileUtility;

public class Version {

	private int totalExecutableCode;
	private int versionId;
	private String name;
	private int totalPassedCount;
	private int totalFailedCount;
	private double totalWeights = 0;
	Set<Integer> faults = null;
	public int examineEffort;
	private double expensive;
	private String technique;
	

	public Version() {
		faults = new HashSet<Integer>();
	}


	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addFault(int lineNumber) {
		faults.add(lineNumber);
	}

	public String getFaultInfo(Map<Integer, StatementSum> map) {
		String res = "";
		for (Integer fault : faults) {
			res += "faultLocation:" + fault;
			StatementSum sum = map.get(fault);
			if (sum != null) {
				res += " ExecutionInfo:[a00=" + sum.getA00() + ",a10="
						+ sum.getA10() + ",a01=" + sum.getA01() + ",a11="
						+ sum.getA11() + "]";
			}
			res += "\n";
		}
		return res;
	}

	public int getFault() {
		Iterator<Integer> iterator = faults.iterator();
		return iterator.next();
	}

	public int getFaultNumber() {
		return faults.size();
	}

	public int getTotalPassedCount() {
		return totalPassedCount;
	}

	public int getTotalFailedCount() {
		return totalFailedCount;
	}

	public void addFaults(Map<Integer, List<Integer>> map) {
		for (int f : map.get(versionId))
			faults.add(f);
	}

	public void calcExamineEffort(List<AbstractSuspiciousness> list) {
		examineEffort = 0;
		for (int i = 0; i < list.size(); i++) {
			examineEffort++;
			if (faults.contains(list.get(i).getLineNumber())) {
				int j = i + 1;
				while (j < list.size()
						&& list.get(i).getSusp() == list.get(j).getSusp()) {
					j++;
					examineEffort++;
				}
				break;
			}
		}
//		System.out.println("totalExecutableCode:"+totalExecutableCode);
		expensive = examineEffort * 1.0 / totalExecutableCode;
	}

	public void writeResultToFile(List<AbstractSuspiciousness> list, String path,
			String fl) {
		StringBuilder sb = new StringBuilder();
		sb.append("lineNumber").append("\t").append("suspiciousness\n");
		for (AbstractSuspiciousness s : list) {
			sb.append(s.getLineNumber()).append("\t").append(s.getSusp())
					.append("\n");
		}
		FileUtility.writeContentToFile(sb.toString(), path + "/" + fl + "_v"
				+ versionId);
	}

	public String toString() {
		return "[technique=" + technique + ",examineEffort="
				+ examineEffort + ",expensive=" + examineEffort * 1.0
				/ totalExecutableCode + "]";
	}
	
	public String getVersionInfo()
	{
		return "[program=" + name + ",version="
				+ versionId + ",totalPassedCount=" + totalPassedCount
				+ ",totalFailedCount=" + totalFailedCount + "]";
	}

	public int getTotalExecutableCode() {
		return totalExecutableCode;
	}

	public void setTotalExecutableCode(int totalExecutableCode) {
		this.totalExecutableCode = totalExecutableCode;
	}

	public double getExpensive() {
		return expensive;
	}

	public void setTechnique(String technique) {
		this.technique = technique;
	}



	public void setTotalPassedCount(int totalPassedCount) {
		this.totalPassedCount = totalPassedCount;
	}



	public void setTotalFailedCount(int totalFailedCount) {
		this.totalFailedCount = totalFailedCount;
	}


	public double getTotalWeights() {
		return totalWeights;
	}


	public void setTotalWeights(double totalWeights) {
		this.totalWeights = totalWeights;
	}


	public int getExamineEffort() {
		return examineEffort;
	}


	public Set<Integer> getFaults() {
		return faults;
	}

}
