package com.greencreeper.recipebot.models.codecs;


import com.greencreeper.recipebot.models.Session;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class SessionCodec implements Codec<Session> {
    @Override
    public Session decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        String id = reader.readString("_id");
        String request = reader.readString("request");
        Session session = new Session(id, request);

        while(reader.readBsonType() != BsonType.END_OF_DOCUMENT){
            session.addIngredient(Integer.parseInt(reader.readName()), reader.readString());
        }
        reader.readEndDocument();
        return session;
    }

    @Override
    public void encode(BsonWriter writer, Session value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", value.getId());
        writer.writeString("request", value.getRequest());
        if(value.getIngredients()!=null) {
            for (Integer index : value.getIngredients().keySet()) {
                writer.writeString(index.toString(), value.getIngredients().get(index));
            }
        }
        writer.writeEndDocument();
    }

    @Override
    public Class<Session> getEncoderClass() {
        return Session.class;
    }
}
