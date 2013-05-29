

public class StringUtility {
	
	public static boolean IsNullOrEmpty(String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	public static String getBaseName(String path)
	{
		int index = path.lastIndexOf("/");
		return path.substring(index+1);
	}
	
	public static int getDigit(String str,int pos)
	{
		int ret = 0;
		int base = 1;
		for (int i = pos; i >= 0 && i < str.length() && Character.isDigit(str.charAt(i)); i--)
		{		
			ret += base*(str.charAt(i)-'0');
			base *= 10;
		}
		return ret;
	}
}
