package sei.buaa.program.cluster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




public class ArffGenerator {

	
	String program,programDir;
	int versionID;
	
	Set<Integer> ignoreTestCases = new HashSet<Integer>();
	
	
	public ArffGenerator()
	{
		ignoreTestCases.add(1514);
		ignoreTestCases.add(1536);
		ignoreTestCases.add(1542);
		ignoreTestCases.add(1554);
		ignoreTestCases.add(1569);
		ignoreTestCases.add(1595);
		ignoreTestCases.add(1616);
		ignoreTestCases.add(1643);
		ignoreTestCases.add(1644);
		ignoreTestCases.add(1670);
		ignoreTestCases.add(1680);
		ignoreTestCases.add(1683);
		ignoreTestCases.add(2397);
		ignoreTestCases.add(2403);
		ignoreTestCases.add(2404);
		ignoreTestCases.add(2410);
		ignoreTestCases.add(2450);
		ignoreTestCases.add(2451);
		ignoreTestCases.add(2462);
		
		ignoreTestCases.add(1401);
		ignoreTestCases.add(1522);
		ignoreTestCases.add(1530);
		ignoreTestCases.add(1531);
		ignoreTestCases.add(1546);
		ignoreTestCases.add(1558);
		ignoreTestCases.add(1563);
		ignoreTestCases.add(1568);
		ignoreTestCases.add(1574);
		ignoreTestCases.add(1583);
		ignoreTestCases.add(1602);
		ignoreTestCases.add(1611);
		ignoreTestCases.add(1631);
		ignoreTestCases.add(1651);
		ignoreTestCases.add(1660);
		ignoreTestCases.add(1661);
		ignoreTestCases.add(1663);
		ignoreTestCases.add(1666);
		ignoreTestCases.add(1682);
		ignoreTestCases.add(2448);
		ignoreTestCases.add(2463);
		ignoreTestCases.add(2466);

	}
	
	public ArffGenerator(String programDir,String program,int versionID)
	{
		this();
		this.programDir = programDir;
		this.program = program;
		this.versionID = versionID;
	}
	
