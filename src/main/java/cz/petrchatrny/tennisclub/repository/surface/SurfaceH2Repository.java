package cz.petrchatrny.tennisclub.repository.surface;

import cz.petrchatrny.tennisclub.model.Surface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Implementation of ISurfaceRepository for manipulation with
 * Surfaces in H2 database.
 *
 * @see Surface
 * @see ISurfaceRepository
 */
@Repository
public class SurfaceH2Repository implements ISurfaceRepository {
    private final EntityManager entityManager;

    public SurfaceH2Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Collection<Surface> getAll(Boolean includeDeleted) {
        String jpql = "SELECT s FROM Surface s ";

        if (!includeDeleted) {
            jpql += "WHERE deletedAt is null ";
        }

        TypedQuery<Surface> query = entityManager.createQuery(jpql, Surface.class);
        return query.getResultList();
    }

    @Override
    public Surface getOne(Long key, Boolean includeDeleted) {
        String jpql = "SELECT s " +
                      "FROM Surface s " +
                      "WHERE id = :key ";

        if (!includeDeleted) {
            jpql += "AND deletedAt is null ";
        }

        TypedQuery<Surface> query = entityManager
                .createQuery(jpql, Surface.class)
                .setParameter("key", key);

        return query.getSingleResult();
    }

    @Override
    @Transactional
    public Surface create(Surface item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    @Transactional
    public Surface update(Surface item) {
        return entityManager.merge(item);
    }

    @Override
    @Transactional
    public void delete(Long key) {
        Surface surface = getOne(key);
        surface.setDeletedAt(LocalDateTime.now());

        entityManager.merge(surface);
    }
}
