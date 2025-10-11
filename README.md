# eVote

# Git Grundlagen – Team-Handout


## Was ist Git und warum sollte es verwendet werden?


## Grundlegende Git-Befehle

### Start
`git init` : Initialisiert ein neues, leeres Git-Repository im aktuellen Verzeichnis. Ein **.git-Ordner** wird erstellt, worin alle Informationen zur Versionskontrolle abgelegt werden.  
`git clone <URL>` : Repository von URL klonen -> Kopiert ein bestehendes Remote-Repository (z. B. von GitHub) auf das eigene lokale System. Dabei wird ein Arbeitsordner erstell worin der Projektinhalt gespeichert wird sowie die Versionsinformationen.  
### Änderungen in Staging Area
`git status` : Zeigt den aktuellen Status 
`git add <Datei>` : eine oder mehrere Dateien werden zur Staging-Area hinzugefügt  
`git add .` : fügt alle geänderten Dateien hinzu
`git diff ` : Vergleicht aktuellen Stand mit letzter Version - macht Änderungen der Dateien Zeilenweise sichtbar. (Nur außerhalb der Staging-Area)
### Versionsänderungen speichern
`git commit -m "Nachricht" ` : Speichert alle aktuell in der Staging-Area befindlichen Änderungen als neue Version (Commit) mit einer Nachricht.
`git commit -am "Nachricht" `: Kombiniert add und commit, erfasst jedoch nur Änderungen an bereits versionierten Dateien. Neue Dateien werden nicht berücksichtigt.
### Versionsänderungen abrufen oder veröffentlichen  
`git pull` :  Holt neue Änderungen vom Remote-Repository und integriert sie automatisch in den aktuellen Branch
`git push` : Überträgt lokale Commits auf das Remote-Repository, um sie für andere verfügbar zu machen.
`git fetch` : Lädt neue Daten vom Remote-Repository herunter, ohne sie automatisch zu integrieren.

### Änderungen rückgängig machen
`git stash` : Speichert aktuelle Arbeitsänderungen temporär, um den Arbeitsordner zu leeren (KEIN Commit).
`git reset` : Setzt den aktuellen Branch auf einen bestimmten Commit zurück. Modus:`--soft, --mixed, --hard`
`git restore <Datei>` : Datei wird auf den Zustand des letzten Commits zurückgesetzt.

### Branches verwalten  
`git branch` : Listet alle lokalen Branches  
`git checkout <Branch>` : Wechselt in einen anderen Branch und aktualisiert den Arbeitsstand entsprechend.  
`git switch <Branch>` : Branch-Wechsel  
`git merge <Branch>` : Änderungen in den aktuellen Branch zusammenführen.  

## Branches und ihre Nutzung, Umgang mit Merge-Konflikten


## Git mit IntelliJ benutzen: Local Repository und Remote Repository


## Nützliche Git-Tools und Plattformen (GitHub)






