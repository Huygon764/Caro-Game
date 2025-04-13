package com.example.carogame.core.model;

public class Player {
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;

    private final int id;
    private final String name;
    private final boolean isAI;

    public Player(int id, String name, boolean isAI) {
        this.id = id;
        this.name = name;
        this.isAI = isAI;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAI() {
        return isAI;
    }
}
