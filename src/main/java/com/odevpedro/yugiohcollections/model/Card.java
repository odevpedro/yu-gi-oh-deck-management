package com.odevpedro.yugiohcollections.model;

import jakarta.persistence.*;

@Entity

public class Card {

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "atribtue")
    private String atribtue;

    @Column(name = "effect")
    private String effect;


    //constructor


    public Card( String name, String type, String atribtue, String effect) {
        this.name = name;
        this.type = type;
        this.atribtue = atribtue;
        this.effect = effect;
    }

    public Card(){

    }

    public Long getId() {
        return id;
    }

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

    public String getAtribtue() {
        return atribtue;
    }

    public void setAtribtue(String atribtue) {
        this.atribtue = atribtue;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }


}
