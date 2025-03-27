# Modifications du système de types dans Jnane

## Résumé des changements

Le système de types du langage Jnane a été modifié pour utiliser les types primitifs Java au lieu des types primitifs spécifiques à Jnane. Cette modification vise à simplifier l'interopérabilité avec Java et à rendre la syntaxe plus familière pour les développeurs Java.

## Correspondance des types

| Type Jnane (ancien) | Type Java (nouveau) |
|---------------------|---------------------|
| Entier              | int                 |
| Chaine              | String              |
| Booleen             | boolean             |
| Decimal             | double              |
| Vide                | null                |
| Liste               | List                |

## Modifications des littéraux booléens

Les littéraux booléens ont également été modifiés pour correspondre à la syntaxe Java :

| Littéral Jnane (ancien) | Littéral Java (nouveau) |
|-------------------------|-------------------------|
| Vrai                    | true                    |
| Faux                    | false                   |

## Impact sur le code existant

Ces modifications affectent tous les fichiers .jn existants qui utilisent les types primitifs Jnane. Les développeurs devront mettre à jour leurs fichiers pour utiliser les nouveaux types Java.

### Exemple de code avant modification

```jnane
@name math.operations:add
@arg x Entier
@arg y Entier
@field resultat Entier

{
    // Implémentation simple de l'addition
    resultat = x + y;
}
```

### Exemple de code après modification

```jnane
@name math.operations:add
@arg x int
@arg y int
@field resultat int

{
    // Implémentation simple de l'addition
    resultat = x + y;
}
```

## Avantages de cette modification

1. **Familiarité** : Les développeurs Java trouveront la syntaxe plus intuitive
2. **Cohérence** : Alignement avec les conventions de nommage Java standard
3. **Interopérabilité** : Facilite l'intégration avec des bibliothèques et frameworks Java
4. **Lisibilité** : Noms de types plus courts et plus standards

## Compatibilité

Cette modification n'est pas rétrocompatible avec le code existant. Les fichiers .jn existants devront être mis à jour pour utiliser les nouveaux types primitifs Java.

## Migration

Pour migrer votre code existant, utilisez les correspondances suivantes :

- Remplacez `Entier` par `int`
- Remplacez `Chaine` par `String`
- Remplacez `Booleen` par `boolean`
- Remplacez `Decimal` par `double`
- Remplacez `Vide` par `null`
- Remplacez `Vrai` par `true`
- Remplacez `Faux` par `false`
