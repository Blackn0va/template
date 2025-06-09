package com.user;

import com.user.util.ThreadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static ThreadManager threadManager;

    public static void main(String[] args) {
        logger.info("🟢 === Anwendung startet ===");

        try {
            threadManager = ThreadManager.getInstance();
            logger.info("🧵 Thread-Management initialisiert");

            // Discord-Bot initialisieren
            String discordToken = "DISCORD-TOKEN";
            if (discordToken == null || discordToken.isEmpty()) {
                logger.error("❌ Kein Discord-Token gefunden! Bitte DISCORD_TOKEN Umgebungsvariable setzen");
                System.exit(1);
            }

            threadManager.initializeDiscordBot(discordToken);
            logger.info("🤖 Discord-Bot wurde gestartet");

            // Shutdown Hook registrieren
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("🛑 Shutdown Hook ausgelöst...");
                shutdown();
            }));

            // Anwendung läuft im Hintergrund
            Thread.currentThread().join();

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("The provided token is invalid")) {
                logger.error("❌ Der Discord-Token ist ungültig! Bitte prüfe, ob die Umgebungsvariable DISCORD_TOKEN korrekt gesetzt ist und ein gültiger Bot-Token verwendet wird.");
            }
            shutdown();
            System.exit(1);
        }
    }

    private static void shutdown() {
        if (threadManager != null) {
            logger.info("🧵 Fahre Thread-Manager herunter...");
            threadManager.shutdown();
            logger.info("✅ Thread-Manager erfolgreich beendet");
        }
        logger.info("🛑 Anwendung beendet");
    }
}
