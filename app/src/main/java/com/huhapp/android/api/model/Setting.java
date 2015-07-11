package com.huhapp.android.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by igbopie on 4/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Setting implements Serializable {
    private String name;
    private String title;
    private String description;
    private Boolean value;

    public Setting(String name, String title, String description, Boolean value) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.value = value;
    }

    public Setting() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", value=" + value +
                '}';
    }
}
