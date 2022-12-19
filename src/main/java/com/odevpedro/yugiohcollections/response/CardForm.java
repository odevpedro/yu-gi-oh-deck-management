package com.odevpedro.yugiohcollections.response;

public class CardForm {

    private String name;

    private String type;

    private String atribute;

    private String effect;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAtribute() {
        return atribute;
    }

    public void setAtribute(String atribute) {
        this.atribute = atribute;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }


    public CardForm(String name, String type, String atribute, String effect) {
        this.name = name;
        this.type = type;
        this.atribute = atribute;
        this.effect = effect;
    }
}
