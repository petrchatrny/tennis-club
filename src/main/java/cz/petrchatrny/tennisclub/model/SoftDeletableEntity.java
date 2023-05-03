package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Abstract entity used for implementation of soft delete operation
 */
@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeletableEntity {

    @NotNull
    protected LocalDateTime createdAt;

    protected LocalDateTime deletedAt;

    @PrePersist
    void createdAt() {
        this.createdAt = LocalDateTime.now();
    }
}
