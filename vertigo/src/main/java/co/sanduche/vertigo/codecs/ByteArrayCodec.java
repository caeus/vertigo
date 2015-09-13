package co.sanduche.vertigo.codecs;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Created by caeus on 11/09/15.
 */
public class ByteArrayCodec implements Codec<byte[]> {

    @Override
    public byte[] decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readBinaryData().getData();
    }

    @Override
    public void encode(BsonWriter writer, byte[] value, EncoderContext encoderContext) {
        writer.writeBinaryData(new BsonBinary(value));
    }

    @Override
    public Class<byte[]> getEncoderClass() {
        return byte[].class;
    }
}
