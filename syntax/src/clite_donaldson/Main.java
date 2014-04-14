/*
 * The driver code for the project
 */

package clite_donaldson;

import java.util.ArrayList;

public class Main {

	/* File names:
	 * array
	 * binarydigits /
	 * cast /
	 * hello /
	 * highest /
	 * newton /
	 */
	public static void main(String[] args) {
		ArrayList<String> files = new ArrayList<String>();
		files.add("programs/newton.cpp");
		files.add("programs/highest.cpp");
		files.add("programs/hello.cpp");
		files.add("programs/cast.cpp");
		files.add("programs/binarydigits.cpp");
		files.add("programs/array.cpp");
		System.out.println("Begin parsing... " + files.get(4));
		Parser parser  = new Parser(new Lexer(files.get(4)));
		Program prog = parser.program();
		prog.display();      // display abstract syntax tree
	}
}
