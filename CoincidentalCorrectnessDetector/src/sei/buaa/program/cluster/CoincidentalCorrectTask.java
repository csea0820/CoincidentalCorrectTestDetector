package sei.buaa.program.cluster;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author csea
 * @Date 2013-6-1 下午5:10:19
 */

public class CoincidentalCorrectTask {

	private List<Integer> versionIds = new ArrayList<Integer>();
	
	public static StringBuilder builder = new StringBuilder();
	
	public void anaylze(String programDir)
	{
		initAnalyzedVersions(programDir+"/faults.txt");
		for (Integer id : versionIds)
		{
			Version v = new Version(programDir, id,new SampleByFailedTests());
			//v.analyzeCoincidentalCorrectness();
			builder.append(v.getResultInfo()).append("\n");
		}
	}
	
	private void initAnalyzedVersions(String faultFile) {
		BufferedReader br = null;
		FileReader fr = null;
		String str;
		try {
			fr = new FileReader(faultFile);
			br = new BufferedReader(fr);

			str = br.readLine();
			while (str != null) {
				String[] nums = str.split("\\s+");
				if (nums.length == 2)versionIds.add(Integer.parseInt(nums[0]));
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
	}
	
	public static void main(String[] args) {
		if (args.length == 0)
		{
			System.out.println("Invalid Usage!");
			System.out.println("Usage: ArffGenerator [programDir1 programDir2 ...]");
		}
		else 
		{
			for (String s: args)
			{
				System.out.println("Analyzing Program " + s);
				new CoincidentalCorrectTask().anaylze(s);
			}
			
			System.out.println(builder.toString());
		}
	}

}
