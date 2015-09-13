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
public interface TypeSafeCodec<T> {
    public void encode(BsonWriter bsonWriter, T value, TypeToken<T> typeToken, VertigoCodecRegistry vertigoCodecRegistry, EncoderContext encoderContext);

    public T decode(BsonReader bsonReader, TypeToken<T> typeToken, VertigoCodecRegistry vertigoCodecRegistry, DecoderContext decoderContext);

    public static <T> TypeSafeCodec<T> wrap(Codec<T> codec) {
        return new CodecWrapper<>(codec);
    }

    public Class<T> getEncoderClass();


}
