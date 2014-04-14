/*
 * Parser.java is a recursive descent parser that inputs a CLite program
 * and generates its abstract syntax.  Each method corresponds to a
 * concrete syntax grammar rule, which appears as a comment at the
 * beginning of the method.
 */

package clite_donaldson;

public class Parser {

	Token token;          // current token from the input stream
	Lexer lexer;

	public Parser(Lexer ts) { // Open the C++Lite source program
		lexer = ts;                          // as a token stream, and
		token = lexer.next();            // retrieve its first Token
	}

	private String match (TokenType t) {
		String value = token.value();

		if (token.type().equals(t))
			token = lexer.next();
		else
			error(t);

		return value;
	}

	private void error(TokenType tok) {
		System.err.println("Syntax error: expecting: " + tok
				+ "; saw: " + token);
		System.exit(1);
	}

	private void error(String tok) {
		System.err.println("Syntax error: expecting: " + tok
				+ "; saw: " + token);
		System.exit(1);
	}

	public Program program() {
		// Program --> void main ( ) '{' Declarations Statements '}'
		TokenType[ ] header = {TokenType.Int, TokenType.Main,
				TokenType.LeftParen, TokenType.RightParen};
		for (int i=0; i<header.length; i++)   // bypass "int main ( )"
			match(header[i]);
		match(TokenType.LeftBrace);
		Declarations decpart = declarations();
		Block body = statements();
		match(TokenType.RightBrace);
		return new Program(decpart, body);
	}

	private Declarations declarations() {
		// Declarations --> { Declaration }
		Declarations ds = new Declarations();
		while (isType()) {
			declaration(ds);
		}
		return ds;
	}

	private void declaration(Declarations ds) {
		// Declaration  --> Type Identifier { , Identifier } ;
		Type t = type();

		do {
			if (token.type().equals(TokenType.Comma)) {
				match(token.type());
			}

			Variable id = new Variable(match(TokenType.Identifier));

			if (token.type().equals(TokenType.LeftBracket)) {
				IntValue size = new IntValue();

				match(TokenType.LeftBracket);
				if (token.type().equals(TokenType.IntLiteral)) {
					size = (IntValue) literal();
				} else {
					error("IntLiteral");
				}

				match(TokenType.RightBracket);
				ds.add(new Declaration(id, t, size));
			} else {
				ds.add(new Declaration(id, t));
			}

		} while(token.type().equals(TokenType.Comma));

		match(TokenType.Semicolon);
	}


	private Type type() {
		// Type  -->  int | bool | float | char
		Type t = null;
		if (token.type().equals(TokenType.Int))
			t = Type.INT;
		else if (token.type().equals(TokenType.Bool))
			t = Type.BOOL;
		else if (token.type().equals(TokenType.Float))
			t = Type.FLOAT;
		else if (token.type().equals(TokenType.Char))
			t = Type.CHAR;
		else error("int | bool | float | char");
		token = lexer.next(); // pass over the type
		return t;
	}

	private Statement statement() {
		// Statement --> ; | Block | Assignment | IfStatement | WhileStatement
		if (token.type().equals(TokenType.Semicolon)) {
			return new Skip();
		} 

		else if (token.type().equals(TokenType.If)) {
			return ifStatement();
		} 

		else if (token.type().equals(TokenType.Else)){ 
			match(TokenType.Else);

			if (token.type().equals(TokenType.If)){ 
				return ifStatement();
			} else {
				Variable id = new Variable(token.value());
				match(TokenType.Identifier);
				return assignment(id);
			}
		}

		else if (token.type().equals(TokenType.While)) {
			return whileStatement();
		} 

		else if (token.type().equals(TokenType.Identifier)) {
			Variable id = new Variable(token.value());
			match(TokenType.Identifier);
			return assignment(id);
		} 

		else if (token.type().equals(TokenType.LeftBrace)) {
			match(TokenType.LeftBrace);
			Block block = statements();
			match(TokenType.RightBrace);
			return block;
		} 

		else {
			error("Unknown statement type: " + token.value());
		}
		return null;
	}

	private Block statements() {
		// Block --> '{' Statements '}'
		Block b = new Block();

		while (!token.type().equals(TokenType.RightBrace) && !token.type().equals(TokenType.Eof)) {
			b.members.add(statement());
		}
		return b;
	}

	private Assignment assignment(Variable id) {
		ArrayRef a;
		Assignment arrayAssign = null;
		boolean array = false;
		
		if (token.type().equals(TokenType.LeftBracket)) {
			array = true;
			match(token.type());
			IntValue pos = (IntValue) literal();
			match(TokenType.RightBracket);
			match(TokenType.Assign);
			Expression e = expression();
			a = new ArrayRef(id.toString(), pos);
			//arrayAssign = new Assignment(a, e, array);
		}

		if (array) {
			match(TokenType.Semicolon);
			return arrayAssign;
		} else {
			match(TokenType.Assign);
			Expression source = expression();
			match(TokenType.Semicolon);
			return new Assignment(id, source);
		}
	}

	/*
	 * Why isn't there a special token for the else statement? 
	 * Example: if shows conditional, but it doesn't show when an else appears.
	 */
	private Conditional ifStatement() {
		// IfStatement --> if ( Expression ) Statement [ else Statement ]
		match(TokenType.If);
		match(TokenType.LeftParen);
		Expression e = expression();
		match(TokenType.RightParen);

		Statement ifStatement = statement();
		Statement elseStatement;

		if (token.type().equals(TokenType.Else)){ 
			elseStatement = statement();
		} else {
			elseStatement = new Skip();
		}

		return new Conditional(e, ifStatement, elseStatement);
	}

