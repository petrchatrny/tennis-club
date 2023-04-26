package cz.petrchatrny.tennisclub.model;

import lombok.Getter;

@Getter
public enum GameType {
    SINGLES(1),
    DOUBLES(2);

    private final int representation;

    GameType(int representation) {
        this.representation = representation;
    }
}
