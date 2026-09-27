package com.marbledhubb.mhs_discord_integration.api;

import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.ConsoleLogEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.GeneralConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ServerEventsConfigManager;
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
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DiscordConsoleLog extends AbstractAppender {

    // Used to not loop, because if a webhook fails to log an error it will send an error..which will fail to log giving an error...forever
    private static final String OWN_LOGGER_NAME = DiscordConsoleLog.class.getName();

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static ConsoleLogEntry config;

    private DiscordConsoleLog(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
        Thread worker = new Thread(this::runWorker, "discord-console-log");
        worker.setDaemon(true);
        worker.start();
    }

    public static DiscordConsoleLog init() {

        config = ServerEventsConfigManager.getInstance().getConsoleLog();

        Level minLevel = Level.getLevel(config.level);

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern("[%d{HH:mm:ss}] [  %-5level] %msg")
                .build();

        DiscordConsoleLog appender = new DiscordConsoleLog("ServerConsole", null, layout);
        appender.start();

        config.addAppender(appender);
        config.getRootLogger().addAppender(appender, minLevel, null);
        context.updateLoggers();
        return appender;
    }

    @Override
    public void append(LogEvent event) {
        if (OWN_LOGGER_NAME.equals(event.getLoggerName())) {
            return;
        }
        queue.offer(new String(getLayout().toByteArray(event)));
    }

    private void runWorker() {
        StringBuilder batch = new StringBuilder();
        while (true) {
            try {
                String first = queue.poll(5, TimeUnit.SECONDS);
                if (first == null) continue;

                batch.setLength(0);
                batch.append(first);
                String next;
                while ((next = queue.poll()) != null && batch.length() < 1800) {
                    batch.append('\n').append(next);
                }
                String avatarUrl = config.avatarUrl;
                if (avatarUrl.equals("default"))
                    avatarUrl = GeneralConfigManager.getInstance().getDefaultAvatarUrl();
                DiscordWebhookMessage message = DiscordWebhookMessage.builder()
                                .username(config.username)
                                        .avatarUrl(avatarUrl)
                                                .allowedMentions(AllowedMentions.none())
                                                        .content("```" + batch.toString() + "```")
                                                                .build();
                DiscordWebhookAPI.sendMessage(config.channelReference, message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}