	private Loop whileStatement() {
		// WhileStatement --> while ( Expression ) Statement
		match(TokenType.While);

		match(TokenType.LeftParen);
		Expression e = expression();
		match(TokenType.RightParen);

		Statement statement = statement();

		return new Loop(e, statement);
	}

	private Expression expression() {
		// Expression --> Conjunction { || Conjunction }
		Expression e = conjunction();

		while (token.type().equals(TokenType.Or)) {
			Operator op = new Operator(token.value());
			match(TokenType.Or);
			Expression term2 = conjunction();
			e = new Binary(op, e, term2);
		}

		return e;
	}

	private Expression conjunction() {
		// Conjunction --> Equality { && Equality }
		Expression e = equality();

		while (token.type().equals(TokenType.And)){ 
			Operator op = new Operator(token.value());
			match(TokenType.And);
			Expression term2 = equality();
			e = new Binary(op, e, term2);
		}

		return e;
	}

	private Expression equality() {
		// Equality --> Relation [ EquOp Relation ]
		Expression e = relation();

		while (isEqualityOp()) {
			Operator op = new Operator(token.value());
			match(token.type());
			Expression term2 = relation();
			e = new Binary(op, e, term2);
		}

		return e;
	}

	private Expression relation(){
		// Relation --> Addition [RelOp Addition]
		Expression e = addition();

		while (isRelationalOp()) {
			Operator op = new Operator(token.value());
			match(token.type());
			Expression term2 = addition();
			e = new Binary(op, e, term2);
		}

		return e;
	}

	private Expression addition() {
		// Addition --> Term { AddOp Term }
		Expression e = term();

		while (isAddOp()) {
			Operator op = new Operator(token.value());
			match(token.type());
			Expression term2 = term();
			e = new Binary(op, e, term2);
		}

		return e;
	}

	private Expression term() {
		// Term --> Factor { MultiplyOp Factor }
		Expression e = factor();

		while (isMultiplyOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term2 = factor();
			e = new Binary(op, e, term2);
		}

		return e;
	}

	private Expression factor() {
		// Factor --> [ UnaryOp ] Primary
		if (isUnaryOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term = primary();
			return new Unary(op, term);
		} else {
			return primary();
		}
	}

	private Expression primary() {
		// Primary --> (Identifier | ArrayRef ) | Literal | ( Expression )
		//             | Type ( Expression )
		Expression e = null;

		if (token.type().equals(TokenType.Identifier)) {
			String id = match(token.type());
			if (token.type().equals(TokenType.LeftBracket)){ 
				match(token.type());
				IntValue size = (IntValue) literal();
				e = new ArrayRef(id, size);
				match(TokenType.RightBracket);
			} else {
				e = new Variable(id);
			}
		} else if (isLiteral()) {
			e = literal();
		} else if (token.type().equals(TokenType.LeftParen)) {
			token = lexer.next();
			e = expression();
			match(TokenType.RightParen);
		} else if (isType()) {
			Operator op = new Operator(token.value());
			match(token.type());
			match(TokenType.LeftParen);
			Expression term = expression();
			match(TokenType.RightParen);
			e = new Unary(op, term);
		} else {
			error("Identifer | Literal | (");
		}

		return e;
	}

	private Value literal() {
		String s = null;
		switch (token.type()) {
		case IntLiteral:
			s = match(TokenType.IntLiteral);
			return new IntValue(Integer.parseInt(s));
		case CharLiteral:
			s = match(TokenType.CharLiteral);
			return new CharValue(s.charAt(0));
		case True:
			s = match(TokenType.True);
			return new BoolValue(true);
		case False:
			s = match(TokenType.False);
			return new BoolValue(false);
		case FloatLiteral:
			s = match(TokenType.FloatLiteral);
			return new FloatValue(Float.parseFloat(s));
		}
		throw new IllegalArgumentException( "should not reach here");
	}


	private boolean isAddOp() {
		return token.type().equals(TokenType.Plus) ||
				token.type().equals(TokenType.Minus);
	}

	private boolean isMultiplyOp() {
		return token.type().equals(TokenType.Multiply) ||
				token.type().equals(TokenType.Divide);
	}

	private boolean isUnaryOp() {
		return token.type().equals(TokenType.Minus) ||
				token.type().equals(TokenType.Not);
	}

	private boolean isEqualityOp() {
		return token.type().equals(TokenType.Equals) ||
				token.type().equals(TokenType.NotEqual);
	}

	private boolean isRelationalOp() {
		return token.type().equals(TokenType.Greater) ||
				token.type().equals(TokenType.GreaterEqual) ||
				token.type().equals(TokenType.Less) ||
				token.type().equals(TokenType.LessEqual);
	}

	private boolean isType() {
		return token.type().equals(TokenType.Int) ||
				token.type().equals(TokenType.Bool) || 
				token.type().equals(TokenType.Float) ||
				token.type().equals(TokenType.Char);
	}

	private boolean isLiteral() {
		return token.type().equals(TokenType.IntLiteral) ||
				isBooleanLiteral() ||
				token.type().equals(TokenType.FloatLiteral) ||
				token.type().equals(TokenType.CharLiteral);
	}

	private boolean isBooleanLiteral() {
		return token.type().equals(TokenType.True) ||
				token.type().equals(TokenType.False);
	}

	public static void main(String args[]) {
		Parser parser  = new Parser(new Lexer(args[0]));
		Program prog = parser.program();
		prog.display();           // display abstract syntax tree
	} //main

} // Parser
