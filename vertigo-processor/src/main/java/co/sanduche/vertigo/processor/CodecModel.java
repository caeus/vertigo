package co.sanduche.vertigo.processor;

import java.util.List;

/**
 * Created by caeus on 6/09/15.
 */
public class CodecModel<T> {
    private String className;
    private String packageName;
    private List<PropertyModel> properties;

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

    public List<PropertyModel> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyModel> fields) {
        this.properties = fields;
    }

    @Override
    public String toString() {

        return "CodecModel{" +
                "className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", properties=" + properties +
                '}';
    }
}
