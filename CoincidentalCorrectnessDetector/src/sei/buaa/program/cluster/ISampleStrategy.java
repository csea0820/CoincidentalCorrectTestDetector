/**
 * @Author csea
 * @Date 2013-6-27 下午8:15:29
 */
package sei.buaa.program.cluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISampleStrategy {
	Set<Integer> sample(Map<Integer,Set<Integer>> cluster,List<sei.buaa.program.analyzer.TestCase> tests);
}
