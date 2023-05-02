package cz.petrchatrny.tennisclub.repository.user;

import cz.petrchatrny.tennisclub.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Implementation of IUserRepository for manipulation with
 * Users in H2 database.
 *
 * @see User
 * @see IUserRepository
 */
@Repository
public class UserH2Repository implements IUserRepository {
    private final EntityManager entityManager;

    public UserH2Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Collection<User> getAll(Boolean includeDeleted) {
        String jpql = "SELECT u FROM User u ";

        if (!includeDeleted) {
            jpql += "WHERE deletedAt is null ";
        }

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        return query.getResultList();
    }

    @Override
    public User getOne(Long key, Boolean includeDeleted) {
        String jpql = "SELECT u " +
                      "FROM User u " +
                      "WHERE id = :key ";

        if (!includeDeleted) {
            jpql += "AND deletedAt is null ";
        }

        TypedQuery<User> query = entityManager
                .createQuery(jpql, User.class)
                .setParameter("key", key);

        return query
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public User getOne(String phoneNumber) {
        String jpql = "SELECT u " +
                      "FROM User u " +
                      "WHERE phoneNumber = :phoneNumber " +
                        "AND deletedAt is null";

        TypedQuery<User> query = entityManager
                .createQuery(jpql, User.class)
                .setParameter("phoneNumber", phoneNumber);

        return query
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public User create(User item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    @Transactional
    public User update(User item) {
        return entityManager.merge(item);
    }

    @Override
    @Transactional
    public void delete(Long key) {
        User user = getOne(key);
        user.setDeletedAt(LocalDateTime.now());

        entityManager.merge(user);
    }
}
