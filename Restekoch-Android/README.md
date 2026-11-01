# Restekoch – Android-App

So bekommst du die APK, ohne etwas zu installieren:

1. Auf **github.com** kostenlos anmelden.
2. Oben rechts auf **+ → New repository** klicken, einen Namen eingeben (z. B. `restekoch`) und auf **Create repository** klicken.
3. Auf **„uploading an existing file“** klicken.
4. Den entpackten Ordner `Restekoch-Android` im Finder öffnen und **Cmd + Shift + .** drücken, damit der versteckte Ordner `.github` sichtbar wird.
5. **Alle** Dateien und Ordner darin (auch `.github`) ins Browserfenster ziehen und unten auf **Commit changes** klicken.
6. Oben auf **Actions** klicken. Der Bau startet von selbst und dauert etwa 3–5 Minuten. Fertig ist er, wenn ein grüner Haken erscheint.
7. Auf der Startseite des Repositorys rechts unter **Releases** die Datei **Restekoch.apk** herunterladen. Das geht am einfachsten direkt auf dem Android-Handy.
8. Die APK auf dem Handy öffnen. Beim ersten Mal fragt Android, ob du Apps aus dieser Quelle erlauben möchtest: zustimmen und installieren.

Jede Änderung an den Dateien im Repository erzeugt automatisch eine neue APK.

## Hinweise

- Die App läuft offline. Kühlschrank, Einkaufsliste und gemerkte Rezepte werden auf dem Handy gespeichert.
- Weil kein KI-Server angebunden ist, zeigt die App Beispielrezepte und Beispielergebnisse beim Foto-Scan. So war es auch schon in der Browser-Version.
- Die App selbst steckt in `app/src/main/assets/index.html`.
