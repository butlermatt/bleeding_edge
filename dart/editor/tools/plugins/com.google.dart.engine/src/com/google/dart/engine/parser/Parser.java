/*
 * Copyright (c) 2012, the Dart project authors.
 * 
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.dart.engine.parser;

import com.google.dart.engine.ast.AdjacentStrings;
import com.google.dart.engine.ast.ArgumentList;
import com.google.dart.engine.ast.ArrayAccess;
import com.google.dart.engine.ast.AssignmentExpression;
import com.google.dart.engine.ast.BinaryExpression;
import com.google.dart.engine.ast.Block;
import com.google.dart.engine.ast.BlockFunctionBody;
import com.google.dart.engine.ast.BooleanLiteral;
import com.google.dart.engine.ast.BreakStatement;
import com.google.dart.engine.ast.CatchClause;
import com.google.dart.engine.ast.ClassDeclaration;
import com.google.dart.engine.ast.Comment;
import com.google.dart.engine.ast.CommentReference;
import com.google.dart.engine.ast.CompilationUnit;
import com.google.dart.engine.ast.CompilationUnitMember;
import com.google.dart.engine.ast.ConditionalExpression;
import com.google.dart.engine.ast.ConstructorDeclaration;
import com.google.dart.engine.ast.ConstructorInitializer;
import com.google.dart.engine.ast.ContinueStatement;
import com.google.dart.engine.ast.Directive;
import com.google.dart.engine.ast.DoStatement;
import com.google.dart.engine.ast.DoubleLiteral;
import com.google.dart.engine.ast.EmptyFunctionBody;
import com.google.dart.engine.ast.EmptyStatement;
import com.google.dart.engine.ast.Expression;
import com.google.dart.engine.ast.ExpressionFunctionBody;
import com.google.dart.engine.ast.ExtendsClause;
import com.google.dart.engine.ast.FieldDeclaration;
import com.google.dart.engine.ast.FieldFormalParameter;
import com.google.dart.engine.ast.ForEachStatement;
import com.google.dart.engine.ast.ForStatement;
import com.google.dart.engine.ast.FormalParameter;
import com.google.dart.engine.ast.FormalParameterList;
import com.google.dart.engine.ast.FunctionBody;
import com.google.dart.engine.ast.FunctionExpression;
import com.google.dart.engine.ast.FunctionExpressionInvocation;
import com.google.dart.engine.ast.FunctionTypedFormalParameter;
import com.google.dart.engine.ast.Identifier;
import com.google.dart.engine.ast.IfStatement;
import com.google.dart.engine.ast.ImplementsClause;
import com.google.dart.engine.ast.ImportCombinator;
import com.google.dart.engine.ast.ImportDirective;
import com.google.dart.engine.ast.ImportExportCombinator;
import com.google.dart.engine.ast.ImportHideCombinator;
import com.google.dart.engine.ast.ImportPrefixCombinator;
import com.google.dart.engine.ast.ImportShowCombinator;
import com.google.dart.engine.ast.InstanceCreationExpression;
import com.google.dart.engine.ast.IntegerLiteral;
import com.google.dart.engine.ast.InterpolationElement;
import com.google.dart.engine.ast.InterpolationExpression;
import com.google.dart.engine.ast.InterpolationString;
import com.google.dart.engine.ast.IsExpression;
import com.google.dart.engine.ast.Label;
import com.google.dart.engine.ast.LabeledStatement;
import com.google.dart.engine.ast.LibraryDirective;
import com.google.dart.engine.ast.ListLiteral;
import com.google.dart.engine.ast.MapLiteral;
import com.google.dart.engine.ast.MapLiteralEntry;
import com.google.dart.engine.ast.MethodInvocation;
import com.google.dart.engine.ast.NamedExpression;
import com.google.dart.engine.ast.NamedFormalParameter;
import com.google.dart.engine.ast.NodeList;
import com.google.dart.engine.ast.NormalFormalParameter;
import com.google.dart.engine.ast.NullLiteral;
import com.google.dart.engine.ast.ParenthesizedExpression;
import com.google.dart.engine.ast.PostfixExpression;
import com.google.dart.engine.ast.PrefixExpression;
import com.google.dart.engine.ast.PrefixedIdentifier;
import com.google.dart.engine.ast.PropertyAccess;
import com.google.dart.engine.ast.ResourceDirective;
import com.google.dart.engine.ast.ReturnStatement;
import com.google.dart.engine.ast.ScriptTag;
import com.google.dart.engine.ast.SimpleFormalParameter;
import com.google.dart.engine.ast.SimpleIdentifier;
import com.google.dart.engine.ast.SimpleStringLiteral;
import com.google.dart.engine.ast.SourceDirective;
import com.google.dart.engine.ast.Statement;
import com.google.dart.engine.ast.StringInterpolation;
import com.google.dart.engine.ast.StringLiteral;
import com.google.dart.engine.ast.SuperExpression;
import com.google.dart.engine.ast.SwitchCase;
import com.google.dart.engine.ast.SwitchDefault;
import com.google.dart.engine.ast.SwitchMember;
import com.google.dart.engine.ast.SwitchStatement;
import com.google.dart.engine.ast.ThisExpression;
import com.google.dart.engine.ast.ThrowStatement;
import com.google.dart.engine.ast.TryStatement;
import com.google.dart.engine.ast.TypeAlias;
import com.google.dart.engine.ast.TypeArgumentList;
import com.google.dart.engine.ast.TypeMember;
import com.google.dart.engine.ast.TypeName;
import com.google.dart.engine.ast.TypeParameter;
import com.google.dart.engine.ast.TypeParameterList;
import com.google.dart.engine.ast.TypedLiteral;
import com.google.dart.engine.ast.VariableDeclaration;
import com.google.dart.engine.ast.VariableDeclarationList;
import com.google.dart.engine.ast.VariableDeclarationStatement;
import com.google.dart.engine.ast.WhileStatement;
import com.google.dart.engine.error.AnalysisError;
import com.google.dart.engine.error.AnalysisErrorListener;
import com.google.dart.engine.scanner.Keyword;
import com.google.dart.engine.scanner.KeywordToken;
import com.google.dart.engine.scanner.StringScanner;
import com.google.dart.engine.scanner.Token;
import com.google.dart.engine.scanner.TokenType;
import com.google.dart.engine.source.Source;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of the class {@code Parser} are used to parse tokens into an AST structure.
 */
public class Parser {
  /*
   * TODO(brianwilkerson) Find commented out references to the method reportError and uncomment
   * them.
   */

  /**
   * Instances of the class {@code FinalConstVarOrType} implement a simple data-holder for a method
   * that needs to return multiple values.
   */
  static class FinalConstVarOrType {
    /**
     * The 'final', 'const' or 'var' keyword, or {@code null} if none was given.
     */
    private Token keyword;

    /**
     * The type, of {@code null} if no type was specified.
     */
    private TypeName type;

    /**
     * Initialize a newly created holder with the given data.
     * 
     * @param keyword the 'final', 'const' or 'var' keyword
     * @param type the type
     */
    public FinalConstVarOrType(Token keyword, TypeName type) {
      this.keyword = keyword;
      this.type = type;
    }

    /**
     * Return the 'final', 'const' or 'var' keyword, or {@code null} if none was given.
     * 
     * @return the 'final', 'const' or 'var' keyword
     */
    public Token getKeyword() {
      return keyword;
    }

    /**
     * Return the type, of {@code null} if no type was specified.
     * 
     * @return the type
     */
    public TypeName getType() {
      return type;
    }
  }

  /**
   * The source being parsed.
   */
  private final Source source;

  /**
   * The error listener that will be informed of any errors that are found during the parse.
   */
  private AnalysisErrorListener errorListener;

  /**
   * The next token to be parsed.
   */
  private Token currentToken;

  private static final String LIBRARY_DIRECTIVE = "library"; //$NON-NLS-1$
  private static final String IMPORT_DIRECTIVE = "import"; //$NON-NLS-1$
  private static final String RESOURCE_DIRECTIVE = "resource"; //$NON-NLS-1$
  private static final String SOURCE_DIRECTIVE = "source"; //$NON-NLS-1$

  /**
   * Initialize a newly created parser.
   * 
   * @param source the source being parsed
   * @param errorListener the error listener that will be informed of any errors that are found
   *          during the parse
   */
  public Parser(Source source, AnalysisErrorListener errorListener) {
    this.source = source;
    this.errorListener = errorListener;
  }

  /**
   * Parse a compilation unit, starting with the given token.
   * 
   * @param token the first token of the compilation unit
   * @return the compilation unit that was parsed
   */
  public CompilationUnit parseCompilationUnit(Token token) {
    currentToken = token;
    return parseCompilationUnit();
  }

  /**
   * Parse an expression, starting with the given token.
   * 
   * @param token the first token of the expression
   * @return the expression that was parsed, or {@code null} if the tokens do not represent a
   *         recognizable expression
   */
  public Expression parseExpression(Token token) {
    currentToken = token;
    return parseExpression();
  }

  /**
   * Parse a statement, starting with the given token.
   * 
   * @param token the first token of the statement
   * @return the statement that was parsed, or {@code null} if the tokens do not represent a
   *         recognizable statement
   */
  public Statement parseStatement(Token token) {
    currentToken = token;
    return parseStatement();
  }

  /**
   * Advance to the next token in the token stream.
   */
  private void advance() {
    currentToken = currentToken.getNext();
  }

  /**
   * Append the character equivalent of the given scalar value to the given builder. Use the start
   * and end indices to report an error, and don't append anything to the builder, if the scalar
   * value is invalid.
   * 
   * @param builder the builder to which the scalar value is to be appended
   * @param scalarValue the value to be appended
   * @param startIndex the index of the first character representing the scalar value
   * @param endIndex the index of the last character representing the scalar value
   */
  private void appendScalarValue(StringBuilder builder, int scalarValue, int startIndex,
      int endIndex) {
    if (scalarValue < 0 || scalarValue > Character.MAX_CODE_POINT
        || (scalarValue >= 0xD800 && scalarValue <= 0xDFFF)) {
      // Illegal escape sequence: invalid code point
      // reportError(ParserErrorCode.INVALID_CODE_POINT));
      return;
    }
    if (scalarValue < Character.MAX_VALUE) {
      builder.append((char) scalarValue);
    } else {
      builder.append(Character.toChars(scalarValue));
    }
  }

  /**
   * Compute the content of a string with the given literal representation.
   * 
   * @param lexeme the literal representation of the string
   * @return the actual value of the string
   */
  private String computeStringValue(String lexeme) {
    if (lexeme.startsWith("@\"\"\"") || lexeme.startsWith("@'''")) { //$NON-NLS-1$ //$NON-NLS-2$
      return lexeme.substring(4, lexeme.length() - 3);
    } else if (lexeme.startsWith("@\"") || lexeme.startsWith("@'")) { //$NON-NLS-1$ //$NON-NLS-2$
      return lexeme.substring(2, lexeme.length() - 1);
    }
    int start = 1;
    int end = lexeme.length() - 1;
    if (lexeme.startsWith("\"\"\"") || lexeme.startsWith("'''")) { //$NON-NLS-1$ //$NON-NLS-2$
      start += 2;
      end -= 2;
    }
    StringBuilder builder = new StringBuilder(end - start + 1);
    int index = start;
    while (index < end) {
      index = translateCharacter(builder, lexeme, index);
    }
    return builder.toString();
  }

  /**
   * Check that the given expression is assignable and report an error if it isn't.
   * 
   * @param expression the expression being checked
   */
  private void ensureAssignable(Expression expression) {
    // Implement this.
    // reportError(ParserErrorCode.ILLEGAL_ASSIGNMENT_TO_NON_ASSIGNABLE);
  }

  /**
   * If the current token is a keyword matching the given string, return it after advancing to the
   * next token. Otherwise throw an exception.
   * 
   * @param keyword the keyword that is expected
   * @return the token that matched the given type
   */
  private Token expect(Keyword keyword) {
    if (!matches(keyword)) {
      // Pass in the error code to use to report the error?
      // reportError(ParserErrorCode.?));
    }
    return getAndAdvance();
  }

  /**
   * If the current token is an identifier matching the given identifier, return it after advancing
   * to the next token. Otherwise throw an exception.
   * 
   * @param identifier the identifier that is expected
   * @return the token that matched the given type
   */
  private Token expect(String identifier) {
    if (!matches(identifier)) {
      // Pass in the error code to use to report the error?
      // reportError(ParserErrorCode.?));
    }
    return getAndAdvance();
  }

  /**
   * If the current token has the expected type, return it after advancing to the next token.
   * Otherwise throw an exception.
   * 
   * @param type the type of token that is expected
   * @return the token that matched the given type
   */
  private Token expect(TokenType type) {
    Token token = currentToken;
    if (!optional(type)) {
      // Pass in the error code to use to report the error?
      // reportError(ParserErrorCode.?));
    }
    return token;
  }

  /**
   * Advance to the next token in the token stream, making it the new current token.
   * 
   * @return the token that was current before this method was invoked
   */
  private Token getAndAdvance() {
    Token token = currentToken;
    advance();
    return token;
  }

