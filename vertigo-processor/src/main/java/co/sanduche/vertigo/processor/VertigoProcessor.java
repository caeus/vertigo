package co.sanduche.vertigo.processor;

import co.sanduche.vertigo.annotations.MongoEntity;
import com.google.auto.service.AutoService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import javafx.util.Pair;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by caeus on 6/09/15.
 */
@AutoService(Processor.class)
public class VertigoProcessor extends AbstractProcessor {
    private final Configuration configuration;
    private final Template template;
    private Types types;
    private Elements elements;


    public VertigoProcessor() throws IOException {


        this.configuration = new Configuration(new Version(2, 3, 23));
        ClassLoader classLoader = this.getClass().getClassLoader();
        configuration.setTemplateLoader(new ClassTemplateLoader(classLoader, ""));
        this.template = configuration.getTemplate("codecTemplate.ftl");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();

        annotations.stream().flatMap(typeElement1 -> roundEnv.getElementsAnnotatedWith(typeElement1).stream())
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(element -> (TypeElement) element)
                .map(this::genModel)
                .forEach(this::genCodec);
        return true;
    }


    private MethodKey getMethodEntry(ExecutableElement executableElement) {
        List<? extends VariableElement> parameters = executableElement.getParameters();
        int size = parameters.size();
        if (size > 1) return null;
        String methodName = executableElement.getSimpleName().toString();
        TypeMirror returnType = executableElement.getReturnType();
        if (size == 0) {
            boolean returnsPrimitiveBoolean = returnType.getKind() == TypeKind.BOOLEAN;
            boolean isGetter = (returnsPrimitiveBoolean && methodName.startsWith("is")) || methodName.startsWith("get");
            if (!isGetter)
                return null;
            return new MethodKey(returnsPrimitiveBoolean ? Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3) :
                    Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4), returnType.toString(), MethodRole.GETTER);
        } else if (size == 1) {
            boolean isVoid = returnType.getKind() == TypeKind.VOID;
            if (!methodName.startsWith("set") || !isVoid)
                return null;
            return new MethodKey(Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4), parameters.get(0).asType().toString(), MethodRole.SETTER);
        }
        return null;
    }

    private boolean filterElements(Element element) {
        return (!element.getModifiers().contains(Modifier.STATIC))
                && (element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.FIELD);
    }

    private Pair<TypeElement, CodecModel> genModel(TypeElement typeElement) {
        Map<ElementKind, List<Element>> classElements = typeElement.getEnclosedElements()
                .stream().filter(this::filterElements)
                .collect(Collectors.groupingBy(element -> element.getKind(), Collectors.toList()));

        Map<String, VariableElement> fields = classElements.getOrDefault(ElementKind.FIELD, Collections.emptyList())
                .stream().map(element -> ((VariableElement) element))
                .collect(Collectors.toMap(variableElement -> variableElement.getSimpleName().toString(),
                        variableElement -> variableElement));

        Map<MethodKey, ExecutableElement> methods = classElements.getOrDefault(ElementKind.METHOD, Collections.emptyList())
                .stream().map(element -> ((ExecutableElement) element))
                .collect(Collectors.toMap(this::getMethodEntry, executableElement -> executableElement));
        methods.forEach((methodKey, executableElement) -> {

        });
        List<RawProperty> rawProperties = fields.entrySet().stream().map(entryNameElement -> {
            String name = entryNameElement.getKey();
            TypeMirror type = entryNameElement.getValue().asType();
            String typeAsString = type.toString();
            ExecutableElement getter = methods.get(new MethodKey(name, typeAsString, MethodRole.GETTER));
            ExecutableElement setter = methods.get(new MethodKey(name, typeAsString, MethodRole.SETTER));
            return new RawProperty(name, entryNameElement.getValue(), getter, setter);
        }).filter(rawProperty -> !rawProperty.isIgnorable()).collect(Collectors.toList());
        rawProperties.forEach(rawProperty -> {
            if (!rawProperty.isValid()) {
                throw new IllegalStateException(String.format("The property %s of the class %s doesn't have getter and/or setter", rawProperty.getName(), typeElement));
            }
        });
        CodecModel cm = new CodecModel();
        cm.setClassName(typeElement.getSimpleName().toString());
        cm.setPackageName(elements.getPackageOf(typeElement).getQualifiedName().toString());
        cm.setProperties(rawProperties.stream().map(this::genPropertyModel).collect(Collectors.toList()));

        return new Pair<>(typeElement, cm);

    }

    private PropertyModel genPropertyModel(RawProperty rawProperty) {
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setName(rawProperty.getName());
        TypeMirror t = rawProperty.getField().asType();
        if (t instanceof PrimitiveType) t = types.boxedClass(((PrimitiveType) t)).asType();
        propertyModel.setClazz(types.erasure(t).toString());
        propertyModel.setType(t.toString());
        propertyModel.setGetter(rawProperty.getGetter().getSimpleName().toString());
        propertyModel.setSetter(rawProperty.getSetter().getSimpleName().toString());
        return propertyModel;
    }

    private void genCodec(Pair<TypeElement, CodecModel> pair) {
        try {
            CodecModel codecModel = pair.getValue();
            TypeElement typeElement = pair.getKey();
            String packageName = codecModel.getPackageName();
            JavaFileObject jo = processingEnv.getFiler().createSourceFile((packageName.trim().length() > 0 ? packageName + "." : "") + codecModel.getClassName() + "$$Codec", typeElement);
            Writer writer = jo.openWriter();
            template.process(codecModel, writer);

            StringWriter out = new StringWriter();
            template.process(codecModel, out);

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        String canonicalName = MongoEntity.class.getCanonicalName();

        return new HashSet<>(Arrays.asList(canonicalName));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private static enum MethodRole {
        GETTER, SETTER;
    }

    private static class MethodKey {
        private final String name;
        private final String type;
        private final MethodRole role;


        public MethodKey(String name, String type, MethodRole role) {
            this.name = name;
            this.type = type;
            this.role = role;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodKey methodKey = (MethodKey) o;

            if (name != null ? !name.equals(methodKey.name) : methodKey.name != null) return false;
            if (type != null ? !type.equals(methodKey.type) : methodKey.type != null) return false;
            return role == methodKey.role;

        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (role != null ? role.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MethodKey{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", role=" + role +
                    '}';
        }
    }

}
