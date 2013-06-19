package sei.buaa.program.analyzer;


public class Constant {
	
	public static final String SOURCE_DIR = "/source.alt/source.orig/";
	public static final String VERSION_DIR  = "/versions.alt/versions.orig/";
	public static final String TEST_PLAN_DIR = "/testplans.alt/";
	public static final String OUT_PUT_DIR = "outputs";
	public static final String INPUT_DIR = "/inputs/";
	public static final String COINCIDENTAL_CORRECTNESS_DIR = "/coincidentalCorrectness/";
	/**
	 * list version id one per line for program variants to be analyzed
	 * */
	public static final String VERSIONS_LIST = "/versions.txt";
	/**
	 * presents fault locations for each faulty program.The line number indicates 
	 * program version id.The content of each line show fault locations information separated by space.
	 * 
	 * <p>For example, if the ith line contains the following string: 123 32. It tells us that
	 * the ith program variant contains two bug, which are located in 123th and 32th line respectively.
	 * An empty line indicates the corresponding program variant is absence of fault.
	 */
	public static final String FAULTS_LIST = "/faults.txt";
	
	public static final String TARANTULA = "Tarantula";
	public static final String RELABEL_TARANTULA = "Tarantula_RELABEL";
	public static final String DISCARD_TARANTULA = "Tarantula_DISCARD";
	public static final String IDEAL_RELABEL_TARANTULA = "Tarantula_RELABEL_IDEAL";
	public static final String IDEAL_DISCARD_TARANTULA = "Tarantula_DISCARD_IDEAL";
	


	public static final String JACCARD = "Jaccard";
	public static final String WJACCARD = "WJaccard";
	public static final String LJACCARD = "LJaccard";


	public static final String OCHIAI = "Ochiai";
	public static final String WOCHIAI = "WOchiai";
	public static final String LOCHIAI = "LOchiai";


	public static final String SBI = "SBI";
	public static final String WSBI = "WSBI";
	public static final String LSBI = "LSBI";


	public static final String WONG = "Wong";
	public static final String SIQ = "SIQ";
	public static final String RA1 = "RA1";
	public static final String VWT = "VWT";
	
	public static final String WONG2 = "Wong2";
	public static final String WWONG2 = "WWong2";

	public static final String AMPLE = "Ample";
	public static final String WAMPLE = "WAmple";
	public static final String LAMPLE = "LAmple";


	public static final String OP = "Op";
	public static final String WOP = "WOp";
	public static final String LOP = "LOp";
	
	public static final String WONG3 = "Wong3";
	public static final String WWONG3 = "WWong3";
	public static final String LWONG3 = "LWong3";


	public static final int NO_ACTION = 0x00;
	public static final int RELABLE = 0x01;
	public static final int DISCARD = 0x02;


}
