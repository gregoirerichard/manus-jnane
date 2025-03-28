# Validation des champs annotés @field et @view

## Résumé

Cette fonctionnalité ajoute un mécanisme de validation en sortie d'appel de fonction Jnane pour vérifier que tous les champs annotés avec `@field` ou `@view` sont correctement définis et disposent du type déclaré.

## Implémentation

### 1. Extraction des annotations

Une nouvelle classe `AnnotationExtractor` a été créée pour extraire les annotations `@field` et `@view` des fichiers Jnane. Cette classe :

- Utilise des expressions régulières pour capturer les annotations dans les deux formats (ancien et nouveau)
- Extrait le nom du champ et son type déclaré
- Distingue les annotations `@field` et `@view`
- Stocke les informations dans une classe interne `FieldInfo`

### 2. Validation des champs

La validation est effectuée dans la méthode `interpretFunctionFromFile` de la classe `JnaneInterpreter`, juste avant de retourner le résultat de l'exécution de la fonction. Le processus de validation :

1. Extrait toutes les annotations `@field` et `@view` du fichier de la fonction
2. Vérifie que chaque champ annoté est défini dans les variables de l'interpréteur
3. Vérifie que le type de la valeur est compatible avec le type déclaré dans l'annotation
4. Lève une exception `IllegalStateException` si un champ n'est pas défini ou a un type incorrect

### 3. Vérification de compatibilité des types

La méthode `isTypeCompatible` vérifie la compatibilité entre la valeur d'un champ et son type déclaré :

- Correspondances directes entre les noms de types
- Correspondances spéciales pour les types primitifs Java (`int`, `double`, `string`, `boolean`)
- Acceptation temporaire des types complexes pour une vérification plus souple

## Exemples d'utilisation

### Exemple 1: Fonction avec champs correctement définis

```java
@name com.jnane.test:fieldValidation
@arg first : int
@arg second : int

@field intResult : int
@view stringView : String
@field boolField : boolean

{
    intResult = first + second;
    stringView = "Résultat: " + intResult;
    boolField = intResult > 10;
}
```

### Exemple 2: Fonction avec champ manquant

```java
@name com.jnane.test:missingFieldValidation
@arg first : int
@arg second : int

@field intResult : int
@view stringView : String  // Ce champ n'est pas défini dans le corps de la fonction
@field boolField : boolean

{
    intResult = first + second;
    // stringView n'est pas défini intentionnellement
    boolField = intResult > 10;
}
```

Erreur générée : `Champ annoté non défini: @view stringView : String`

### Exemple 3: Fonction avec type incorrect

```java
@name com.jnane.test:wrongTypeValidation
@arg first : int
@arg second : int

@field intResult : int
@view stringView : String
@field boolField : boolean

{
    intResult = first + second;
    stringView = intResult;  // Erreur: assigne un int à un champ de type String
    boolField = true;
}
```

Erreur générée : `Type incompatible pour le champ stringView: attendu String, trouvé Integer`

## Avantages

1. **Sécurité accrue** : Détection précoce des erreurs de programmation
2. **Meilleure documentation** : Les annotations servent à la fois de documentation et de contrat
3. **Interopérabilité** : Facilite l'utilisation des fonctions comme paramètres d'autres fonctions
4. **Débogage simplifié** : Messages d'erreur clairs indiquant le problème exact

## Limitations actuelles et améliorations futures

1. La vérification des types complexes est actuellement permissive
2. Pas de support pour les types génériques ou paramétrés
3. Possibilité d'ajouter une validation plus stricte des types complexes à l'avenir
