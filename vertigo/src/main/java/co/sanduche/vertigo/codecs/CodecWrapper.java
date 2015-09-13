package co.sanduche.vertigo.codecs;

import com.google.common.reflect.TypeToken;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Created by caeus on 11/09/15.
 */
public class CodecWrapper<T> implements TypeSafeCodec<T> {
    private final Codec<T> codec;

    public CodecWrapper(Codec<T> codec){
        this.codec=codec;
    }
    @Override
    public void encode(BsonWriter bsonWriter, T value, TypeToken<T> typeToken, VertigoCodecRegistry vertigoCodecRegistry, EncoderContext encoderContext) {
        codec.encode(bsonWriter,value,encoderContext);
    }

    @Override
    public T decode(BsonReader bsonReader, TypeToken<T> typeToken, VertigoCodecRegistry vertigoCodecRegistry, DecoderContext decoderContext) {
        return codec.decode(bsonReader,decoderContext);
    }

    @Override
    public Class<T> getEncoderClass() {
        return codec.getEncoderClass();
    }


    public Codec<T> getCodec() {
        return codec;
    }

}
