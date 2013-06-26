/**
 * @Author csea
 * @Date 2013-6-20 下午8:24:48
 */
package sei.buaa.program.cluster;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class AttributeFilter {
	
	
	private int totalFailedCnt = 0;
	private double K = 0.6;
	
	class Entity implements Comparable<Entity>{
		int lineNumber;
		int executionCount;
		
		Entity(int lineNumber){
			this.lineNumber = lineNumber;
			executionCount = 0;
		}

		@Override
		public int compareTo(Entity o) {
			return executionCount - o.executionCount;
		}
	}
	
	Map<Integer,Entity> counter = new TreeMap<Integer,Entity>();
	Set<Integer> filter = null;
	
	
	public void count(List<Integer> executedLine)
	{
		for (Integer line : executedLine)
		{
			Entity e = counter.get(line);
			if (e == null)
			{
				e = new Entity(line);
				counter.put(line, e);
			}
			e.executionCount++;
		}
		totalFailedCnt++;
	}
	
	/** false:保留该属性；true:丢弃该属性 */
	public boolean filter(int lineNumber)
	{
		if (filter == null)
			initFilter();
		
		if (filter.contains(lineNumber))return false;
		return true;
	}
	
	private void initFilter()
	{	
		filter = new TreeSet<Integer>();
		int threshold = (int) (K*totalFailedCnt);
		for (Entity e : counter.values())
		{
			if (e.executionCount >= threshold)
				filter.add(e.lineNumber);
		}	
	}
	
	public void setK(double k) {
		K = k;
	}
	
	public int getAttributeCount()
	{
		if (filter == null)
			initFilter();
		return filter.size();
	}
}
