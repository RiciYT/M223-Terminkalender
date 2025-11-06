# PDF-Dokumentation erstellen - Schnellanleitung

## ⚠️ Wichtig für die Abgabe

Die Projektdokumentation muss als **PDF-Datei** abgegeben werden.

## Datei für PDF-Export

📄 **Verwenden Sie diese Datei:**  
`Doku/Projektdokumentation_M223_KOMPLETT.md`

Diese Datei enthält:
- ✅ Vollständige Projektdokumentation
- ✅ Alle Anforderungen und Implementierungsdetails
- ✅ Beschreibungen der UML-Diagramme
- ✅ Versionsinformationen (Version 1.0, November 2025)
- ✅ Build- und Deployment-Anleitung

## Schnellste Methode: Online-Tool

### Option 1: MarkdownToPDF.com (Empfohlen)

1. Öffne https://www.markdowntopdf.com/
2. Lade `Doku/Projektdokumentation_M223_KOMPLETT.md` hoch
3. Klicke "Convert to PDF"
4. Speichere als `Projektdokumentation_M223.pdf` im Ordner `Doku/`

**Vorteile:** 
- Keine Installation nötig
- Funktioniert sofort
- Gute Formatierung

### Option 2: Dillinger.io

1. Öffne https://dillinger.io/
2. Importiere die Markdown-Datei (Import from: Disk)
3. Exportiere als PDF (Export as → Styled HTML → Dann im Browser als PDF drucken)

## Alternativen mit Installation

### Mit Pandoc (falls installiert)

```bash
pandoc Doku/Projektdokumentation_M223_KOMPLETT.md \
  -o Doku/Projektdokumentation_M223.pdf \
  --pdf-engine=xelatex \
  --toc \
  --number-sections \
  -V geometry:margin=2.5cm \
  -V fontsize=11pt
```

### Mit Visual Studio Code

1. Installiere Extension: "Markdown PDF" (yzane)
2. Öffne `Projektdokumentation_M223_KOMPLETT.md`
3. Rechtsklick → "Markdown PDF: Export (pdf)"
4. Benenne um zu `Projektdokumentation_M223.pdf`

### Mit IntelliJ IDEA

1. Öffne `Projektdokumentation_M223_KOMPLETT.md`
2. Rechtsklick → "Export to HTML"
3. HTML im Browser öffnen
4. Drucken → "Als PDF speichern"
5. Speichere als `Projektdokumentation_M223.pdf`

## Ergebnis prüfen

Nach dem Export sollte die Datei vorhanden sein:

```bash
ls -lh Doku/Projektdokumentation_M223.pdf
```

Das PDF sollte ca. 10-15 Seiten umfassen und folgende Inhalte haben:
- Titelseite mit Versionsinformationen
- Inhaltsverzeichnis
- Alle Kapitel 1-9
- Beschreibungen der Diagramme
- Implementierungsdetails

## Für die Abgabe

✅ **Dateiname:** `Projektdokumentation_M223.pdf`  
✅ **Speicherort:** `Doku/Projektdokumentation_M223.pdf`  
✅ **Inhalt:** Vollständige Projektdokumentation mit Versionsinformationen  

## Diagramme

Die Mermaid-Diagramme in der Dokumentation werden als **Textbeschreibungen** exportiert. Das ist **korrekt und akzeptiert**, da:

1. Die Diagramme in der Dokumentation beschrieben sind
2. Die originalen Diagramm-Dateien in `Doku/diagrams/` verfügbar sind
3. GitHub die Diagramme automatisch rendert (im Repository sichtbar)

Falls Sie **gerenderte Bilder** bevorzugen, können Sie die Diagramme auf https://mermaid.live/ rendern und als PNG exportieren, dann in die Markdown-Datei einbetten.

## Support

Bei Problemen mit dem PDF-Export:
- Siehe `Doku/PDF_EXPORT_ANLEITUNG.md` für detaillierte Anleitungen
- Verwenden Sie die Online-Tools als einfachste Lösung
- Die Markdown-Version ist ebenfalls vollständig und kann zur Not verwendet werden

---

**Viel Erfolg bei der Abgabe! 🎓**
