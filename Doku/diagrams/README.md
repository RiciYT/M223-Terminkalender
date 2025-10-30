# Diagrams Directory - M223 Terminkalender

This directory contains all UML diagrams and documentation for the M223 Terminkalender project.

## 📊 Available Diagrams

### 1. State Diagram (Zustandsdiagramm) ⭐ Version 2.0

**Purpose:** Shows the complete navigation flow through the web application.

**Files:**
- `state-diagram.puml` - PlantUML source
- `state-diagram.md` - Mermaid diagram with description

**Documentation:**
- `STATE_DIAGRAM_IMPROVEMENTS.md` - Detailed technical improvements
- `STATE_DIAGRAM_COMPARISON.md` - Before/after comparison with validation
- `ZUSTANDSDIAGRAMM_ZUSAMMENFASSUNG.md` - Executive summary in German

**Features:**
- ✅ 21 transitions covering all navigation paths
- ✅ 7 states representing all pages
- ✅ 100% code coverage validated
- ✅ Precise authorization requirements
- ✅ Two access modes documented (Private Key vs Access Code)

**Last Updated:** October 30, 2025  
**Version:** 2.0

---

### 2. Entity-Relationship Diagram (ERD)

**Purpose:** Shows the database schema and relationships.

**Files:**
- `erd-diagram.puml` - PlantUML source
- `erd-diagram.md` - Mermaid diagram with description

**Features:**
- Entities: RESERVATIONS, PARTICIPANTS
- Relationship: One-to-Many (1:n)
- All attributes with constraints
- Validation rules

---

### 3. Class Diagram (Klassendiagramm)

**Purpose:** Shows the application architecture and class relationships.

**Files:**
- `class-diagram.puml` - PlantUML source
- `class-diagram.md` - Mermaid diagram with description

**Features:**
- All packages: model, repository, service, web, web.dto
- All classes with attributes and methods
- Relationships and dependencies
- Layer architecture visible

---

## 🔧 How to Use

### Viewing Diagrams

**Option 1: GitHub (Recommended)**
- View the `.md` files directly on GitHub - Mermaid diagrams render automatically

**Option 2: PlantUML**
- Install PlantUML: `brew install plantuml` (Mac) or download from plantuml.com
- Generate PNG: `plantuml state-diagram.puml`
- Generates: `state-diagram.png`

**Option 3: Online Tools**
- PlantUML Online: http://www.plantuml.com/plantuml/uml/
- Mermaid Live Editor: https://mermaid.live/

**Option 4: IDE Integration**
- **IntelliJ IDEA:** Install "PlantUML Integration" plugin
- **VS Code:** Install "PlantUML" or "Markdown Preview Mermaid Support" extensions

### Editing Diagrams

1. **Edit the source file** (`.puml` or `.md`)
2. **Validate syntax** using online tools or IDE plugins
3. **Keep both versions in sync** (PlantUML and Mermaid should match)
4. **Update documentation** if making significant changes

---

## 📚 Documentation Standards

Each diagram type has:
- ✅ PlantUML version (`.puml`)
- ✅ Mermaid version (`.md`)
- ✅ Description in the `.md` file
- ✅ Integration in main documentation

For the State Diagram specifically:
- ✅ Technical improvements documentation
- ✅ Before/after comparison
- ✅ Executive summary
- ✅ 100% validation against code

---

## 🔄 Version History

### State Diagram
- **v2.0** (2025-10-30): Complete overhaul with +5 transitions, 100% coverage
- **v1.0** (2024-10): Initial version

### ERD
- **v1.0** (2024-10): Initial version

### Class Diagram
- **v1.0** (2024-10): Initial version

---

## 📖 References

- **Main Documentation:** `../Projektdokumentation_M223.md`
- **Final Report:** `../../ABSCHLUSSBERICHT.md`
- **README:** `../../README.md`

---

## 🤝 Contributing

When updating diagrams:

1. **Understand the current state** - Read existing documentation
2. **Validate against code** - Ensure diagrams match implementation
3. **Update both formats** - Keep PlantUML and Mermaid in sync
4. **Document changes** - Explain what changed and why
5. **Update references** - Update main documentation if needed

---

**Project:** M223 Terminkalender  
**Module:** 223 – Multiuser-Applikationen objektorientiert realisieren  
**Last Updated:** October 30, 2025
