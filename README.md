# Compilateur pour le langage de programmation Causal

Ce projet est un compilateur pour le langage de programmation Causal, un langage spécialisé dans la manipulation de réseaux causaux représentant des états.

## Caractéristiques du langage Causal

- **États typés et immutables** : Représentation formelle des états d'un système avec un typage fort
- **Fonctions pures** : Organisées en namespaces avec une notation `ns:fonction(arg1, arg2, ...)`
- **Vues** : Mécanisme permettant d'observer un état selon différentes perspectives
- **Pattern lentille** : Facilite la modification d'objets imbriqués tout en préservant l'immutabilité
- **Annotations** : Système riche pour enrichir les définitions (`@name`, `@arg`, `@field`, `@view`)
- **Conservation d'historique** : Traçabilité complète des transformations d'états

## Structure du projet

- `src/main/antlr4/` : Contient la grammaire ANTLR4 du langage Causal
- `src/main/java/` : Code source Java du compilateur
- `src/test/` : Tests unitaires et d'intégration

## Prérequis

- Java 11 ou supérieur
- Maven 3.6 ou supérieur

## Compilation

Pour compiler le projet :

```bash
mvn clean package
```

Cela générera un fichier JAR exécutable dans le répertoire `target/`.

## Utilisation

Pour analyser un fichier Causal :

```bash
java -jar target/causal-lang-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar fichier.causal
```

## Fonctionnalités actuelles

- Analyse lexicale et syntaxique complète du langage Causal
- Construction d'un arbre syntaxique abstrait (AST)
- Affichage formaté de l'AST

## Fonctionnalités à venir

- Vérification sémantique
- Génération de code
- Optimisations
- Interpréteur pour exécution directe

## Licence

Ce projet est sous licence MIT.
