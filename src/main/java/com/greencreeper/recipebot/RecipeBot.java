package com.greencreeper.recipebot;


import com.brsanthu.googleanalytics.*;
import com.google.inject.Inject;
import com.greencreeper.recipebot.models.Ingredient;
import com.greencreeper.recipebot.models.Recipe;
import com.greencreeper.recipebot.models.Session;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;

public class RecipeBot extends TelegramLongPollingBot{

    public static final String START_COMMAND = "/start";
    public static final String HELP_COMMAND = "/help";
    public static final String RECIPE_COMMAND = "/recipe";
    public static final String FEEDBACK_COMMAND = "/feedback";

    public static final String TAG_BOLD = "<b>";
    public static final String TAG_ITALIC = "<i>";
    public static final String TAG_BOLD_CLOSE = "</b>";
    public static final String TAG_ITALIC_CLOSE = "</i>";
    public static final String TAG_NEWLINE = "\n";

    private BotConfig config;
    private MongoStorage mongo;
    private GoogleAnalytics analytics;

    @Inject
    public RecipeBot(BotConfig config, MongoStorage mongo){
        this.config = config;
        this.mongo = mongo;
        analytics = new GoogleAnalytics(config.getTrackingCode());
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasCallbackQuery()){
            CallbackQuery query = update.getCallbackQuery();
            Session session = mongo.getSessionStorage()
                    .getSession(query.getMessage().getChatId().toString());

            String msg = session.getRequest();
            String exclude = session.getIngredient(Integer.parseInt(query.getData()));
            msg += " -"+ exclude.replaceAll(" ", " -");
            session.setRequest(msg);
            mongo.getSessionStorage().storeSession(session);
            processMessage(msg, query.getMessage().getChatId().toString());
            analytics.postAsync(new EventHit("button", "remove-ingridient").clientId(query.getMessage().getChatId().toString()));
        }

