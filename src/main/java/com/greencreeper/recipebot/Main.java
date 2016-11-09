package com.greencreeper.recipebot;


import com.google.inject.Guice;
import com.google.inject.Injector;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) {

        if(args.length==1 && args[0].equalsIgnoreCase("test")){
            System.setProperty("istest", "true");
        } else {
            System.setProperty("istest", "false");
        }

        Injector injector = Guice.createInjector(new ConfigModule());
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(injector.getInstance(RecipeBot.class));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
