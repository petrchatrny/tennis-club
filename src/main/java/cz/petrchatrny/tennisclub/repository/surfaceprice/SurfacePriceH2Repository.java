package cz.petrchatrny.tennisclub.repository.surfaceprice;

import cz.petrchatrny.tennisclub.model.SurfacePrice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class SurfacePriceH2Repository implements ISurfacePriceRepository {
    private final EntityManager entityManager;

    public SurfacePriceH2Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public SurfacePrice create(SurfacePrice price) {
        entityManager.persist(price);
        return price;
    }

    @Override
    @Transactional
    public void invalidate(Long id) {
        String jpql = "SELECT sp " +
                      "FROM SurfacePrice sp " +
                      "WHERE id = :id ";

        TypedQuery<SurfacePrice> query = entityManager
                .createQuery(jpql, SurfacePrice.class)
                .setParameter("id", id);

        SurfacePrice price = query.getSingleResult();
        price.setValidTo(LocalDateTime.now());

        entityManager.merge(price);
    }
}
