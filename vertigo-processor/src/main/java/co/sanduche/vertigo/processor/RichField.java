package co.sanduche.vertigo.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by caeus on 6/09/15.
 */
public class RichField {
    private VariableElement field;
    private ExecutableElement getter;
    private ExecutableElement seter;
    private String name;

    public VariableElement getField() {
        return field;
    }

    public void setField(VariableElement field) {
        this.field = field;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public void setGetter(ExecutableElement getter) {
        this.getter = getter;
    }

    public ExecutableElement getSetter() {
        return seter;
    }

    public void setSeter(ExecutableElement seter) {
        this.seter = seter;
    }
    public boolean isValid(){
        return field !=null&&getter!=null&&seter!=null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