        if(update.hasMessage()){
            Message message = update.getMessage();

            if (message.isCommand())
            {
                String command = message.getEntities().get(0).getText();
                if(command.equalsIgnoreCase(START_COMMAND)){
                    StringBuilder sb = new StringBuilder();
                    sb.append("Привет ").append(message.getFrom().getFirstName())
                            .append("\n @").append(config.getBotUsername())
                            .append(" позволяет искать рецепты по ингредиентам которые есть в Вашем холодильнике. \n ")
                            .append("Чтобы узнать подробнее введите /help");

                    sendResponse(sb.toString() , message.getChatId().toString());
                    analytics.postAsync(new PageViewHit(START_COMMAND,"Start Bot").clientId(message.getChatId().toString()));

                } else if(command.equalsIgnoreCase(HELP_COMMAND)){
                    sendResponse("Просто введите список ингредиентов \n\n" +
                            "Например: \n" +
                            "капуста свекла картофель \n \n"+
                            "Если в полученном ответе есть ингредиенты которых у вас нет, вы можете исключить их, " +
                            "нажав на кнопку с названием отсутсвующего ингредиента. После этого вам будет предложен новый рецепт. \n\n" +
                            "Так же возможно исключить необходимуй ингредиент во время запроса, для этого нужно добавить '-{ингредиент}' к запросу \n" +
                            "\n Например: \n\n" +
                            "-паприка \n\n" +
                            "Отзывы и пожелания вы можете оставить используя команду /feedback", message.getChatId().toString());
                    analytics.postAsync(new PageViewHit(HELP_COMMAND, "Help").clientId(message.getChatId().toString()));
                } else if(command.startsWith(RECIPE_COMMAND)){
                    sendRecipe(command.replace(RECIPE_COMMAND+"_", ""), message.getChatId().toString());
                    analytics.postAsync(new PageViewHit(RECIPE_COMMAND, "View recipe").clientId(message.getChatId().toString()));
                } else if(command.equalsIgnoreCase(FEEDBACK_COMMAND)){
                    mongo.getFeedbackStorage().saveFeedback(message.getText(), message.getFrom());
                    sendResponse("Отзыв отправлен, спасибо", message.getChatId().toString());
                    analytics.postAsync(new PageViewHit(FEEDBACK_COMMAND, "Send feedback").clientId(message.getChatId().toString()));
                }
            }

            else if(message.hasText()){
                analytics.postAsync(new PageViewHit("/search","Search recipe").clientId(message.getChatId().toString()));
                processMessage(message.getText(), message.getChatId().toString());
            }
        }
    }

    private void processMessage(String message, String chatId){
        Recipe recipe = mongo.getRecipeStorage().findRecipes(message);
        if(recipe == null){
            sendResponse("Рецептов по запросу не найдено \n\n "+ message +"\n\n /help", chatId);
            analytics.postAsync(new EventHit("search", "not-round").clientId(chatId));
            return;
        }
        Session s = new Session(chatId, message);
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            s.addIngredient(i, recipe.getIngredients().get(i).getName());
        }
        mongo.getSessionStorage().storeSession(s);

        InlineKeyboardMarkup keyboard = createIngredientsKeyboard(recipe.getIngredients());
        sendKeyboardResponse(TAG_BOLD+recipe.getName()+TAG_BOLD_CLOSE+"\n \n Посмотреть полностью /recipe_" + recipe.getId(), chatId, keyboard);
    }

    private void sendRecipe(String id, String chatId){
        Recipe r = mongo.getRecipeStorage().getOne(id);
        if(r == null){
            sendResponse("Рецепт не найден", chatId);
            analytics.postAsync(new EventHit("recipe", "404").clientId(chatId));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TAG_BOLD).append(r.getName()).append(TAG_BOLD_CLOSE)
                .append(TAG_NEWLINE).append(TAG_NEWLINE).append(r.getDescription())
                .append(TAG_NEWLINE).append(TAG_NEWLINE);

        for(Ingredient i: r.getIngredients()){
            sb.append(TAG_ITALIC).append(" -").append(i.getName())
                    .append(": ").append(i.getQuantity()).append(TAG_ITALIC_CLOSE).append(TAG_NEWLINE);
        }
        sb.append(TAG_NEWLINE);

        for (int i = 0; i < r.getDirections().size(); i++) {
            sb.append(TAG_BOLD).append(i+1).append(". ").append(TAG_BOLD_CLOSE)
                    .append(r.getDirections().get(i).getText().replaceAll("^\\d?\\.", "")).append(TAG_NEWLINE);
        }
        sb.append(TAG_NEWLINE);
        sb.append(r.getBackQuote());

        sendResponse(sb.toString() , chatId);

    }

    private void sendResponse(String r, String chatID){
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(chatID);
        sendMessageRequest.setText(r);
        sendMessageRequest.enableHtml(true);

        try{
            sendMessage(sendMessageRequest);
        } catch (TelegramApiException e) {
            analytics.postAsync(new ExceptionHit("Error sending response").clientId(chatID));
            mongo.getFeedbackStorage().saveFeedback(e.toString());
        }
    }
    private void sendKeyboardResponse(String r, String chatID, InlineKeyboardMarkup keyboard){
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(chatID);
        sendMessageRequest.setText(r);
        sendMessageRequest.setReplyMarkup(keyboard);
        sendMessageRequest.enableHtml(true);


        try{
            sendMessage(sendMessageRequest);
        } catch (TelegramApiException e) {
            analytics.postAsync(new ExceptionHit("Error sending Keyboard response").clientId(chatID));
            mongo.getFeedbackStorage().saveFeedback(e.toString());
        }
    }


    private InlineKeyboardMarkup createIngredientsKeyboard(List<Ingredient> ingredients){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.get(i);
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText("\u274C "+ing.getName());
            btn.setCallbackData(String.valueOf(i));
            buttons.add(Arrays.asList(btn));
        }
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