  /**
   * Return {@code true} if the given character is a valid hexadecimal digit.
   * 
   * @param character the character being tested
   * @return {@code true} if the character is a valid hexadecimal digit
   */
  private boolean isHexDigit(char character) {
    return ('0' <= character && character <= '9') || ('A' <= character && character <= 'F')
        || ('a' <= character && character <= 'f');
  }

  /**
   * Return {@code true} if the current token matches the given keyword.
   * 
   * @param keyword the keyword that can optionally appear in the current location
   * @return {@code true} if the current token matches the given keyword
   */
  private boolean matches(Keyword keyword) {
    return currentToken.getType() == TokenType.KEYWORD
        && ((KeywordToken) currentToken).getKeyword() == keyword;
  }

  /**
   * Return {@code true} if the current token matches the given identifier.
   * 
   * @param identifier the identifier that can optionally appear in the current location
   * @return {@code true} if the current token matches the given identifier
   */
  private boolean matches(String identifier) {
    return currentToken.getType() == TokenType.IDENTIFIER
        && currentToken.getLexeme().equals(identifier);
  }

  /**
   * Return {@code true} if the current token has the given type.
   * 
   * @param type the type of token that can optionally appear in the current location
   * @return {@code true} if the current token has the given type
   */
  private boolean matches(TokenType type) {
    return currentToken.getType() == type;
  }

  /**
   * If the current token is a keyword matching the given string, then advance to the next token and
   * return {@code true}. Otherwise, return {@code false} without advancing.
   * 
   * @param keyword the keyword that can optionally appear in the current location
   * @return {@code true} if the current token is a keyword matching the given string
   */
  private boolean optional(Keyword keyword) {
    if (matches(keyword)) {
      advance();
      return true;
    }
    return false;
  }

  /**
   * If the current token has the given type, then advance to the next token and return {@code true}
   * . Otherwise, return {@code false} without advancing.
   * 
   * @param type the type of token that can optionally appear in the current location
   * @return {@code true} if the current token has the given type
   */
  private boolean optional(TokenType type) {
    TokenType currentType = currentToken.getType();
    if (currentType != type) {
      if (type == TokenType.GT) {
        if (currentType == TokenType.GT_GT) {
          int offset = currentToken.getOffset();
          Token first = new Token(TokenType.GT, offset);
          Token second = new Token(TokenType.GT, offset + 1);
          second.setNext(currentToken.getNext());
          first.setNext(second);
          currentToken.getPrevious().setNext(first);
          currentToken = second;
          return true;
        } else if (currentType == TokenType.GT_GT_GT) {
          int offset = currentToken.getOffset();
          Token first = new Token(TokenType.GT, offset);
          Token second = new Token(TokenType.GT, offset + 1);
          Token third = new Token(TokenType.GT, offset + 2);
          third.setNext(currentToken.getNext());
          second.setNext(third);
          first.setNext(second);
          currentToken.getPrevious().setNext(first);
          currentToken = second;
          return true;
        }
      }
      return false;
    }
    advance();
    return true;
  }

