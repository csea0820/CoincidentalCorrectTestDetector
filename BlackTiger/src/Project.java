import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Project {
	
	final static String xp_path = "/Users/csea/git/BlackTiger/BlackTiger";
	
	public Project()
	{
		
	}
	
	public void analysis(String [] programs)
	{
		int cc = 0,clusters = 0;
		for (String prog: programs)
		{
			List<Integer> versions = readVersion(xp_path+"/"+prog+"/versions.txt");		
			
			for (int id: versions)
			{
				Version v = new Version();
				v.setProgram(prog);
				
				v.setVersionID(id);
				
				v.go(xp_path+"/"+prog+"/testCaseExecutedStmts/v"+id, 
						xp_path+"/"+prog+"/coinCidentalCorrectness/coincidentalCorrectness."+id);
				cc += v.getCoincidentalCorrectnessFound();
				clusters += v.getTotalClusterSize();
			}
		}
		System.out.println("total cc found:" + cc);
		System.out.println("total size of clusters:"+ clusters);
		System.out.println("CC in Cluster on average:"+cc*1.0/clusters);
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
