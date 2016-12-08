package ai.api.sample;

/**
 * Created by mickey on 12/8/16.
 */

public class Filter {

    public Filter(String value) {
        this.value = value;
    }

    public String getName() {
        return name;

    }

    public Filter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String value) {
        this.value = value;
    }

    private String name;
    private String value;
}
