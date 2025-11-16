Genutztes Plugin: SonarQube for IDE

# Refactoring mit SonarQube for IDE

Das Plugin linted CodeSmells, nicht eingehaltene NamingConventions und potenzielle Risiken zum Beipiel durch ungenutzte Variablen oder hoher Komplexität direkt in der IDE. Es bietet zudem teilweise automatische Refactoring-Vorschläge an, die direkt im Code umgesetzt werden können.

## Erkannte Probleme

- Hohe Komplexität des Poll-Konstruktors
  Sonar-Hinwies: Refactor this method to reduce its Cognitive Complexity from 16 to the 15 allowed.
  Fieser Hinweis wurde genauso gemeinsam mit dem betroffenen Code an das LLM Vorgeschlagen.
  Das LLM hat vorgeschlagen die Validierung der Konstruktor-Parameter und die Initialisierung der VoteCounts in separate Methoden auszulagern. Mit Hilfe des LLMs ist der Konstruktor nun eine übersichtliche Methode, die in einer Zeile die Parameter validiert, dann die Objekteigenschaften initialisiert und abschließend die VoteCounts initialisiert.
- Auskommentierter Code wurde entfernt
- In regex wurde 0-9 durch \d ersetzt, umso expliziter zu sein
- SonarQube: Method has 9 parameters, which is greater than 7 authorized. <- wurde ignoriert, da es in diesem Fall auf einen Konstruktor beschränkt ist und hier bereits so weite es geht ValueObjects wie Adresse, email, Name verwendet wurden um die Parameteranzahl soweit wie möglich zu reduzieren.
- VoterRegisteredEvent could be a record. Wurde umgesetzt. So wurde auch der Hinweis behoben bei den Gettern, die nicht vom Standard abweichten.