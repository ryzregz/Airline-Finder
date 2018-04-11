package model;

/**
 * Created by mac on 2/26/18.
 */

public class Airport {
    String name,code;

    public Airport() {
    }

    public Airport(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
