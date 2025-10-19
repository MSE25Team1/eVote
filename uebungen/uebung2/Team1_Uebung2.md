#Übung 2: Einrichtung einer CI/CD-Pipeline

## 1. Einführung in CI/CD
## 2. Einrichtung der CI/CD-Pipeline
## 3. Testing
## 4. Deployment-Konzepte
## 5. Branching und Pull Requests in Verbindung mit CI/CD

Grundlegend Umsetzung mit:
- **main-Branch** für stabilen Projektstand
- **feature-Branches** für neue Funktionen

Änderungen werden über Pull Requests in main nach einem 4-Augen-Prinzip gemergt.

CI/CD-Integration erfolgt mit **GitHub Actions** und GitHub Actions führt bei jedem Push in einem Branch automatisch die Pipeline aus.
Erst wenn alle Checks erfolgreich sind, kann der Pull Request gemergt werden.
Zusätzlich wird beim Merge in main automatisch eine (Test-)Website erzeugt und über **GitHub Pages** veröffentlicht.
Der Status der Pipeline und der Tests ist unter Actions des Repositories sichtbar.

