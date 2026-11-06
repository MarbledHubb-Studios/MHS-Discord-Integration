package com.marbledhubb.mhs_discord_integration.api;

import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.ConsoleLogEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.GeneralConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ServerEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.core.DiscordAttachment;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DiscordConsoleLog extends AbstractAppender {

    // Used to not loop, because if a webhook fails to log an error it will send an error..which will fail to log giving an error...forever
    private static final String OWN_LOGGER_NAME = DiscordConsoleLog.class.getName();
    private static final int MAX_CONTENT = 1900;

    private static final ThreadLocal<Boolean> SENDING = ThreadLocal.withInitial(() -> false);

    private static boolean pinged = false;
    private static final Path LOG_FILE = Path.of("logs", "latest.log");

    private record Entry(String text, boolean fatal) {}

    private final BlockingQueue<Entry> queue = new LinkedBlockingQueue<>();
    private final Object sendLock = new Object();
    private static ConsoleLogEntry config;


    private DiscordConsoleLog(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
        Thread worker = new Thread(this::runWorker, "discord-console-log");
        worker.setDaemon(true);
        worker.start();
    }

    public static DiscordConsoleLog init() {

        config = ServerEventsConfigManager.getInstance().getConsoleLog();

        Level minLevel = Level.toLevel(config.level, Level.ERROR);

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withCharset(StandardCharsets.UTF_8)
                .withPattern("[%d{HH:mm:ss}] [  %-5level] %msg")
                .build();

        DiscordConsoleLog appender = new DiscordConsoleLog("ServerConsole", null, layout);
        appender.start();

        Level refLevel = minLevel.isMoreSpecificThan(Level.ERROR) ? Level.ERROR : minLevel;

        config.addAppender(appender);
        config.getRootLogger().addAppender(appender, refLevel, null);
        context.updateLoggers();
        return appender;
    }

    @Override
    public void append(LogEvent event) {

        if (SENDING.get() || OWN_LOGGER_NAME.equals(event.getLoggerName())) {
            return;
        }

        Level minLevel = Level.toLevel(config.level, Level.ERROR);

        boolean fatal = Level.FATAL.equals(event.getLevel())
                || (event.getMarker() != null && event.getMarker().isInstanceOf("FATAL"));

        if (!fatal && !event.getLevel().isMoreSpecificThan(minLevel)) {
            return;
        }

        String text = new String(getLayout().toByteArray(event), StandardCharsets.UTF_8);

        // Split too long messages
        for (String line : text.lines().toList()) {
            if (line.isEmpty()) continue;
            // Only a single line longer than Discord's limit has to be split mid line
            for (int i = 0; i < line.length(); i += MAX_CONTENT) {
                queue.offer(new Entry(line.substring(i, Math.min(line.length(), i + MAX_CONTENT)), fatal));
            }
        }


        // If the issue is fatal, send right now before thread goes rip
        if (fatal) {
            drain(queue.poll());
        }
    }

    private void runWorker() {
        while (true) {
            try {
                drain(queue.poll(5, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void drain(Entry entry) {
        synchronized (sendLock) {
            boolean sawFatal = false;
            while (entry != null) {
                StringBuilder batch = new StringBuilder(entry.text());
                sawFatal |= entry.fatal();

                Entry next;
                while ((next = queue.peek()) != null
                        && batch.length() + 1 + next.text().length() <= MAX_CONTENT) {
                    queue.poll();
                    batch.append('\n').append(next.text());
                    sawFatal |= next.fatal();
                }

                Entry following = queue.poll();
                send(batch.toString(), sawFatal && following == null);
                entry = following;
            }
        }
    }

    private void send(String content, boolean containsFatal) {
        SENDING.set(true);
        try {
            String avatarUrl = config.avatarUrl;
            if (avatarUrl.equals("default"))
                avatarUrl = GeneralConfigManager.getInstance().getDefaultAvatarUrl();

            DiscordWebhookMessage message = DiscordWebhookMessage.builder()
                    .username(config.username)
                    .avatarUrl(avatarUrl)
                    .allowedMentions(AllowedMentions.none())
                    .content("```" + content + "```")
                    .build();
            DiscordWebhookAPI.sendMessage(config.channelReference, message);

            if (containsFatal) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!pinged) {
                    pinged = true;
                    DiscordWebhookMessage.Builder pingMessageBuilder = DiscordWebhookMessage.builder()
                            .username(config.username)
                            .avatarUrl(avatarUrl)
                            .allowedMentions(AllowedMentions.custom(false, true, true));

                    if (config.sendNotificationOnFatal) {
                        pingMessageBuilder.content(config.fatalNotification);
                    } else {
                        pingMessageBuilder.content("");
                    }

                    if (config.uploadLog) {
                        DiscordWebhookAPI.sendFiles(config.channelReference, pingMessageBuilder.build(),
                                List.of(DiscordAttachment.of(LOG_FILE)));
                    } else {
                        if (config.sendNotificationOnFatal)
                            DiscordWebhookAPI.sendMessage(config.channelReference, pingMessageBuilder.build());
                    }
                }
            }
        } catch (Exception ignored) {
            // Cookie time
        } finally {
            SENDING.set(false);
        }
    }

}