
# Branches und ihre Nutzung sowie Umgang mit Merge-Konflikten
Branches ermöglichen paralleles Arbeiten, indem sie voneinander separate Entwicklingszweige schaffen.
So können mehrere Funktionen unabhängig entwickelt und später zusammengeführt werden.

Beim Zusammenführen, also dem "Merge", kann es zu Konflikten kommen. Gerade dann, wenn in beiden Branches die gleichen Stellen einer Datei verändert wurden.
Die Konflikte betreffen nur die Person, die den Merge durchführt. Der Rest des Teams bemerkt von dem Konflikt nichts.
In diesem Fall kann Git nicht automatisch erkennen, welche Änderung genau übernommen werden soll und unterbricht deswegen den Merge-Prozess.

**Grundlegend kann man zwei Arten von Merge-Konflikten definieren:**

### 1. Git kann Merge nicht starten
Ein Merge-Vorgang kann fehlschlagen, wenn sich im Arbeitsverzeichnis oder im Staging-Bereich nicht gespeicherte Änderungen befinden.
Git verhindert in diesem Fall den Merge, um zu vermeiden, dass diese lokalen Anpassungen von den neuen Commits überschrieben werden.

Dabei handelt es sich nicht um einen Konflikt mit anderen Branches, sondern um Konflikte mit eigenen, noch nicht gesicherten Änderungen.
Um fortzufahren, sollte der lokale Zustand bereinigt oder gesichert werden.

```
git stash     - Änderungen zwischenspeichern
git commit    - Änderungen festschreiben
git reset     - Änderungen verwerfen
git checkout  - zu einem anderen Branch wechseln
```

Erst danach kann der Merge erfolgreich ausgeführt werden.

### 2. Während des Merge-Vorgangs tritt ein Fehler auf
Tritt ein Fehler während eines Merge-Vorgangs auf, gibt es Konflikte zwischen dem aktuellen Branch und dem Branch, der gemergt wird.
Diese Konflikte entstehen dann, wenn einer oder mehrere Personen denselben Codeabschnitt verändert haben.

Git versucht hier dann, die Änderungen automatisch zusammenzuführen, kann dies jedoch nicht in allen Fällen.
Die betroffenen Dateien müssen dann manuell überprüft und angepasst werden.
In solchen Fällen zeigt Git eine entsprechende Fehlermeldung an, die auf den Konflikt hinweist und die betroffenen Dateien nennt.

## Merge-Konflikte über die Befehlszeile beheben
Ein Weg, einen Merge-Konflikt zu lösen, ist das manuelle Bearbeiten der betroffenen Datei.
Man öffnet dazu die Datei in deinem Editor, entfernt die Konfliktmarker und übernimmt die gewünschten Änderungen.
```
git add <Dateiname>
git commit -m "Merge-Konflikt in <Dateiname> behoben"
```
Git erkennt, dass der Konflikt gelöst wurde, und erstellt dann automatisch einen Merge-Commit um den Vorgang abzuschließen.

**Nützliche Befehle bei Merge-Konflikten:**
```
git status          - zeigt Status der Dateien
git log             - zeigt Commit-Verlauf
git log --merge     - Protokoll-Liste wird erstellt, die zwischen mergenden Branches für Konflikte sorgen.
git checkout        - wechselt zu einem anderen Branch oder verwirft Änderungen
git reset           - setzt Änderungen zurück
git merge --abort   - Merge-Prozess verlassen, Branch auf Status vor dem Merge zurücksetzen.
```







