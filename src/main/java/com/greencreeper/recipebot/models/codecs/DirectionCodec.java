package com.greencreeper.recipebot.models.codecs;


import com.greencreeper.recipebot.models.Direction;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class DirectionCodec implements Codec<Direction> {

    private CodecRegistry codecRegistry;

    public DirectionCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public Direction decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        String img = reader.readString("img_url");
        String direction = reader.readString("direction");
        reader.readEndDocument();
        return new Direction(img, direction);
    }

    @Override
    public void encode(BsonWriter writer, Direction value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("img_url", value.getImageUrl());
        writer.writeString("direction", value.getText());
        writer.writeEndDocument();
    }

    @Override
    public Class<Direction> getEncoderClass() {
        return Direction.class;
    }
}
