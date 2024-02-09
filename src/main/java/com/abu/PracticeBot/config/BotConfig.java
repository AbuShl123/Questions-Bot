package com.abu.PracticeBot.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class BotConfig {

    static {
        System.setProperty("logfile", System.getProperty("user.dir") + "/logs/app.log");
        String propPath = System.getProperty("user.dir") + "/src/main/resources/application.properties";

        try(var in = new FileInputStream(propPath)) {

            Properties properties = new Properties();

            properties.load(in);

            BOT_NAME = properties.getProperty("bot.name");
            TOKEN = properties.getProperty("bot.token");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String BOT_NAME;
    public static final String TOKEN;

    private BotConfig() {
    }
}