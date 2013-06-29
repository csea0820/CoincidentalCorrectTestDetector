/**
 * @Author csea
 * @Date 2013-6-27 下午8:35:39
 */
package sei.buaa.program.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import sei.buaa.program.analyzer.Statement;

public class SampleByClusterSuspicious implements ISampleStrategy {

	class ClusterEntity implements Comparable<ClusterEntity> {
		Set<Integer> cluster = null;
		double suspiciousDistance = 0.0;

		public void setSuspiciousDistance(double suspiciousDistance) {
			this.suspiciousDistance = suspiciousDistance;
		}

		public Set<Integer> getCluster() {
			return cluster;
		}

		public void setCluster(Set<Integer> cluster) {
			this.cluster = cluster;
		}

		@Override
		public int compareTo(ClusterEntity o) {
			if (suspiciousDistance - o.suspiciousDistance < 0)
				return 1;
			else if (suspiciousDistance - o.suspiciousDistance > 0)
				return -1;
			return 0;
		}

		@Override
		public String toString() {
			return "ClusterEntity [clusterNumber=" + cluster.size()
					+ ", suspiciousDistance=" + suspiciousDistance + "]";
		}

	}

	@Override
	public Set<Integer> sample(Map<Integer, Set<Integer>> cluster,
			List<sei.buaa.program.analyzer.TestCase> tests) {

		List<ClusterEntity> clusters = new ArrayList<ClusterEntity>();

		Map<Integer, Integer> statistics = failedExecutionSummary(tests);

//		System.out.println(statistics);
		
		sei.buaa.program.analyzer.TestCase[] testCases = new sei.buaa.program.analyzer.TestCase[tests
				.size()+10];
		for (sei.buaa.program.analyzer.TestCase t : tests)
			testCases[t.getId()] = t;

		for (Integer clusterID : cluster.keySet()) {
			Set<Integer> entity = cluster.get(clusterID);
			double score = 0.0;
			for (Integer e : entity) {
				for (Statement s : testCases[e].getStatements()) {
					if (s.isExecuted()) {
						Integer susp = statistics.get(s.getLineNumber());
						if (susp != null)
							score += susp;
					}
				}
			}

			ClusterEntity ce = new ClusterEntity();
			ce.setSuspiciousDistance(score / entity.size());
			ce.setCluster(entity);
			clusters.add(ce);
		}

		Collections.sort(clusters);
//		for (ClusterEntity ce : clusters)
//			System.out.println(ce);

		return clusterSample(clusters);
	}

	private Set<Integer> clusterSample(List<ClusterEntity> list) {
		Set<Integer> result = new TreeSet<Integer>();

		double mostSuspScore = list.get(0).suspiciousDistance;
		result.addAll(list.get(0).getCluster());
		double suspiciousThreshold = mostSuspScore * 0.03;

		for (int i = 1; i < list.size(); i++) {
			if (Math.abs(list.get(list.size() * 2 / 3).suspiciousDistance
					- mostSuspScore) < suspiciousThreshold)
				continue;
			
			if (Math.abs(mostSuspScore - list.get(i).suspiciousDistance) < suspiciousThreshold)
			{
				result.addAll(list.get(i).getCluster());
			}
		}

		return result;
	}

	private Map<Integer, Integer> failedExecutionSummary(
			List<sei.buaa.program.analyzer.TestCase> tests) {
		Map<Integer, Integer> statistics = new TreeMap<Integer, Integer>();
		for (sei.buaa.program.analyzer.TestCase t : tests) {
			if (t.isPassed() == false) {
				for (Statement s : t.getStatements()) {
					if (s.isExecuted()) {
						Integer integer = statistics.get(s.getLineNumber());
						if (integer == null)
							integer = 0;
						statistics.put(s.getLineNumber(), integer + 1);
					}
				}
			}
		}
		return statistics;
	}

}
