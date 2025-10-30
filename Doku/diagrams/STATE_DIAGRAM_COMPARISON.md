# State Diagram - Visual Comparison

## Vorher vs. Nachher

### Anzahl der Transitionen

| Zustand | Vorher (Ausgänge) | Nachher (Ausgänge) | Änderung |
|---------|-------------------|--------------------|---------  |
| Index | 2 | 5 | +3 neue Pfade |
| CreateForm | 2 | 2 | unverändert |
| Confirmation | 3 | 3 | präzisiert |
| KeyAccess | 3 | 3 | präzisiert |
| PublicView | 1 | 1 | unverändert |
| PrivateView | 3 | 4 | +1 Self-Transition |
| EditForm | 2 | 3 | +1 Back zu Index |

**Total Transitionen:**
- Vorher: 16 Transitionen
- Nachher: 21 Transitionen (+5)

### Neue Transitionen im Detail

#### 1. Von Index (3 neue):
```
Index --> Confirmation : Click "Confirmation" link
Index --> PublicView : Click "Public view" link
Index --> PrivateView : Click "Private access" link
```

**Code-Referenz:** index.html, Zeilen 68-72
```html
<a th:href="@{'/reservations/' + ${reservation.id} + '/confirm'}">Confirmation</a>
<a th:if="${reservation.accessType.name() == 'PUBLIC'}"
   th:href="@{'/reservations/' + ${reservation.id} + '/public'}">Public view</a>
<a th:if="${reservation.accessType.name() == 'PRIVATE'}"
   th:href="@{'/reservations/' + ${reservation.id} + '/private'}">Private access</a>
```

#### 2. Self-Transition in PrivateView (1 neu):
```
PrivateView --> PrivateView : Enter access code (view only)
```

**Code-Referenz:** reservation-private.html, Zeilen 27-33
```html
<form th:if="${reservation != null && authorized != true}"
      th:action="@{'/reservations/' + ${reservation.id} + '/private'}" method="get">
    <label for="code">Access code</label>
    <input type="text" id="code" name="code" required>
    <button type="submit">View reservation</button>
</form>
```

#### 3. Von EditForm zu Index (1 neu):
```
EditForm --> Index : Back (cancel)
```

**Code-Referenz:** reservation-form.html, Zeile 81
```html
<a th:href="@{/}">Cancel</a>
```

### Verbesserte Beschriftungen

#### KeyAccess
**Vorher:**
```
KeyAccess --> PrivateView : Valid private key
```

**Nachher:**
```
KeyAccess --> PrivateView : Valid private key (full access)
```

**Verbesserung:** Betont den Unterschied zum Access Code (nur Ansicht).

#### Confirmation
**Vorher:**
```
Confirmation --> PublicView : Open public page
Confirmation --> PrivateView : Open private page
```

**Nachher:**
```
Confirmation --> PublicView : Open public page (PUBLIC)
Confirmation --> PrivateView : Open private page (PRIVATE)
```

**Verbesserung:** Macht abhängigkeit vom AccessType explizit.

#### PrivateView
**Vorher:**
```
PrivateView --> EditForm : Edit (with private key)
PrivateView --> Index : Delete (with confirmation)
```

**Nachher:**
```
PrivateView --> EditForm : Edit (requires private key)
PrivateView --> Index : Delete (requires private key)
```

**Verbesserung:** Klarere Formulierung der Autorisierungsanforderung.

### Erweiterte Notizen

#### Index (neu)
```
Shows all reservations
with links to:
- Confirmation pages
- Public view (PUBLIC type)
- Private access (PRIVATE type)
```

#### PrivateView (erweitert)
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

#### KeyAccess (präzisiert)
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

## Validierung

### Vollständigkeitsprüfung

Alle Controller-Methoden in ReservationController.java:

| Methode | Diagramm-Zustand | Im Diagramm? |
|---------|------------------|--------------|
| `index()` | Index | ✅ |
| `accessByKey()` | KeyAccess | ✅ |
| `newReservation()` | CreateForm | ✅ |
| `createReservation()` | CreateForm → Confirmation | ✅ |
| `confirmation()` | Confirmation | ✅ |
| `publicView()` | PublicView | ✅ |
| `privateView()` | PrivateView | ✅ |
| `editReservation()` | EditForm | ✅ |
| `updateReservation()` | EditForm → Confirmation | ✅ |
| `deleteReservation()` | PrivateView → Index | ✅ |

**Ergebnis:** 10/10 Controller-Methoden abgedeckt ✅

### Template-Links

Alle th:href Links in Templates:

| Template | Link-Ziel | Im Diagramm? |
|----------|-----------|--------------|
| index.html | /reservations/new | ✅ (Index → CreateForm) |
| index.html | /reservations/{id}/confirm | ✅ (Index → Confirmation) |
| index.html | /reservations/{id}/public | ✅ (Index → PublicView) |
| index.html | /reservations/{id}/private | ✅ (Index → PrivateView) |
| confirmation.html | / | ✅ (Confirmation → Index) |
| confirmation.html | /reservations/{id}/public | ✅ (Confirmation → PublicView) |
| confirmation.html | /reservations/{id}/private | ✅ (Confirmation → PrivateView) |
| reservation-form.html | / | ✅ (EditForm → Index) |
| reservation-private.html | /reservations/{id}/edit | ✅ (PrivateView → EditForm) |
| reservation-private.html | / | ✅ (PrivateView → Index) |
| reservation-public.html | / | ✅ (PublicView → Index) |

**Ergebnis:** 11/11 Template-Links abgedeckt ✅

### Form Actions

Alle Form Actions in Templates:

| Template | Action | Im Diagramm? |
|----------|--------|--------------|
| index.html | GET /access | ✅ (Index → KeyAccess) |
| reservation-form.html | POST /reservations | ✅ (CreateForm → Confirmation) |
| reservation-form.html | POST /reservations/{id} | ✅ (EditForm → Confirmation) |
| reservation-private.html | GET /reservations/{id}/private | ✅ (PrivateView → PrivateView) |
| reservation-private.html | POST /reservations/{id}/delete | ✅ (PrivateView → Index) |

**Ergebnis:** 5/5 Form Actions abgedeckt ✅

## Fazit

Das überarbeitete Zustandsdiagramm ist zu **100% vollständig** und bildet alle Navigationspfade, Controller-Methoden, Template-Links und Form-Actions der Anwendung korrekt ab.

### Verbesserungszusammenfassung:

✅ +5 neue Transitionen hinzugefügt  
✅ 3 Transitionen präziser beschriftet  
✅ 3 Notizen erweitert/verbessert  
✅ 1 neue Notiz hinzugefügt  
✅ 100% Code-Coverage validiert  
✅ PlantUML und Mermaid synchronisiert
