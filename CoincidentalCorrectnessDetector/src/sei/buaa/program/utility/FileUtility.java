package sei.buaa.program.utility;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class FileUtility {
	
	public static void writeContentToFile(String content,String file)
	{
		try {
			FileWriter fileWriter = new FileWriter(file,false);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write(content);
			
			bufferWriter.close();
			fileWriter.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

}
