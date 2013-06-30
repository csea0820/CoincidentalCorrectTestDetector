package sei.buaa.program.analyzer;


import java.text.SimpleDateFormat;
import java.util.Date;

import sei.buaa.program.cluster.ArffPredicateGenerator;
import sei.buaa.program.cluster.ArffStatementGenerator;
import sei.buaa.program.cluster.StringUtility;
import sei.buaa.program.utility.FileUtility;


public class SiemensAnalyzer {

	private StringBuilder expenseSummary = new StringBuilder(1000);
	
	Expensive tarantulaExp = new Expensive(Constant.TARANTULA);
	Expensive tarantulaRelabelExp = new Expensive(Constant.RELABEL_TARANTULA);
	Expensive tarantulaDiscardExp = new Expensive(Constant.DISCARD_TARANTULA);
	
	Expensive tarantulaRelabelExp_ideal = new Expensive(Constant.IDEAL_RELABEL_TARANTULA);
	Expensive tarantulaDiscardExp_ideal = new Expensive(Constant.IDEAL_DISCARD_TARANTULA);
	
	Expensive jaccardExp = new Expensive(Constant.JACCARD);
	Expensive ochiaiExp = new Expensive(Constant.OCHIAI);
	Expensive sbiExp = new Expensive(Constant.SBI);
	
	int[] fnAndFp = new int[4];


	
	private int totalVersions;
	private int singleFaultsVersions;
	private int multiFaultsVersions;
	private int nonFaultVersions;
	private int analyzeVersions;
	
	private StringBuilder sb = new StringBuilder(5000);
	
	public void analyze(String[] programPaths)
	{
		expenseSummary.append("Subject,Tarantula").append("\n");
		
		for (String path : programPaths)
		{
			String programName = StringUtility.getBaseName(path);
			System.out.println("analyzing program " + programName);
			
//			ArffPredicateGenerator asg = new ArffPredicateGenerator();
//			asg.setPruneAttributes(true);
//			asg.arffGenerator(path);
//			asg.setK(0.6);
			
			ProjectAnalyzer pa = new ProjectAnalyzer(this,path,false,false);
			pa.analyze();	
			totalVersions += pa.getTotalVersions();
			singleFaultsVersions += pa.getSingleFaultVersions();
			multiFaultsVersions += pa.getMultiFaultsVersions();
			nonFaultVersions += pa.getNonFaultVersions();
			analyzeVersions += pa.getAnalyzeVersions();
			sb.append(pa.getDiagnosisContent());
			
			FileUtility.writeContentToFile(pa.getTscoreReductionInfo().toString(), "/Users/csea/Documents/Experiment/Siemens/tscoreReduction/"+programName+".csv");
			
			System.gc();
		}
		sb.append("Total Versions:" + totalVersions).append("\n");
		sb.append("NonFaultVersions :" + nonFaultVersions).append("\n");
		sb.append("SingleFaultsVersions :" + singleFaultsVersions).append("\n");
		sb.append("MultiFaultsVersions :" + multiFaultsVersions).append("\n");
		sb.append("AnalyzeVersions:"+analyzeVersions).append("\n");
		evaluation();
	}
	
	private void evaluation()
	{
		sb.append(String.format("%-10s","Interval")).append("\t")
		.append(String.format("%-10s", "Tarantula")).append("\t")
		.append(String.format("%-10s", "Tarantula_Relabel")).append("\t")	
		.append(String.format("%-10s", "Tarantula_Discard")).append("\t")
		.append(String.format("%-10s", "Tarantula_Relabel_ideal")).append("\t")	
		.append(String.format("%-10s", "Tarantula_Discard_ideal")).append("\n");	

		int a = 1;
		int interval = 5;
		while (a <= (100/interval))
		{
			sb.append(String.format("%-10s",((a-1)*interval+"-"+a*interval))).append("\t");
			sb.append(String.format("%-10d",tarantulaExp.getIntervalNumber(a*0.01*interval))).append("\t")
			  .append(String.format("%-10d",tarantulaRelabelExp.getIntervalNumber(a*0.01*interval))).append("\t")
			  .append(String.format("%-10d",tarantulaDiscardExp.getIntervalNumber(a*0.01*interval))).append("\t")
			  .append(String.format("%-10d",tarantulaRelabelExp_ideal.getIntervalNumber(a*0.01*interval))).append("\t")
			  .append(String.format("%-10d",tarantulaDiscardExp_ideal.getIntervalNumber(a*0.01*interval))).append("\n");

			a += 1;
		}
		
		sb.append("false negative = ").append(fnAndFp[0]*1.0/fnAndFp[1]).append(",false positve = ")
		  .append(fnAndFp[2]*1.0/fnAndFp[3]);
		
		System.out.println(sb.toString());
		FileUtility.writeContentToFile(sb.toString(), "/Users/csea/Documents/Experiment/Siemens/result/"+getCurrentDate()+".result");
//		System.out.println(expenseSummary.toString());
	}
	
	private String getCurrentDate()
	{
		Date date=new Date();
		SimpleDateFormat formater=new SimpleDateFormat();
		formater.applyPattern("yyyy_MM_dd_HH_mm");
		return formater.format(date);
	}
	
	public Expensive getTarantulaExp() {
		return tarantulaExp;
	}

	public Expensive getJaccardExp() {
		return jaccardExp;
	}

	public void addToFnAndFp(int res[])
	{
		for (int i = 0; i < 4; i++)
			fnAndFp[i] += res[i];
	}
	
	public Expensive getOchiaiExp() {
		return ochiaiExp;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 0)
		{
			SiemensAnalyzer sa = new SiemensAnalyzer();
			sa.analyze(args);
		}
	}

	public Expensive getSbiExp() {
		return sbiExp;
	}


	public StringBuilder getExpenseSummary() {
		return expenseSummary;
	}

	public void setExpenseSummary(StringBuilder expenseSummary) {
		this.expenseSummary = expenseSummary;
	}

	public Expensive getTarantulaRelabelExp() {
		return tarantulaRelabelExp;
	}

	public Expensive getTarantulaDiscardExp() {
		return tarantulaDiscardExp;
	}

	public Expensive getTarantulaRelabelExp_ideal() {
		return tarantulaRelabelExp_ideal;
	}

	public Expensive getTarantulaDiscardExp_ideal() {
		return tarantulaDiscardExp_ideal;
	}

}
