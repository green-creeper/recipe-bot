package com.greencreeper.recipebot.models.codecs;


import com.greencreeper.recipebot.models.Direction;
import com.greencreeper.recipebot.models.Ingredient;
import com.greencreeper.recipebot.models.Recipe;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;


public class RecipeCodec implements Codec<Recipe> {

    private CodecRegistry codecRegistry;

    public RecipeCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public Recipe decode(BsonReader reader, DecoderContext decoderContext) {
        Recipe recipe = new Recipe();
        reader.readStartDocument();
        recipe.setId(reader.readObjectId("_id").toString());
        recipe.setName(reader.readString("title"));
        reader.readString("link");
        recipe.setDescription(reader.readString("description"));
        try {
            recipe.setBackQuote(reader.readString("backquote"));
        } catch (BsonSerializationException ignored){}
        finally {
            Codec<Ingredient> ingredientCodec = codecRegistry.get(Ingredient.class);
            List<Ingredient> ingredients = new ArrayList<>();
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                ingredients.add(ingredientCodec.decode(reader, decoderContext));
            }
            reader.readEndArray();
            recipe.setIngredients(ingredients);
        }
        Codec<Direction> directionCodec = codecRegistry.get(Direction.class);
        List<Direction> directions = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            directions.add(directionCodec.decode(reader, decoderContext));
        }
        reader.readEndArray();
        recipe.setDirections(directions);

        try {
            reader.readDouble("score");
        } catch (BSONException ignored){}
        finally {
            reader.readEndDocument();
        }
        return recipe;
    }

    @Override
    public void encode(BsonWriter writer, Recipe value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("title", value.getName());
        writer.writeString("link", value.getLink());
        if(value.getDescription()!=null)
            writer.writeString("description", value.getDescription());
        if(value.getBackQuote()!=null)
            writer.writeString("backquote", value.getBackQuote());
        writer.writeStartArray("ingredients");
        for(Ingredient i: value.getIngredients()){
            Codec<Ingredient> ingredientCodec = codecRegistry.get(Ingredient.class);
            encoderContext.encodeWithChildContext(ingredientCodec, writer, i);
        }
        writer.writeEndArray();
        writer.writeStartArray("directions");
        for(Direction i: value.getDirections()){
            Codec<Direction> directionCodec = codecRegistry.get(Direction.class);
            encoderContext.encodeWithChildContext(directionCodec, writer, i);
        }
        writer.writeEndArray();
        writer.writeEndDocument();
    }

    @Override
    public Class<Recipe> getEncoderClass() {
        return Recipe.class;
    }
}
