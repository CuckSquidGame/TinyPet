# Projet Tiny Pet, module Développement d'applications sur le Cloud

## Réalisé par Florian Lassalle

J'ai réalisé cette application en solo, je vous prie donc de m'excuser si elle est moins aboutie que celle réalisée par les autres groupes.
<hr>

URL de l'application :
https://tinypet-389612.ew.r.appspot.com/

URL du GITHUB :
https://github.com/CuckSquidGame/TinyPet

Note :
L'application met parfois du temps à se mettre à jour visuellement

(exemples : 
- la page d'accueil peut mettre quelques secondes à charger
- une fois une pétition signée, elle peut mettre quelques secondes avant d'apparaître dans "mes pétitions")

Dans le doute, ne pas hésiter à mettre un coup de CTRL + F5

Les fonctionnalités implémentées :
- Un servlet pour peupler le datastore avec des pétitions (en cliquant sur "Populate the table Petition")
- L'affichage des 100 pétitions les plus signées, triées ensuite par date
- La création d'une pétition (en saisissant son nom, son contenu, et ses tags (important de les saisir séparés par des virgules et sans espace). L'email de l'utilisateur et son nom sont récupérés immédiatement pour être stockés respectivement parmi ses signataires et comme propriétaire)
- Possibilité de signer une pétition (on ne peut signer une pétition qu'une fois)
- Rechercher les pétitions signées par un utilisateur
- Rechercher les pétitions par leur nom
- Rechercher les pétitions par leur tags
- Une page "Mes pétitions" contenant les pétitions signées par l'utilisateur connecté (donc aussi logiquement celles qu'il a créées)
- La connexion via compte Google

## Les index utilisés :

``` 
indexes:

- kind: Pétition 
  properties: 
    - name: Date 
      direction: desc
    - name: Signataires
    - name: nbSignatures
      direction: desc
    - name: Propriétaire
    - name: Body
    - name: Nom
    - name: Tags


- kind: Pétition 
  ancestor: false
  properties: 
    - name: nbSignatures 
      direction: desc
    - name: Date
      direction: desc

- kind: Pétition 
  ancestor: false
  properties: 
    - name: Signataires
    - name: Date 
      direction: desc
```