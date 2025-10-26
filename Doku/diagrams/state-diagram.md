# Zustandsdiagramm - M223 Terminkalender Navigation

```mermaid
stateDiagram-v2
    [*] --> Index
    Index --> CreateForm : Create Reservation
    Index --> KeyAccess : Enter Key
    
    CreateForm --> Confirmation : Submit valid form
    CreateForm --> CreateForm : Validation error
    
    KeyAccess --> PublicView : Public Key
    KeyAccess --> PrivateView : Private Key
    KeyAccess --> Index : Invalid key
    
    Confirmation --> Index : Back to overview
    Confirmation --> PublicView : Open public page
    Confirmation --> PrivateView : Open private page
    
    PrivateView --> EditForm : Edit (with private key)
    PrivateView --> Index : Delete (with confirmation)
    PrivateView --> Index : Back
    
    PublicView --> Index : Back
    
    EditForm --> Confirmation : Update successful
    EditForm --> EditForm : Validation error
    
    note right of KeyAccess
        Decision:
        - Public Key → Read-only view
        - Private Key → Management view
    end note
    
    note right of PrivateView
        Only accessible with:
        - Private key (full management)
        - OR access code (view only)
    end note
```

## Beschreibung

Dieses Zustandsdiagramm zeigt die Navigation durch die Webapplikation:

- **Index**: Startseite mit Übersicht aller Reservierungen
- **KeyAccess**: Zugriffskontrolle über Public/Private Key
- **CreateForm**: Formular zum Erstellen einer neuen Reservation
- **EditForm**: Formular zum Bearbeiten einer existierenden Reservation (nur mit Private Key)
- **Confirmation**: Bestätigungsseite nach Erstellen/Aktualisieren
- **PublicView**: Öffentliche Ansicht einer Reservation
- **PrivateView**: Private Ansicht mit Verwaltungsfunktionen (Edit/Delete)
