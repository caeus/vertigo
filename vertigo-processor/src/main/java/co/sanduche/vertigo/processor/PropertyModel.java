package co.sanduche.vertigo.processor;

/**
 * Created by caeus on 6/09/15.
 */
public class PropertyModel {
    private String name;
    private String clazz;
    private String type;
    private String getter;
    private String setter;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }


    @Override
    public String toString() {
        return "PropertyModel{" +
                "clazz='" + clazz + '\'' +
                ", getter='" + getter + '\'' +
                ", setter='" + setter + '\'' +
                '}';
    }


}
