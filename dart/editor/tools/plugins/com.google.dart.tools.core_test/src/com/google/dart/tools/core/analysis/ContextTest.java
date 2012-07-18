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
package com.google.dart.tools.core.analysis;

import com.google.dart.compiler.ast.DartUnit;
import com.google.dart.compiler.ast.LibraryUnit;
import com.google.dart.tools.core.AbstractDartCoreTest;
import com.google.dart.tools.core.internal.model.EditorLibraryManager;
import com.google.dart.tools.core.internal.model.SystemLibraryManagerProvider;
import com.google.dart.tools.core.test.util.FileUtilities;
import com.google.dart.tools.core.test.util.TestUtilities;

import java.io.File;

public class ContextTest extends AbstractDartCoreTest {

  private static final long FIVE_MINUTES_MS = 300000;

  private static File tempDir;
  private static File libraryFile;
  private static File dartFile;
  private static File doesNotExist;

  /**
   * Called once prior to executing the first test in this class
   */
  public static void setUpOnce() throws Exception {
    tempDir = TestUtilities.createTempDirectory();
    TestUtilities.copyPluginRelativeContent("Money", tempDir);
    libraryFile = new File(tempDir, "money.dart");
    assertTrue(libraryFile.exists());
    dartFile = new File(tempDir, "simple_money.dart");
    assertTrue(dartFile.exists());
    doesNotExist = new File(tempDir, "doesNotExist.dart");
  }

  /**
   * Called once after executing the last test in this class
   */
  public static void tearDownOnce() {
    FileUtilities.delete(tempDir);
    tempDir = null;
  }

  private AnalysisServer server;
  private Context context;
  private Listener listener;

  public void test_parse_library() throws Exception {
    DartUnit dartUnit = context.parse(libraryFile, libraryFile, FIVE_MINUTES_MS);
    assertEquals("Money", dartUnit.getTopDeclarationNames().iterator().next());
    assertParsed(1, 0);

    listener.reset();
    dartUnit = context.parse(libraryFile, libraryFile, FIVE_MINUTES_MS);
    assertEquals("Money", dartUnit.getTopDeclarationNames().iterator().next());
    assertParsed(0, 0);
  }

  public void test_parse_libraryDoesNotExist() throws Exception {
    DartUnit dartUnit = context.parse(doesNotExist, doesNotExist, FIVE_MINUTES_MS);
    assertEquals(0, dartUnit.getTopDeclarationNames().size());
    assertParsed(1, 1);
  }

  public void test_parse_source() throws Exception {
    DartUnit dartUnit = context.parse(libraryFile, dartFile, FIVE_MINUTES_MS);
    assertEquals("SimpleMoney", dartUnit.getTopDeclarationNames().iterator().next());
    assertParsed(2, 0);

    listener.reset();
    dartUnit = context.parse(libraryFile, dartFile, FIVE_MINUTES_MS);
    assertEquals("SimpleMoney", dartUnit.getTopDeclarationNames().iterator().next());
    assertParsed(0, 0);
  }

  public void test_parse_sourceDoesNotExist() throws Exception {
    DartUnit dartUnit = context.parse(libraryFile, doesNotExist, FIVE_MINUTES_MS);
    assertEquals(0, dartUnit.getTopDeclarationNames().size());
    assertParsed(2, 1);
  }

  public void test_resolve() throws Exception {
    LibraryUnit libraryUnit = context.resolve(libraryFile, FIVE_MINUTES_MS);
    assertEquals("Money", libraryUnit.getName());
    assertTrue(listener.getParsedCount() > 10);
    listener.assertResolved(libraryFile);
    listener.assertNoErrors();
    listener.assertNoDuplicates();
    listener.assertNoDiscards();

    listener.reset();
    libraryUnit = context.resolve(libraryFile, FIVE_MINUTES_MS);
    assertEquals("Money", libraryUnit.getName());
    listener.assertParsedCount(0);
    listener.assertResolvedCount(0);
    listener.assertNoErrors();
    listener.assertNoDuplicates();
    listener.assertNoDiscards();
  }

  public void test_resolve_doesNotExist() throws Exception {
    LibraryUnit libraryUnit = context.resolve(doesNotExist, FIVE_MINUTES_MS);
    assertNotNull(libraryUnit);
    listener.assertParsed(doesNotExist, doesNotExist);
    listener.assertResolved(doesNotExist);
    assertTrue(listener.getErrorCount() > 0);
    listener.assertNoDuplicates();
    listener.assertNoDiscards();
  }

  @Override
  protected void setUp() throws Exception {
    EditorLibraryManager libraryManager = SystemLibraryManagerProvider.getAnyLibraryManager();
    server = new AnalysisServer(libraryManager);
    context = server.getSavedContext();
    listener = new Listener(server);
    server.start();
  }

  @Override
  protected void tearDown() throws Exception {
    server.stop();
  }

  private void assertParsed(int expectedParseCount, int expectedErrorCount) {
    listener.assertParsedCount(expectedParseCount);
    listener.assertResolvedCount(0);
    listener.assertErrorCount(expectedErrorCount);
    listener.assertNoDuplicates();
    listener.assertNoDiscards();
  }
}