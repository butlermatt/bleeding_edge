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
package com.google.dart.tools.ui.web.css;

import com.google.dart.tools.ui.web.DartWebPlugin;
import com.google.dart.tools.ui.web.utils.CssAttributes;
import com.google.dart.tools.ui.web.utils.HtmlKeywords;
import com.google.dart.tools.ui.web.utils.WordDetector;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.List;

/**
 * The tokenizer (ITokenScanner) for CSS content.
 */
public class CssScanner extends RuleBasedScanner {

  public CssScanner() {
    Token keywordToken = new Token(new TextAttribute(DartWebPlugin.getPlugin().getEditorColor(
        DartWebPlugin.COLOR_KEYWORD), null, SWT.BOLD));
    Token attributeToken = new Token(new TextAttribute(DartWebPlugin.getPlugin().getEditorColor(
        DartWebPlugin.COLOR_STATIC_FIELD)));
    Token stringToken = new Token(new TextAttribute(DartWebPlugin.getPlugin().getEditorColor(
        DartWebPlugin.COLOR_STRING)));
    Token commentToken = new Token(new TextAttribute(DartWebPlugin.getPlugin().getEditorColor(
        DartWebPlugin.COLOR_COMMENTS)));

    List<IRule> rules = new ArrayList<IRule>();

    WordRule keywordRule = new WordRule(new WordDetector());

    for (String keyword : HtmlKeywords.getKeywords()) {
      keywordRule.addWord(keyword, keywordToken);
    }

    rules.add(keywordRule);

    WordRule attributeRule = new WordRule(new WordDetector());

    for (String attribute : CssAttributes.getAttributes()) {
      attributeRule.addWord(attribute, attributeToken);
    }

    rules.add(attributeRule);

    rules.add(new MultiLineRule("/*", "*/", commentToken));
    rules.add(new SingleLineRule("\"", "\"", stringToken, '\\'));
    rules.add(new SingleLineRule("'", "'", stringToken, '\\'));

    setRules(rules.toArray(new IRule[rules.size()]));
  }

}
