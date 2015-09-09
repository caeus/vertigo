package co.sanduche.vertigo.codecs;

import co.sanduche.vertigo.annotations.MongoEntity;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by caeus on 7/09/15.
 */
public class VertigoCodecProvider implements CodecProvider {

    private final Map<String, Codec> codecs = new HashMap<>();

    public VertigoCodecProvider() {

    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (!clazz.isAnnotationPresent(MongoEntity.class)) {
            return null;
        }
        return safeGet(clazz,registry);
    }

    private <T> Codec<T> safeGet(Class<T> clazz,CodecRegistry registry) {


        return codecs.computeIfAbsent(clazz.getName(), className -> {
            try {
                Constructor<Codec> constructor = (Constructor<Codec>) clazz.getClassLoader().loadClass(className + "$$Codec").getDeclaredConstructor(CodecRegistry.class, VertigoCodecProvider.class);
                return Objects.requireNonNull(constructor.newInstance(registry, this),
                        String.format("The codec for class %s was not created, the vertigo-processor was probably not executed properly", className));
            } catch (Exception e) {
                throw new IllegalStateException(String.format("The codec for class %s doesn't exist, the vertigo-processor was probably not executed properly", className), e);
            }
        });
    }
}
