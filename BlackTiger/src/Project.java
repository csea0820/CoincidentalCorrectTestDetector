import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Project {
	
	final static String xp_path = "/Users/csea/git/CoincidentalCorrectness/BlackTiger";
	
	public Project()
	{
		
	}
	
	public void analysis(String [] programs)
	{
		int cc = 0,clusters = 0,totalCC = 0,passTestSize = 0;
		for (String prog: programs)
		{
			List<Integer> versions = readVersion(xp_path+"/"+prog+"/versions.txt");		
			
			int cct = 0, clustert = 0, totalCCT = 0, passTestSizet = 0;
			
			for (int id: versions)
			{
				Version v = new Version();
				v.setProgram(prog);
				
				v.setVersionID(id);
				
				v.go(xp_path+"/"+prog+"/testCaseExecutedStmts/v"+id, 
						xp_path+"/"+prog+"/coinCidentalCorrectness/coincidentalCorrectness."+id);
				totalCC += v.getM_coincidnetCorrectnessTotalCnt();
				passTestSize += v.getPassTestSize();
				cc += v.getm_coincidentalCorrectnessTotalFound();
				clusters += v.getTotalClusterSize();
				v.showSampleResults();
				
				totalCCT += v.getM_coincidnetCorrectnessTotalCnt();
				passTestSizet += v.getPassTestSize();
				cct += v.getm_coincidentalCorrectnessTotalFound();
				clustert += v.getTotalClusterSize();
			}
		
//			System.out.println("---------------Clusters Result Over programs "+prog+"-------------------");
//			System.out.println("------------------------------------------------------------------------");
//			Utility.calClusterResults(clustert, cct, totalCCT, passTestSizet);
		}
		
		System.out.println("---------------Clusters Result Over all test programs-------------------");
		System.out.println("------------------------------------------------------------------------");
		Utility.calClusterResults(clusters, cc, totalCC, passTestSize);
	}
	
	private List<Integer> readVersion(String file)
	{
		List<Integer> res = new ArrayList<Integer>();
		
		
		BufferedReader br = null;
		FileReader fr = null;
		String str = null;
		
		try {
			
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			str = br.readLine();
			while (str != null)
			{
				res.add(Integer.parseInt(str));
				str = br.readLine();
			}
						
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
	
	public static void main(String[] args)
	{
		for (String str:args)
			System.out.println(str);
		
		Project p = new Project();
		p.analysis(args);
		
		//new CoincidentalCorrectScriptGen().generate();
	}

}
