package co.sanduche.vertigo.codecs;

import co.sanduche.vertigo.annotations.MongoEntity;
import com.google.common.reflect.TypeToken;
import com.mongodb.MongoClient;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by caeus on 7/09/15.
 */
public class VertigoCodecRegistry implements CodecRegistry {

    private final Map<Class, TypeSafeCodec> collectionCodecs;
    private final Map<Class, CodecWrapper> mongoEntityCodecs = new HashMap<>();
    private final Map<Class, CodecWrapper> defaultCodecs = new HashMap<>();
    private final CodecRegistry defaultCodecRegistry;

    public VertigoCodecRegistry(List<TypeSafeCodec> codecs, CodecRegistry defaultCodecRegistry) {
        this.collectionCodecs = codecs.stream().collect(Collectors.toMap(superCodec -> superCodec.getEncoderClass(), superCodec -> superCodec));
        this.defaultCodecRegistry = defaultCodecRegistry;
    }

    public VertigoCodecRegistry() {
        this(Arrays.asList(new MapCodec(), new ListCodec()), MongoClient.getDefaultCodecRegistry());
    }

    public <T> CodecWrapper<T> getCodecFor(Class<T> clazz) {
        CodecWrapper<T> codec = getMongoEntityCodec(clazz);
        if (codec == null) codec = getDefaultCodecFor(clazz);
        return codec;
    }

    private <T> CodecWrapper getDefaultCodecFor(Class<T> clazz) {
        //if there's no codec for clazz, defaultCodecRegistry will raise an exception... that's expected.
        return defaultCodecs.computeIfAbsent(clazz, aClass -> new CodecWrapper(defaultCodecRegistry.get(aClass)));
    }


    private <T> CodecWrapper<T> getMongoEntityCodec(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(MongoEntity.class)) return null;
        return mongoEntityCodecs.computeIfAbsent(clazz, innerClazz -> {
            try {
                Constructor<Codec<T>> constructor = (Constructor<Codec<T>>) clazz.getClassLoader().loadClass(innerClazz.getName() + "$$Codec").getDeclaredConstructor(VertigoCodecRegistry.class);
                Codec<T> codec = constructor.newInstance(this);
                return Objects.requireNonNull(new CodecWrapper(codec),
                        String.format("The codec for class %s was not created, the vertigo-processor was probably not executed properly", innerClazz));
            } catch (Exception e) {
                throw new IllegalStateException(String.format("The codec for class %s doesn't existt, the vertigo-processor was probably not executed properly", innerClazz), e);
            }
        });
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz) {
        return getCodecFor(clazz).getCodec();
    }


    public <T> TypeSafeCodec<T> lookupCodec(TypeToken<T> typeToken) {
        Class clazz = typeToken.getRawType();
        if (collectionCodecs.containsKey(clazz)) {
            return collectionCodecs.get(clazz);
        } else {
            return getCodecFor(clazz);
        }

    }

}
