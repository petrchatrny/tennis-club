package cz.petrchatrny.tennisclub.model;

import lombok.Getter;

@Getter
public enum UserRole {
    USER(1),
    ADMIN(2);

    private final int representation;

    UserRole(int representation) {
        this.representation = representation;
    }
}