  /**
   * Parse an additive expression.
   * 
   * <pre>
   * additiveExpression ::=
   *     multiplicativeExpression (additiveOperator multiplicativeExpression)*
   *   | 'super' (additiveOperator multiplicativeExpression)+
   * </pre>
   * 
   * @return the additive expression that was parsed
   */
  private Expression parseAdditiveExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && currentToken.getNext().getType().isAdditiveOperator()) {
      expression = new SuperExpression(getAndAdvance());
    } else {
      expression = parseMultiplicativeExpression();
    }
    while (currentToken.getType().isAdditiveOperator()) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseMultiplicativeExpression());
    }
    return expression;
  }

  /**
   * Parse an argument.
   * 
   * <pre>
   * argument ::=
   *     namedArgument
   *   | expression
   *
   * namedArgument ::=
   *     label expression
   * </pre>
   * 
   * @return the argument that was parsed
   */
  private Expression parseArgument() {
    //
    // Both namedArgument and expression can start with an identifier, but only namedArgument can
    // have an identifier followed by a colon.
    //
    if (matches(TokenType.IDENTIFIER) && peekMatches(TokenType.COLON)) {
      SimpleIdentifier label = new SimpleIdentifier(getAndAdvance());
      Label name = new Label(label, getAndAdvance());
      return new NamedExpression(name, parseExpression());
    } else {
      return parseExpression();
    }
  }

  /**
   * Parse a list of arguments.
   * 
   * <pre>
   * arguments ::=
   *     '(' argumentList? ')'
   * 
   * argumentList ::=
   *     namedArgument (',' namedArgument)*
   *   | expressionList (',' namedArgument)*
   * </pre>
   * 
   * @return the argument list that was parsed
   */
  private ArgumentList parseArgumentList() {
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    List<Expression> arguments = new ArrayList<Expression>();
    if (matches(TokenType.CLOSE_PAREN)) {
      return new ArgumentList(leftParenthesis, arguments, getAndAdvance());
    }
    //
    // Even though unnamed arguments must all appear before any named arguments, we allow them to
    // appear in any order so that we can recover faster.
    //
    Expression argument = parseArgument();
    arguments.add(argument);
    boolean foundNamedArgument = argument instanceof NamedExpression;
    boolean generatedError = false;
    while (optional(TokenType.COMMA)) {
      argument = parseArgument();
      arguments.add(argument);
      if (foundNamedArgument) {
        if (!generatedError && !(argument instanceof NamedExpression)) {
          // Report the error, once, but allow the arguments to be in any order in the AST.
          reportError(ParserErrorCode.POSITIONAL_AFTER_NAMED_ARGUMENT);
          generatedError = true;
        }
      } else if (argument instanceof NamedExpression) {
        foundNamedArgument = true;
      }
    }
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    return new ArgumentList(leftParenthesis, arguments, rightParenthesis);
  }

  /**
   * Parse an assignable expression.
   * 
   * <pre>
   * assignableExpression ::=
   *     primary (arguments* assignableSelector)+
   *   | 'super' assignableSelector
   *   | identifier
   * </pre>
   * 
   * @return the assignable expression that was parsed
   */
  private Expression parseAssignableExpression() {
    if (matches(Keyword.SUPER)) {
      return parseAssignableSelector(new SuperExpression(getAndAdvance()), false);
    }
    //
    // A primary expression can start with an identifier. We resolve the ambiguity by determining
    // whether the primary consists of anything other than an identifier and/or is followed by an
    // assignableSelector.
    //
    Expression expression = parsePrimaryExpression();
    boolean optional = expression instanceof SimpleIdentifier;
    while (true) {
      while (matches(TokenType.OPEN_PAREN)) {
        ArgumentList argumentList = parseArgumentList();
        expression = new FunctionExpressionInvocation(expression, argumentList);
        optional = false;
      }
      Expression selectorExpression = parseAssignableSelector(expression, optional);
      if (selectorExpression == expression) {
        return expression;
      }
      expression = selectorExpression;
      optional = true;
    }
  }

  /**
   * Parse an assignable selector.
   * 
   * <pre>
   * assignableSelector ::=
   *     '[' expression ']'
   *   | '.' identifier
   * </pre>
   * 
   * @param prefix the expression preceding the selector
   * @param optional {@code true} if the selector is optional
   * @return the assignable selector that was parsed
   */
  private Expression parseAssignableSelector(Expression prefix, boolean optional) {
    if (matches(TokenType.OPEN_SQUARE_BRACKET)) {
      Token leftBracket = getAndAdvance();
      Expression index = parseExpression();
      Token rightBracket = expect(TokenType.CLOSE_SQUARE_BRACKET);
      return new ArrayAccess(prefix, leftBracket, index, rightBracket);
    } else if (matches(TokenType.PERIOD)) {
      Token period = getAndAdvance();
      return new PropertyAccess(prefix, period, parseSimpleIdentifier());
    } else {
      if (!optional) {
        // Report the missing selector.
        // reportError(ParserErrorCode.?);
      }
      return prefix;
    }
  }

  /**
   * Parse a bitwise and expression.
   * 
   * <pre>
   * bitwiseAndExpression ::=
   *     equalityExpression ('&' equalityExpression)*
   *   | 'super' ('&' equalityExpression)+
   * </pre>
   * 
   * @return the bitwise and expression that was parsed
   */
  private Expression parseBitwiseAndExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && peekMatches(TokenType.AMPERSAND)) {
      expression = new SuperExpression(getAndAdvance());
    } else {
      expression = parseEqualityExpression();
    }
    while (matches(TokenType.AMPERSAND)) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseEqualityExpression());
    }
    return expression;
  }

  /**
   * Parse a bitwise or expression.
   * 
   * <pre>
   * bitwiseOrExpression ::=
   *     bitwiseXorExpression ('|' bitwiseXorExpression)*
   *   | 'super' ('|' bitwiseXorExpression)+
   * </pre>
   * 
   * @return the bitwise or expression that was parsed
   */
  private Expression parseBitwiseOrExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && peekMatches(TokenType.BAR)) {
      expression = new SuperExpression(getAndAdvance());
    } else {
      expression = parseBitwiseXorExpression();
    }
    while (matches(TokenType.BAR)) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseBitwiseXorExpression());
    }
    return expression;
  }

  /**
   * Parse a bitwise exclusive-or expression.
   * 
   * <pre>
   * bitwiseXorExpression ::=
   *     bitwiseAndExpression ('^' bitwiseAndExpression)*
   *   | 'super' ('^' bitwiseAndExpression)+
   * </pre>
   * 
   * @return the bitwise exclusive-or expression that was parsed
   */
  private Expression parseBitwiseXorExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && peekMatches(TokenType.CARET)) {
      expression = new SuperExpression(getAndAdvance());
    } else {
      expression = parseBitwiseAndExpression();
    }
    while (matches(TokenType.CARET)) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseBitwiseAndExpression());
    }
    return expression;
  }

  /**
   * Parse a block.
   * 
   * <pre>
   * block ::=
   *     '{' statements '}'
   * </pre>
   * 
   * @return the block that was parsed
   */
  private Block parseBlock() {
    Token leftBracket = expect(TokenType.OPEN_CURLY_BRACKET);
    List<Statement> statements = new ArrayList<Statement>();
    while (!matches(TokenType.EOF) && !matches(TokenType.CLOSE_CURLY_BRACKET)) {
      statements.add(parseStatement());
    }
    Token rightBracket = expect(TokenType.CLOSE_CURLY_BRACKET);
    return new Block(leftBracket, statements, rightBracket);
  }

  /**
   * Parse a boolean literal.
   * 
   * <pre>
   * booleanLiteral ::=
   *    'false'
   *  | 'true'
   * </pre>
   * 
   * @return the boolean literal that was parsed
   */
  private BooleanLiteral parseBooleanLiteral() {
    if (matches(Keyword.FALSE)) {
      return new BooleanLiteral(getAndAdvance(), false);
    } else if (matches(Keyword.TRUE)) {
      return new BooleanLiteral(getAndAdvance(), true);
    }
    // Expected a boolean literal
    // reportError(ParserErrorCode.?));
    return null;
  }

  /**
   * Parse a break statement.
   * 
   * <pre>
   * breakStatement ::=
   *     'break' identifier? ';'
   * </pre>
   * 
   * @return the break statement that was parsed
   */
  private Statement parseBreakStatement() {
    Token breakKeyword = expect(Keyword.BREAK);
    SimpleIdentifier label = null;
    if (matches(TokenType.IDENTIFIER)) {
      label = parseSimpleIdentifier();
    }
    Token semicolon = expect(TokenType.SEMICOLON);
    return new BreakStatement(breakKeyword, label, semicolon);
  }

  /**
   * Parse a cascade section.
   * 
   * <pre>
   * cascadeSection ::=
   *     '..' cascadeSelector arguments* (assignableSelector arguments*)* cascadeAssignment?
   *
   * cascadeSelector ::=
   *     '[' expression ']'
   *   | identifier
   *
   * cascadeAssignment ::=
   *     assignmentOperator expressionWithoutCascade
   * </pre>
   * 
   * @param target the target of the method invocation
   * @return the expression representing the cascaded method invocation
   */
  private Expression parseCascadeSection(Expression target) {
    Token period = expect(TokenType.PERIOD_PERIOD);
    Expression expression = target;
    SimpleIdentifier functionName = null;
    if (currentToken.getType() == TokenType.IDENTIFIER) {
      functionName = parseSimpleIdentifier();
    } else if (currentToken.getType() == TokenType.OPEN_SQUARE_BRACKET) {
      Token leftBracket = getAndAdvance();
      Expression index = parseExpression();
      Token rightBracket = expect(TokenType.CLOSE_SQUARE_BRACKET);
      expression = new ArrayAccess(expression, leftBracket, index, rightBracket);
    } else {
      reportError(ParserErrorCode.UNEXPECTED_TOKEN, currentToken.getLexeme());
      return expression;
    }
    if (currentToken.getType() == TokenType.OPEN_PAREN) {
      while (currentToken.getType() == TokenType.OPEN_PAREN) {
        if (functionName != null) {
          expression = new MethodInvocation(expression, period, functionName, parseArgumentList());
          functionName = null;
        } else {
          expression = new FunctionExpressionInvocation(expression, parseArgumentList());
        }
      }
    } else if (functionName != null) {
      expression = new PropertyAccess(expression, period, functionName);
    }
    boolean progress = true;
    while (progress) {
      progress = false;
      Expression selector = parseAssignableSelector(expression, false);
      if (selector != expression) {
        expression = selector;
        progress = true;
        while (currentToken.getType() == TokenType.OPEN_PAREN) {
          expression = new FunctionExpressionInvocation(expression, parseArgumentList());
        }
      }
    }
    if (currentToken.getType().isAssignmentOperator()) {
      Token operator = getAndAdvance();
      ensureAssignable(expression);
      expression = new AssignmentExpression(expression, operator, parseExpression());
    }
    return expression;
  }

  /**
   * Parse a class declaration.
   * 
   * <pre>
   * classDeclaration ::=
   *     'abstract'? 'class' name typeParameterList? extendsClause? implementsClause? '{' memberDefinition* '}'
   * </pre>
   * 
   * @return the class declaration that was parsed
   */
  private ClassDeclaration parseClassDeclaration() {
    Comment comment = parseDocumentationComment();
    Token abstractKeyword = null;
    if (matches(Keyword.ABSTRACT)) {
      abstractKeyword = getAndAdvance();
    }
    Token keyword = expect(Keyword.CLASS);
    SimpleIdentifier name = parseSimpleIdentifier();
    TypeParameterList typeParameters = null;
    if (matches(TokenType.LT)) {
      typeParameters = parseTypeParameterList();
    }
    ExtendsClause extendsClause = null;
    if (matches(Keyword.EXTENDS)) {
      extendsClause = parseExtendsClause();
    }
    ImplementsClause implementsClause = null;
    if (matches(Keyword.IMPLEMENTS)) {
      implementsClause = parseImplementsClause();
    }
    Token leftBracket = expect(TokenType.OPEN_CURLY_BRACKET);
    List<TypeMember> members = new ArrayList<TypeMember>();
    while (!matches(TokenType.EOF) && !matches(TokenType.CLOSE_CURLY_BRACKET)) {
      members.add(parseClassMember());
    }
    Token rightBracket = expect(TokenType.CLOSE_CURLY_BRACKET);
    return new ClassDeclaration(comment, abstractKeyword, keyword, name, typeParameters,
        extendsClause, implementsClause, leftBracket, members, rightBracket);
  }

  /**
   * Parse a class member.
   * 
   * <pre>
   * classMemberDefinition ::=
   *     declaration ';'
   *   | methodSignature functionBody
   * </pre>
   * 
   * @return the class member that was parsed
   */
  private TypeMember parseClassMember() {
    if (matches(Keyword.FACTORY)) {
      return parseFactoryConstructor();
    }
    // TODO(brianwilkerson) Implement the rest of this method.
//    Token start = currentToken;
//    parseModifiers();
    Token getOrSet = null;
    if (matches(Keyword.GET) || matches(Keyword.SET)) {
      getOrSet = getAndAdvance();
    }
//    Token peek = peekAfterType(token);
//    if (isIdentifier(peek) && !token.getLexeme().equals("operator")) {
//      // Skip type.
//      token = peek;
//    }
//    if (token == getOrSet) {
//      token = token.getNext();
//    }
//    if (optional("operator", token)) {
//      token = parseOperatorName(token);
//    } else {
//      SimpleIdentifier name = parseSimpleIdentifier();
//    }
    boolean isField;
    if (matches(TokenType.OPEN_PAREN) || matches(TokenType.PERIOD)) {
      isField = false;
    } else if (matches(TokenType.EQ) || matches(TokenType.SEMICOLON) || matches(TokenType.COMMA)) {
      isField = true;
    } else {
      // reportError(ParserErrorCode.?));
      return null;
    }
    if (isField) {
      Token keyword = null;
      TypeName type = null;
      List<VariableDeclaration> fields = new ArrayList<VariableDeclaration>();
//      if (getOrSet != null) {
//        //listener.recoverableError("unexpected", getOrSet);
//      }
      Token equals = null;
      Expression initializer = null;
      if (matches(TokenType.EQ)) {
        equals = getAndAdvance();
        initializer = parseExpression();
      }
      fields.add(new VariableDeclaration(null, null /* name */, equals, initializer));
      while (optional(TokenType.COMMA)) {
        advance();
        SimpleIdentifier name = parseSimpleIdentifier();
        equals = null;
        initializer = null;
        if (matches(TokenType.EQ)) {
          equals = getAndAdvance();
          initializer = parseExpression();
        }
        fields.add(new VariableDeclaration(null, name, equals, initializer));
      }
      VariableDeclarationList fieldList = new VariableDeclarationList(keyword, type, fields);
      Token semicolon = expect(TokenType.SEMICOLON);
      return new FieldDeclaration(null, null, fieldList, semicolon);
    } else {
//      token = parseQualifiedRestOpt(token);
//      token = parseFormalParameters(token);
//      token = parseInitializersOpt(token);
//      token = parseFunctionBody(token, false);
      return null;
    }
  }

  /**
   * Parse a comment reference from the source between square brackets.
   * 
   * <pre>
   * commentReference ::=
   *     prefixedIdentifier
   * </pre>
   * 
   * @param referenceSource the source occurring between the square brackets within a documentation
   *          comment
   * @param sourceOffset the offset of the first character of the reference source
   * @return the comment reference that was parsed
   */
  private CommentReference parseCommentReference(String referenceSource, int sourceOffset) {
    try {
      final boolean[] errorFound = {false};
      AnalysisErrorListener listener = new AnalysisErrorListener() {
        @Override
        public void onError(AnalysisError error) {
          errorFound[0] = true;
        }
      };
      StringScanner scanner = new StringScanner(null, referenceSource, listener);
      Token firstToken = scanner.tokenize();
      if (!errorFound[0] && firstToken.getType() == TokenType.IDENTIFIER) {
        firstToken.setOffset(firstToken.getOffset() + sourceOffset);
        Token secondToken = firstToken.getNext();
        Token thirdToken = secondToken.getNext();
        Token nextToken;
        Identifier identifier;
        if (secondToken.getType() == TokenType.PERIOD
            && thirdToken.getType() == TokenType.IDENTIFIER) {
          secondToken.setOffset(secondToken.getOffset() + sourceOffset);
          thirdToken.setOffset(thirdToken.getOffset() + sourceOffset);
          identifier = new PrefixedIdentifier(new SimpleIdentifier(firstToken), secondToken,
              new SimpleIdentifier(thirdToken));
          nextToken = thirdToken.getNext();
        } else {
          identifier = new SimpleIdentifier(firstToken);
          nextToken = firstToken.getNext();
        }
        if (nextToken.getType() != TokenType.EOF) {
          // reportError(ParserErrorCode.?);
        }
        return new CommentReference(identifier);
      }
    } catch (Exception exception) {
      // reportError(ParserErrorCode.?);
    }
    return null;
  }

  /**
   * Parse all of the comment references occurring in the given array of documentation comments.
   * 
   * @param tokens the comment tokens representing the documentation comments to be parsed
   * @return the comment references that were parsed
   */
  private List<CommentReference> parseCommentReferences(Token[] tokens) {
    List<CommentReference> references = new ArrayList<CommentReference>();
    for (Token token : tokens) {
      String comment = token.getLexeme();
      int leftIndex = comment.indexOf('[');
      while (leftIndex >= 0) {
        int rightIndex = comment.indexOf(']', leftIndex);
        if (rightIndex >= 0) {
          CommentReference reference = parseCommentReference(
              comment.substring(leftIndex + 1, rightIndex), token.getOffset() + leftIndex + 1);
          if (reference != null) {
            references.add(reference);
          }
        } else {
          rightIndex = leftIndex + 1;
        }
        leftIndex = comment.indexOf('[', rightIndex);
      }
    }
    return references;
  }

  /**
   * Parse a compilation unit.
   * 
   * <pre>
   * compilationUnit ::=
   *     scriptTag? directives* topLevelDeclaration*
   * </pre>
   * 
   * @return the compilation unit that was parsed
   */
  private CompilationUnit parseCompilationUnit() {
    ScriptTag scriptTag = null;
    if (matches(TokenType.SCRIPT_TAG)) {
      scriptTag = new ScriptTag(getAndAdvance());
    }
    boolean libraryDirectiveFound = false;
    boolean declarationFound = false;
    boolean errorGenerated = false;
    List<Directive> directives = new ArrayList<Directive>();
    List<CompilationUnitMember> declarations = new ArrayList<CompilationUnitMember>();
    while (!matches(TokenType.EOF)) {
      if (matches(TokenType.HASH)) {
        if (declarationFound && !errorGenerated) {
          // reportError(ParserErrorCode.?));
          errorGenerated = true;
        }
        Directive directive = parseDirective();
        if (declarations.size() > 0) {
          reportError(ParserErrorCode.DIRECTIVE_OUT_OF_ORDER);
        }
        if (directive instanceof LibraryDirective) {
          if (libraryDirectiveFound) {
            reportError(ParserErrorCode.ONLY_ONE_LIBRARY_DIRECTIVE);
          } else {
            libraryDirectiveFound = true;
          }
        }
        directives.add(directive);
      } else {
        declarations.add(parseCompilationUnitMember());
        declarationFound = true;
      }
    }
    // Check the order of the directives.
    // reportError(ParserErrorCode.?));
    return new CompilationUnit(scriptTag, directives, declarations);
  }

  /**
   * Parse a compilation unit member.
   * 
   * <pre>
   * compilationUnitMember ::=
   *     classDefinition
   *   | functionTypeAlias
   *   | functionSignature functionBody
   *   | returnType? getOrSet identifier formalParameterList functionBody
   *   | (final | const) type? staticFinalDeclarationList ';'
   *   | variableDeclaration ';'
   * </pre>
   * 
   * @return the compilation unit member that was parsed
   */
  private CompilationUnitMember parseCompilationUnitMember() {
    if (matches(Keyword.ABSTRACT) || matches(Keyword.CLASS)) {
      return parseClassDeclaration();
    } else if (matches(Keyword.TYPEDEF)) {
      return parseTypeAlias();
    } else if (matches(TokenType.KEYWORD)) {
      Keyword keyword = ((KeywordToken) currentToken).getKeyword();
      if (keyword == Keyword.VAR || keyword == Keyword.FINAL || keyword == Keyword.CONST) {
        //return parseTopLevelVariables();
      }
    }
    // TODO(brianwilkerson) Finish implementing this
    return null;
  }

  /**
   * Parse a conditional expression.
   * 
   * <pre>
   * conditionalExpression ::=
   *     logicalOrExpression ('?' expressionWithoutCascade ':' expressionWithoutCascade)?
   * </pre>
   * 
   * @return the conditional expression that was parsed
   */
  private Expression parseConditionalExpression() {
    Expression condition = parseLogicalOrExpression();
    if (!matches(TokenType.QUESTION)) {
      return condition;
    }
    Token question = getAndAdvance();
    Expression thenExpression = parseExpressionWithoutCascade();
    Token colon = expect(TokenType.COLON);
    Expression elseExpression = parseExpressionWithoutCascade();
    return new ConditionalExpression(condition, question, thenExpression, colon, elseExpression);
  }

  /**
   * Parse a const expression.
   * 
   * <pre>
   * constExpression ::=
   *     instanceCreationExpression
   *   | listLiteral
   *   | mapLiteral
   * </pre>
   * 
   * @return the const expression that was parsed
   */
  private Expression parseConstExpression() {
    Token keyword = expect(Keyword.CONST);
    if (matches(TokenType.OPEN_SQUARE_BRACKET) || matches(TokenType.INDEX)) {
      return parseListLiteral(keyword, null);
    } else if (matches(TokenType.OPEN_CURLY_BRACKET)) {
      return parseMapLiteral(keyword, null);
    } else if (matches(TokenType.LT)) {
      return parseListOrMapLiteral(keyword);
    }
    return parseInstanceCreationExpression(keyword);
  }

  /**
   * Parse a continue statement.
   * 
   * <pre>
   * continueStatement ::=
   *     'continue' identifier? ';'
   * </pre>
   * 
   * @return the continue statement that was parsed
   */
  private Statement parseContinueStatement() {
    Token keyword = getAndAdvance();
    SimpleIdentifier label = null;
    if (matches(TokenType.IDENTIFIER)) {
      label = parseSimpleIdentifier();
    }
    Token semicolon = expect(TokenType.SEMICOLON);
    return new ContinueStatement(keyword, label, semicolon);
  }

  /**
   * Parse a directive.
   * 
   * <pre>
   * directive ::=
   *     libraryDirective
   *   | importDirective
   *   | sourceDirective
   *   | resourceDirective
   * </pre>
   * 
   * @return the directive that was parsed
   */
  private Directive parseDirective() {
    Token hash = expect(TokenType.HASH);
    if (currentToken.getOffset() != hash.getOffset() + 1) {
      // reportError(ParserErrorCode.?);
    }
    if (matches(TokenType.IDENTIFIER)) {
      String lexeme = currentToken.getLexeme();
      if (lexeme.equals(LIBRARY_DIRECTIVE)) {
        return parseLibraryDirective(hash);
      } else if (lexeme.equals(IMPORT_DIRECTIVE)) {
        return parseImportDirective(hash);
      } else if (lexeme.equals(SOURCE_DIRECTIVE)) {
        return parseSourceDirective(hash);
      } else if (lexeme.equals(RESOURCE_DIRECTIVE)) {
        return parseResourceDirective(hash);
      } else {
        // reportError(ParserErrorCode.?, lexeme);
      }
    }
    return null;
  }

  /**
   * Parse a documentation comment.
   * 
   * <pre>
   * documentationComment ::=
   *     multiLineComment?
   *   | singleLineComment*
   * </pre>
   * 
   * @return the documentation comment that was parsed, or {@code null} if there was no comment
   */
  private Comment parseDocumentationComment() {
    List<Token> commentTokens = new ArrayList<Token>();
    Token commentToken = currentToken.getPrecedingComments();
    while (commentToken != null) {
      if (commentToken.getType() == TokenType.SINGLE_LINE_COMMENT) {
        if (commentToken.getLexeme().startsWith("///")) { //$NON-NLS-1$
          if (commentTokens.size() == 1 && commentTokens.get(0).getLexeme().startsWith("/**")) { //$NON-NLS-1$
            commentTokens.clear();
          }
          commentTokens.add(commentToken);
        }
      } else {
        if (commentToken.getLexeme().startsWith("/**")) { //$NON-NLS-1$
          commentTokens.clear();
          commentTokens.add(commentToken);
        }
      }
      commentToken = commentToken.getNext();
    }
    if (commentTokens.isEmpty()) {
      return null;
    }
    Token[] tokens = commentTokens.toArray(new Token[commentTokens.size()]);
    List<CommentReference> references = parseCommentReferences(tokens);
    return Comment.createDocumentationComment(tokens, references);
  }

  /**
   * Parse a do statement.
   * 
   * <pre>
   * doStatement ::=
   *     'do' statement 'while' '(' expression ')' ';'
   * </pre>
   * 
   * @return the do statement that was parsed
   */
  private Statement parseDoStatement() {
    Token doKeyword = expect(Keyword.DO);
    Statement body = parseStatement();
    Token whileKeyword = expect(Keyword.WHILE);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    Expression condition = parseExpression();
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Token semicolon = expect(TokenType.SEMICOLON);
    return new DoStatement(doKeyword, body, whileKeyword, leftParenthesis, condition,
        rightParenthesis, semicolon);
  }

  /**
   * Parse an empty statement.
   * 
   * <pre>
   * emptyStatement ::=
   *     ';'
   * </pre>
   * 
   * @return the empty statement that was parsed
   */
  private Statement parseEmptyStatement() {
    return new EmptyStatement(getAndAdvance());
  }

  /**
   * Parse an equality expression.
   * 
   * <pre>
   * equalityExpression ::=
   *     relationalExpression (equalityOperator relationalExpression)?
   *   | 'super' equalityOperator relationalExpression
   * </pre>
   * 
   * @return the equality expression that was parsed
   */
  private Expression parseEqualityExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && currentToken.getNext().getType().isEqualityOperator()) {
      expression = new SuperExpression(getAndAdvance());
    } else {
      expression = parseRelationalExpression();
    }
    while (currentToken.getType().isEqualityOperator()) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseRelationalExpression());
    }
    return expression;
  }

  /**
   * Parse an expression that does not contain any cascades.
   * 
   * <pre>
   * expression ::=
   *     assignableExpression assignmentOperator expression
   *   | conditionalExpression cascadeSection*
   * </pre>
   * 
   * @return the expression that was parsed
   */
  private Expression parseExpression() {
    //
    // assignableExpression is a subset of conditionalExpression, so we can parse a conditional
    // expression and then determine whether it is followed by an assignmentOperator, checking for
    // conformance to the restricted grammar after making that determination.
    //
    Expression expression = parseConditionalExpression();
    TokenType tokenType = currentToken.getType();
    if (tokenType == TokenType.PERIOD_PERIOD) {
      while (tokenType == TokenType.PERIOD_PERIOD) {
        expression = parseCascadeSection(expression);
        tokenType = currentToken.getType();
      }
    } else if (tokenType.isAssignmentOperator()) {
      Token operator = getAndAdvance();
      ensureAssignable(expression);
      expression = new AssignmentExpression(expression, operator, parseExpression());
      return expression;
    }
    return expression;
  }

  /**
   * Parse a list of expressions.
   * 
   * <pre>
   * expressionList ::=
   *     expression (',' expression)*
   * </pre>
   * 
   * @return the expression that was parsed
   */
  private List<Expression> parseExpressionList() {
    List<Expression> expressions = new ArrayList<Expression>();
    expressions.add(parseExpression());
    while (optional(TokenType.COMMA)) {
      expressions.add(parseExpression());
    }
    return expressions;
  }

  /**
   * Parse either an expression statement or a declaration.
   * 
   * <pre>
   * topLevelVariables ::=
   *     variableDeclarationList ';'
   * </pre>
   * 
   * @return the expression statement or declaration that was parsed
   */
  private Statement parseExpressionStatementOrDeclaration() {
    // TODO(brianwilkerson) Implement this
    return null;
  }

  /**
   * Parse an expression that does not contain any cascades.
   * 
   * <pre>
   * expressionWithoutCascade ::=
   *     assignableExpression assignmentOperator expressionWithoutCascade
   *   | conditionalExpression
   * </pre>
   * 
   * @return the expression that was parsed
   */
  private Expression parseExpressionWithoutCascade() {
    //
    // assignableExpression is a subset of conditionalExpression, so we can parse a conditional
    // expression and then determine whether it is followed by an assignmentOperator, checking for
    // conformance to the restricted grammar after making that determination.
    //
    Expression expression = parseConditionalExpression();
    if (currentToken.getType().isAssignmentOperator()) {
      Token operator = getAndAdvance();
      ensureAssignable(expression);
      expression = new AssignmentExpression(expression, operator, parseExpressionWithoutCascade());
    }
    return expression;
  }

  /**
   * Parse a class extends clause.
   * 
   * <pre>
   * classExtendsClause ::=
   *     'extends' type
   * </pre>
   * 
   * @return the class extends clause that was parsed
   */
  private ExtendsClause parseExtendsClause() {
    Token keyword = expect(Keyword.EXTENDS);
    TypeName superclass = parseTypeName();
    return new ExtendsClause(keyword, superclass);
  }

  /**
   * Parse a factory constructor.
   * 
   * <pre>
   * factoryConstructor ::=
   *     factoryConstructorSignature functionBody
   * 
   * factoryConstructorSignature ::=
   *     'factory' qualified  ('.' identifier)? formalParameterList
   * </pre>
   * 
   * @return the factory constructor that was parsed
   */
  private ConstructorDeclaration parseFactoryConstructor() {
    Comment comment = parseDocumentationComment();
    Token keyword = expect(Keyword.FACTORY);
    Identifier returnType = parseSimpleIdentifier();
    Token period = null;
    SimpleIdentifier name = null;
    if (matches(TokenType.PERIOD)) {
      period = getAndAdvance();
      name = parseSimpleIdentifier();
      if (matches(TokenType.PERIOD)) {
        returnType = new PrefixedIdentifier((SimpleIdentifier) returnType, period, name);
        period = getAndAdvance();
        name = parseSimpleIdentifier();
      }
    }
    FormalParameterList parameters = parseFormalParameterList();
    Token colon = null;
    List<ConstructorInitializer> initializers = new ArrayList<ConstructorInitializer>();
    FunctionBody body = parseFunctionBody(true, false);
    return new ConstructorDeclaration(comment, keyword, returnType, period, name, parameters,
        colon, initializers, body);
  }

  /**
   * Parse the 'final', 'const', 'var' or type preceding a variable declaration.
   * 
   * <pre>
   * finalConstVarOrType ::=
   *   | 'final' type?
   *   | 'const' type?
   *   | 'var'
   *   | type
   * </pre>
   * 
   * @param optional {@code true} if the keyword and type are optional
   * @return the 'final', 'const', 'var' or type that was parsed
   */
  private FinalConstVarOrType parseFinalConstVarOrType(boolean optional) {
    Token keyword = null;
    TypeName type = null;
    if (matches(Keyword.FINAL) || matches(Keyword.CONST)) {
      keyword = getAndAdvance();
      if (peekMatches(TokenType.IDENTIFIER) || peekMatches(TokenType.LT)
          || peekMatches(Keyword.THIS)) {
        type = parseTypeName();
      }
    } else if (matches(Keyword.VAR)) {
      keyword = getAndAdvance();
    } else {
      if (peekMatches(TokenType.IDENTIFIER) || peekMatches(TokenType.LT)
          || peekMatches(Keyword.THIS)) {
        type = parseReturnType();
      } else if (!optional) {
        // reportError(ParserErrorCode.?);
      }
    }
    return new FinalConstVarOrType(keyword, type);
  }

  /**
   * Parse a formal parameter.
   * 
   * <pre>
   * defaultFormalParameter:
   *     normalFormalParameter ('=' expression)?
   * </pre>
   * 
   * @return the formal parameter that was parsed
   */
  private FormalParameter parseFormalParameter() {
    NormalFormalParameter parameter = parseNormalFormalParameter();
    if (matches(TokenType.EQ)) {
      // Validate that these are only used for optional parameters.
      // reportError(ParserErrorCode.?));
      Token equals = getAndAdvance();
      Expression defaultValue = parseExpression();
      return new NamedFormalParameter(parameter, equals, defaultValue);
    }
    return parameter;
  }

  /**
   * Parse a list of formal parameters.
   * 
   * <pre>
   * formalParameterList ::=
   *     '(' ')'
   *   | '(' normalFormalParameters (',' namedFormalParameters)? ')'
   *   | '(' namedFormalParameters ')'
   *
   * normalFormalParameters ::=
   *     normalFormalParameter (',' normalFormalParameter)*
   *
   * namedFormalParameters ::=
   *     '[' defaultFormalParameter (',' defaultFormalParameter)* ']'
   * </pre>
   * 
   * @return the formal parameters that were parsed
   */
  private FormalParameterList parseFormalParameterList() {
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    List<FormalParameter> parameters = new ArrayList<FormalParameter>();
    if (matches(TokenType.CLOSE_PAREN)) {
      return new FormalParameterList(leftParenthesis, parameters, null, null, getAndAdvance());
    }
    //
    // Even though it is invalid to have named parameters outside the square brackets, or unnamed
    // parameters inside the square brackets, we allow both in order to recover better.
    //
    Token leftBracket = null;
    do {
      if (matches(TokenType.OPEN_SQUARE_BRACKET)) {
        if (leftBracket != null) {
          // reportError(ParserErrorCode.?));
        }
        leftBracket = getAndAdvance();
      }
      FormalParameter parameter = parseFormalParameter();
      if (leftBracket == null) {
        if (parameter instanceof NamedFormalParameter) {
          // reportError(ParserErrorCode.?));
        }
      } else {
        if (!(parameter instanceof NamedFormalParameter)) {
          // reportError(ParserErrorCode.?));
        }
      }
      parameters.add(parameter);
    } while (optional(TokenType.COMMA));
    Token rightBracket = leftBracket == null ? null : expect(TokenType.CLOSE_SQUARE_BRACKET);
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    return new FormalParameterList(leftParenthesis, parameters, leftBracket, rightBracket,
        rightParenthesis);
  }

  /**
   * Parse a for statement.
   * 
   * <pre>
   * forStatement ::=
   *     'for' '(' forLoopParts ')' statement
   * 
   * forLoopParts ::=
   *     forInitializerStatement expression? ';' expressionList?
   *   | declaredIdentifier 'in' expression
   *   | identifier 'in' expression
   * 
   * forInitializerStatement ::=
   *     variableDeclarationList ';'
   *   | expression? ';'
   * </pre>
   * 
   * @return the for statement that was parsed
   */
  private Statement parseForStatement() {
    Token forKeyword = expect(Keyword.FOR);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    VariableDeclarationList variableList = null;
    Expression initialization = null;
    if (!matches(TokenType.SEMICOLON)) {
      if (matches(TokenType.IDENTIFIER) && peekMatches(Keyword.IN)) {
        List<VariableDeclaration> variables = new ArrayList<VariableDeclaration>();
        variables.add(new VariableDeclaration(null, parseSimpleIdentifier(), null, null));
        variableList = new VariableDeclarationList(null, null, variables);
      } else {
        variableList = parseVariableDeclarationList();
      }
      if (matches(Keyword.IN)) {
        NodeList<VariableDeclaration> variables = variableList.getVariables();
        if (variables.size() > 1) {
          // reportError(ParserErrorCode.?);
        }
        VariableDeclaration variable = variables.get(0);
        if (variable.getInitializer() != null) {
          // reportError(ParserErrorCode.?);
        }
        SimpleFormalParameter loopParameter = new SimpleFormalParameter(variableList.getKeyword(),
            variableList.getType(), variable.getName());
        Token inKeyword = expect(Keyword.IN);
        Expression iterator = parseExpression();
        Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
        Statement body = parseStatement();
        return new ForEachStatement(forKeyword, leftParenthesis, loopParameter, inKeyword,
            iterator, rightParenthesis, body);
      }
      // Ensure that the loop parameter is not final.
      // reportError(ParserErrorCode.?);
    }
    Token leftSeparator = expect(TokenType.SEMICOLON);
    Expression condition = null;
    if (!matches(TokenType.SEMICOLON)) {
      condition = parseExpression();
    }
    Token rightSeparator = expect(TokenType.SEMICOLON);
    List<Expression> updaters = null;
    if (!matches(TokenType.CLOSE_PAREN)) {
      updaters = parseExpressionList();
    }
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Statement body = parseStatement();
    return new ForStatement(forKeyword, leftParenthesis, variableList, initialization,
        leftSeparator, condition, rightSeparator, updaters, rightParenthesis, body);
  }

  /**
   * Parse a function body.
   * 
   * <pre>
   * functionBody ::=
   *     '=>' expression ';'
   *   | block
   * </pre>
   * 
   * @param inConstructor {@code true} if the function body is being parsed as part of a constructor
   * @param inExpression {@code true} if the function body is being parsed as part of an expression
   * @return the function body that was parsed
   */
  private FunctionBody parseFunctionBody(boolean inConstructor, boolean inExpression) {
    if (inConstructor && matches(TokenType.SEMICOLON)) {
      return new EmptyFunctionBody(getAndAdvance());
    } else if (matches(TokenType.FUNCTION)) {
      Token functionDefinition = getAndAdvance();
      Expression expression = parseExpression();
      Token semicolon = null;
      if (!inExpression) {
        semicolon = expect(TokenType.SEMICOLON);
      }
      return new ExpressionFunctionBody(functionDefinition, expression, semicolon);
    } else if (matches(TokenType.OPEN_CURLY_BRACKET)) {
      return new BlockFunctionBody(parseBlock());
    } else {
      // Invalid function body
      // reportError(ParserErrorCode.?));
      return null;
    }
  }

