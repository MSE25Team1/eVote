# eVote

## Git Grundlagen – Team-Handout


### Was ist Git und warum sollte es verwendet werden?


### Grundlegende Git-Befehle

#### Start
`git init` : Initialisiert ein neues, leeres Git-Repository im aktuellen Verzeichnis. Ein **.git-Ordner** wird erstellt, worin alle Informationen zur Versionskontrolle abgelegt werden.  
`git clone <URL>` : Repository von URL klonen → Kopiert ein bestehendes Remote-Repository (z. B. von GitHub) auf das eigene lokale System. Dabei wird ein Arbeitsordner erstell worin der Projektinhalt gespeichert wird sowie die Versionsinformationen.  
#### Änderungen in Staging Area
`git status` : Zeigt den aktuellen Status 
`git add <Datei>` : eine oder mehrere Dateien werden zur Staging-Area hinzugefügt  
`git add .` : fügt alle geänderten Dateien hinzu
`git diff ` : Vergleicht aktuellen Stand mit letzter Version - macht Änderungen der Dateien Zeilenweise sichtbar. (Nur außerhalb der Staging-Area)
#### Versionsänderungen speichern
`git commit -m "Nachricht" ` : Speichert alle aktuell in der Staging-Area befindlichen Änderungen als neue Version (Commit) mit einer Nachricht.
`git commit -am "Nachricht" `: Kombiniert add und commit, erfasst jedoch nur Änderungen an bereits versionierten Dateien. Neue Dateien werden nicht berücksichtigt.
#### Versionsänderungen abrufen oder veröffentlichen  
`git pull` : Holt neue Änderungen vom Remote-Repository und integriert sie automatisch in den aktuellen Branch
`git push` : Überträgt lokale Commits auf das Remote-Repository, um sie für andere verfügbar zu machen.
`git fetch` : Lädt neue Daten vom Remote-Repository herunter, ohne sie automatisch zu integrieren.

#### Änderungen rückgängig machen
`git stash` : Speichert aktuelle Arbeitsänderungen temporär, um den Arbeitsordner zu leeren (KEIN Commit).
`git reset` : Setzt den aktuellen Branch auf einen bestimmten Commit zurück. Modus:`--soft, --mixed, --hard`
`git restore <Datei>` : Datei wird auf den Zustand des letzten Commits zurückgesetzt.

#### Branches verwalten  
`git branch` : Listet alle lokalen Branches  
`git checkout <Branch>` : Wechselt in einen anderen Branch und aktualisiert den Arbeitsstand entsprechend.  
`git switch <Branch>` : Branch-Wechsel  
`git merge <Branch>` : Änderungen in den aktuellen Branch zusammenführen.  

### Branches und ihre Nutzung, Umgang mit Merge-Konflikten


### Git mit IntelliJ benutzen: Local Repository und Remote Repository

### Nützliche Git-Tools und Plattformen

#### Git Plattformen

