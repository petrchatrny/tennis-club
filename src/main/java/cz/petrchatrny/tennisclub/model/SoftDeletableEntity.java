package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
