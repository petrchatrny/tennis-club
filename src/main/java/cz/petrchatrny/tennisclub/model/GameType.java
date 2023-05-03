package cz.petrchatrny.tennisclub.model;

import lombok.Getter;

/**
 * Representation of type of tennis game
 */
@Getter
public enum GameType {
    SINGLES(1),
    DOUBLES(2);

    private final int representation;

    GameType(int representation) {
        this.representation = representation;
    }
}
