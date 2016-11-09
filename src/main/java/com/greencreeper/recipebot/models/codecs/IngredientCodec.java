package com.greencreeper.recipebot.models.codecs;


import com.greencreeper.recipebot.models.Ingredient;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class IngredientCodec implements Codec<Ingredient>{
    @Override
    public Ingredient decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        String name = reader.readString("name");
        String amount = reader.readString("amount");
        reader.readEndDocument();
        return new Ingredient(name, amount);
    }

    @Override
    public void encode(BsonWriter writer, Ingredient value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("name", value.getName());
        writer.writeString("amount", value.getQuantity());
        writer.writeEndDocument();
    }

    @Override
    public Class<Ingredient> getEncoderClass() {
        return Ingredient.class;
    }
}