//  /**
//   * Parse a function signature.
//   * 
//   * <pre>
//   * functionSignature ::=
//   *     returnType? identifier formalParameterList
//   * </pre>
//   * 
//   * @return the function signature that was parsed
//   */
//  private void parseFunctionSignature() {
//    TypeName returnType = parseReturnType();
//    SimpleIdentifier functionName = null;
//    if (!matches(TokenType.IDENTIFIER)) {
//      if (returnType.getName() instanceof SimpleIdentifier && returnType.getTypeArguments() == null) {
//        functionName = (SimpleIdentifier) returnType.getName();
//        returnType = null;
//      } else {
//        // Missing identifier
//        // reportError(ParserErrorCode.?));
//      }
//    } else {
//      functionName = parseSimpleIdentifier();
//    }
//    parseFormalParameterList();
//    // TODO(brianwilkerson) Implement this
//  }

  /**
   * Parse a function expression.
   * 
   * <pre>
   * functionExpression ::=
   *     (returnType? identifier)? formalParameterList functionExpressionBody
   * 
   * functionExpressionBody ::=
   *     '=>' expression
   *   | block
   * </pre>
   * 
   * @return the function expression that was parsed
   */
  private Expression parseFunctionExpression() {
    TypeName returnType = null;
    if (matches(Keyword.VOID)
        || (matches(TokenType.IDENTIFIER) && (peekMatches(TokenType.IDENTIFIER) || peekMatches(TokenType.LT)))) {
      returnType = parseReturnType();
    }
    SimpleIdentifier name = null;
    if (matches(TokenType.IDENTIFIER)) {
      name = parseSimpleIdentifier();
    }
    FormalParameterList parameters = parseFormalParameterList();
    FunctionBody body = parseFunctionBody(false, true);
    return new FunctionExpression(returnType, name, parameters, body);
  }

  /**
   * Parse an if statement.
   * 
   * <pre>
   * ifStatement ::=
   *     'if' '(' expression ')' statement ('else' statement)?
   * </pre>
   * 
   * @return the if statement that was parsed
   */
  private Statement parseIfStatement() {
    Token ifKeyword = expect(Keyword.IF);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    Expression condition = parseExpression();
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Statement thenStatement = parseStatement();
    Token elseKeyword = null;
    Statement elseStatement = null;
    if (matches(Keyword.ELSE)) {
      elseKeyword = getAndAdvance();
      elseStatement = parseStatement();
    }
    return new IfStatement(ifKeyword, leftParenthesis, condition, rightParenthesis, thenStatement,
        elseKeyword, elseStatement);
  }

  /**
   * Parse an implements clause.
   * 
   * <pre>
   * implementsClause ::=
   *     'implements' type (',' type)*
   * </pre>
   * 
   * @return the implements clause that was parsed
   */
  private ImplementsClause parseImplementsClause() {
    Token keyword = expect(Keyword.IMPLEMENTS);
    List<TypeName> interfaces = new ArrayList<TypeName>();
    interfaces.add(parseTypeName());
    while (optional(TokenType.COMMA)) {
      interfaces.add(parseTypeName());
    }
    return new ImplementsClause(keyword, interfaces);
  }

  /**
   * Parse an import directive.
   * 
   * <pre>
   * importDirective ::=
   *     '#import' '(' stringLiteral (',' export)? (',' combinator )* (',' prefix)? ')' ';'
   * 
   * export ::=
   *     'export:' booleanLiteral
   * 
   * combinator ::=
   *     'show:' listLiteral
   *   | 'hide:' listLiteral
   * 
   * prefix ::=
   *     'prefix:' stringLiteral
   * </pre>
   * 
   * @param hash the token for the hash sign preceding the import keyword
   * @return the import directive that was parsed
   */
  private ImportDirective parseImportDirective(Token hash) {
    Token keyword = expect(IMPORT_DIRECTIVE);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    StringLiteral libraryUri = parseStringLiteral();
    List<ImportCombinator> combinators = new ArrayList<ImportCombinator>();
    boolean hasExport = false;
    boolean hasShow = false;
    boolean hasHide = false;
    boolean hasPrefix = false;
    while (matches(TokenType.COMMA)) {
      Token comma = getAndAdvance();
      Token kind = expect(TokenType.IDENTIFIER);
      Token colon = expect(TokenType.COLON);
      if (kind.getLexeme().equals("export")) { //$NON-NLS-1$
        if (hasExport) {
          // Duplicated combinator
          // reportError(ParserErrorCode.?));
        }
        hasExport = true;
        BooleanLiteral shouldExport = parseBooleanLiteral();
        combinators.add(new ImportExportCombinator(comma, kind, colon, shouldExport));
      } else if (kind.getLexeme().equals("show")) { //$NON-NLS-1$
        if (hasShow) {
          // Duplicated combinator
          // reportError(ParserErrorCode.?));
        }
        hasShow = true;
        ListLiteral shownNames = parseListLiteral(null, null);
        combinators.add(new ImportShowCombinator(comma, kind, colon, shownNames));
      } else if (kind.getLexeme().equals("hide")) { //$NON-NLS-1$
        if (hasHide) {
          // Duplicated combinator
          // reportError(ParserErrorCode.?));
        }
        hasHide = true;
        ListLiteral hiddenNames = parseListLiteral(null, null);
        combinators.add(new ImportHideCombinator(comma, kind, colon, hiddenNames));
      } else if (kind.getLexeme().equals("prefix")) { //$NON-NLS-1$
        if (hasPrefix) {
          // Duplicated combinator
          // reportError(ParserErrorCode.?));
        }
        hasPrefix = true;
        StringLiteral prefix = parseStringLiteral();
        combinators.add(new ImportPrefixCombinator(comma, kind, colon, prefix));
      } else {
        // Illegal combinator.
        // reportError(ParserErrorCode.?, kind.getLexeme()));
      }
    }
    // Check the order of the combinators.
    // reportError(ParserErrorCode.?));
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Token semicolon = expect(TokenType.SEMICOLON);
    return new ImportDirective(hash, keyword, leftParenthesis, libraryUri, combinators,
        rightParenthesis, semicolon);
  }

  /**
   * Parse an instance creation expression.
   * 
   * <pre>
   * instanceCreationExpression ::=
   *     ('new' | 'const') type ('.' identifier)? argumentList
   * </pre>
   * 
   * @param keyword the 'new' or 'const' keyword that introduces the expression
   * @return the instance creation expression that was parsed
   */
  private InstanceCreationExpression parseInstanceCreationExpression(Token keyword) {
    TypeName type = parseTypeName();
    Token period = null;
    SimpleIdentifier identifier = null;
    if (matches(TokenType.PERIOD)) {
      period = getAndAdvance();
      identifier = parseSimpleIdentifier();
    }
    ArgumentList argumentList = parseArgumentList();
    return new InstanceCreationExpression(keyword, type, period, identifier, argumentList);
  }

  /**
   * Parse a library directive.
   * 
   * <pre>
   * libraryDirective ::=
   *     '#library' '(' stringLiteral ')' ';'
   * </pre>
   * 
   * @param hash the token for the hash sign preceding the library keyword
   * @return the library directive that was parsed
   */
  private LibraryDirective parseLibraryDirective(Token hash) {
    Token keyword = expect(LIBRARY_DIRECTIVE);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    StringLiteral libraryName = parseStringLiteral();
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Token semicolon = expect(TokenType.SEMICOLON);
    return new LibraryDirective(hash, keyword, leftParenthesis, libraryName, rightParenthesis,
        semicolon);
  }

  /**
   * Parse a list literal.
   * 
   * <pre>
   * listLiteral ::=
   *     'const'? typeArguments? '[' (expressionList ','?)? ']'
   * </pre>
   * 
   * @param modifier the 'const' modifier appearing before the literal, or {@code null} if there is
   *          no modifier
   * @param typeArguments the type arguments appearing before the literal, or {@code null} if there
   *          are no type arguments
   * @return the list literal that was parsed
   */
  private ListLiteral parseListLiteral(Token modifier, TypeArgumentList typeArguments) {
    if (matches(TokenType.INDEX)) {
      Token leftBracket = new Token(TokenType.OPEN_SQUARE_BRACKET, currentToken.getOffset());
      ArrayList<Expression> elements = new ArrayList<Expression>();
      Token rightBracket = new Token(TokenType.CLOSE_SQUARE_BRACKET, currentToken.getOffset() + 1);
      return new ListLiteral(modifier, typeArguments, leftBracket, elements, rightBracket);
    }
    Token leftBracket = expect(TokenType.OPEN_SQUARE_BRACKET);
    ArrayList<Expression> elements = new ArrayList<Expression>();
    if (matches(TokenType.CLOSE_SQUARE_BRACKET)) {
      return new ListLiteral(modifier, typeArguments, leftBracket, elements, getAndAdvance());
    }
    elements.add(parseExpression());
    while (optional(TokenType.COMMA)) {
      if (matches(TokenType.CLOSE_SQUARE_BRACKET)) {
        return new ListLiteral(modifier, typeArguments, leftBracket, elements, getAndAdvance());
      }
      elements.add(parseExpression());
    }
    Token rightBracket = expect(TokenType.CLOSE_SQUARE_BRACKET);
    return new ListLiteral(modifier, typeArguments, leftBracket, elements, rightBracket);
  }

  /**
   * Parse a list or map literal.
   * 
   * <pre>
   * listOrMapLiteral ::=
   *     listLiteral
   *   | mapLiteral
   * </pre>
   * 
   * @param modifier the 'const' modifier appearing before the literal, or {@code null} if there is
   *          no modifier
   * @return the list or map literal that was parsed
   */
  private TypedLiteral parseListOrMapLiteral(Token modifier) {
    TypeArgumentList typeArguments = parseTypeArgumentList();
    if (matches(TokenType.OPEN_CURLY_BRACKET)) {
      return parseMapLiteral(modifier, typeArguments);
    } else if (matches(TokenType.OPEN_SQUARE_BRACKET) || matches(TokenType.INDEX)) {
      return parseListLiteral(modifier, typeArguments);
    }
    reportError(ParserErrorCode.EXPECTED_LIST_OR_MAP_LITERAL);
    return null;
  }

  /**
   * Parse a logical and expression.
   * 
   * <pre>
   * logicalAndExpression ::=
   *     bitwiseOrExpression ('&&' bitwiseOrExpression)*
   * </pre>
   * 
   * @return the logical and expression that was parsed
   */
  private Expression parseLogicalAndExpression() {
    Expression expression = parseBitwiseOrExpression();
    while (matches(TokenType.AMPERSAND_AMPERSAND)) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseBitwiseOrExpression());
    }
    return expression;
  }

  /**
   * Parse a logical or expression.
   * 
   * <pre>
   * logicalOrExpression ::=
   *     logicalAndExpression ('||' logicalAndExpression)*
   * </pre>
   * 
   * @return the logical or expression that was parsed
   */
  private Expression parseLogicalOrExpression() {
    Expression expression = parseLogicalAndExpression();
    while (matches(TokenType.BAR_BAR)) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseLogicalAndExpression());
    }
    return expression;
  }

  /**
   * Parse a map literal.
   * 
   * <pre>
   * mapLiteral ::=
   *     'const'? typeArguments? '{' (mapLiteralEntry (',' mapLiteralEntry)* ','?)? '}'
   * </pre>
   * 
   * @return the map literal that was parsed
   */
  private MapLiteral parseMapLiteral(Token modifier, TypeArgumentList typeArguments) {
    Token leftBracket = expect(TokenType.OPEN_CURLY_BRACKET);
    ArrayList<MapLiteralEntry> entries = new ArrayList<MapLiteralEntry>();
    if (matches(TokenType.CLOSE_CURLY_BRACKET)) {
      return new MapLiteral(modifier, typeArguments, leftBracket, entries, getAndAdvance());
    }
    entries.add(parseMapLiteralEntry());
    while (optional(TokenType.COMMA)) {
      if (matches(TokenType.CLOSE_CURLY_BRACKET)) {
        return new MapLiteral(modifier, typeArguments, leftBracket, entries, getAndAdvance());
      }
      entries.add(parseMapLiteralEntry());
    }
    Token rightBracket = expect(TokenType.CLOSE_CURLY_BRACKET);
    return new MapLiteral(modifier, typeArguments, leftBracket, entries, rightBracket);
  }

  /**
   * Parse a map literal entry.
   * 
   * <pre>
   * mapLiteralEntry ::=
   *     stringLiteral ':' expression
   * </pre>
   * 
   * @return the map literal entry that was parsed
   */
  private MapLiteralEntry parseMapLiteralEntry() {
    StringLiteral key = parseStringLiteral();
    Token separator = expect(TokenType.COLON);
    Expression value = parseExpression();
    return new MapLiteralEntry(key, separator, value);
  }

  /**
   * Parse a multiplicative expression.
   * 
   * <pre>
   * multiplicativeExpression ::=
   *     unaryExpression (multiplicativeOperator unaryExpression)*
   *   | 'super' (multiplicativeOperator unaryExpression)+
   * </pre>
   * 
   * @return the multiplicative expression that was parsed
   */
  private Expression parseMultiplicativeExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && currentToken.getNext().getType().isMultiplicativeOperator()) {
      expression = new SuperExpression(getAndAdvance());
    } else {
      expression = parseUnaryExpression();
    }
    while (currentToken.getType().isMultiplicativeOperator()) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseUnaryExpression());
    }
    return expression;
  }

  /**
   * Parse a new expression.
   * 
   * <pre>
   * newExpression ::=
   *     instanceCreationExpression
   * </pre>
   * 
   * @return the new expression that was parsed
   */
  private InstanceCreationExpression parseNewExpression() {
    return parseInstanceCreationExpression(expect(Keyword.NEW));
  }

  /**
   * Parse a non-labeled statement.
   * 
   * <pre>
   * nonLabeledStatement ::=
   *     block
   *   | assertStatement
   *   | breakStatement
   *   | continueStatement
   *   | doStatement
   *   | forStatement
   *   | ifStatement
   *   | returnStatement
   *   | switchStatement
   *   | throwStatement
   *   | tryStatement
   *   | whileStatement
   *   | variableDeclarationList ';'
   *   | expressionStatement
   *   | functionSignature functionBody
   * </pre>
   * 
   * @return the non-labeled statement that was parsed
   */
  private Statement parseNonLabeledStatement() {
    if (matches(TokenType.OPEN_CURLY_BRACKET)) {
      return parseBlock();
    } else if (matches(TokenType.KEYWORD)) {
      Keyword keyword = ((KeywordToken) currentToken).getKeyword();
      if (keyword == Keyword.BREAK) {
        return parseBreakStatement();
      } else if (keyword == Keyword.CONTINUE) {
        return parseContinueStatement();
      } else if (keyword == Keyword.DO) {
        return parseDoStatement();
      } else if (keyword == Keyword.FOR) {
        return parseForStatement();
      } else if (keyword == Keyword.IF) {
        return parseIfStatement();
      } else if (keyword == Keyword.RETURN) {
        return parseReturnStatement();
      } else if (keyword == Keyword.SWITCH) {
        return parseSwitchStatement();
      } else if (keyword == Keyword.THROW) {
        return parseThrowStatement();
      } else if (keyword == Keyword.TRY) {
        return parseTryStatement();
      } else if (keyword == Keyword.WHILE) {
        return parseWhileStatement();
      } else if (keyword == Keyword.VAR || keyword == Keyword.FINAL || keyword == Keyword.CONST) {
        return parseVariableDeclarationStatement();
      } else {
        // Expected a statement
        // reportError(ParserErrorCode.?));
        return null;
      }
    } else if (matches(TokenType.SEMICOLON)) {
      return parseEmptyStatement();
    } else {
      return parseExpressionStatementOrDeclaration();
    }
  }

  /**
   * Parse a normal formal parameter.
   * 
   * <pre>
   * normalFormalParameter ::=
   *     functionSignature
   *   | fieldFormalParameter
   *   | simpleFormalParameter
   * 
   * functionSignature:
   *     returnType? identifier formalParameterList
   * 
   * fieldFormalParameter ::=
   *     finalConstVarOrType? 'this' '.' identifier
   * </pre>
   * 
   * @return the normal formal parameter that was parsed
   */
  private NormalFormalParameter parseNormalFormalParameter() {
    FinalConstVarOrType holder = parseFinalConstVarOrType(true);
    Token thisKeyword = null;
    Token period = null;
    if (matches(Keyword.THIS)) {
      // Validate that field initializers are only used in constructors.
      // reportError(ParserErrorCode.?));
      thisKeyword = getAndAdvance();
      period = expect(TokenType.PERIOD);
    }
    SimpleIdentifier identifier = parseSimpleIdentifier();
    if (matches(TokenType.OPEN_PAREN)) {
      if (thisKeyword != null) {
        // Decide how to recover from this error.
        // reportError(ParserErrorCode.?);
      }
      FormalParameterList parameters = parseFormalParameterList();
      return new FunctionTypedFormalParameter(holder.getKeyword(), holder.getType(), identifier,
          parameters);
    }
    // Validate that the type is not void because this is not a function signature.
    // reportError(ParserErrorCode.?));
    if (thisKeyword != null) {
      return new FieldFormalParameter(holder.getKeyword(), holder.getType(), thisKeyword, period,
          identifier);
    }
    return new SimpleFormalParameter(holder.getKeyword(), holder.getType(), identifier);
  }

  /**
   * Parse a postfix expression.
   * 
   * <pre>
   * postfixExpression ::=
   *     assignableExpression postfixOperator
   *   | primary selector*
   *
   * selector ::=
   *     assignableSelector
   *   | argumentList
   * </pre>
   * 
   * @return the postfix expression that was parsed
   */
  private Expression parsePostfixExpression() {
    Expression operand = parseAssignableExpression();
    if (!currentToken.getType().isIncrementOperator()) {
      return operand;
    }
    Token operator = getAndAdvance();
    return new PostfixExpression(operand, operator);
  }

  /**
   * Parse a prefixed identifier.
   * 
   * <pre>
   * prefixedIdentifier ::=
   *     identifier ('.' identifier)?
   * </pre>
   * 
   * @return the prefixed identifier that was parsed
   */
  private Identifier parsePrefixedIdentifier() {
    SimpleIdentifier qualifier = parseSimpleIdentifier();
    if (!matches(TokenType.PERIOD)) {
      return qualifier;
    }
    Token period = getAndAdvance();
    SimpleIdentifier qualified = parseSimpleIdentifier();
    return new PrefixedIdentifier(qualifier, period, qualified);
  }

  /**
   * Parse a primary expression.
   * 
   * <pre>
   * primary ::=
   *     thisExpression
   *   | 'super' assignableSelector
   *   | functionExpression
   *   | literal
   *   | identifier
   *   | newExpression
   *   | constObjectExpression
   *   | '(' expression ')'
   * 
   * literal ::=
   *     nullLiteral
   *   | booleanLiteral
   *   | numericLiteral
   *   | stringLiteral
   *   | mapLiteral
   *   | listLiteral
   * </pre>
   * 
   * @return the primary expression that was parsed
   */
  private Expression parsePrimaryExpression() {
    if (matches(Keyword.THIS)) {
      return new ThisExpression(getAndAdvance());
    } else if (matches(Keyword.SUPER)) {
      return parseAssignableSelector(new SuperExpression(getAndAdvance()), false);
    } else if (matches(Keyword.NULL)) {
      return new NullLiteral(getAndAdvance());
    } else if (matches(Keyword.FALSE)) {
      return new BooleanLiteral(getAndAdvance(), false);
    } else if (matches(Keyword.TRUE)) {
      return new BooleanLiteral(getAndAdvance(), true);
    } else if (matches(TokenType.DOUBLE)) {
      Token token = getAndAdvance();
      return new DoubleLiteral(token, Double.parseDouble(token.getLexeme()));
    } else if (matches(TokenType.HEXADECIMAL)) {
      Token token = getAndAdvance();
      return new IntegerLiteral(token, new BigInteger(token.getLexeme().substring(2), 16));
    } else if (matches(TokenType.INT)) {
      Token token = getAndAdvance();
      return new IntegerLiteral(token, new BigInteger(token.getLexeme()));
    } else if (matches(TokenType.STRING)) {
      return parseStringLiteral();
    } else if (matches(TokenType.OPEN_CURLY_BRACKET)) {
      return parseMapLiteral(null, null);
    } else if (matches(TokenType.OPEN_SQUARE_BRACKET) || matches(TokenType.INDEX)) {
      return parseListLiteral(null, null);
    } else if (matches(TokenType.IDENTIFIER)) {
      return parsePrefixedIdentifier();
    } else if (matches(Keyword.NEW)) {
      return parseNewExpression();
    } else if (matches(Keyword.CONST)) {
      return parseConstExpression();
    } else if (matches(TokenType.OPEN_PAREN)) {
      Token leftParenthesis = getAndAdvance();
      Expression expression = parseExpression();
      Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
      return new ParenthesizedExpression(leftParenthesis, expression, rightParenthesis);
    } else if (matches(TokenType.LT)) {
      return parseListOrMapLiteral(null);
    } else {
      return parseFunctionExpression();
    }
  }

  /**
   * Parse a relational expression.
   * 
   * <pre>
   * relationalExpression ::=
   *     shiftExpression ('is' type | 'as' type | relationalOperator shiftExpression)?
   *   | 'super' relationalOperator shiftExpression
   * </pre>
   * 
   * @return the relational expression that was parsed
   */
  private Expression parseRelationalExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && currentToken.getNext().getType().isRelationalOperator()) {
      expression = new SuperExpression(getAndAdvance());
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseShiftExpression());
      return expression;
    }
    expression = parseShiftExpression();
    while (matches(Keyword.AS) || matches(Keyword.IS)
        || currentToken.getType().isRelationalOperator()) {
      if (currentToken.getType().isRelationalOperator()) {
        Token operator = getAndAdvance();
        expression = new BinaryExpression(expression, operator, parseShiftExpression());
      } else if (matches(Keyword.IS)) {
        Token isOperator = getAndAdvance();
        Token notOperator = null;
        if (matches(TokenType.BANG)) {
          notOperator = getAndAdvance();
        }
        expression = new IsExpression(expression, isOperator, notOperator, parseTypeName());
      } else {
        Token isOperator = getAndAdvance();
        expression = new IsExpression(expression, isOperator, null, parseTypeName());
      }
    }
    return expression;
  }

  /**
   * Parse a resource directive.
   * 
   * <pre>
   * resourceDirective ::=
   *     '#resource' '(' stringLiteral ')' ';'
   * </pre>
   * 
   * @param hash the token for the hash sign preceding the resource keyword
   * @return the resource directive that was parsed
   */
  private ResourceDirective parseResourceDirective(Token hash) {
    Token keyword = expect(RESOURCE_DIRECTIVE);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    StringLiteral resourceUri = parseStringLiteral();
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Token semicolon = expect(TokenType.SEMICOLON);
    return new ResourceDirective(hash, keyword, leftParenthesis, resourceUri, rightParenthesis,
        semicolon);
  }

  /**
   * Parse a return statement.
   * 
   * <pre>
   * returnStatement ::=
   *     'return' expression? ';'
   * </pre>
   * 
   * @return the return statement that was parsed
   */
  private Statement parseReturnStatement() {
    Token returnKeyword = expect(Keyword.RETURN);
    if (matches(TokenType.SEMICOLON)) {
      return new ReturnStatement(returnKeyword, null, getAndAdvance());
    }
    Expression expression = parseExpression();
    Token semicolon = expect(TokenType.SEMICOLON);
    return new ReturnStatement(returnKeyword, expression, semicolon);
  }

  /**
   * Parse a return type.
   * 
   * <pre>
   * returnType ::=
   *     'void'
   *   | type
   * </pre>
   * 
   * @return the return type that was parsed
   */
  private TypeName parseReturnType() {
    if (matches(Keyword.VOID)) {
      return new TypeName(new SimpleIdentifier(getAndAdvance()), null);
    } else {
      return parseTypeName();
    }
  }

  /**
   * Parse a shift expression.
   * 
   * <pre>
   * shiftExpression ::=
   *     additiveExpression (shiftOperator additiveExpression)*
   *   | 'super' (shiftOperator additiveExpression)+
   * </pre>
   * 
   * @return the shift expression that was parsed
   */
  private Expression parseShiftExpression() {
    Expression expression;
    if (matches(Keyword.SUPER) && currentToken.getNext().getType().isShiftOperator()) {
      expression = new SuperExpression(getAndAdvance());
    } else {
      expression = parseAdditiveExpression();
    }
    while (currentToken.getType().isShiftOperator()) {
      Token operator = getAndAdvance();
      expression = new BinaryExpression(expression, operator, parseAdditiveExpression());
    }
    return expression;
  }

  /**
   * Parse a simple formal parameter.
   * 
   * <pre>
   * simpleFormalParameter ::=
   *     finalConstVarOrType? identifier
   * </pre>
   * 
   * @return the simple formal parameter that was parsed
   */
  private SimpleFormalParameter parseSimpleFormalParameter() {
    FinalConstVarOrType holder = parseFinalConstVarOrType(true);
    SimpleIdentifier identifier = null;
    if (matches(TokenType.IDENTIFIER)) {
      identifier = parseSimpleIdentifier();
    } else {
      if (holder.getKeyword() == null && holder.getType().getName() instanceof SimpleIdentifier
          && holder.getType().getTypeArguments() == null) {
        identifier = (SimpleIdentifier) holder.getType().getName();
      } else {
        // Missing parameter name
        // reportError(ParserErrorCode.?));
      }
    }
    return new SimpleFormalParameter(holder.getKeyword(), holder.getType(), identifier);
  }

  /**
   * Parse a simple identifier.
   * 
   * <pre>
   * identifier ::=
   *     IDENTIFIER
   * </pre>
   * 
   * @return the simple identifier that was parsed
   */
  private SimpleIdentifier parseSimpleIdentifier() {
    if (matches(TokenType.IDENTIFIER)) {
      return new SimpleIdentifier(getAndAdvance());
    } else if (matches(TokenType.KEYWORD)
        && ((KeywordToken) currentToken).getKeyword().isPseudoKeyword()) {
      return new SimpleIdentifier(getAndAdvance());
    }
    reportError(ParserErrorCode.EXPECTED_IDENTIFIER);
    return null;
  }

  /**
   * Parse a source directive.
   * 
   * <pre>
   * sourceDirective ::=
   *     '#source' '(' stringLiteral ')' ';'
   * </pre>
   * 
   * @param hash the token for the hash sign preceding the source keyword
   * @return the source directive that was parsed
   */
  private SourceDirective parseSourceDirective(Token hash) {
    Token keyword = expect(SOURCE_DIRECTIVE);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    StringLiteral sourceUri = parseStringLiteral();
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Token semicolon = expect(TokenType.SEMICOLON);
    return new SourceDirective(hash, keyword, leftParenthesis, sourceUri, rightParenthesis,
        semicolon);
  }

  /**
   * Parse a statement.
   * 
   * <pre>
   * statement ::=
   *     label* nonLabeledStatement
   * </pre>
   * 
   * @return the statement that was parsed
   */
  private Statement parseStatement() {
    List<Label> labels = new ArrayList<Label>();
    while (matches(TokenType.IDENTIFIER) && peekMatches(TokenType.COLON)) {
      SimpleIdentifier label = parseSimpleIdentifier();
      Token colon = expect(TokenType.COLON);
      labels.add(0, new Label(label, colon));
    }
    Statement statement = parseNonLabeledStatement();
    return new LabeledStatement(labels, statement);
  }

  /**
   * Parse a list of statements within a switch statement.
   * 
   * <pre>
   * statements ::=
   *     statement*
   * </pre>
   * 
   * @return the statements that were parsed
   */
  private ArrayList<Statement> parseStatements() {
    ArrayList<Statement> statements = new ArrayList<Statement>();
    while (!matches(TokenType.EOF) && !matches(Keyword.CASE) && !matches(Keyword.DEFAULT)
        && !matches(TokenType.CLOSE_CURLY_BRACKET)
        && !(matches(TokenType.IDENTIFIER) && peekMatches(TokenType.COLON))) {
      statements.add(parseStatement());
    }
    return statements;
  }

  /**
   * Parse a string literal that contains interpolations.
   * 
   * @return the string literal that was parsed
   */
  private StringInterpolation parseStringInterpolation(Token string) {
    List<InterpolationElement> elements = new ArrayList<InterpolationElement>();
    elements.add(new InterpolationString(string));
    while (matches(TokenType.STRING_INTERPOLATION_EXPRESSION)
        || matches(TokenType.STRING_INTERPOLATION_IDENTIFIER)) {
      if (matches(TokenType.STRING_INTERPOLATION_EXPRESSION)) {
        Token openToken = getAndAdvance();
        Expression expression = parseExpression();
        Token rightBracket = expect(TokenType.CLOSE_CURLY_BRACKET);
        elements.add(new InterpolationExpression(openToken, expression, rightBracket));
      } else {
        Token openToken = getAndAdvance();
        Expression expression = parseSimpleIdentifier();
        elements.add(new InterpolationExpression(openToken, expression, null));
      }
      if (matches(TokenType.STRING)) {
        elements.add(new InterpolationString(getAndAdvance()));
      }
    }
    return new StringInterpolation(elements);
  }

  /**
   * Parse a string literal.
   * 
   * <pre>
   * stringLiteral ::=
   *     MULTI_LINE_STRING+
   *   | SINGLE_LINE_STRING+
   * </pre>
   * 
   * @return the string literal that was parsed
   */
  private StringLiteral parseStringLiteral() {
    List<StringLiteral> strings = new ArrayList<StringLiteral>();
    while (matches(TokenType.STRING)) {
      Token string = getAndAdvance();
      if (matches(TokenType.STRING_INTERPOLATION_EXPRESSION)
          || matches(TokenType.STRING_INTERPOLATION_IDENTIFIER)) {
        strings.add(parseStringInterpolation(string));
      } else {
        strings.add(new SimpleStringLiteral(string, computeStringValue(string.getLexeme())));
      }
    }
    if (strings.size() < 1) {
      reportError(ParserErrorCode.EXPECTED_STRING_LITERAL);
      return null;
    } else if (strings.size() == 1) {
      return strings.get(0);
    } else {
      return new AdjacentStrings(strings);
    }
  }

  /**
   * Parse a switch statement.
   * 
   * <pre>
   * switchStatement ::=
   *     'switch' '(' expression ')' '{' switchCase* defaultCase? '}'
   * 
   * switchCase ::=
   *     label* ('case' expression ':') statements
   * 
   * defaultCase ::=
   *     label* 'default' ':' statements
   * </pre>
   * 
   * @return the switch statement that was parsed
   */
  private SwitchStatement parseSwitchStatement() {
    Token keyword = expect(Keyword.SWITCH);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    Expression expression = parseExpression();
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Token leftBracket = expect(TokenType.OPEN_CURLY_BRACKET);
    ArrayList<SwitchMember> members = new ArrayList<SwitchMember>();
    while (!matches(TokenType.EOF) && !matches(TokenType.CLOSE_CURLY_BRACKET)) {
      List<Label> labels = new ArrayList<Label>();
      if (matches(TokenType.IDENTIFIER) && peekMatches(TokenType.COLON)) {
        SimpleIdentifier identifier = parseSimpleIdentifier();
        Token colon = expect(TokenType.COLON);
        labels.add(new Label(identifier, colon));
      }
      if (matches(Keyword.CASE)) {
        Token caseKeyword = getAndAdvance();
        Expression caseExpression = parseExpression();
        Token colon = expect(TokenType.COLON);
        members.add(new SwitchCase(labels, caseKeyword, caseExpression, colon, parseStatements()));
      } else if (matches(Keyword.DEFAULT)) {
        Token defaultKeyword = getAndAdvance();
        Token colon = expect(TokenType.COLON);
        members.add(new SwitchDefault(labels, defaultKeyword, colon, parseStatements()));
      } else {
        // We need to advance, otherwise we could end up in an infinite loop, but this could be a
        // lot smarter about recovering from the error.
        reportError(ParserErrorCode.EXPECTED_CASE_OR_DEFAULT);
        getAndAdvance();
      }
    }
    Token rightBracket = expect(TokenType.CLOSE_CURLY_BRACKET);
    return new SwitchStatement(keyword, leftParenthesis, expression, rightParenthesis, leftBracket,
        members, rightBracket);
  }

  /**
   * Parse a throw statement.
   * 
   * <pre>
   * throwStatement ::=
   *     'throw' expression? ';'
   * </pre>
   * 
   * @return the throw statement that was parsed
   */
  private Statement parseThrowStatement() {
    Token keyword = expect(Keyword.THROW);
    if (matches(TokenType.SEMICOLON)) {
      return new ThrowStatement(keyword, null, getAndAdvance());
    }
    Expression expression = parseExpression();
    Token semicolon = expect(TokenType.SEMICOLON);
    return new ThrowStatement(keyword, expression, semicolon);
  }

  /**
   * Parse a list of top-level variables.
   * 
   * <pre>
   * topLevelVariables ::=
   *     variableDeclarationList ';'
   * </pre>
   * 
   * @return the top-level variables that were parsed
   */
  private VariableDeclarationList parseTopLevelVariables() {
    // TODO(brianwilkerson) Define the AST node that should be returned and then implement this
    return null;
  }

  /**
   * Parse a try statement.
   * 
   * <pre>
   * tryStatement ::=
   *     'try' block (onPart+ finallyPart? | finallyPart)
   * 
   * onPart ::=
   *     catchPart block
   *   | 'on' qualified catchPart? block
   * 
   * catchPart ::=
   *     'catch' '(' identifier (',' identifier)? ')'
   * 
   * finallyPart ::=
   *     'finally' block
   * </pre>
   * 
   * @return the try statement that was parsed
   */
  private Statement parseTryStatement() {
    Token tryKeyword = expect(Keyword.TRY);
    Block body = parseBlock();
    ArrayList<CatchClause> catchClauses = new ArrayList<CatchClause>();
    Block finallyClause = null;
    while (matches(Keyword.ON) || matches(Keyword.CATCH)) {
      Token onKeyword = null;
      TypeName exceptionType = null;
      if (matches(Keyword.ON)) {
        onKeyword = getAndAdvance();
        exceptionType = new TypeName(parsePrefixedIdentifier(), null);
      }
      Token catchKeyword = null;
      Token leftParenthesis = null;
      SimpleIdentifier exceptionParameter = null;
      Token comma = null;
      SimpleIdentifier stackTraceParameter = null;
      Token rightParenthesis = null;
      if (matches(Keyword.CATCH)) {
        catchKeyword = getAndAdvance();
        leftParenthesis = expect(TokenType.OPEN_PAREN);
        exceptionParameter = parseSimpleIdentifier();
        if (matches(TokenType.COMMA)) {
          comma = getAndAdvance();
          stackTraceParameter = parseSimpleIdentifier();
        }
        rightParenthesis = expect(TokenType.CLOSE_PAREN);
      }
      Block catchBody = parseBlock();
      catchClauses.add(new CatchClause(onKeyword, exceptionType, catchKeyword, leftParenthesis,
          exceptionParameter, comma, stackTraceParameter, rightParenthesis, catchBody));
    }
    Token finallyKeyword = null;
    if (matches(Keyword.FINALLY)) {
      finallyKeyword = getAndAdvance();
      finallyClause = parseBlock();
    } else {
      if (catchClauses.isEmpty()) {
        reportError(ParserErrorCode.CATCH_OR_FINALLY_EXPECTED);
      }
    }
    return new TryStatement(tryKeyword, body, catchClauses, finallyKeyword, finallyClause);
  }

  /**
   * Parse a type alias.
   * 
   * <pre>
 * typeAlias ::=
 *     'typedef' returnType? name typeParameterList? formalParameterList ';'
 * </pre>
   * 
   * @return the type alias that was parsed
   */
  private TypeAlias parseTypeAlias() {
    Comment comment = parseDocumentationComment();
    Token keyword = expect(Keyword.TYPEDEF);
    TypeName returnType = null;
    if (!peekMatches(TokenType.OPEN_PAREN) && !peekMatches(TokenType.LT)) {
      returnType = parseTypeName();
    }
    SimpleIdentifier name = parseSimpleIdentifier();
    TypeParameterList typeParameters = null;
    if (matches(TokenType.LT)) {
      typeParameters = parseTypeParameterList();
    }
    FormalParameterList parameters = parseFormalParameterList();
    Token semicolon = expect(TokenType.SEMICOLON);
    return new TypeAlias(comment, keyword, returnType, name, typeParameters, parameters, semicolon);
  }

