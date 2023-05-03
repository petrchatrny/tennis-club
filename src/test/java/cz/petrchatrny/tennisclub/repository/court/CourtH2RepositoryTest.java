package cz.petrchatrny.tennisclub.repository.court;

import cz.petrchatrny.tennisclub.model.Court;
import cz.petrchatrny.tennisclub.model.Surface;
import cz.petrchatrny.tennisclub.repository.surface.ISurfaceRepository;
import cz.petrchatrny.tennisclub.repository.surface.SurfaceH2Repository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CourtH2RepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    private Surface surface1;
    private Surface surface2;

    @BeforeEach
    void setUp() {
        ISurfaceRepository surfaceRepository = new SurfaceH2Repository(entityManager.getEntityManager());
        this.surface1 = surfaceRepository.create(new Surface());
        this.surface2 = surfaceRepository.create(new Surface());
    }

    @Test
    void getAll() {
        // will get created
        CourtH2Repository repository = getRepository();
        for (int i = 0; i < 3; i++) {
            Court court = Court.builder().surface(surface1).build();
            repository.create(court);
        }

        assertEquals(3, repository.getAll().size());

        // won't get deleted
        Court court = repository.getAll().stream().toList().get(0);
        repository.delete(court.getId());

        assertEquals(2, repository.getAll().size());

        // will get deleted
        assertEquals(3, repository.getAll(true).size());
    }

    @Test
    void getAllBySurface() {
        Court deletedOne = null;
        for (int i = 0; i < 4; i++) {
            Court court = Court.builder().surface(surface1).build();
            court = getRepository().create(court);

            if (deletedOne == null) {
                getRepository().delete(court.getId());
                deletedOne = court;
            }
        }

        for (int i = 0; i < 8; i++) {
            Court court = Court.builder().surface(surface2).build();
            getRepository().create(court);
        }

        assertEquals(3, getRepository().getAllBySurface(surface1.getId()).size()); // 1 is deleted
        assertEquals(8, getRepository().getAllBySurface(surface2.getId()).size());
    }

    @Test
    void getOne() {
        Court court = Court.builder().surface(surface1).build();
        getRepository().create(court);

        assertEquals(court.getId(), getRepository().getOne(court.getId()).getId());
        getRepository().delete(court.getId());
        assertNull(getRepository().getOne(court.getId()));
        assertNotNull(getRepository().getOne(court.getId(), true));
    }

    @Test
    void create() {
        assertThrows(PersistenceException.class, () -> getRepository().create(
                Court.builder().surface(null).build()
        ));

        Court court = Court.builder().surface(surface1).build();
        getRepository().create(court);
        assertNotNull(court.getId());
    }

    @Test
    void update() {
        Court court = Court.builder().surface(surface1).build();
        getRepository().create(court);

        court.setSurface(surface2);
        getRepository().update(court);

        assertEquals(surface2.getId(), court.getSurface().getId());
    }

    @Test
    void delete() {
        Court court = Court.builder().surface(surface1).build();
        Long id = getRepository().create(court).getId();

        getRepository().delete(id);
        assertNotNull(court.getDeletedAt());

        // double delete
        assertThrows(NullPointerException.class, () -> getRepository().delete(id));

    }

    private CourtH2Repository getRepository() {
        return new CourtH2Repository(entityManager.getEntityManager());
    }

}