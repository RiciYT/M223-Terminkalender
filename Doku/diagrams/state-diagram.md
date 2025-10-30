# Zustandsdiagramm - M223 Terminkalender Navigation

```mermaid
stateDiagram-v2
    [*] --> Index
    
    Index --> CreateForm : Create Reservation
    Index --> KeyAccess : Enter Key
    Index --> Confirmation : Confirmation link
    Index --> PublicView : Public view link
    Index --> PrivateView : Private access link
    
    CreateForm --> Confirmation : Submit valid form
    CreateForm --> CreateForm : Validation error
    
    KeyAccess --> PublicView : Public Key
    KeyAccess --> PrivateView : Private Key (full access)
    KeyAccess --> Index : Invalid key
    
    Confirmation --> Index : Back to overview
    Confirmation --> PublicView : Open public page (PUBLIC)
    Confirmation --> PrivateView : Open private page (PRIVATE)
    
    PrivateView --> EditForm : Edit (requires private key)
    PrivateView --> Index : Delete (requires private key)
    PrivateView --> Index : Back
    PrivateView --> PrivateView : Enter access code (view only)
    
    PublicView --> Index : Back
    
    EditForm --> Confirmation : Update successful
    EditForm --> EditForm : Validation error
    EditForm --> Index : Back (cancel)
    
    note right of KeyAccess
        Decision:
        - Public Key → Read-only view
        - Private Key → Full management access
    end note
    
    note right of PrivateView
        Two access modes:
        1. Private Key → Full management
           (Edit/Delete available)
        2. Access Code → View only
           (Edit/Delete not available)
        
        Initial state prompts for
        access code if not authorized
    end note
    
    note right of Index
        Shows all reservations
        with links to:
        - Confirmation pages
        - Public view (PUBLIC type)
        - Private access (PRIVATE type)
    end note
```

## Beschreibung

Dieses Zustandsdiagramm zeigt die Navigation durch die Webapplikation:

- **Index**: Startseite mit Übersicht aller Reservierungen und Links zu Confirmation, Public View und Private Access
- **KeyAccess**: Zugriffskontrolle über Public/Private Key
- **CreateForm**: Formular zum Erstellen einer neuen Reservation
- **EditForm**: Formular zum Bearbeiten einer existierenden Reservation (nur mit Private Key)
- **Confirmation**: Bestätigungsseite nach Erstellen/Aktualisieren mit Access Keys
- **PublicView**: Öffentliche Ansicht einer Reservation (read-only)
- **PrivateView**: Private Ansicht mit zwei Zugriffsmodi:
  - **Mit Private Key**: Vollzugriff mit Edit/Delete-Funktionen
  - **Mit Access Code**: Nur Lesezugriff ohne Verwaltungsfunktionen

## Verbesserungen gegenüber vorheriger Version

1. **Direkte Navigation von Index**: Hinzugefügt von Index zu Confirmation, PublicView und PrivateView (über Links in der Tabelle)
2. **Zwei Zugriffsmodi für PrivateView**: Unterscheidung zwischen Private Key (voller Zugriff) und Access Code (nur ansehen)
3. **Self-Transition in PrivateView**: Zeigt den Zustand, wenn Access Code eingegeben wird
4. **Zurück-Navigation von EditForm**: EditForm kann auch direkt zu Index zurückkehren (Cancel-Aktion)
5. **Erweiterte Notizen**: Klarere Beschreibung der verschiedenen Zugriffsmodi und verfügbaren Links
