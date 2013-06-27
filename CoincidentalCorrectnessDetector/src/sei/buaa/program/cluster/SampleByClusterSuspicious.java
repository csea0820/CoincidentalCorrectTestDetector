/**
 * @Author csea
 * @Date 2013-6-27 下午8:35:39
 */
package sei.buaa.program.cluster;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SampleByClusterSuspicious implements ISampleStrategy {

	@Override
	public Set<Integer> sample(Map<Integer, Set<Integer>> cluster,
			List<sei.buaa.program.analyzer.TestCase> tests) {
		
		Set<Integer> result = new TreeSet<Integer>();
		
		int clusterSize = cluster.keySet().size();
		double []scores = new double[clusterSize+1];
		Arrays.fill(scores, 0);
		
		
		
		return result;
	}

}
