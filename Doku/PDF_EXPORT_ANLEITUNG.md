# Dokumentations-Export Anleitung

Diese Anleitung erklärt, wie die Projektdokumentation als PDF exportiert werden kann.

## Option 1: Pandoc (Empfohlen)

Pandoc ist ein universeller Dokumenten-Konverter, der Markdown in PDF konvertieren kann.

### Installation

**Windows:**
```bash
choco install pandoc miktex
```

**macOS:**
```bash
brew install pandoc
brew install --cask mactex
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install pandoc texlive-xetex texlive-fonts-recommended texlive-plain-generic
```

### PDF Generierung

```bash
pandoc Doku/Projektdokumentation_M223.md \
    --pdf-engine=xelatex \
    -o Doku/Projektdokumentation_M223.pdf \
    --toc \
    --number-sections \
    -V geometry:margin=2.5cm \
    -V fontsize=11pt \
    -V mainfont="DejaVu Sans" \
    --highlight-style=tango
```

**Parameter-Erklärung:**
- `--pdf-engine=xelatex`: Verwendet XeLaTeX für bessere Unicode-Unterstützung
- `-o`: Output-Datei
- `--toc`: Inhaltsverzeichnis generieren
- `--number-sections`: Abschnitte nummerieren
- `-V geometry:margin=2.5cm`: Seitenränder setzen
- `-V fontsize=11pt`: Schriftgröße
- `--highlight-style=tango`: Syntax-Highlighting für Code

### Mit Cover Page

Für eine professionelle Cover Page, erstelle `title.txt`:

```yaml
---
title: "M223 Terminkalender - Projektdokumentation"
author: "Team Name"
date: "Oktober 2025"
subtitle: "Multiuser-Applikationen objektorientiert realisieren"
---
```

Dann:
```bash
pandoc title.txt Doku/Projektdokumentation_M223.md \
    --pdf-engine=xelatex \
    -o Doku/Projektdokumentation_M223.pdf \
    --toc \
    --number-sections \
    -V geometry:margin=2.5cm
```

## Option 2: IntelliJ IDEA

1. Öffne `Projektdokumentation_M223.md` in IntelliJ IDEA
2. Installiere das "Markdown" Plugin (falls nicht vorhanden)
3. Rechtsklick auf die Datei → "Export to PDF" oder "Export to HTML"
4. Falls "Export to PDF" nicht verfügbar:
   - Erst als HTML exportieren
   - HTML in Browser öffnen
   - Browser-Druckfunktion nutzen (Ctrl+P) → "Als PDF speichern"

## Option 3: Visual Studio Code

Mit dem Extension "Markdown PDF":

1. Installiere Extension: "Markdown PDF" von yzane
2. Öffne `Projektdokumentation_M223.md`
3. Rechtsklick → "Markdown PDF: Export (pdf)"

## Option 4: Online-Tools

### Markdown to PDF (https://www.markdowntopdf.com/)

1. Besuche https://www.markdowntopdf.com/
2. Lade `Projektdokumentation_M223.md` hoch
3. Klicke auf "Convert"
4. Lade das generierte PDF herunter

### Dillinger (https://dillinger.io/)

1. Besuche https://dillinger.io/
2. Kopiere den Markdown-Inhalt in den Editor
3. Klicke auf "Export as" → "PDF"

## PlantUML Diagramme rendern

Die PlantUML-Diagramme in der Dokumentation können separat gerendert werden:

### Option 1: PlantUML Online Server

1. Besuche http://www.plantuml.com/plantuml/
2. Kopiere den PlantUML-Code (aus `.puml` Dateien)
3. Paste in den Editor
4. Klicke "Submit" zum Rendern
5. Lade als PNG/SVG herunter

### Option 2: PlantUML CLI

```bash
# Installation
brew install plantuml  # macOS
sudo apt-get install plantuml  # Linux

# Alle Diagramme rendern
plantuml Doku/diagrams/*.puml
```

### Option 3: IntelliJ/VS Code Plugin

- IntelliJ: Plugin "PlantUML Integration"
- VS Code: Extension "PlantUML"

Diagramme werden automatisch in der IDE angezeigt.

## Mermaid Diagramme

Die Mermaid-Diagramme in den `.md` Dateien werden automatisch in den meisten Markdown-Viewern gerendert:

- **GitHub**: Zeigt Mermaid automatisch an
- **VS Code**: Mit "Markdown Preview Mermaid Support" Extension
- **IntelliJ**: Mit Mermaid Plugin

Für PDF-Export: Mermaid wird von Pandoc nicht direkt unterstützt. Verwende:

1. Mermaid Live Editor: https://mermaid.live/
2. Kopiere den Mermaid-Code
3. Exportiere als PNG/SVG
4. Füge Bild in Dokumentation ein

## Vollständiges Paket erstellen

Um alle Diagramme einzubetten und ein vollständiges PDF zu erstellen:

```bash
# 1. PlantUML Diagramme rendern
plantuml -tpng Doku/diagrams/*.puml

# 2. Kopie der Markdown-Datei mit eingebetteten Bildern erstellen
# (Manuell die PlantUML-Code-Blöcke durch Bild-Links ersetzen)

# 3. PDF generieren
pandoc Doku/Projektdokumentation_M223_mit_Bildern.md \
    --pdf-engine=xelatex \
    -o Doku/Projektdokumentation_M223.pdf \
    --toc \
    --number-sections \
    -V geometry:margin=2.5cm
```

## Empfohlener Workflow

1. **Entwicklung**: Markdown-Dateien in IDE bearbeiten
2. **Review**: GitHub für Online-Ansicht nutzen (zeigt Mermaid automatisch)
3. **Abgabe**: Pandoc für finales PDF verwenden

## Troubleshooting

### "pandoc: xelatex not found"

Installiere eine TeX-Distribution:
- Windows: MiKTeX
- macOS: MacTeX
- Linux: TeX Live

### PlantUML Syntax-Fehler

Teste Diagramme online auf http://www.plantuml.com/plantuml/ vor dem lokalen Rendern.

### PDF-Schriftprobleme

Verwende eine universelle Schriftart:
```bash
-V mainfont="Arial" # oder "Helvetica" oder "DejaVu Sans"
```

## Abgabe-Checkliste

- [ ] Projektdokumentation als PDF exportiert
- [ ] Alle UML-Diagramme sind sichtbar (als Code oder Bilder)
- [ ] Inhaltsverzeichnis ist vorhanden
- [ ] Seitenzahlen sind korrekt
- [ ] Code-Snippets sind lesbar formatiert
- [ ] Dateiname: `Projektdokumentation_M223.pdf`
- [ ] PDF liegt im Ordner `Doku/`
