

public class FaultScriptGenerate extends ScriptGenerate{

	public static final String EXECUTION_PROFILE = "execution.sh";


//	public final static int FAULT_VERSION = 0;
//	public final static int ORIGINAL_VERSION = 1;
	
	//public final static String INPUTS_DIR = "/inputs/";
	
	
	public FaultScriptGenerate(String program, int versionID) {
		super(program, versionID);
		// TODO Auto-generated constructor stub
	}
	
	public void generate()
	{
		buffer = new StringBuffer();
		
		createDIR("testCaseExecutedPreds");
		buffer.append("cd ./testCaseExecutedPreds\n");
		createDIR("v"+versionID);
		buffer.append("cd ..\n");
		
//		buffer.append("cp ./versions.alt/versions.orig/v"+versionID+"/"+program+".c .\n");
		
		buffer.append("gcc -lm ./source.alt/source.orig/"+program+".c -o a.out\n");
		buffer.append("gcc -fprofile-arcs -ftest-coverage -lm ./versions.alt/versions.orig/v"+versionID+"/"+program+".c -o b.out\n");
		
		openBufferedReader("."+TEST_PLAN_FILE);
		String testcase = readTestCase();
		int count = 1;
		while (testcase != null)
		{
			
			buffer.append("./a.out " + testcase + " > tmp1\n");
			buffer.append("./b.out " + testcase + " > tmp2\n");
			
			buffer.append("result=1\n");
			buffer.append("gcov -bc ./"+program+".c > /dev/null\n");
			buffer.append("if ! diff tmp1 tmp2\n");
			buffer.append("then\n");
			buffer.append("result=0\n");
			buffer.append("fi\n");
			buffer.append("java -jar Parser.jar "+program+".c.gcov "+count + " $result "+ versionID + "\n");
			buffer.append("rm "+program+".gcda\n");
			buffer.append("mv *.res ./testCaseExecutedPreds/v"+versionID+"\n\n\n");
//			buffer.append("echo "+count+"\n");
			count++;
		
			testcase = readTestCase();
		}
		
//		buffer.append("rm "+program+".c");
		
		writeBufferToFile(EXECUTION_PROFILE);
		closeBufferedReader();
	}
	
//	private void exe(int type,String testcase)
//	{
//		Process process;
//		String program = "./a.out ";
//		
//		String output = " tmp1";
//		
//		if (type == FaultScriptGenerate.FAULT_VERSION)
//		{
//			program = "./b.out ";
//			output = " tmp2 2>err";
//		}
//		
//
//		try {
//			String commod = program + convert(testcase) + " >"+output;
//			ScriptGenerate scriptGenerate = new ScriptGenerate();
//			scriptGenerate.generate(commod);
//			process = Runtime.getRuntime().exec(
//					"sh exe.sh");
//			
//			BufferedReader br = new BufferedReader(new InputStreamReader(process
//					.getInputStream()));
//			String str = br.readLine();
//			while (str != null)
//			{
//				System.out.println(str);
//				str = br.readLine();
//			}
//			process.waitFor();
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//	}
	
//	public void run(String testcase)
//	{
//		exe(FaultScriptGenerate.ORIGINAL_VERSION,testcase);
//		exe(FaultScriptGenerate.FAULT_VERSION,testcase);
//	}
	
	public static void main(String [] args)
	{
		ScriptGenerate scriptGen = new FaultScriptGenerate(args[0],Integer.parseInt(args[1]));
		scriptGen.generate();
	}
}
