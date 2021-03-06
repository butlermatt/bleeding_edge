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
package com.google.dart.tools.ui.internal.cleanup.migration;

import com.google.dart.compiler.ast.ASTVisitor;
import com.google.dart.compiler.ast.DartExpression;
import com.google.dart.compiler.ast.DartMethodDefinition;
import com.google.dart.tools.internal.corext.SourceRangeFactory;

/**
 * In specification 1.0 M1 "operator equals(other)" was replaced with "operator ==(other)".
 * 
 * @coverage dart.editor.ui.cleanup
 */
public class Migrate_1M1_equals_CleanUp extends AbstractMigrateCleanUp {
  @Override
  protected void createFix() {
    unitNode.accept(new ASTVisitor<Void>() {
      @Override
      public Void visitMethodDefinition(DartMethodDefinition node) {
        DartExpression name = node.getName();
        if (node.getModifiers().isOperator() && name.toString().equals("equals")) {
          addReplaceEdit(SourceRangeFactory.create(name), "==");
        }
        return super.visitMethodDefinition(node);
      }
    });
  }

}
