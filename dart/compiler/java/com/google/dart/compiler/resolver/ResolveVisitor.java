// Copyright (c) 2012, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

package com.google.dart.compiler.resolver;

import com.google.dart.compiler.ErrorCode;
import com.google.dart.compiler.ast.ASTVisitor;
import com.google.dart.compiler.ast.DartCatchBlock;
import com.google.dart.compiler.ast.DartFunction;
import com.google.dart.compiler.ast.DartFunctionTypeAlias;
import com.google.dart.compiler.ast.DartIdentifier;
import com.google.dart.compiler.ast.DartNode;
import com.google.dart.compiler.ast.DartParameter;
import com.google.dart.compiler.ast.DartThisExpression;
import com.google.dart.compiler.ast.DartTypeNode;
import com.google.dart.compiler.ast.DartTypeParameter;
import com.google.dart.compiler.type.DynamicType;
import com.google.dart.compiler.type.FunctionType;
import com.google.dart.compiler.type.Type;
import com.google.dart.compiler.type.Types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Shared visitor between Resolver and MemberBuilder.
 */
abstract class ResolveVisitor extends ASTVisitor<Element> {
  private final CoreTypeProvider typeProvider;

  ResolveVisitor(CoreTypeProvider typeProvider) {
    this.typeProvider = typeProvider;
  }

  abstract ResolutionContext getContext();

  final MethodElement resolveFunction(DartFunction node, MethodElement element) {
    for (DartParameter parameter : node.getParameters()) {
      Elements.addParameter(element, (VariableElement) parameter.accept(this));
    }
    resolveFunctionWithParameters(node, element);
    Type returnType =
        resolveType(
            node.getReturnTypeNode(),
            element.getModifiers().isStatic(),
            element.getModifiers().isFactory(),
            TypeErrorCode.NO_SUCH_TYPE,
            TypeErrorCode.WRONG_NUMBER_OF_TYPE_ARGUMENTS);
    ClassElement functionElement = typeProvider.getFunctionType().getElement();
    FunctionType type = Types.makeFunctionType(getContext(), functionElement,
                                               element.getParameters(), returnType);
    Elements.setType(element, type);
    for (DartParameter parameter : node.getParameters()) {
      if (!(parameter.getQualifier() instanceof DartThisExpression) &&
          parameter.getModifiers().isNamed() &&
          DartIdentifier.isPrivateName(parameter.getElement().getName())) {
        getContext().onError(parameter.getName(),
            ResolverErrorCode.NAMED_PARAMETERS_CANNOT_START_WITH_UNDER);
      }
    }
    return element;
  }

  /**
   * Allows subclass to process {@link DartFunction} element with parameters, but before
   * {@link FunctionType} is created for it.
   */
  protected void resolveFunctionWithParameters(DartFunction node, MethodElement element) {
  }

  final FunctionAliasElement resolveFunctionAlias(DartFunctionTypeAlias node) {
    HashSet<String> parameterNames = new HashSet<String>();
    for (DartTypeParameter parameter : node.getTypeParameters()) {
      TypeVariableElement typeVar = (TypeVariableElement) parameter.getElement();
      String parameterName = typeVar.getName();
      if (parameterNames.contains(parameterName)) {
        getContext().onError(parameter, ResolverErrorCode.DUPLICATE_TYPE_VARIABLE, parameterName);
      } else {
        parameterNames.add(parameterName);
      }
      getContext().getScope().declareElement(parameterName, typeVar);
    }
    return null;
  }

  abstract boolean isStaticContext();

  abstract boolean isFactoryContext();

  @Override
  public Element visitParameter(DartParameter node) {
    ErrorCode typeErrorCode;
    ErrorCode wrongNumberErrorCode;
    if (node.getParent() instanceof DartCatchBlock) {
      typeErrorCode = ResolverErrorCode.NO_SUCH_TYPE;
      wrongNumberErrorCode = ResolverErrorCode.WRONG_NUMBER_OF_TYPE_ARGUMENTS;
    } else {
      typeErrorCode = TypeErrorCode.NO_SUCH_TYPE;
      wrongNumberErrorCode = TypeErrorCode.WRONG_NUMBER_OF_TYPE_ARGUMENTS;
    }
    Type type = resolveType(node.getTypeNode(), isStaticContext(), isFactoryContext(),
        typeErrorCode, wrongNumberErrorCode);
    VariableElement element =
        Elements.parameterElement(
            getEnclosingElement(),
            node,
            node.getParameterName(),
            node.getModifiers());
    List<DartParameter> functionParameters = node.getFunctionParameters();
    if (functionParameters != null) {
      List<VariableElement> parameterElements =
          new ArrayList<VariableElement>(functionParameters.size());
      for (DartParameter parameter: functionParameters) {
        parameterElements.add((VariableElement) parameter.accept(this));
      }
      ClassElement functionElement = typeProvider.getFunctionType().getElement();
      type = Types.makeFunctionType(getContext(), functionElement, parameterElements, type);
    }
    Elements.setType(element, type);
    recordElement(node.getName(), element);
    return recordElement(node, element);
  }

  protected EnclosingElement getEnclosingElement() {
    return null;
  }

  final Type resolveType(DartTypeNode node, boolean isStatic, boolean isFactory,
                         ErrorCode errorCode, ErrorCode wrongNumberErrorCode) {
    if (node == null) {
      return getTypeProvider().getDynamicType();
    }
    assert node.getType() == null || node.getType() instanceof DynamicType;
    Type type = getContext().resolveType(node, isStatic, isFactory, errorCode, wrongNumberErrorCode);
    if (type == null) {
      type = getTypeProvider().getDynamicType();
    }
    node.setType(type);
    Element element = type.getElement();
    recordElement(node.getIdentifier(), element);
    if (element != null && element.getMetadata().isDeprecated()) {
      getContext().onError(node.getIdentifier(), TypeErrorCode.DEPRECATED_ELEMENT, element.getName());
    }
    return type;
  }

  protected <E extends Element> E recordElement(DartNode node, E element) {
    node.getClass();
    if (element == null) {
      // TypeAnalyzer will diagnose unresolved identifiers.
      return null;
    }
    node.setElement(element);
    return element;
  }

  CoreTypeProvider getTypeProvider() {
    return typeProvider;
  }
}
