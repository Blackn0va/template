# 🎮 Template Java

[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Blackn0va/template/graphs/commit-activity)
![Maintainer](https://img.shields.io/badge/maintainer-Blackn0va-blue)
[![CC-0 license](https://img.shields.io/badge/License-CC--0-blue.svg)](https://creativecommons.org/licenses/by-nd/4.0)


## 🚀 Schnellstart

### Voraussetzungen
- Java JDK 21 oder höher
- Maven



### Kompilieren und Installieren

1. Repository klonen:
```bash
git clone https://github.com/Blackn0va/template.git
cd template
```

2. Mit Maven kompilieren:
```bash
mvn clean install
```

3. JAR-Datei in den gewünschten Zielordner kopieren (z.B. /root/template/live)

## 🖥️ Linux Service Installation

1. Service-Datei erstellen:
```bash
sudo nano /etc/systemd/system/template.service
```

2. Service konfigurieren:
```ini
[Unit]
Description=Template Anwendung in Java
After=network.target

[Service]
Type=simple
User=dein_user
WorkingDirectory=/pfad/zu/deinem/template
ExecStart=/usr/bin/java -jar template.jar


Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

3. Service aktivieren:
```bash
sudo systemctl enable template.service
sudo systemctl start template.service
```

## 📋 Wichtige Befehle und Pfade

```
╭─── 🎮 TEMPLATE JAVA ───────────────────────────────────────╮
│                                                             │
│ 📂 ARBEITSPFADE:                                            │
│    • HAUPTPFAD: /root/template/live                        │
│                                                             │
│ 📋 SERVICE-BEFEHLE:                                         │
│    • START:   sudo systemctl start template.service         │
│    • STOP:    sudo systemctl stop template.service         │
│    • RESTART: sudo systemctl restart template.service      │
│                                                             │
│ 📜 LOGS ANZEIGEN:                                           │
│    • LIVE:    sudo journalctl -u template.service -f       │
│    • HISTORY: sudo journalctl -u template.service -n 50    │
╰─────────────────────────────────────────────────────────────╯
```

### Monitoring
```bash
# Service-Status
sudo systemctl status template.service

# Live-Logs
sudo journalctl -u template.service -f
```

## 📝 Lizenz

Dieses Projekt steht unter der CC-0 Lizenz.