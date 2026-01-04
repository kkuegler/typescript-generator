
package cz.habarta.typescript.generator.compiler;

import cz.habarta.typescript.generator.parser.Model;

public interface ModelTransformer {

    Model transformModel(SymbolTable symbolTable, Model model);

}
