package com.jnane.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Interpréteur pour le langage Jnane
 */
public class JnaneInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(JnaneInterpreter.class);

    // Stockage des variables
    private final Map<String, Object> variables = new HashMap<>();

    // Stockage des définitions de fonctions et leurs paramètres
    private final Map<String, Set<String>> functionParameters = new HashMap<>();

    /**
     * Constructeur
     */
    public JnaneInterpreter() {
        logger.debug("Initialisation de l'interpréteur Jnane");
        // Initialiser les paramètres connus pour math:add
        Set<String> mathAddParams = new HashSet<>();
        mathAddParams.add("first");
        mathAddParams.add("second");
        functionParameters.put("math:add", mathAddParams);
    }

    /**
     * Récupère la valeur d'une variable
     *
     * @param name Nom de la variable
     * @return Valeur de la variable
     */
    public Object getVariableValue(String name) {
        logger.debug("Récupération de la valeur de la variable {}: {}", name, variables.get(name));
        return variables.get(name);
    }

    /**
     * Définit la valeur d'une variable
     *
     * @param name Nom de la variable
     * @param value Valeur de la variable
     */
    public void setVariableValue(String name, Object value) {
        variables.put(name, value);
        logger.debug("Variable définie: {} = {}", name, value);
    }

    /**
     * Interprète une addition entre deux entiers
     *
     * @param a Premier entier
     * @param b Deuxième entier
     * @return Résultat de l'addition
     */
    public int interpretAddition(int a, int b) {
        int result = a + b;
        logger.debug("Interprétation de l'addition: {} + {} = {}", a, b, result);
        return result;
    }

    /**
     * Vérifie si un paramètre est valide pour une fonction donnée
     *
     * @param functionName Nom de la fonction
     * @param paramName Nom du paramètre
     * @return true si le paramètre est valide, false sinon
     */
    public boolean isValidParameter(String functionName, String paramName) {
        if (!functionParameters.containsKey(functionName)) {
            logger.error("Fonction inconnue: {}", functionName);
            return false;
        }

        boolean isValid = functionParameters.get(functionName).contains(paramName);
        if (!isValid) {
            logger.error("Paramètre inconnu '{}' pour la fonction '{}'", paramName, functionName);
        }
        return isValid;
    }

    /**
     * Interprète un appel de fonction avec arguments nommés
     *
     * @param functionName Nom de la fonction
     * @param namedArgs Arguments nommés de la fonction
     * @return Résultat de l'appel de fonction
     * @throws IllegalArgumentException si un argument inconnu est fourni
     */
    public Object interpretFunctionCallWithNamedArgs(String functionName, Map<String, Object> namedArgs) {
        logger.debug("Interprétation de l'appel de fonction: {} avec arguments nommés: {}", functionName, namedArgs);

        // Vérifier que tous les arguments sont valides
        for (String argName : namedArgs.keySet()) {
            if (!isValidParameter(functionName, argName)) {
                String errorMsg = "Argument inconnu '" + argName + "' pour la fonction '" + functionName + "'";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
        }

        if (functionName.equals("math:add")) {
            // Vérifier que les arguments requis sont présents
            if (!namedArgs.containsKey("first") || !namedArgs.containsKey("second")) {
                String errorMsg = "Arguments manquants pour la fonction math:add. Requis: 'first' et 'second'";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            Object firstArg = namedArgs.get("first");
            Object secondArg = namedArgs.get("second");

            logger.debug("Arguments pour math:add: first={} (type: {}), second={} (type: {})",
                    firstArg, (firstArg != null ? firstArg.getClass().getSimpleName() : "null"),
                    secondArg, (secondArg != null ? secondArg.getClass().getSimpleName() : "null"));

            try {
                int first = convertToInt(firstArg);
                int second = convertToInt(secondArg);
                int result = interpretAddition(first, second);
                // Stocker directement le résultat dans la variable "resultat"
                setVariableValue("resultat", result);
                logger.debug("Résultat de math:add stocké dans la variable 'resultat': {}", result);
                return result;
            } catch (NumberFormatException e) {
                logger.error("Impossible de convertir les arguments en entiers: first={}, second={}", firstArg, secondArg, e);
                throw new IllegalArgumentException("Les arguments de math:add doivent être des entiers", e);
            }
        } else if (functionName.equals("print")) {
            // Gestion de la fonction print (qui n'utilise pas d'arguments nommés)
            if (namedArgs.containsKey("message")) {
                String message = String.valueOf(namedArgs.get("message"));
                logger.info("Print: {}", message);
                return message;
            } else {
                logger.warn("Fonction print appelée sans argument 'message'");
                return null;
            }
        }

        logger.warn("Fonction non supportée: {}", functionName);
        return null;
    }

    /**
     * Interprète un appel de fonction (compatibilité avec l'ancienne méthode)
     *
     * @param functionName Nom de la fonction
     * @param args Arguments de la fonction
     * @return Résultat de l'appel de fonction
     */
    public Object interpretFunctionCall(String functionName, Object... args) {
        logger.debug("Interprétation de l'appel de fonction (méthode legacy): {} avec {} arguments", functionName, args.length);

        // Convertir les arguments positionnels en arguments nommés
        Map<String, Object> namedArgs = new HashMap<>();

        if (functionName.equals("math:add") && args.length == 2) {
            namedArgs.put("first", args[0]);
            namedArgs.put("second", args[1]);
            return interpretFunctionCallWithNamedArgs(functionName, namedArgs);
        } else if (functionName.equals("print") && args.length > 0) {
            namedArgs.put("message", args[0]);
            return interpretFunctionCallWithNamedArgs(functionName, namedArgs);
        }

        logger.warn("Fonction non supportée ou arguments invalides: {}", functionName);
        return null;
    }

    /**
     * Convertit un objet en entier
     *
     * @param obj Objet à convertir
     * @return Valeur entière
     * @throws NumberFormatException si la conversion échoue
     */
    private int convertToInt(Object obj) throws NumberFormatException {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).intValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else {
            throw new NumberFormatException("Impossible de convertir l'objet en entier: " + obj);
        }
    }

    /**
     * Réinitialise l'état de l'interpréteur
     */
    public void reset() {
        variables.clear();
        logger.debug("État de l'interpréteur réinitialisé");
    }

    /**
     * Exécute un script Jnane
     *
     * @param visitor Visiteur pour l'analyse du script
     * @param tree Arbre syntaxique du script
     * @return Résultat de l'exécution
     */
    public Object executeScript(JnaneExpressionVisitor visitor, org.antlr.v4.runtime.tree.ParseTree tree) {
        logger.info("Exécution du script Jnane");
        // Ne pas réinitialiser les variables pour permettre le partage entre les scripts
        // reset();
        Object result = visitor.visit(tree);
        logger.info("Exécution du script terminée");
        // Afficher toutes les variables définies pour le débogage
        logger.debug("Variables définies après exécution: {}", variables);
        return result;
    }
}
