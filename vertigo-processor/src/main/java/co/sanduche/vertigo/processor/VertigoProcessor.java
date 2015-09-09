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
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by caeus on 6/09/15.
 */
@AutoService(Processor.class)
public class VertigoProcessor extends AbstractProcessor {
    private final Configuration configuration;
    private final Template template;

    public VertigoProcessor() throws IOException {
        this.configuration = new Configuration(new Version(2, 3, 23));
        ClassLoader classLoader = this.getClass().getClassLoader();

        configuration.setTemplateLoader(new ClassTemplateLoader(classLoader, ""));
        this.template = configuration.getTemplate("codecTemplate.ftl");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(typeElement -> {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(typeElement);
            elementsAnnotatedWith
                    .stream().filter(element -> element instanceof TypeElement)
                    .map(element -> (TypeElement) element)
                    .map(this::genModel).forEach(this::genCodec);
        });
        return true;
    }

    private Pair<TypeElement, CodecModel> genModel(TypeElement e) {
        Types types = processingEnv.getTypeUtils();
        CodecModel cm = new CodecModel();
        cm.setClassName(e.getSimpleName().toString());
        cm.setPackageName(((PackageElement) e.getEnclosingElement()).getQualifiedName().toString());
        Map<String, RichField> richFields = new HashMap<>();
        e.getEnclosedElements().stream()
                .map(element -> element)
                .filter(element -> {
                    Set<Modifier> modifiers = element.getModifiers();
                    if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.TRANSIENT)) {
                        return false;
                    }
                    if (element instanceof ExecutableElement) {
                        ExecutableElement executableElement = ((ExecutableElement) element);
                        String type = executableElement.getSimpleName().toString().substring(0, 3);
                        if (type.equals("set")) {
                            return executableElement.getParameters().size() == 1;
                        } else if (type.equals("get")) {
                            return executableElement.getParameters().size() == 0;
                        } else {
                            return false;
                        }
                    } else if (element instanceof VariableElement) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .forEach(element -> {
                    if (element instanceof VariableElement) {
                        VariableElement variableElement = (VariableElement) element;
                        String name = variableElement.getSimpleName().toString();
                        RichField rf = richFields.get(name);
                        if (rf == null) {
                            rf = new RichField();
                            richFields.put(name, rf);
                        }
                        rf.setField(variableElement);
                    } else if (element instanceof ExecutableElement) {
                        ExecutableElement executableElement = ((ExecutableElement) element);
                        String name = executableElement.getSimpleName().toString();
                        String type = name.substring(0, 3);
                        name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                        RichField rf = richFields.get(name);
                        if (rf == null) {
                            rf = new RichField();
                            richFields.put(name, rf);
                        }
                        if (type.equals("get"))
                            rf.setGetter(executableElement);
                        else if (type.equals("set"))
                            rf.setSeter(executableElement);

                    }
                });
        cm.setFields(richFields.entrySet().stream()
                .map(nameField -> {
                    RichField value = nameField.getValue();
                    value.setName(nameField.getKey());
                    return value;
                })
                .filter(richField -> richField.isValid())
                .map(richField -> {
                    FieldModel fieldModel = new FieldModel();
                    fieldModel.setName(richField.getName());
                    String clazz = types.erasure(richField.getField().asType()).toString();
                    System.out.println("clazzWithErasure = " + clazz);
                    fieldModel.setClazz(clazz);
                    fieldModel.setGetter(richField.getGetter().getSimpleName().toString());
                    fieldModel.setSetter(richField.getSetter().getSimpleName().toString());
                    return fieldModel;
                }).collect(Collectors.toList()));
        return new Pair<>(e, cm);

    }

    private void genCodec(Pair<TypeElement, CodecModel> pair) {
        try {
            CodecModel codecModel = pair.getValue();
            TypeElement typeElement = pair.getKey();
            JavaFileObject jo = processingEnv.getFiler().createSourceFile(codecModel.getPackageName() + "." + codecModel.getClassName() + "$$Codec", typeElement);
            Writer writer = jo.openWriter();
            template.process(codecModel,writer);
            writer.close();
            StringWriter sWriter = new StringWriter();
            template.process(codecModel,sWriter);
            sWriter.close();
            System.out.println(sWriter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        String canonicalName = MongoEntity.class.getCanonicalName();
        System.out.println("canonicalName = " + canonicalName);
        return new HashSet<>(Arrays.asList(canonicalName));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}