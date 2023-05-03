package cz.petrchatrny.tennisclub.repository.reservation;

import cz.petrchatrny.tennisclub.model.Reservation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of IReservationRepository for manipulation with
 * Reservations in H2 database.
 *
 * @see Reservation
 * @see IReservationRepository
 */
@Repository
public class ReservationH2Repository implements IReservationRepository {
    private final EntityManager entityManager;

    public ReservationH2Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Collection<Reservation> getAll(Boolean includeDeleted) {
        String jpql = "SELECT r FROM Reservation r ";

        if (!includeDeleted) {
            jpql += "WHERE deletedAt is null ";
        }

        TypedQuery<Reservation> query = entityManager.createQuery(jpql, Reservation.class);
        return query.getResultList();
    }

    @Override
    public Collection<Reservation> getAll(String userPhoneNumber, Long courtId, Boolean pendingOnly) {
        String jpql = "SELECT r " +
                      "FROM Reservation r " +
                      "WHERE deletedAt is null ";
         Map<String, Object> parameters = new HashMap<>();

        if (userPhoneNumber != null) {
            jpql += "AND r.user.phoneNumber = :phoneNumber ";
            parameters.put("phoneNumber", userPhoneNumber);
        }

        if (courtId != null) {
            jpql += "AND r.court.id = :courtId ";
            parameters.put("courtId", courtId);
        }

        if (pendingOnly != null && pendingOnly) {
            jpql += "AND heldUntil > NOW() ";
        }

        jpql += "ORDER BY r.createdAt DESC ";

        TypedQuery<Reservation> query = entityManager.createQuery(jpql, Reservation.class);
        for (String key : parameters.keySet()) {
            query.setParameter(key, parameters.get(key));
        }

        return query.getResultList();
    }

    @Override
    public Reservation getOne(Long key, Boolean includeDeleted) {
        String jpql = "SELECT r " +
                      "FROM Reservation r " +
                      "WHERE r.id = :key ";

        if (!includeDeleted) {
            jpql += "AND deletedAt is null ";
        }

        TypedQuery<Reservation> query = entityManager
                .createQuery(jpql, Reservation.class)
                .setParameter("key", key);

        return query.getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Boolean isIntervalConflicting(Long courtId, LocalDateTime heldAt, LocalDateTime heldUntil) {
        String jpql = "SELECT r " +
                      "FROM Reservation r " +
                      "WHERE deletedAt is null " +
                        "AND court.id = :courtId " +
                        "AND NOT (:heldUntil <= r.heldAt OR :heldAt >= r.heldUntil)";

        TypedQuery<Reservation> query = entityManager
                .createQuery(jpql, Reservation.class)
                .setParameter("courtId", courtId)
                .setParameter("heldAt", heldAt)
                .setParameter("heldUntil", heldUntil);

        return !query.getResultList().isEmpty();
    }

    @Override
    public BigDecimal getPrice(Long id) {
        String jpql = "SELECT DATEDIFF(MINUTE, r.heldAt, r.heldUntil) * sp.pricePerMinuteInCzk AS total_price " +
                      "FROM Reservation r " +
                        "JOIN r.court c " +
                        "JOIN c.surface s " +
                        "JOIN c.surface.prices sp " +
                      "WHERE r.id = :id " +
                        "AND sp.validFrom <= r.heldAt " +
                        "AND (sp.validTo >= r.heldUntil OR sp.validTo = null)";

        TypedQuery<BigDecimal> query = entityManager
                .createQuery(jpql, BigDecimal.class)
                .setParameter("id", id);

        return query.getSingleResult();
    }

    @Override
    @Transactional
    public Reservation create(Reservation item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    @Transactional
    public Reservation update(Reservation item) {
        return entityManager.merge(item);
    }

    @Override
    @Transactional
    public void delete(Long key) {
        Reservation r = getOne(key);
        r.setDeletedAt(LocalDateTime.now());

        entityManager.merge(r);
    }
}
