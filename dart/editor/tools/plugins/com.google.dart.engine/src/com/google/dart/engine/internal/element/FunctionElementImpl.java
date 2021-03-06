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
package com.google.dart.engine.internal.element;

import com.google.dart.engine.ast.Identifier;
import com.google.dart.engine.element.ElementKind;
import com.google.dart.engine.element.FunctionElement;

/**
 * Instances of the class {@code FunctionElementImpl} implement a {@code FunctionElement}.
 */
public class FunctionElementImpl extends ExecutableElementImpl implements FunctionElement {
  /**
   * An empty array of function elements.
   */
  public static final FunctionElement[] EMPTY_ARRAY = new FunctionElement[0];

  /**
   * Initialize a newly created synthetic function element.
   */
  public FunctionElementImpl() {
    super("", -1);
    setSynthetic(true);
  }

  /**
   * Initialize a newly created function element to have the given name.
   * 
   * @param name the name of this element
   */
  public FunctionElementImpl(Identifier name) {
    super(name);
  }

  @Override
  public ElementKind getKind() {
    return ElementKind.FUNCTION;
  }
}
