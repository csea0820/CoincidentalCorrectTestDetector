/**
 * @Author csea
 * @Date 2013-6-27 下午8:17:01
 */
package sei.buaa.program.cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SampleByFailedTests implements ISampleStrategy {


	@Override
	public Set<Integer> sample(Map<Integer, Set<Integer>> cluster,List<sei.buaa.program.analyzer.TestCase> tests) {

		
		Set<Integer> result = new HashSet<Integer>();

		Set<Integer> failedTestCaseIDS = new TreeSet<Integer>();
		for (sei.buaa.program.analyzer.TestCase test : tests)
			if (test.isPassed() == false)
				failedTestCaseIDS.add(test.getId());
		
		for (Integer key : cluster.keySet()) {
			Set<Integer> set = cluster.get(key);
			for (Integer failedTestID : failedTestCaseIDS) {
				if (set.contains(failedTestID)) {
					result.addAll(set);
					break;
				}
			}
		}

		for (Integer v : failedTestCaseIDS)
			if (result.contains(v))
				result.remove(v);

		return result;

	}
}
