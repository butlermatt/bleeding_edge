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

import com.google.dart.engine.EngineTestCase;
import com.google.dart.engine.ast.ArgumentList;
import com.google.dart.engine.ast.BinaryExpression;
import com.google.dart.engine.ast.Expression;
import com.google.dart.engine.ast.FunctionExpressionInvocation;
import com.google.dart.engine.ast.LabeledStatement;
import com.google.dart.engine.ast.PropertyAccess;
import com.google.dart.engine.ast.ReturnStatement;
import com.google.dart.engine.ast.SimpleIdentifier;
import com.google.dart.engine.ast.Statement;
import com.google.dart.engine.error.AnalysisError;
import com.google.dart.engine.error.AnalysisErrorListener;
import com.google.dart.engine.scanner.StringScanner;
import com.google.dart.engine.scanner.Token;

/**
 * The class {@code ComplexParserTest} defines parser tests that test the parsing of more complex
 * code fragments or the interactions between multiple parsing methods. For example, tests to ensure
 * that the precedence of operations is being handled correctly should be defined in this class.
 * <p>
 * Simpler tests should be defined in the class {@link SimpleParserTest}.
 */
public class ComplexParserTest extends EngineTestCase {
  public void test_additiveExpression_normal() throws Exception {
    BinaryExpression expression = parseExpression("x + y - z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_additiveExpression_super() throws Exception {
    BinaryExpression expression = parseExpression("super + y - z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_assignableExpression_arguments_normal_chain() throws Exception {
    PropertyAccess propertyAccess1 = parseExpression("a(b)(c).d(e).f");
    assertEquals("f", propertyAccess1.getPropertyName().getIdentifier());
    //
    // a(b)(c).d(e)
    //
    FunctionExpressionInvocation invocation2 = assertInstanceOf(FunctionExpressionInvocation.class,
        propertyAccess1.getTarget());
    ArgumentList argumentList2 = invocation2.getArgumentList();
    assertNotNull(argumentList2);
    assertEquals(1, argumentList2.getArguments().size());
    //
    // a(b)(c).d
    //
    PropertyAccess propertyAccess3 = assertInstanceOf(PropertyAccess.class,
        invocation2.getFunction());
    assertEquals("d", propertyAccess3.getPropertyName().getIdentifier());
    //
    // a(b)(c)
    //
    FunctionExpressionInvocation invocation4 = assertInstanceOf(FunctionExpressionInvocation.class,
        propertyAccess3.getTarget());
    ArgumentList argumentList4 = invocation4.getArgumentList();
    assertNotNull(argumentList4);
    assertEquals(1, argumentList4.getArguments().size());
    //
    // a(b)
    //
    FunctionExpressionInvocation invocation5 = assertInstanceOf(FunctionExpressionInvocation.class,
        invocation4.getFunction());
    assertInstanceOf(SimpleIdentifier.class, invocation5.getFunction());
    ArgumentList argumentList5 = invocation4.getArgumentList();
    assertNotNull(argumentList5);
    assertEquals(1, argumentList5.getArguments().size());
  }

  public void test_bitwiseAndExpression_normal() throws Exception {
    BinaryExpression expression = parseExpression("x & y & z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_bitwiseAndExpression_super() throws Exception {
    BinaryExpression expression = parseExpression("super & y & z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_bitwiseOrExpression_normal() throws Exception {
    BinaryExpression expression = parseExpression("x | y | z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_bitwiseOrExpression_super() throws Exception {
    BinaryExpression expression = parseExpression("super | y | z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_bitwiseXorExpression_normal() throws Exception {
    BinaryExpression expression = parseExpression("x ^ y ^ z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_bitwiseXorExpression_super() throws Exception {
    BinaryExpression expression = parseExpression("super ^ y ^ z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_equalityExpression_normal() throws Exception {
    BinaryExpression expression = parseExpression("x == y != z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_equalityExpression_super() throws Exception {
    BinaryExpression expression = parseExpression("super == y != z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_logicalAndExpression() throws Exception {
    BinaryExpression expression = parseExpression("x && y && z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_logicalOrExpression() throws Exception {
    BinaryExpression expression = parseExpression("x || y || z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_multipleLabels() throws Exception {
    LabeledStatement statement = parseStatement("a: b: c: return x;");
    assertEquals(3, statement.getLabels().size());
    assertInstanceOf(ReturnStatement.class, statement.getStatement());
  }

  public void test_multiplicativeExpression_normal() throws Exception {
    BinaryExpression expression = parseExpression("x * y / z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_multiplicativeExpression_super() throws Exception {
    BinaryExpression expression = parseExpression("super * y / z");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_shiftExpression_normal() throws Exception {
    BinaryExpression expression = parseExpression("x >> 4 << 3");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  public void test_shiftExpression_super() throws Exception {
    BinaryExpression expression = parseExpression("super >> 4 << 3");
    assertInstanceOf(BinaryExpression.class, expression.getLeftOperand());
  }

  /**
   * Parse the given source as an expression.
   * 
   * @param source the source to be parsed
   * @return the expression that was parsed
   * @throws Exception if the source could not be parsed, if the source contains a compilation
   *           error, or if the result would have been {@code null}.
   */
  @SuppressWarnings("unchecked")
  private <E extends Expression> E parseExpression(String source) throws Exception {
    AnalysisErrorListener listener = new AnalysisErrorListener() {
      @Override
      public void onError(AnalysisError event) {
        fail("Unexpected compilation error: " + event.getMessage() + " (" + event.getOffset()
            + ", " + event.getLength() + ")");
      }
    };
    StringScanner scanner = new StringScanner(null, source, listener);
    Token token = scanner.tokenize();
    Parser parser = new Parser(null, listener);
    Expression expression = parser.parseExpression(token);
    assertNotNull(expression);
    return (E) expression;
  }

  /**
   * Parse the given source as a statement.
   * 
   * @param source the source to be parsed
   * @return the statement that was parsed
   * @throws Exception if the source could not be parsed, if the source contains a compilation
   *           error, or if the result would have been {@code null}.
   */
  @SuppressWarnings("unchecked")
  private <E extends Statement> E parseStatement(String source) throws Exception {
    AnalysisErrorListener listener = new AnalysisErrorListener() {
      @Override
      public void onError(AnalysisError event) {
        fail("Unexpected compilation error: " + event.getMessage() + " (" + event.getOffset()
            + ", " + event.getLength() + ")");
      }
    };
    StringScanner scanner = new StringScanner(null, source, listener);
    Token token = scanner.tokenize();
    Parser parser = new Parser(null, listener);
    Statement statement = parser.parseStatement(token);
    assertNotNull(statement);
    return (E) statement;
  }
}