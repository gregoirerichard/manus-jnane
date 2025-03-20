package com.causallang.compiler;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import com.causallang.compiler.CausalLangParser;

/**
 * Interface du visiteur pour l'AST du langage Causal.
 * Cette interface étend le visiteur généré par ANTLR.
 */
public interface CausalLangVisitor extends CausalLangParserVisitor<Object> {
    
    // Méthodes supplémentaires spécifiques au langage Causal peuvent être ajoutées ici
    
}
