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
package com.google.dart.tools.ui.cleanup;

import com.google.dart.compiler.ast.DartUnit;
import com.google.dart.tools.core.refactoring.CompilationUnitChange;
import com.google.dart.tools.core.utilities.compiler.DartCompilerUtilities;
import com.google.dart.tools.ui.internal.cleanup.migration.AbstractMigrateCleanUp;
import com.google.dart.tools.ui.internal.cleanup.migration.Migrate_1M1_catch_CleanUp;
import com.google.dart.tools.ui.internal.cleanup.migration.Migrate_1M1_equals_CleanUp;
import com.google.dart.tools.ui.internal.cleanup.migration.Migrate_1M1_get_CleanUp;
import com.google.dart.tools.ui.internal.cleanup.migration.Migrate_1M1_library_CleanUp;
import com.google.dart.tools.ui.refactoring.AbstractDartTest;

/**
 * Test for {@link AbstractMigrateCleanUp}.
 */
public final class MigrateCleanUpTest extends AbstractDartTest {

  public void test_1M1_catch_alreadyNewSyntax_withoutType() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_catch_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "main() {",
        "  try {",
        "  } catch (e, stack) {",
        "  }",
        "}",
        "");
    assertNoFix(cleanUp, initial);
  }

  public void test_1M1_catch_alreadyNewSyntax_withType() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_catch_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "main() {",
        "  try {",
        "  } on Exception catch (e, stack) {",
        "  }",
        "}",
        "");
    assertNoFix(cleanUp, initial);
  }

  public void test_1M1_catch_withExceptionType() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_catch_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "main() {",
        "  try {",
        "  } catch (final Exception e, stack) {",
        "  }",
        "}",
        "");
    String expected = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "main() {",
        "  try {",
        "  } on Exception catch (e, stack) {",
        "  }",
        "}",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  public void test_1M1_catch_withStackType() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_catch_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "main() {",
        "  try {",
        "  } catch (e, Object stack) {",
        "  }",
        "}",
        "");
    String expected = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "main() {",
        "  try {",
        "  } catch (e, stack) {",
        "  }",
        "}",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  public void test_1M1_equals() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_equals_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {",
        "  operator equals(other) => false;",
        "}",
        "");
    String expected = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {",
        "  operator ==(other) => false;",
        "}",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  public void test_1M1_equals_noOp() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_equals_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {",
        "  operator ==(other) => false;",
        "}",
        "");
    assertNoFix(cleanUp, initial);
  }

  public void test_1M1_getter() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_get_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {",
        "  get test() => 42;",
        "}",
        "");
    String expected = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {",
        "  get test => 42;",
        "}",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  public void test_1M1_getter_noOp() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_get_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {",
        "  get test => 42;",
        "}",
        "");
    assertNoFix(cleanUp, initial);
  }

  public void test_1M1_library() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_library_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "#library('myLib');",
        "");
    String expected = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "library myLib;",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  public void test_1M1_library_import() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_library_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "#import('A.dart');",
        "#import('B.dart', prefix: 'bbb');",
        "");
    String expected = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "import 'A.dart';",
        "import 'B.dart' as bbb;",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  public void test_1M1_library_isScript() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_library_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "main() {",
        "}",
        "");
    assertNoFix(cleanUp, initial);
  }

  public void test_1M1_library_partOf() throws Exception {
    setUnitContent("Main.dart", new String[] {
        "// filler filler filler filler filler filler filler filler filler filler",
        "library Main;",
        "part 'Test.dart';",
        ""});
    ICleanUp cleanUp = new Migrate_1M1_library_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {}",
        "");
    String expected = makeSource(
        "part of Main;",
        "",
        "// filler filler filler filler filler filler filler filler filler filler",
        "class A {}",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  public void test_1M1_library_source() throws Exception {
    ICleanUp cleanUp = new Migrate_1M1_library_CleanUp();
    String initial = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "#source('A.dart');",
        "");
    String expected = makeSource(
        "// filler filler filler filler filler filler filler filler filler filler",
        "part 'A.dart';",
        "");
    assertCleanUp(cleanUp, initial, expected);
  }

  private void assertCleanUp(ICleanUp cleanUp, String initial, String expected) throws Exception {
    ICleanUpFix fix = prepareFix(cleanUp, initial);
    assertNotNull(fix);
    // assert result
    CompilationUnitChange unitChange = fix.createChange(null);
    String result = unitChange.getPreviewContent(null);
    assertEquals(expected, result);
  }

  private void assertNoFix(ICleanUp cleanUp, String initial) throws Exception {
    ICleanUpFix fix = prepareFix(cleanUp, initial);
    assertNull(fix);
  }

  private ICleanUpFix prepareFix(ICleanUp cleanUp, String initial) throws Exception {
    setTestUnitContent(initial);
    // prepare CleanUpContext
    CleanUpContext context;
    {
      DartUnit ast = DartCompilerUtilities.resolveUnit(testUnit);
      context = new CleanUpContext(testUnit, ast);
    }
    // check requirements
    {
      CleanUpRequirements requirements = cleanUp.getRequirements();
      assertTrue(requirements.requiresAST());
    }
    // prepare ICleanUpFix
    ICleanUpFix fix = cleanUp.createFix(context);
    return fix;
  }

}