/**
     * Parse a list of type arguments.
     * 
     * <pre>
     * typeArguments ::=
     *     '<' typeList '>'
     * 
     * typeList ::=
     *     type (',' type)*
     * </pre>
     * 
     * @return the type name that was parsed
     */
  private TypeArgumentList parseTypeArgumentList() {
    Token leftBracket = expect(TokenType.LT);
    ArrayList<TypeName> arguments = new ArrayList<TypeName>();
    arguments.add(parseTypeName());
    while (optional(TokenType.COMMA)) {
      arguments.add(parseTypeName());
    }
    Token rightBracket = expect(TokenType.GT);
    return new TypeArgumentList(leftBracket, arguments, rightBracket);
  }

  /**
   * Parse a type name.
   * 
   * <pre>
   * type ::=
   *     qualified typeArguments?
   * </pre>
   * 
   * @return the type name that was parsed
   */
  private TypeName parseTypeName() {
    Identifier typeName = parsePrefixedIdentifier();
    TypeArgumentList typeArguments = null;
    if (matches(TokenType.LT)) {
      typeArguments = parseTypeArgumentList();
    }
    return new TypeName(typeName, typeArguments);
  }

  /**
   * Parse a type parameter.
   * 
   * <pre>
   * typeParameter ::=
   *     name ('extends' bound)?
   * </pre>
   * 
   * @return the type parameter that was parsed
   */
  private TypeParameter parseTypeParameter() {
    SimpleIdentifier name = parseSimpleIdentifier();
    if (matches(Keyword.EXTENDS)) {
      Token keyword = getAndAdvance();
      TypeName bound = parseTypeName();
      return new TypeParameter(name, keyword, bound);
    }
    return new TypeParameter(name, null, null);
  }

