import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ScriptGenerate {
	
	
	public final static String INPUTS_DIR = "/inputs/";
	public final static String TEST_PLAN_FILE = "/testplans.alt/universe";
	
	protected StringBuffer  buffer = new StringBuffer();
	
	String executeTestCase = "exe.sh";
	
	protected BufferedReader bufferReader = null;
	protected FileReader fileReader = null;
	
	protected String program = null;
	protected int versionID = 0;
	
	public ScriptGenerate(String program,int versionID)
	{
		this.program = program;
		this.versionID = versionID;
	}
	
	protected void createDIR(String file)
	{
		buffer.append("if [ ! -d \""+file+"\" ]\n");
		buffer.append("then\n");
		buffer.append("\t mkdir "+file+"\n");
		buffer.append("fi\n");
	}
	
	protected void createFile(String file)
	{	
		buffer.append("if [ -f \""+file+"\" ]\n");
		buffer.append("then\n");
		buffer.append("\t rm "+file+"\n");
		buffer.append("fi\n");
		buffer.append("touch "+file+"\n");
	}
	
	
	protected void openBufferedReader(String fileName)
	{
		try {
			fileReader = new FileReader(fileName);
			bufferReader = new BufferedReader(fileReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String readTestCase()
	{
		String str = null;
		try {
			str = bufferReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convert(str);
	}
	
	protected void closeBufferedReader()
	{
		if (fileReader != null)
			try {
				fileReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		if (bufferReader != null)
			try {
				bufferReader.close();
			} catch (IOException e) {
					e.printStackTrace();
			}	
	}
	
	protected String convert(String s)
	{
		if (s == null)return s;
		if (s.startsWith(".."))
			s = s.substring(1);
		String[] temp = s.split("<");
		String result = new String();
		for (int i = 0; i < temp.length; i++)
		{
			if (temp[i].length() == 0)continue;
			
			if (s.indexOf("<" + temp[i]) != -1)
			{
				temp[i] = " ." + ScriptGenerate.INPUTS_DIR+temp[i].trim();
				result += "<" + temp[i];
			} else
				result += temp[i];
		}
		return result;
	}
	
	public void writeBufferToFile(String file)
	{
		try {
			FileWriter fileWriter = new FileWriter(file,false);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write(buffer.toString());
			
			bufferWriter.close();
			fileWriter.close();		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generate()
	{
		
	}
	
	
	public void generateScript(ScriptGenerate scriptGen)
	{
		scriptGen.generate();
	}
	
//	public void generate(String commod)
//	{
//		try {
//			FileWriter fileWriter = new FileWriter("exe.sh",false);
//			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
//			buffer.append(commod);
//			bufferWriter.write(buffer.toString());
//			
//			bufferWriter.close();
//			fileWriter.close();
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public static void main(String [] args)
//	{
//		//new ScriptGenerate().generate();
////		ScriptGenerate scriptGen = new CoincidentalCorrectScriptGen(args[0],Integer.parseInt(args[1]));
////		scriptGen.generate();
//	}
	
}
