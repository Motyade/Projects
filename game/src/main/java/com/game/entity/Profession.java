package com.game.entity;

public enum Profession {
    WARRIOR("WARRIOR"),
    ROGUE("ROGUE"),
    SORCERER("SORCERER"),
    CLERIC("CLERIC"),
    PALADIN("PALADIN"),
    NAZGUL("NAZGUL"),
    WARLOCK("WARLOCK"),
    DRUID("DRUID");

    private String value;

    Profession(String value){
        this.value = value;
    }
}
