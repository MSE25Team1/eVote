# Übung 2: Einrichtung einer CI/CD-Pipeline
___
## 1. Einführung in CI/CD

___
## 2. Einrichtung der CI/CD-Pipeline

___
## 3. Testing in CI/CD

In modernen **CI/CD-Prozessen** sind automatisierte Tests ein zentraler Bestandteil der Build-Pipeline. Sie stellen sicher, dass neuer Code nur dann integriert und bereitgestellt wird, wenn alle definierten Tests erfolgreich durchlaufen wurden. Dadurch wird die Codequalität gesichert und Fehler werden frühzeitig erkannt.

In unserem Projekt wird das Testen über Maven ausgeführt, das in der **`pom.xml`** konfiguriert ist. Maven ist ein Build-Management- und Projektautomatisierungstool, das Abhängigkeiten verwaltet, den Quellcode kompiliert und Tests ausführt.

User CI/CD-Workflow (`test-and-publish.yml`) enthält den zentralen Befehl:

```yaml
- name: Run tests and build
  run: mvn -B -DskipTests=false package
```

### Parametererklärung

- **`-B`** steht für *Batch Mode* und sorgt dafür, dass Maven ohne interaktive Eingaben ausgeführt wird – ideal für automatisierte, skriptgesteuerte Builds.
- **`-DskipTests=false`** stellt sicher, dass die Tests **nicht** übersprungen, sondern tatsächlich ausgeführt werden.


### Maven Build Lifecycle

Der Befehl `mvn package` liest die Konfigurationen aus der **`pom.xml`**, in der Compiler-, Test- und Plugin-Definitionen enthalten sind.  
Maven durchläuft dabei automatisch alle Phasen des standardisierten **Build-Lifecycles**:

| **Phase**      | **Beschreibung**                                                     |
|----------------|----------------------------------------------------------------------|
| `validate`     | prüft die Projektstruktur                                            |
| `compile`      | kompiliert den Quellcode aus `src/main/java`                         |
| `test-compile` | kompiliert den Testcode aus `src/test/java`                          |
| `test`         | führt automatisiert **JUnit 5 Tests** aus (über das Surefire Plugin) |


### Aufbau und Funktionsweise der Tests

Die **Testklasse** `AppTest.java` liegt unter `src/test/java` und überprüft mit **JUnit 5** die Funktionen der **Produktivklasse** `App.java`.  
Durch die Annotation `@Test` erkennt Maven automatisch, welche Methoden getestet werden sollen.

Beispielsweise wird geprüft, ob die Methode `greet()` den richtigen Standardtext zurückgibt, wenn kein Benutzername angegeben wird.

### Automatische Testausführung in der CI/CD-Pipeline

Sobald neuer Code gepusht wird, führt GitHub Actions automatisch mvn package aus, startet die Tests und bricht den Build ab, falls ein Test fehlschlägt. Nur bei „grünen“ Tests wird der nächste Schritt der Pipeline, das Bauen und Veröffentlichen der Seite, ausgeführt.
___
## 4. Deployment-Konzepte

___
## 5. Branching und Pull Requests in Verbindung mit CI/CD

Grundlegend Umsetzung mit:
- **main-Branch** für stabilen Projektstand
- **feature-Branches** für neue Funktionen

Änderungen werden über Pull Requests in main nach einem 4-Augen-Prinzip gemergt.

CI/CD-Integration erfolgt mit **GitHub Actions** und GitHub Actions führt bei jedem Push in einem Branch automatisch die Pipeline aus.
Erst wenn alle Checks erfolgreich sind, kann der Pull Request gemergt werden.
Zusätzlich wird beim Merge in main automatisch eine (Test-)Website erzeugt und über **GitHub Pages** veröffentlicht.
Der Status der Pipeline und der Tests ist unter Actions des Repositories sichtbar.

