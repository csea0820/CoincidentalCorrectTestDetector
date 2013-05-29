

public class CoincidentalCorrectScriptGen extends ScriptGenerate{
	
	public CoincidentalCorrectScriptGen(String program, int versionID) {
		super(program, versionID);
		// TODO Auto-generated constructor stub
	}


	public static final String COINCIDENTAL_CORRECT_SCRIPT = "coincidentalCorrect.sh";
	
	
	public void generate()
	{
		
		buffer = new StringBuffer();
		
		buffer.append("cd ./coincidentalCorrectness\n");
		createFile("coincidentalCorrectness."+versionID);
		buffer.append("cd ..\n");
		buffer.append("gcc ./versions.alt/versions.orig/v"+versionID+"/" + program + ".c -o b.out -lm\n");
		
		int count = 1;
		openBufferedReader("."+ScriptGenerate.TEST_PLAN_FILE);	
		String str = readTestCase();
		buffer.append("echo ---Begin executing tests---\n");
		buffer.append("echo ---Waiting---\n");
		while (str != null)
		{
			buffer.append("./a.out " + str + " > tmp1\n");
			buffer.append("./b.out " + str + " > tmp2 2>err\n");
			buffer.append("if  diff tmp1 tmp2\n");
			buffer.append("then\n");
			buffer.append(" if cat err | grep CC \n");
			buffer.append("then\n");
			buffer.append("rm err\n");
			buffer.append("echo "+count+" >> ./coincidentalCorrectness/coincidentalCorrectness."+versionID+"\n");
			buffer.append("fi\nfi\n\n");
			count++;
			
			str = readTestCase();
		}
		buffer.append("echo ---Finishing executing tests---\n");
		
		writeBufferToFile(COINCIDENTAL_CORRECT_SCRIPT);
		
		closeBufferedReader();
	}
	
	public static void main(String [] args)
	{
		//new ScriptGenerate().generate();
		ScriptGenerate scriptGen = new CoincidentalCorrectScriptGen(args[0],Integer.parseInt(args[1]));
		scriptGen.generate();
	}

}
