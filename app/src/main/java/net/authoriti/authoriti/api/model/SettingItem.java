package net.authoriti.authoriti.api.model;

public class SettingItem {
    String name = "";

    public SettingItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
