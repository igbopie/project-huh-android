package com.huhapp.android.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by igbopie on 4/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionType implements Serializable {
    private String color;
    private String word;
    private int weight;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "QuestionType{" +
                "color='" + color + '\'' +
                ", word='" + word + '\'' +
                ", weight=" + weight +
                '}';
    }
}
