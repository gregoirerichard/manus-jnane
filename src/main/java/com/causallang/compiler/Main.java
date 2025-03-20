package com.causallang.compiler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.IOException;

/**
 * Classe principale mise à jour pour utiliser le nouveau visiteur AST.
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar causal-lang-compiler.jar <fichier.causal>");
            System.exit(1);
        }
        
        String inputFile = args[0];
        
        try {
            // Création du lexer et du parser
            CharStream input = CharStreams.fromFileName(inputFile);
            CausalLangLexer lexer = new CausalLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CausalLangParser parser = new CausalLangParser(tokens);
            
            // Configuration du gestionnaire d'erreurs
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                        int line, int charPositionInLine, 
                                        String msg, RecognitionException e) {
                    System.err.println("Erreur de syntaxe à la ligne " + line + ":" + charPositionInLine + " - " + msg);
                }
            });
            
            // Analyse syntaxique
            ParseTree tree = parser.program();
            
            // Construction de l'AST
            ASTBuilderVisitor astBuilder = new ASTBuilderVisitor();
            ASTNode ast = astBuilder.visit(tree);
            
            // Affichage de l'AST
            System.out.println("Arbre Syntaxique Abstrait (AST):");
            System.out.println(ast);
            
            // Utilisation du visiteur simple pour afficher les éléments visités
            System.out.println("\nParcours de l'arbre syntaxique:");
            CausalLangVisitorImpl visitor = new CausalLangVisitorImpl();
            visitor.visit(tree);
            
            System.out.println("\nAnalyse syntaxique terminée avec succès.");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'analyse: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
