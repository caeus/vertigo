package co.sanduche.vertigo.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by caeus on 6/09/15.
 */
public abstract class VertigoCodec<T> implements Codec<T>{

    private final CodecRegistry codecRegistry;

    public VertigoCodec(CodecRegistry codecRegistry){
        this.codecRegistry=codecRegistry;

    }
    public void writeValue(BsonWriter bsonWriter,T target, Object value, EncoderContext encoderContext){

    }
    public <C> C readValue(BsonReader reader,Class<C> fieldType,DecoderContext decoderContext){
        return null;
    }
    public void skipValue(BsonReader reader,DecoderContext decoderContext){


    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {

        return null;
    }

    @Override
    public Class<T> getEncoderClass() {
        return null;
    }
}
