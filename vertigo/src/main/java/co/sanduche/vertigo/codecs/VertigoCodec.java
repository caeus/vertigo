package co.sanduche.vertigo.codecs;

import com.google.common.reflect.TypeToken;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Created by caeus on 6/09/15.
 */
public abstract class VertigoCodec<T> implements Codec<T> {


    private final VertigoCodecRegistry vertigoCodecRegistry;

    public VertigoCodec(VertigoCodecRegistry vertigoCodecRegistry) {

        this.vertigoCodecRegistry = vertigoCodecRegistry;

    }

    public <G> void writeProperty(final BsonWriter writer,
                                  final String name, final G value, final TypeToken<G> typeToken,
                                  final EncoderContext encoderContext) {
        if (value != null) {
            writer.writeName(name);
            writeValue(writer, value, typeToken, encoderContext);
        }
    }


    private <G> void writeValue(final BsonWriter writer, final G value, final TypeToken<G> typeToken, final EncoderContext encoderContext) {
        if (value == null) {
            writer.writeNull();
        } else {
            TypeSafeCodec<G> codec = vertigoCodecRegistry.lookupCodec(typeToken);
            codec.encode(writer, value, typeToken, vertigoCodecRegistry, encoderContext);
        }
    }

    public <C> C readValue(BsonReader reader, TypeToken<C> fieldType, DecoderContext decoderContext) {
        TypeSafeCodec<C> cTypeSafeCodec = vertigoCodecRegistry.lookupCodec(fieldType);
        return cTypeSafeCodec.decode(reader, fieldType, vertigoCodecRegistry, decoderContext);
    }

    public void skipValue(BsonReader reader, DecoderContext decoderContext) {
        BsonSkipper.skipValue(reader);
    }

    private static enum BsonSkipper implements Consumer<BsonReader> {
        END_OF_DOCUMENT {
            @Override
            public void accept(BsonReader reader) {
                //??
            }
        },
        DOUBLE {
            @Override
            public void accept(BsonReader reader) {
                reader.readDouble();
            }
        },
        STRING {
            @Override
            public void accept(BsonReader reader) {
                reader.readString();
            }
        },
        DOCUMENT {
            @Override
            public void accept(BsonReader reader) {
                reader.readStartDocument();
                while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    reader.readName();
                    BsonSkipper.valueOf(reader.getCurrentBsonType().name()).accept(reader);
                }
                reader.readEndDocument();
            }
        },
        ARRAY {
            @Override
            public void accept(BsonReader reader) {
                reader.readStartArray();
                while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    BsonSkipper.valueOf(reader.getCurrentBsonType().name()).accept(reader);
                }
                reader.readEndArray();
            }
        },
        BINARY {
            @Override
            public void accept(BsonReader reader) {
                reader.readBinaryData();
            }
        },
        UNDEFINED {
            @Override
            public void accept(BsonReader reader) {
                reader.readUndefined();
            }
        },
        OBJECT_ID {
            @Override
            public void accept(BsonReader reader) {
                reader.readObjectId();
            }
        },
        BOOLEAN {
            @Override
            public void accept(BsonReader reader) {
                reader.readBoolean();
            }
        },
        DATE_TIME {
            @Override
            public void accept(BsonReader reader) {
                reader.readDateTime();
            }
        },
        NULL {
            @Override
            public void accept(BsonReader reader) {
                reader.readNull();
            }
        },
        REGULAR_EXPRESSION {
            @Override
            public void accept(BsonReader reader) {
                reader.readRegularExpression();
            }
        },
        DB_POINTER {
            @Override
            public void accept(BsonReader reader) {
                reader.readDBPointer();
            }
        },
        JAVASCRIPT {
            @Override
            public void accept(BsonReader reader) {
                reader.readJavaScript();
            }
        },
        SYMBOL {
            @Override
            public void accept(BsonReader reader) {
                reader.readSymbol();
            }
        },
        JAVASCRIPT_WITH_SCOPE {
            @Override
            public void accept(BsonReader reader) {
                reader.readJavaScriptWithScope();
            }
        },
        INT32 {
            @Override
            public void accept(BsonReader reader) {
                reader.readInt32();
            }
        },
        TIMESTAMP {
            @Override
            public void accept(BsonReader reader) {
                reader.readTimestamp();
            }
        },
        INT64 {
            @Override
            public void accept(BsonReader reader) {
                reader.readInt64();
            }
        },
        MIN_KEY {
            @Override
            public void accept(BsonReader reader) {
                reader.readMinKey();
            }
        },
        MAX_KEY {
            @Override
            public void accept(BsonReader reader) {
                reader.readMaxKey();
            }
        };

        public static void skipValue(BsonReader reader) {
            BsonSkipper.valueOf(reader.getCurrentBsonType().name()).accept(reader);
        }
    }

}
