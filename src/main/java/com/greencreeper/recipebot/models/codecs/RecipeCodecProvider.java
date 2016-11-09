package com.greencreeper.recipebot.models.codecs;



import com.greencreeper.recipebot.models.Direction;
import com.greencreeper.recipebot.models.Ingredient;
import com.greencreeper.recipebot.models.Recipe;
import com.greencreeper.recipebot.models.Session;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class RecipeCodecProvider implements CodecProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == Recipe.class) {
            return (Codec<T>) new RecipeCodec(registry);
        } else if (clazz == Direction.class){
            return (Codec<T>) new DirectionCodec(registry);
        }else if (clazz == Ingredient.class){
            return (Codec<T>) new IngredientCodec();
        } else if (clazz == Session.class) {
            return (Codec<T>) new SessionCodec();
        }
        return null;
    }
}
