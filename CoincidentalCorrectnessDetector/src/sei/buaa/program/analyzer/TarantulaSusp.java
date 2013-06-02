package sei.buaa.program.analyzer;


public class TarantulaSusp implements ISuspsCalculator {

	// public TarantulaSusp(int ln) {
	// }

	public double calcSups(StatementSum eSum) {
		int a00 = eSum.getA00();
		int a01 = eSum.getA01();
		int a10 = eSum.getA10();
		int a11 = eSum.getA11();
		
		double susp = 0;
		double a = a11 * 1.0 / (a11 + a01);
		double b = a10 * 1.0 / (a10 + a00);
		if (a + b == 0)
			susp = 0;
		else
			susp = a / (a + b);

		return susp;
	}
	
}
