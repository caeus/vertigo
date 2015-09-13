package co.sanduche.vertigo.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by caeus on 6/09/15.
 */
public class RawProperty {
    private final String name;
    private final VariableElement field;
    private final ExecutableElement getter;
    private final ExecutableElement setter;


    public RawProperty(String name, VariableElement field, ExecutableElement getter, ExecutableElement setter) {
        this.name = name;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public VariableElement getField() {
        return field;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public ExecutableElement getSetter() {
        return setter;
    }

    public boolean isIgnorable() {
        return getter == null && setter == null;
    }


    public boolean isValid() {
        return getter != null && setter != null;
    }

    @Override
    public String toString() {
        return "RawProperty{" +
                "name='" + name + '\'' +
                ", field=" + field +
                ", getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