	private TestCase parser(String gcovFile){
		
		String fileName = StringUtility.getBaseName(gcovFile);
		int passed = StringUtility.getDigit(fileName, fileName.length() - 1);
		int testCaseId = StringUtility.getDigit(fileName, fileName.length()-3);
		
		TestCase tc = new TestCase();
		tc.setName(String.valueOf(testCaseId));
		tc.setResult(passed == 1? false:true);
		
		if (program.equals("schedule2") && versionID == 8)
		{
			if (ignoreTestCases.contains(testCaseId))
			{
				tc.setResult(false);
				return tc;
			}
		}
		
		//it is a passed test case
//		if (passed == 0)
//		{
			FileInputStream fr = null;
			BufferedReader br = null;
			String str;
			
			Stmt lastStmt = null;
			ArrayList<Integer> branchProfile = new ArrayList<Integer>();
			boolean lastStmtIsBranch = false;
			
			try {
				fr = new FileInputStream(gcovFile);
				br = new BufferedReader(new InputStreamReader(fr,"utf8"));
				
				str = br.readLine();
				
				while (str != null)
				{
					if (str.charAt(8) == '#')
					{
						str = str.replaceFirst("#####", "    0");
					}
					
					if (str.startsWith("branch"))
					{
						lastStmtIsBranch = true;
						String[] values = str.split("\\s+");

						if (values[2].equals("被执行"))
						{
							try{
							branchProfile.add(Integer.parseInt(values[3]));
							}catch (NumberFormatException NFE)
							{
								System.err.println(program+"_v"+versionID+" Test:"+testCaseId+" Line:"+str);
							}
						}
						else branchProfile.add(0);
					}
					
					// if the line contains a number in front of the first ':', then
					// take it out with the line number, write it into spectrum file
					if (str.charAt(8) != '-' && str.charAt(9) == ':')
					{
						
						if (lastStmtIsBranch == true)
						{
							lastStmt.setPredicate(true);
							lastStmt.setBranchExecutionCount(branchProfile);
							branchProfile = new ArrayList<Integer>();
							lastStmtIsBranch = false;
						}
						
						String[] strs = str.split(":");
						if (strs[2].trim().equals("{"))
						{
							str = br.readLine();
							continue;
						}
						int column = Integer.parseInt(strs[1].trim());
						int times = Integer.parseInt(strs[0].trim());
						
						Stmt st = new Stmt();
						st.setLineNumber(column);
						st.setTimes(times);
						
						tc.addStmt(st);
						lastStmt = st;
						
					}
					// read another line
					str = br.readLine();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				try {
					fr.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
//		}
		
		return tc;
	}
	
	public void generator(String programDir)
	{		
		File dir = new File(programDir);

		List<TestCase> tcs = new ArrayList<TestCase>();
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				TestCase tc = parser(file.getAbsolutePath());
//				if (tc.isResult() == true)
					tcs.add(tc);
			}
		} else {
			System.err.println(programDir + " is not a directory!");
		}
		
		generateArff(tcs);
	}
	
	
	private void generateArff(List<TestCase> tcs)
	{
		
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("@relation "+program+versionID+"\n");
		
		int attrCnt = getAttrSize(tcs.get(0));
		
		buffer.append("@attribute ID integer\n");
		for (int i = 0; i < attrCnt; i++)
		buffer.append("@attribute A"+i+" real\n");
		
		buffer.append("@data\n");
		for (int i = 0; i < tcs.size(); i++)
		{
			buffer.append(tcs.get(i).getName());
			for (int j = 0; j < tcs.get(i).getStmts().size(); j++)
			{
				if (tcs.get(i).getStmts().get(j).isPredicate)
				for (int k = 0; k < tcs.get(i).getStmts().get(j).getBranchExecutionCount().size();k++)
				{
//					if (j == 0 && k == 0)
//						buffer.append(tcs.get(i).getStmts().get(j).getBranchExecutionCount().get(k));
					buffer.append(","+tcs.get(i).getStmts().get(j).getBranchExecutionCount().get(k));
				}
			}
			buffer.append("\n");
		}
		//System.out.println(buffer.toString());
		writeBufferToFile(programDir+"/output_arff/"+program+"_v"+versionID+".arff",buffer);
	}
	
	
	
	public void writeBufferToFile(String file,StringBuffer buffer)
	{
		try {
			
			File f = new File(file);
			if (f.exists() == false)
				f.createNewFile();
			
			
			FileWriter fileWriter = new FileWriter(file,false);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write(buffer.toString());
			
			bufferWriter.close();
			fileWriter.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private int getAttrSize(TestCase tc)
	{
		int size = 0;
		for (int i = 0; i < tc.getStmts().size(); i++)
		{
			if (tc.getStmts().get(i).isPredicate)
				size += tc.getStmts().get(i).getBranchExecutionCount().size();
		}
		return size;
	}
	
	
	public void arffGenerator(String programDir)
	{
		this.programDir = programDir;
		program = StringUtility.getBaseName(programDir);
		File dir = new File(programDir+"/outputs_predicate");
		
		File[] files = dir.listFiles();
		for (File file : files) {
			versionID = Integer.valueOf(file.getName().substring(1));
			//System.out.println(versionID);
			generator(file.getAbsolutePath());
		}
	}
	
	
	/**
	 * @param args
	 */
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
				new ArffGenerator().arffGenerator(s);
			}
		}
		
//		new ArffGenerator().arffGenerator("/Users/csea/Documents/Experiment/Siemens/schedule2");

		
//		ArffGenerator ag = new ArffGenerator("/Users/csea/Documents/Experiment/Siemens/print_tokens","print_tokens", 1);
//		ag.generator("/Users/csea/Documents/Experiment/Siemens/print_tokens/outputs_predicate/v1");
	}

}
