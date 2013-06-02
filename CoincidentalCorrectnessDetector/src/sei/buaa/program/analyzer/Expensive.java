package sei.buaa.program.analyzer;
/**
 * 
 */


import java.util.ArrayList;
import java.util.List;

/**
 * @author csea
 *
 */
public class Expensive {
	
	String technique;
	List<Double> exp = new ArrayList<Double>();
	
	public Expensive(String tech)
	{
		this.technique = tech;
	}
	
	public void addExpensive(double v)
	{
		exp.add(v);
	}
	
	public int getIntervalNumber(double up)
	{
		int ret = 0;
		for (Double v: exp)
		{
			if ( v <= up)ret++;
		}
		
		return ret;
	}

	public String getTechnique() {
		return technique;
	}

}
