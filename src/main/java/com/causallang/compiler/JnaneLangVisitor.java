package com.causallang.compiler;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import com.causallang.compiler.JnaneLangParser;

/**
 * Interface du visiteur pour l'AST du langage Jnane.
 * Cette interface étend le visiteur généré par ANTLR.
 */
public interface JnaneLangVisitor extends JnaneLangParserVisitor<Object> {
    
    // Méthodes supplémentaires spécifiques au langage Jnane peuvent être ajoutées ici
    
}
