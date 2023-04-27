package cz.petrchatrny.tennisclub.repository;

import java.util.Collection;

/**
 * Extension of ICRUDRepository interface.
 *
 * <p>It adds new methods for working with SoftDeletableEntity.</p>
 *
 * @see ICRUDRepository
 * @see cz.petrchatrny.tennisclub.model.SoftDeletableEntity
 *
 * @param <E> data type of entity
 * @param <K> data type of entity's primary key
 */
public interface ICRUDSoftDeleteRepository<E, K> extends ICRUDRepository<E, K> {
    /**
     * Gets all entities, including deleted ones.
     *
     * @param includeDeleted flag for including deleted entities
     * @return collection of all entities from database
     */
    Collection<E> getAll(Boolean includeDeleted);

    /**
     * Gets one entity by its primary key. Selection may also include deleted entities.
     *
     * @param key primary key of entity
     * @param includeDeleted flag for including deleted entities
     * @return  one entity found by primary key
     */
    E getOne(K key, Boolean includeDeleted);

    @Override
    default Collection<E> getAll() {
        return getAll(false);
    }

    @Override
    default E getOne(K key) {
        return getOne(key, false);
    }
}
