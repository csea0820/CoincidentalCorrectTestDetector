import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Utility {

	public static void calClusterResults(int m_totalChoosenSize,
			int m_coincidentalCorrectnessTotalFound,
			int m_coincidnetCorrectnessTotalCnt, int passTestSize) {

		System.out.println("Choosen Cluster Size:" + m_totalChoosenSize);
		System.out.println("Coincidental Correctness in Choosen Cluster:"
				+ m_coincidentalCorrectnessTotalFound);
		System.out.println("Percentage of CC in Cluster:"
				+ m_coincidentalCorrectnessTotalFound * 1.0
				/ m_totalChoosenSize);
		System.out
				.println("False Negative:"
						+ (m_coincidnetCorrectnessTotalCnt - m_coincidentalCorrectnessTotalFound)
						* 1.0 / m_coincidnetCorrectnessTotalCnt);
		System.out
				.println("False Postive:"
						+ (m_totalChoosenSize - m_coincidentalCorrectnessTotalFound)
						* 1.0
						/ (passTestSize - m_coincidnetCorrectnessTotalCnt));
		System.out.println("--------------------------------------");

	}
	
	public static void writeToFile(String file,String content)
	{
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			pw.write(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally
		{
			pw.close();
		}

	}

}
