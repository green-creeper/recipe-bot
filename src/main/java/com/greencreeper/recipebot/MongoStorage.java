package com.greencreeper.recipebot;

import com.google.inject.Inject;
import com.greencreeper.recipebot.models.Recipe;
import com.greencreeper.recipebot.models.Session;
import com.greencreeper.recipebot.models.codecs.RecipeCodecProvider;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.telegram.telegrambots.api.objects.User;

import static com.mongodb.client.model.Filters.*;

public class MongoStorage {

    private FeedbackStorage feedbackStorage;
    private MongoDatabase database;
    private SessionStorage sessionStorage;
    private RecipeStorage recipeStorage;

    @Inject
    private MongoStorage(BotConfig config) {

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromProviders(new RecipeCodecProvider()),
                MongoClient.getDefaultCodecRegistry());
        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(codecRegistry).build();
        MongoClient mongoClient = new MongoClient(new ServerAddress(config.getDbHost(), config.getDbPort()), options);
        database = mongoClient.getDatabase(config.getDbName());
    }

    public SessionStorage getSessionStorage() {
        if (sessionStorage == null) {
            sessionStorage = new SessionStorage(database);
        }
        return sessionStorage;
    }

    public RecipeStorage getRecipeStorage() {
        if (recipeStorage == null) {
            recipeStorage = new RecipeStorage(database);
        }
        return recipeStorage;
    }
    public FeedbackStorage getFeedbackStorage() {

        if (feedbackStorage == null) {
            feedbackStorage = new FeedbackStorage(database);
        }
        return feedbackStorage;
    }

    public class SessionStorage {
        private MongoDatabase database;
        private MongoCollection<Session> sessions;

        private SessionStorage(MongoDatabase database) {
            this.database = database;
            this.sessions = database.getCollection("session", Session.class);
        }

        public void storeSession(Session s){
            sessions.replaceOne(eq("_id", s.getId()), s, new UpdateOptions().upsert(true));
        }

        public Session getSession(String id){
            return sessions.find(eq("_id", id)).first();
        }
    }

    public class RecipeStorage {
        private MongoDatabase database;
        private MongoCollection<Recipe> recipes;

        private RecipeStorage(MongoDatabase database) {
            this.database = database;
            this.recipes = database.getCollection("recipes", Recipe.class);
        }

        public Recipe findRecipes(String ingredients) {
            FindIterable<Recipe> recipes = this.recipes.find(new Document("$text", new Document("$search", ingredients)));
            Recipe answer = recipes.projection(new Document("score", new Document("$meta", "textScore")))
                    .sort(new Document("score", new Document("$meta", "textScore"))).first();

            return answer;
        }

        public Recipe getOne(String id) {
            return this.recipes.find(new Document("_id", new ObjectId(id))).first();
        }

    }

    public class FeedbackStorage {
        private MongoDatabase database;
        private MongoCollection feedback;

        private FeedbackStorage(MongoDatabase database) {
            this.database = database;
            this.feedback = database.getCollection("feedback");
        }

        public void saveFeedback(String text, User u){
            feedback.insertOne(new Document("text", text)
                    .append("uid", u.getId())
                    .append("username", u.getUserName())
                    .append("firstname", u.getFirstName())
                    .append("lastname", u.getLastName()));
        }
        public void saveFeedback(String text){
            feedback.insertOne(new Document("text", text)
                    .append("uid", "ERROR"));
        }
    }

}
