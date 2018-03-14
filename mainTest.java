package seng_project_iteration_1;

import seng_project_iteration_1.ExitDeniedSecurityManager.ExitSecurityException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.Permission;

public class MainTest {
	public static String BASEDIR = "C:\\Users\\katie\\";

	/**
	 * Set a security manager to catch System.exit() calls
	 */
	@Before
	public void setup() {
		System.setSecurityManager(new ExitDeniedSecurityManager());
	}
	
	/**
	 * Tests that the program accepts 2 command line arguments
	 * @throws IOException 
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testValidUsage() throws IOException {
		String pathname = BASEDIR + "Documents";
		String type = "java.lang.String";
		String[] args = {pathname, type};		
		
		Scanner sc = new Scanner();		
		sc.main(args);
		
		
		// TODO: Assert something
	}

	/**
	 * Tests that running the program with more or less than 2 arguments prints a usage string and exits
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testUsageInvalid() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = BASEDIR + "Documents";
		String[] args = {pathname};
		
		Scanner sc = new Scanner();
		
		try {
			sc.main(args);
		} catch (ExitSecurityException e) {
			assertEquals("Usage:	java Scanner <path to directory> <java type of interest>\r\n" + "",
					outContent.toString());
		}
		
		
		// TODO: Still throws ExitSecurityException?

	}
	
	/**
	 * Checks that when given a nonexistent directory, the program prints an error message and exits
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testFakeDir() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = BASEDIR + "fakedir";
		String[] args = {pathname, "java.lang.String"};
		Scanner sc = new Scanner();
		
		try {
			sc.main(args);
		} catch (ExitSecurityException e) {
			assertEquals("[Scanner]:\tInvalid directory or path\r\n" + "", outContent.toString());
		}
	}

	/**
	 * Checks that when given a directory with no java files, the program prints an error message and exits
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testDirWithNoJavaFiles() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = BASEDIR + "Documents\\Empty";
		String type = "java.lang.String";
		String[] args = {pathname, type};
		
		Scanner sc = new Scanner();

		try {
			sc.main(args);
		} catch (ExitSecurityException e) {
			assertEquals("[Scanner]:\tNo java files in the specified directory.\r\n" + "", outContent.toString());
		}
	}


	/**
	 * Checks that when given a directory with more than one java file, references to a type
	 * are counted across all files
	 * @throws IOException
	 */
	@Test
	public void testRefCountWithMultipleJavaFiles() throws IOException {
		// TODO
	}

	/**
	 * Checks that when given a directory with more than one java file, declarations of a type
	 * are counted across all files
	 * @throws IOException
	 */
	@Test
	public void testDecCountWithMultipleJavaFiles() throws IOException {
		// TODO
	}

	
	/**
	 * 
	 */
	@Test
	public void testconvertToStringEmptyFile() {
		// TODO
	}


	// TODO: Add ASTParser tests
	

}



class ExitDeniedSecurityManager extends SecurityManager {
	 
    @SuppressWarnings("serial")
	public static final class ExitSecurityException extends SecurityException {
        private final int status;
 
        public ExitSecurityException(final int status) {
            this.status = status;
        }
 
        public int getStatus() {
            return this.status;
        }
    }
 
    @Override
    public void checkExit(final int status) {
        throw new ExitSecurityException(status);
    }
 
    @Override
    public void checkPermission(final Permission perm) {}
}
