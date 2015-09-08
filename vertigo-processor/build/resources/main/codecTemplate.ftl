package ${packageName};

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import co.sanduche.mongo.SanducheCodec;


public class ${className}$$Codec extends SanducheCodec<${className}>{

    public ${className}$$Codec(CodecRegistry codecRegistry){
        super(codecRegistry);
    }

    @Override
    public ${className} decode(BsonReader bsonReader, DecoderContext decoderContext) {
        try{
            bsonReader.readStartDocument();
            ${className} target=${className}.class.newInstance();
            <#if fields?size gt 0 >
            while(bsonReader.readBsonType()!= BsonType.END_OF_DOCUMENT){
                String name=bsonReader.readName();
                switch(name){
                    <#list fields as field>
                    case "${field.name}":target.${field.setter}(readValue(bsonReader,${field.clazz}.class,decoderContext));
                        break;
                    </#list>
                    default:skipValue(bsonReader,decoderContext);break;
                }
            }
            </#if>
            bsonReader.readEndDocument();
            return target;
        }catch(InstantiationException e){
            throw new IllegalStateException(e);
        }catch(IllegalAccessException e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void encode(BsonWriter bsonWriter, ${className} target, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();
        <#if fields?size gt 0>
        <#list fields as field>
        writeValue(bsonWriter,target,target.${field.getter}(),encoderContext);
        </#list>
        </#if>
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<${className}> getEncoderClass() {
        return ${className}.class;
    }
}