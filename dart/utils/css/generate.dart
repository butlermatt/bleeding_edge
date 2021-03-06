// Copyright (c) 2011, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

class Generate {

  // Build up list of all known class selectors in all CSS files.
  static List<String> computeClassSelectors(RuleSet ruleset, classes) {
    for (final selector in ruleset.selectorGroup.selectors) {
      var selSeqs = selector.simpleSelectorSequences;
      for (final selSeq in selSeqs) {
        var simpleSelector = selSeq.simpleSelector;
        if (simpleSelector is ClassSelector) {
          String className = simpleSelector.name;
          if (classes.indexOf(className) == -1) {   // Class name already known?
            // No, expose it.
            classes.add(className);
          }
        }
      }
    }

    return classes;
  }

  static dartClass(var files, String outPath, Stylesheet stylesheet,
      String filename) {

    List<String> knownClasses = [];

    StringBuffer buff = new StringBuffer(
        '// File generated by Dart CSS from source file ${filename}\n' +
        '// Do not edit.\n\n');

    // Emit class for any @stylet directive.
    for (final production in stylesheet._topLevels) {
      if (production is Directive) {
        if (production is StyletDirective) {
          // TODO(terry): Need safer mechanism for stylets in different files
          //              and stylets with colliding names.
          buff.add('class ${production.dartClassName} {\n');
          buff.add('  // selector, properties<propertyName, value>\n');
          buff.add('  static const selectors = const {\n');

          for (final ruleset in production.rulesets) {
            for (final selector in ruleset.selectorGroup.selectors) {
              var selSeq = selector.simpleSelectorSquences;
              if (selSeq.length == 1) {
                buff.add('    \'${selSeq.toString()}\' : const {\n');
              }
            }

            for (final decl in ruleset.declarationGroup.declarations) {
              buff.add('      \'${decl.property}\' : ' +
                  '\'${decl.expression.toString()}\',\n');
            }
            buff.add('    },\n');   // End of declarations for stylet class.
          }
          buff.add('  };\n');       // End of static selectors constant.
          buff.add('}\n\n');        // End of stylet class
        } else if (production is IncludeDirective) {
          for (final topLevel in production.styleSheet._topLevels) {
            if (topLevel is RuleSet) {
              knownClasses = computeClassSelectors(topLevel, knownClasses);
            }
          }
        }
      } else if (production is RuleSet) {
        knownClasses = computeClassSelectors(production, knownClasses);
      }
    }

    // Generate all known classes encountered in all processed CSS files.
    StringBuffer classSelectors = new StringBuffer(
      'class CSS {\n' +
      '  // CSS class selectors:\n');
    for (final className in knownClasses) {
      String classAsDart = className.replaceAll('-', '_').toUpperCase();
      classSelectors.add('  static const String ${classAsDart} = ' +
          '\'${className}\';\n');
    }
    classSelectors.add('}\n');            // End of class selectors.
    buff.add(classSelectors.toString());

    // Write Dart file.
    String outFile = '${outPath}CSS.dart';
    files.writeString(outFile, buff.toString());

    return outFile;
  }
}

