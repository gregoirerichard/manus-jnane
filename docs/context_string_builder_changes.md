# Amélioration de la méthode toString dans ContextStringBuilder

## Résumé des modifications

La méthode `buildString` dans la classe `ContextStringBuilder` a été modifiée pour améliorer l'affichage de la structure du programme parsé. Les modifications apportées permettent :

1. D'afficher la valeur des éléments terminaux directement
2. De conserver sur la même ligne les assignations et appels de fonction
3. D'améliorer la lisibilité générale de la représentation textuelle

## Détails techniques

### Traitement des nœuds terminaux

Les nœuds terminaux sont maintenant affichés directement avec leur valeur textuelle, suivie d'un espace :

```java
if (context instanceof TerminalNode) {
    sb.append(context.getText()).append(" ");
    return;
}
```

### Détection des assignations et appels de fonction

La méthode identifie les contextes d'assignation et d'appel de fonction en vérifiant si leur nom contient certains motifs :

```java
boolean isAssignment = contextName.contains("Assignment") || contextName.equals("AssignmentExpr");
boolean isFunctionCall = contextName.contains("FunctionCall") || contextName.equals("FunctionCallExpr");
```

### Traitement spécial pour les assignations et appels de fonction

Pour ces contextes spécifiques, tous les enfants sont traités sur la même ligne :

```java
if (isAssignment || isFunctionCall) {
    for (int i = 0; i < level; i++) {
        sb.append(INDENT);
    }
    sb.append(contextName).append(": ");
    
    // Traiter les enfants sur la même ligne
    for (int i = 0; i < context.getChildCount(); i++) {
        ParseTree child = context.getChild(i);
        buildString(child, sb, 0); // Pas d'indentation supplémentaire
    }
    sb.append("\n");
}
```

## Exemple de résultat attendu

Avant :
```
Program
  Statement
    ExpressionStmt
      Expression
        AssignmentExpr
          ConditionalExpr
            LogicalOrExpr
              LogicalAndExpr
                EqualityExpr
                  RelationalExpr
                    AdditiveExpr
                      MultiplicativeExpr
                        UnaryExpr
                          PostfixExpr
                            PrimaryExpr
                              ID
          EQUALS
          ConditionalExpr
            LogicalOrExpr
              LogicalAndExpr
                EqualityExpr
                  RelationalExpr
                    AdditiveExpr
                      MultiplicativeExpr
                        UnaryExpr
                          PostfixExpr
                            PrimaryExpr
                              FunctionCallExpr
                                ID
                                COLON
                                ID
                                LPAREN
                                ArgumentList
                                  Expression
                                    ...
                                  COMMA
                                  Expression
                                    ...
                                RPAREN
```

Après :
```
Program
  Statement
    ExpressionStmt
      Expression
        AssignmentExpr: resultat = math:add(first: 5, second: 3)
```

## Avantages de cette modification

1. **Lisibilité améliorée** : La structure du programme est plus facile à comprendre
2. **Concision** : Les expressions complexes sont affichées de manière plus compacte
3. **Valeurs visibles** : Les valeurs des éléments terminaux sont directement visibles
4. **Meilleure représentation** : Les assignations et appels de fonction sont affichés d'une manière qui reflète mieux leur structure logique
