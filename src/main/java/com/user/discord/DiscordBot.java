package com.user.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordBot extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DiscordBot.class);
    private JDA jda;

    public DiscordBot(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Discord Token darf nicht null oder leer sein!");
        }
        this.jda = initializeJDA(token);
    }

    private JDA initializeJDA(String token) {
        try {
            JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(this)
                .build();
            jda.awaitReady();
            logger.info("🤖 Discord-Bot erfolgreich initialisiert");
            return jda;
        } catch (InvalidTokenException e) {
            logger.error("❌ Der angegebene Discord-Token ist ungültig! Bitte überprüfe die Umgebungsvariable DISCORD_TOKEN oder deine Konfiguration.");
            throw new RuntimeException("Fehler beim Initialisieren des Discord-Bots: The provided token is invalid!", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Discord-Bot wurde während der Initialisierung unterbrochen", e);
        } catch (Exception e) {
            logger.error("Fehler beim Initialisieren des Discord-Bots: {}", e.getMessage(), e);
            throw new RuntimeException("Fehler beim Initialisieren des Discord-Bots: " + e.getMessage(), e);
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("🤖 Discord-Bot ist online als: {}", event.getJDA().getSelfUser().getName());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Beispiel: Reagiere auf "!ping"
        if (!event.getAuthor().isBot() && event.getMessage().getContentRaw().equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("🏓 Pong!").queue();
            logger.info("Ping-Pong-Befehl verarbeitet.");
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public void shutdown() {
        if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN) {
            jda.shutdown();
            logger.info("🛑 Discord-Bot wird heruntergefahren");
        }
    }
}
