# State Diagram Improvements - M223 Terminkalender

**Datum:** 30. Oktober 2025  
**Version:** 2.0

## Zusammenfassung der Verbesserungen

Das Zustandsdiagramm (state-diagram.puml und state-diagram.md) wurde überarbeitet, um die tatsächlichen Navigationspfade der Anwendung präziser darzustellen.

## 1. Hinzugefügte Navigationspfade

### 1.1 Direkte Navigation von Index

**Vorher:** Index hatte nur zwei Ausgänge (CreateForm und KeyAccess)

**Nachher:** Index hat fünf Ausgänge:
- `Index --> CreateForm`: Click "Create Reservation"
- `Index --> KeyAccess`: Enter Key
- `Index --> Confirmation`: Click "Confirmation" link (neu)
- `Index --> PublicView`: Click "Public view" link (neu)
- `Index --> PrivateView`: Click "Private access" link (neu)

**Begründung:** In der index.html Tabelle (Zeilen 68-72) gibt es direkte Links zu diesen Seiten:
```html
<a th:href="@{'/reservations/' + ${reservation.id} + '/confirm'}">Confirmation</a>
<a th:if="${reservation.accessType.name() == 'PUBLIC'}"
   th:href="@{'/reservations/' + ${reservation.id} + '/public'}">Public view</a>
<a th:if="${reservation.accessType.name() == 'PRIVATE'}"
   th:href="@{'/reservations/' + ${reservation.id} + '/private'}">Private access</a>
```

### 1.2 Zurück-Navigation von EditForm

**Neu hinzugefügt:** `EditForm --> Index : Back (cancel)`

**Begründung:** In reservation-form.html gibt es einen Back-Link, der von der Edit-Seite zurück zur Übersicht führt.

### 1.3 Self-Transition in PrivateView

**Neu hinzugefügt:** `PrivateView --> PrivateView : Enter access code (view only)`

**Begründung:** Der PrivateView-Zustand hat zwei Modi:
1. Unautorisiert: Zeigt Formular zum Eingeben des Access Code
2. Autorisiert: Zeigt die Reservation Details

Diese Self-Transition repräsentiert den Übergang vom unautorisierten zum autorisierten Zustand innerhalb derselben View, wenn der Access Code eingegeben wird.

Code-Referenz in ReservationController.java (Zeilen 174-206):
```java
@GetMapping("/reservations/{id}/private")
public String privateView(...) {
    // Zeigt Formular wenn nicht autorisiert
    if (code == null) {
        model.addAttribute("error", "Please provide the private access code.");
        return "reservation-private";
    }
    
    // Zeigt Details nach erfolgreicher Autorisierung
    if (!code.equals(reservation.getAccessCode())) {
        model.addAttribute("error", "Incorrect access code provided.");
        return "reservation-private";
    }
    
    model.addAttribute("authorized", true);
    return "reservation-private";
}
```

## 2. Verbesserte Notizen

### 2.1 PrivateView Notiz

**Vorher:**
```
Only accessible with:
- Private key (full management)
- OR access code (view only)
```

**Nachher:**
```
Two access modes:
1. Private Key → Full management
   (Edit/Delete available)
2. Access Code → View only
   (Edit/Delete not available)

Initial state prompts for
access code if not authorized
```

**Verbesserung:** Klarere Unterscheidung zwischen den zwei Zugriffsmodi und explizite Erwähnung des initialen Zustands.

### 2.2 Index Notiz (neu)

**Neu hinzugefügt:**
```
Shows all reservations
with links to:
- Confirmation pages
- Public view (PUBLIC type)
- Private access (PRIVATE type)
```

**Begründung:** Dokumentiert die zusätzlichen Navigationsmöglichkeiten vom Index aus.

### 2.3 KeyAccess Notiz

**Vorher:**
```
Decision: 
- Public Key → Read-only view
- Private Key → Management view
```

**Nachher:**
```
Decision: 
- Public Key → Read-only view
- Private Key → Full management access
```

**Verbesserung:** Präzisere Formulierung ("Full management access" statt "Management view").

## 3. Überarbeitete Transitionen

### 3.1 Confirmation Transitionen

