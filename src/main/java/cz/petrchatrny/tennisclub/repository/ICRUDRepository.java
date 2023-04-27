package cz.petrchatrny.tennisclub.repository;

import java.util.Collection;

/**
 * Interface for performing CRUD operations upon database entity.
 *
 * @param <E> data type of entity
 * @param <K> data type of entity's primary key
 */
public interface ICRUDRepository<E, K> {
    /**
     * Gets all entities.
     *
     * @return collection of all entities from database
     */
    Collection<E> getAll();

    /**
     * Gets one entity by its primary key.
     *
     * @param key primary key of entity
     * @return one entity found by primary key
     */
    E getOne(K key);

    /**
     * Creates new entity.
     *
     * @param item entity to be created
     * @return created entity
     */
    E create(E item);

    /**
     * Updates existing entity.
     *
     * @param item entity to be updated
     * @return updated entity
     */
    E update(E item);

    /**
     * Deletes entity by its primary key.
     *
     * @param key primary key of entity
     */
    void delete(K key);
}