- [GitHub](https://github.com)
  > GitHub ist eine proprietäre und öffentliche Softwareentwicklungsplattform auf Grundlage der Versionsverwaltungs-Software Git. GitHub war im Jahr 2011 bei Open-Source-Software der populärste Dienst seiner Art, gemessen an der Anzahl der Codebeiträge („Commits“). Der Dienst hat über 83 Millionen registrierte Nutzer und verwaltet über 200 Millionen Repositories (Stand: August 2022). Neben vielen sehr kleinen oder oft nur vom Besitzer genutzten Projekten gibt es mehrere bekannte größere Open-Source-Projekte, die bei der Versionsverwaltung ihres Quelltextes GitHub verwenden. Das Unternehmen GitHub, Inc. hat seinen Sitz in San Francisco in den USA und gehört zu Microsoft.
  >
  > Quelle: https://en.wikipedia.org/wiki/GitHub

- [GitLab](https://gitlab.com)
  > GitLab ist eine Softwareentwicklungsplattform auf Git-Basis. GitLab bietet ein Issue-Tracking-System mit Kanban-Board, ein System für Continuous Integration und Continuous Delivery (CI/CD), ein Wiki, eine Container-Registry, einen Sicherheitsscanner für Container und Sourcecode sowie Multi-Cluster-Verwaltung und -Überwachung. GitLab ist in Produkte für Entwickler, wie zum Beispiel AWS oder Google Cloud, integrierbar und über eine API fernsteuerbar. GitLab ist in den Programmiersprachen Ruby und Go geschrieben.
  >
  > Die GitLab Community Edition (CE) wird als Open-Source-Software unter der MIT-Lizenz entwickelt. Seit August 2013 bietet die GitLab Inc. mit der Enterprise Edition (EE) eine speziell für Unternehmen entwickelte Version an. Man kann GitLab auf eigener Hardware betreiben oder seit 2012 auf GitLab.com als GitLab Enterprise Edition – als Software as a Service (SaaS). Neben kostenlosem Hosting privater und öffentlicher Repositorys wird auch kostenpflichtiger Support angeboten.
  >
  > Quelle: https://de.wikipedia.org/wiki/GitLab

  Viele Hochschulen bieten ihren Studierenden und Mitarbeitenden eigens gehostete GitLab-Instanzen an, z. B.:
    - [GitLab an der THL](https://git.mylab.th-luebeck.de)
    - [GitLab an der BHT](https://gitlab.bht-berlin.de)

#### Git Clients

- [GitHub Desktop](https://desktop.github.com)
  > Experience Git without the struggle
  >
  > Whether you're new to Git or a seasoned user, GitHub Desktop simplifies your development workflow.
  >
  > Quelle: https://desktop.github.com
- [Sourcetree](https://www.sourcetreeapp.com)
  > A free Git client for Windows and Mac
  >
  > Sourcetree simplifies how you interact with your Git repositories so you can focus on coding. Visualize and manage your repositories through Sourcetree's simple Git GUI.
  >
  > Quelle: https://www.sourcetreeapp.com
- [GitKraken Desktop](https://www.gitkraken.com/git-client)
  > Simplifying Git for any OS
  >
  > Countless developers and teams worldwide use GitKraken Desktop for its intuitive GUI, powerful terminal, and cross-platform support for Windows, Mac, and Linux.
  >
  > Quelle: https://www.gitkraken.com/git-client

#### Git Lernressourcen

- [Learn Git Branching](https://learngitbranching.js.org/) - Interaktives Git-Lernspiel
  > Interested in learning Git? Well you've come to the right place! "Learn Git Branching" is the most visual and interactive way to learn Git on the web; you'll be challenged with exciting levels, given step-by-step demonstrations of powerful features, and maybe even have a bit of fun along the way.
  >
  > Quelle: https://learngitbranching.js.org/
- [Git Explained in 100 Seconds](https://www.youtube.com/watch?v=hwP7WQkmECE&t=9s) von [Fireship](https://www.youtube.com/@Fireship) - Kurzvideo über Git
  > Learn the basics of Git in 100 seconds.
  >
  > Quelle: https://www.youtube.com/watch?v=hwP7WQkmECE&t=9s
- [Git and GitHub for Beginners - Crash Course](https://www.youtube.com/watch?v=RGOj5yH7evk&t=70s) von [freeCodeCamp](https://www.youtube.com/@freecodecamp) - 1-stündiger Crash-Kurs über Git und GitHub
  > Learn about Git and GitHub in this tutorial. These are important tools for all developers to understand. Git and GitHub make it easier to manage different software versions and make it easier for multiple people to work on the same software project.
  >
  > Quelle: https://www.youtube.com/watch?v=RGOj5yH7evk&t=70s
- [Awesome Git](https://github.com/dictcp/awesome-git) - Eine kuratierte Liste von Git-Ressourcen
  > A curated list of amazingly awesome Git tools, resources and shiny things.
  >
  > Quelle: https://github.com/dictcp/awesome-git