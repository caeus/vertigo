package co.sanduche.vertigo.processor;

import java.util.List;

/**
 * Created by caeus on 6/09/15.
 */
public class CodecModel {
    private String className;
    private String packageName;
    private List<FieldModel> fields;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<FieldModel> getFields() {
        return fields;
    }

    public void setFields(List<FieldModel> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "CodecModel{" +
                "className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", fields=" + fields +
                '}';
    }
}