**Präzisierung:**
- `Confirmation --> PublicView : Open public page (PUBLIC)` 
- `Confirmation --> PrivateView : Open private page (PRIVATE)`

**Begründung:** Die Links in confirmation.html (Zeilen 51-54) sind abhängig vom AccessType:
```html
<a th:if="${reservation.accessType.name() == 'PUBLIC'}"
   th:href="@{'/reservations/' + ${reservation.id} + '/public'}">Open public page</a>
<a th:if="${reservation.accessType.name() == 'PRIVATE'}"
   th:href="@{'/reservations/' + ${reservation.id} + '/private'}">Open private page</a>
```

### 3.2 KeyAccess Transitionen

**Präzisierung:**
- `KeyAccess --> PrivateView : Valid private key (full access)`

**Begründung:** Betont, dass der Private Key vollen Zugriff gewährt, im Gegensatz zum Access Code.

## 4. Code-Mapping: Diagramm ↔ Implementation

| Zustand | Template | Controller Method |
|---------|----------|-------------------|
| Index | index.html | `index()` |
| CreateForm | reservation-form.html | `newReservation()` |
| EditForm | reservation-form.html (editMode) | `editReservation()` |
| Confirmation | reservation-confirmation.html | `confirmation()` |
| PublicView | reservation-public.html | `publicView()` |
| PrivateView | reservation-private.html | `privateView()` |
| KeyAccess | - (processing) | `accessByKey()` |

| Transition | HTTP Method | Endpoint |
|-----------|-------------|----------|
| CreateForm → Confirmation | POST | `/reservations` |
| EditForm → Confirmation | POST | `/reservations/{id}` |
| KeyAccess → PublicView | GET | `/reservations/{id}/public` |
| KeyAccess → PrivateView | GET | `/reservations/{id}/private` |
| PrivateView → EditForm | GET | `/reservations/{id}/edit` |
| PrivateView → Index (Delete) | POST | `/reservations/{id}/delete` |

## 5. Verbesserungen im Detail

### 5.1 Vollständigkeit

Das aktualisierte Diagramm zeigt **alle** Navigationspfade, die in der Anwendung existieren:
- ✅ Direkte Links aus der Index-Tabelle
- ✅ Key-basierte Navigation
- ✅ Formular-Submissions
- ✅ Zurück-Buttons und Links
- ✅ Zustandsübergänge innerhalb derselben View

### 5.2 Präzision

**Private View Zugriffsmodi:**
- Das Diagramm unterscheidet nun klar zwischen:
  - Private Key Zugriff (Edit/Delete verfügbar)
  - Access Code Zugriff (nur Ansehen möglich)
  
**Access Conditions:**
- Transitionen sind mit präzisen Bedingungen beschriftet
- Labels wie "(requires private key)" machen Autorisierungsanforderungen explizit

### 5.3 Klarheit

**Bessere Notizen:**
- Mehrzeilige Notizen mit klarer Strukturierung
- Nummerierte Listen für verschiedene Modi
- Explizite Erwähnung verfügbarer Funktionen

## 6. Validierung gegen Source Code

Alle Änderungen wurden gegen den tatsächlichen Source Code validiert:

1. **ReservationController.java** (Zeilen 1-207): Alle Endpunkte und Redirects überprüft
2. **index.html** (Zeilen 1-80): Alle Links in der Tabelle identifiziert
3. **reservation-confirmation.html** (Zeilen 1-60): Links zu Public/Private Views
4. **reservation-private.html** (Zeilen 1-75): Access Code Formular und Management Buttons
5. **reservation-form.html**: Back Links und Form Actions

## 7. Fazit

Das überarbeitete Zustandsdiagramm ist nun:
- ✅ **Vollständig**: Alle Navigationspfade sind enthalten
- ✅ **Präzise**: Bedingungen und Autorisierung sind klar beschriftet
- ✅ **Korrekt**: Validiert gegen den tatsächlichen Source Code
- ✅ **Verständlich**: Erweiterte Notizen erklären komplexe Zugriffsmodi

Die Diagramme (PlantUML und Mermaid) sind synchronisiert und können für die Dokumentation verwendet werden.