/**
   * Parse a list of type parameters.
   * 
   * <pre>
   * typeParameterList ::=
   *     '<' typeParameter (',' typeParameter)* '>'
   * </pre>
   * 
   * @return the list of type parameters that were parsed
   */
  private TypeParameterList parseTypeParameterList() {
    Token leftBracket = expect(TokenType.LT);
    List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
    typeParameters.add(parseTypeParameter());
    while (optional(TokenType.COMMA)) {
      typeParameters.add(parseTypeParameter());
    }
    Token rightBracket = expect(TokenType.GT);
    return new TypeParameterList(leftBracket, typeParameters, rightBracket);
  }

  /**
   * Parse a unary expression.
   * 
   * <pre>
   * unaryExpression ::=
   *     prefixOperator unaryExpression
   *   | postfixExpression
   *   | unaryOperator 'super'
   *   | '-' 'super'
   *   | incrementOperator assignableExpression
   * </pre>
   * 
   * @return the unary expression that was parsed
   */
  private Expression parseUnaryExpression() {
    if (matches(TokenType.MINUS)) {
      Token operator = getAndAdvance();
      if (matches(Keyword.SUPER)) {
        return new PrefixExpression(operator, new SuperExpression(getAndAdvance()));
      }
      return new PrefixExpression(operator, parseUnaryExpression());
    } else if (matches(TokenType.BANG)) {
      Token operator = getAndAdvance();
      if (matches(Keyword.SUPER)) {
        return new PrefixExpression(operator, new SuperExpression(getAndAdvance()));
      }
      return new PrefixExpression(operator, parseUnaryExpression());
    } else if (matches(TokenType.TILDE)) {
      Token operator = getAndAdvance();
      if (matches(Keyword.SUPER)) {
        return new PrefixExpression(operator, new SuperExpression(getAndAdvance()));
      }
      return new PrefixExpression(operator, parseUnaryExpression());
    } else if (currentToken.getType().isIncrementOperator()) {
      Token operator = getAndAdvance();
      if (matches(Keyword.SUPER)) {
        //
        // Even though it is not valid to use an incrementing operator ('++' or '--') before 'super',
        // we can (and therefore must) interpret "--super" as semantically equivalent to "-(-super)".
        // Unfortunately, we cannot do the same for "++super" because "+super" is also not valid.
        //
        if (operator.getType() == TokenType.MINUS_MINUS) {
          int offset = operator.getOffset();
          Token firstOperator = new Token(TokenType.MINUS, offset);
          Token secondOperator = new Token(TokenType.MINUS, offset + 1);
          secondOperator.setNext(currentToken);
          firstOperator.setNext(secondOperator);
          operator.getPrevious().setNext(firstOperator);
          return new PrefixExpression(firstOperator, new PrefixExpression(secondOperator,
              new SuperExpression(getAndAdvance())));
        } else {
          // Invalid operator before 'super'
          // reportError(ParserErrorCode.?));
          return new PrefixExpression(operator, new SuperExpression(getAndAdvance()));
        }
      }
      return new PrefixExpression(operator, parseAssignableExpression());
    }
    return parsePostfixExpression();
  }

  /**
   * Parse a variable declaration.
   * 
   * <pre>
   * variableDeclaration ::=
   *     identifier ('=' expression)?
   * </pre>
   * 
   * @return the variable declaration that was parsed
   */
  private VariableDeclaration parseVariableDeclaration(Comment comment) {
    Comment localComment = parseDocumentationComment();
    SimpleIdentifier name = parseSimpleIdentifier();
    Token equals = null;
    Expression initializer = null;
    if (matches(TokenType.EQ)) {
      equals = getAndAdvance();
      initializer = parseExpression();
    }
    return new VariableDeclaration(localComment == null ? comment : localComment, name, equals,
        initializer);
  }

  /**
   * Parse a variable declaration list.
   * 
   * <pre>
   * variableDeclarationList ::=
   *     finalConstVarOrType variableDeclaration (',' variableDeclaration)*
   * </pre>
   * 
   * @return the variable declaration list that was parsed
   */
  private VariableDeclarationList parseVariableDeclarationList() {
    Comment comment = parseDocumentationComment();
    FinalConstVarOrType holder = parseFinalConstVarOrType(false);
    List<VariableDeclaration> variables = new ArrayList<VariableDeclaration>();
    variables.add(parseVariableDeclaration(comment));
    while (matches(TokenType.COMMA)) {
      getAndAdvance();
      variables.add(parseVariableDeclaration(comment));
    }
    return new VariableDeclarationList(holder.getKeyword(), holder.getType(), variables);
  }

  /**
   * Parse a variable declaration statement.
   * 
   * <pre>
   * variableDeclarationStatement ::=
   *     variableDeclarationList ';'
   * </pre>
   * 
   * @return the variable declaration statement that was parsed
   */
  private VariableDeclarationStatement parseVariableDeclarationStatement() {
    VariableDeclarationList variableList = parseVariableDeclarationList();
    Token semicolon = expect(TokenType.SEMICOLON);
    return new VariableDeclarationStatement(variableList, semicolon);
  }

  /**
   * Parse a while statement.
   * 
   * <pre>
   * whileStatement ::=
   *     'while' '(' expression ')' statement
   * </pre>
   * 
   * @return the while statement that was parsed
   */
  private Statement parseWhileStatement() {
    Token keyword = expect(Keyword.WHILE);
    Token leftParenthesis = expect(TokenType.OPEN_PAREN);
    Expression condition = parseExpression();
    Token rightParenthesis = expect(TokenType.CLOSE_PAREN);
    Statement body = parseStatement();
    return new WhileStatement(keyword, leftParenthesis, condition, rightParenthesis, body);
  }

  /**
   * Return {@code true} if the token following the current token matches the given keyword.
   * 
   * @param keyword the keyword that can optionally appear after the current location
   * @return {@code true} if the token following the current token matches the given keyword
   */
  private boolean peekMatches(Keyword keyword) {
    return currentToken.getNext().getType() == TokenType.KEYWORD
        && ((KeywordToken) currentToken.getNext()).getKeyword() == keyword;
  }

  /**
   * Return {@code true} if the token following the current token has the given type.
   * 
   * @param type the type of token that can optionally appear after the current location
   * @return {@code true} if the token following the current token has the given type
   */
  private boolean peekMatches(TokenType type) {
    return currentToken.getNext().getType() == type;
  }

  /**
   * Report an error with the given error code and arguments.
   * 
   * @param errorCode the error code of the error to be reported
   * @param arguments the arguments to the error, used to compose the error message
   */
  private void reportError(ParserErrorCode errorCode, Object... arguments) {
    errorListener.onError(new AnalysisError(source, errorCode, arguments));
  }

  /**
   * Translate the characters at the given index in the given string, appending the translated
   * character to the given builder. The index is assumed to be valid.
   * 
   * @param builder the builder to which the translated character is to be appended
   * @param lexeme the string containing the character(s) to be translated
   * @param index the index of the character to be translated
   * @return the index of the next character to be translated
   */
  private int translateCharacter(StringBuilder builder, String lexeme, int index) {
    char currentChar = lexeme.charAt(index);
    if (currentChar != '\\') {
      builder.append(currentChar);
      return index + 1;
    }
    //
    // We have found an escape sequence, so we parse the string to determine what kind of escape
    // sequence and what character to add to the builder.
    //
    int length = lexeme.length();
    int currentIndex = index + 1;
    if (currentIndex >= length) {
      // Illegal escape sequence: no char after escape
      // reportError(ParserErrorCode.?));
      return length;
    }
    currentChar = lexeme.charAt(currentIndex);
    if (currentChar == 'n') {
      builder.append('\n'); // newline
    } else if (currentChar == 'r') {
      builder.append('\r'); // carriage return
    } else if (currentChar == 'f') {
      builder.append('\f'); // form feed
    } else if (currentChar == 'b') {
      builder.append('\b'); // backspace
    } else if (currentChar == 't') {
      builder.append('\t'); // tab
    } else if (currentChar == 'v') {
      builder.append('\u000B'); // vertical tab
    } else if (currentChar == 'x') {
      if (currentIndex + 2 >= length) {
        // Illegal escape sequence: not enough hex digits
        // reportError(ParserErrorCode.?));
        return length;
      }
      char firstDigit = lexeme.charAt(currentIndex + 1);
      char secondDigit = lexeme.charAt(currentIndex + 2);
      if (!isHexDigit(firstDigit) || !isHexDigit(secondDigit)) {
        // Illegal escape sequence: invalid hex digit
        // reportError(ParserErrorCode.?));
      } else {
        builder.append((char) ((Character.digit(firstDigit, 16) << 4) + Character.digit(
            secondDigit, 16)));
      }
      return currentIndex + 3;
    } else if (currentChar == 'u') {
      currentIndex++;
      if (currentIndex >= length) {
        // Illegal escape sequence: not enough hex digits
        // reportError(ParserErrorCode.?));
        return length;
      }
      currentChar = lexeme.charAt(currentIndex);
      if (currentChar == '{') {
        currentIndex++;
        if (currentIndex >= length) {
          // Illegal escape sequence: incomplete escape
          // reportError(ParserErrorCode.?));
          return length;
        }
        currentChar = lexeme.charAt(currentIndex);
        int digitCount = 0;
        int value = 0;
        while (currentChar != '}') {
          if (!isHexDigit(currentChar)) {
            // Illegal escape sequence: invalid hex digit
            // reportError(ParserErrorCode.?));
            currentIndex++;
            while (currentIndex < length && lexeme.charAt(currentIndex) != '}') {
              currentIndex++;
            }
            return currentIndex + 1;
          }
          digitCount++;
          value = (value << 4) + Character.digit(currentChar, 16);
          currentIndex++;
          if (currentIndex >= length) {
            // Illegal escape sequence: incomplete escape
            // reportError(ParserErrorCode.?));
            return length;
          }
          currentChar = lexeme.charAt(currentIndex);
        }
        if (digitCount < 1 || digitCount > 6) {
          // Illegal escape sequence: not enough or too many hex digits
          // reportError(ParserErrorCode.?));
        }
        appendScalarValue(builder, value, index, currentIndex);
        return currentIndex + 1;
      } else {
        if (currentIndex + 3 >= length) {
          // Illegal escape sequence: not enough hex digits
          // reportError(ParserErrorCode.?));
          return length;
        }
        char firstDigit = currentChar;
        char secondDigit = lexeme.charAt(currentIndex + 1);
        char thirdDigit = lexeme.charAt(currentIndex + 2);
        char fourthDigit = lexeme.charAt(currentIndex + 3);
        if (!isHexDigit(firstDigit) || !isHexDigit(secondDigit) || !isHexDigit(thirdDigit)
            || !isHexDigit(fourthDigit)) {
          // Illegal escape sequence: invalid hex digits
          // reportError(ParserErrorCode.?));
        } else {
          appendScalarValue(builder, ((((((Character.digit(firstDigit, 16) << 4) + Character.digit(
              secondDigit, 16)) << 4) + Character.digit(thirdDigit, 16)) << 4) + Character.digit(
              fourthDigit, 16)), index, currentIndex + 3);
        }
        return currentIndex + 4;
      }
    } else {
      builder.append(currentChar);
    }
    return currentIndex + 1;
  }
}