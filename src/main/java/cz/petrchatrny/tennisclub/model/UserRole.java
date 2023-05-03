package cz.petrchatrny.tennisclub.model;

import lombok.Getter;

/**
 * Representation of possible user roles
 */
@Getter
public enum UserRole {
    USER(1),
    ADMIN(2);

    private final int representation;

    UserRole(int representation) {
        this.representation = representation;
    }
}
