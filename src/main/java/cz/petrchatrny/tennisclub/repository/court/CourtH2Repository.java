package cz.petrchatrny.tennisclub.repository.court;

import cz.petrchatrny.tennisclub.model.Court;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Implementation of ICourtRepository for manipulation with
 * Courts in H2 database.
 *
 * @see Court
 * @see ICourtRepository
 */
@Repository
public class CourtH2Repository implements ICourtRepository {
    private final EntityManager entityManager;

    public CourtH2Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Collection<Court> getAll(Boolean includeDeleted) {
        String jpql = "SELECT c FROM Court c ";

        if (!includeDeleted) {
            jpql += "WHERE deletedAt is null ";
        }

        TypedQuery<Court> query = entityManager.createQuery(jpql, Court.class);
        return query.getResultList();
    }

    @Override
    public Court getOne(Long key, Boolean includeDeleted) {
        String jpql = "SELECT c " +
                      "FROM Court c " +
                      "WHERE id = :key ";

        if (!includeDeleted) {
            jpql += "AND deletedAt is null ";
        }

        TypedQuery<Court> query = entityManager
                .createQuery(jpql, Court.class)
                .setParameter("key", key);

        return query
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public Court create(Court item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    @Transactional
    public Court update(Court item) {
        entityManager.merge(item);
        return item;
    }

    @Override
    @Transactional
    public void delete(Long key) {
        Court court = getOne(key);
        court.setDeletedAt(LocalDateTime.now());

        entityManager.merge(court);
    }
}
