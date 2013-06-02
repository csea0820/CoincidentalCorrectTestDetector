package sei.buaa.program.cluster;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * @Author csea
 * @Date 2013-6-1 下午2:20:51
 */

public class ClusteringAnalysis {

	
	int m_clusterNumber = 0;
	
	ClusteringAnalysis()
	{
		
	}
	
	
	public Map<Integer,Set<Integer>> cluster(String arffFile)
	{
		Instances insts = null;
		
		SimpleKMeans skm = new SimpleKMeans();
		File file= new File(arffFile);
		ArffLoader loader = new ArffLoader();
		
		int instance_cnt = 0;
		
		ArrayList<Integer> testCaseIds = new ArrayList<Integer>();
		
		Map<Integer,Set<Integer>> clusters = new TreeMap<Integer,Set<Integer>>();
		
		try {
			loader.setFile(file);
			insts=loader.getDataSet();
			
			instance_cnt = insts.size();
			
			for (Instance inst: insts)
				testCaseIds.add(Integer.parseInt(inst.toString(0)));

			//configuration for cluster
			insts.deleteAttributeAt(0);
			skm.setNumClusters(m_clusterNumber = (int)(instance_cnt*0.06));
			skm.setInitializeUsingKMeansPlusPlusMethod(true);
			skm.setPreserveInstancesOrder(true);
			
			//begin to cluster
			try{
			skm.buildClusterer(insts);
			}catch (IllegalArgumentException e)
			{
				System.err.println("m_clusterNumber:"+m_clusterNumber);
				System.err.println("File "+arffFile+" can't normalize array. Sum is zero.");
			}
			
			int[] cid = skm.getAssignments();
			for (int i = 0; i < testCaseIds.size(); i++)
			{
				Set<Integer> set = clusters.get(cid[i]);
				if (set == null)
				{
					set = new HashSet<Integer>();
					clusters.put(cid[i], set);
				}
				set.add(testCaseIds.get(i));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		for (Integer key : clusters.keySet())
//		{
//			System.out.println("{"+key+","+clusters.get(key)+"}");
//		}
		
		return clusters;
	}
	
	
	public static void main(String[] args)
	{
		ClusteringAnalysis ca = new ClusteringAnalysis();
		ca.cluster("/Users/csea/Documents/Experiment/Siemens/schedule/output_arff/schedule_v1.arff");
	}
	
}
