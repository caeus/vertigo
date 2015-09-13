package co.sanduche.vertigo.codecs;

import com.google.common.reflect.TypeToken;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caeus on 11/09/15.
 */
public class MapCodec<I> implements TypeSafeCodec<Map<String, I>> {
    @Override
    public void encode(BsonWriter bsonWriter, Map<String, I> value, TypeToken<Map<String, I>> typeToken, VertigoCodecRegistry vertigoCodecRegistry, EncoderContext encoderContext) {
        TypeToken<I> of = extractInnerType(typeToken);
        TypeSafeCodec<I> typeSafeCodec = vertigoCodecRegistry.lookupCodec(of);
        bsonWriter.writeStartDocument();
        value.forEach((s, i) -> {
            bsonWriter.writeName(s);
            typeSafeCodec.encode(bsonWriter, i, of, vertigoCodecRegistry, encoderContext.getChildContext());
        });
        bsonWriter.writeEndDocument();
    }

    @Override
    public Map<String, I> decode(BsonReader bsonReader, TypeToken<Map<String, I>> typeToken, VertigoCodecRegistry vertigoCodecRegistry, DecoderContext decoderContext) {
        TypeToken<I> of = extractInnerType(typeToken);
        TypeSafeCodec<I> typeSafeCodec = vertigoCodecRegistry.lookupCodec(of);
        bsonReader.readStartDocument();
        HashMap<String, I> map = new HashMap<String, I>();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            map.put(bsonReader.readName(), typeSafeCodec.decode(bsonReader, of, vertigoCodecRegistry, decoderContext));
        }
        bsonReader.readEndDocument();
        return map;
    }

    @Override
    public Class<Map<String, I>> getEncoderClass() {
        return (Class) Map.class;
    }


    private TypeToken<I> extractInnerType(TypeToken<Map<String, I>> typeToken) {
        Type innerType = ((ParameterizedType) typeToken.getType()).getActualTypeArguments()[1];
        return (TypeToken<I>) TypeToken.of(innerType);
    }
}
