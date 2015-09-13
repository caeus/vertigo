package co.sanduche.vertigo.codecs;

import com.google.common.reflect.TypeToken;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caeus on 11/09/15.
 */
public class ListCodec<I> implements TypeSafeCodec<List<I>> {


    @Override
    public void encode(BsonWriter bsonWriter, List<I> value, TypeToken<List<I>> typeToken, VertigoCodecRegistry vertigoCodecRegistry, EncoderContext encoderContext) {
        TypeToken<I> innerToken = extractInnerType(typeToken);
        TypeSafeCodec<I> typeSafeCodec = vertigoCodecRegistry.lookupCodec(innerToken);
        bsonWriter.writeStartArray();
        value.forEach(i -> {
            typeSafeCodec.encode(bsonWriter, i, innerToken, vertigoCodecRegistry, encoderContext.getChildContext());
        });
        bsonWriter.writeEndArray();
    }

    @Override
    public List<I> decode(BsonReader bsonReader, TypeToken<List<I>> typeToken, VertigoCodecRegistry vertigoCodecRegistry, DecoderContext decoderContext) {
        TypeToken<I> innerToken = extractInnerType(typeToken);
        TypeSafeCodec<I> typeSafeCodec = vertigoCodecRegistry.lookupCodec(innerToken);
        ArrayList<I> list = new ArrayList<I>();
        bsonReader.readStartArray();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(typeSafeCodec.decode(bsonReader, innerToken, vertigoCodecRegistry, decoderContext));
        }
        bsonReader.readEndArray();
        return list;
    }

    @Override
    public Class<List<I>> getEncoderClass() {
        return (Class) List.class;
    }

    private TypeToken<I> extractInnerType(TypeToken<List<I>> typeToken) {
        Type innerType = ((ParameterizedType) typeToken.getType()).getActualTypeArguments()[0];
        return (TypeToken<I>) TypeToken.of(innerType);
    }


}
