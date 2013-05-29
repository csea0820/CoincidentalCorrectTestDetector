import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Parser {
	
	int totalFailCnt = 0;
	
	int numberOfPredicate = 0;
	
	public Parser()
	{
		
	}
	
	public TestCase parse(String gcovFile,String testCase,int result,int id)
	{
		
		
		TestCase tc = new TestCase();
		tc.setName(testCase);
		tc.setResult(result==1);
		
		if ( !tc.isResult() )
			totalFailCnt++;
		
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

//					System.out.println(str);
//					System.out.print(values[0]+" ");
//					System.out.print(values[1]+" ");
//					System.out.println(values[2]+" ");
//					System.out.print(str.substring(10,15)+" ");
//					System.out.print(str.substring(16)+" ");
//					for (String s:values)
//						System.out.print(s+" ");
//					System.out.println("\n");
					if (values[2].equals("被执行"))
					{
						branchProfile.add(Integer.parseInt(values[3]));
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
			//tc.showInfo();
			tc.writeToFile(testCase);
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//return tc;
		}
		return tc;
	}
	
	
	public int getFailedTestCases()
	{
		return totalFailCnt;
	}
	
	public static void main(String[] args)
	{
		Parser parser = new Parser();
		
		parser.parse(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		//parser.parse("/Users/csea/Documents/Experiment/Siemens/print_tokens/print_tokens.c.gcov", "1", 1,1);
		//System.out.println("Failed test cases:"+parser.totalFailCnt);
	}

}
