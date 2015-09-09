package co.sanduche.vertigo.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Map;
import java.util.Objects;

/**
 * Created by caeus on 6/09/15.
 */
public abstract class VertigoCodec<T> implements Codec<T> {

    private final CodecRegistry codecRegistry;
    private final VertigoCodecProvider vertigoCodecProvider;

    public VertigoCodec(CodecRegistry codecRegistry, VertigoCodecProvider vertigoCodecProvider) {
        this.codecRegistry = codecRegistry;
        this.vertigoCodecProvider = vertigoCodecProvider;

    }

    public void writeProperty(final BsonWriter writer,
                               final T target, final String name,final Object value,
                               final EncoderContext encoderContext) {
        writer.writeName(name);
        writeValue(writer,target,value,encoderContext);
    }

    private void writeValue(final BsonWriter writer, final T target, final Object value, final EncoderContext encoderContext) {
        if (value == null) {
            writer.writeNull();
        } else if (Iterable.class.isAssignableFrom(value.getClass())) {
            writeIterable(writer, target, (Iterable<Object>) value, encoderContext.getChildContext());
        } else if (Map.class.isAssignableFrom(value.getClass())) {
            writeMap(writer, target, (Map<String, Object>) value, encoderContext.getChildContext());
        } else {
            Codec codec = codecRegistry.get(value.getClass());
            if (codec == null)
                codec = Objects.requireNonNull(vertigoCodecProvider.get(value.getClass(), codecRegistry),
                        String.format("There are no Codec for class %s", value.getClass()));
            codec.encode(writer, value, encoderContext);
        }
    }

    private void writeMap(final BsonWriter writer, final T target, final Map<String, Object> map, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            writer.writeName(entry.getKey());
            writeValue(writer, target, entry.getValue(), encoderContext);
        }
        writer.writeEndDocument();
    }


    private void writeIterable(BsonWriter writer, T target, Iterable<Object> values, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (final Object value : values) {
            writeValue(writer, target, value, encoderContext);
        }
        writer.writeEndArray();
    }

    public <C> C readValue(BsonReader reader, Class<C> fieldType, DecoderContext decoderContext) {
        return null;
    }

    public void skipValue(BsonReader reader, DecoderContext decoderContext) {


    }


}
