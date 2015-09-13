<#if packageName?trim?length gt 0>
package ${packageName};
</#if>

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import co.sanduche.vertigo.codecs.VertigoCodec;
import co.sanduche.vertigo.codecs.VertigoCodecRegistry;
import com.google.common.reflect.TypeToken;


public class ${className}$$Codec extends VertigoCodec<${className}>{

    <#list properties as property>
    private TypeToken<${property.type}> $${property.name}$=new TypeToken<${property.type}>(){};
    </#list>

    public ${className}$$Codec(VertigoCodecRegistry vertigoCodecRegistry){
        super(vertigoCodecRegistry);
    }

    @Override
    public ${className} decode(BsonReader bsonReader, DecoderContext decoderContext) {

        bsonReader.readStartDocument();
        ${className} target=new ${className}();
        <#if properties?size gt 0 >
        while(bsonReader.readBsonType()!= BsonType.END_OF_DOCUMENT){
            String name=bsonReader.readName();
            switch(name){
                <#list properties as property>
                case "${property.name}":target.${property.setter}(readValue(bsonReader,$${property.name}$,decoderContext));
                    break;
                </#list>
                default:skipValue(bsonReader,decoderContext);break;
            }
        }
        </#if>
        bsonReader.readEndDocument();
        return target;

    }

    @Override
    public void encode(BsonWriter bsonWriter, ${className} target, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();
        <#if properties?size gt 0>
        <#list properties as property>
        writeProperty(bsonWriter,"${property.name}",target.${property.getter}(),$${property.name}$,encoderContext.getChildContext());
        </#list>
        </#if>
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<${className}> getEncoderClass() {
        return ${className}.class;
    }
}