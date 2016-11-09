package com.greencreeper.recipebot;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class BotConfig {

    private String botUsername;
    private String token;
    private String dbHost;
    private int dbPort;
    private String dbName;

    public BotConfig() {
        boolean prod = System.getProperty("istest").equals("false");
        Config globalConf = ConfigFactory.load();

        Config conf = globalConf.getConfig(prod?"prod":"test");

        botUsername = conf.getString("username");
        token = conf.getString("token");
        dbHost = conf.getString("db.host");
        dbPort = conf.getInt("db.port");
        dbName = conf.getString("db.database");
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getToken() {
        return token;
    }

    public String getDbHost() {
        return dbHost;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

}
