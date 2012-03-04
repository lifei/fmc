package lifei.fmc;

/**
 * Main Service for faster mirah compiler
 * @author lifei
 * @version 1.0
 */
public class FastMirahCompilerMain {
	
	/**
	 * @param argv
	 */
	public static void main(String[] argv)
	{

		String[] tmp = {"compile", "--help"};

		try {
			org.mirah.MirahCommand.main(tmp);
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}

		FastMirahCompilerServer.begin();
	}

}
