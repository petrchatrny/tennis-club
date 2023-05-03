package cz.petrchatrny.tennisclub.repository.surface;

import cz.petrchatrny.tennisclub.model.Surface;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SurfaceH2RepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void getAll() {
        // will get created
        SurfaceH2Repository repository = getRepository();
        Surface mockSurface = repository.create(new Surface());

        for (int i = 0; i < 2; i++) {
            repository.create(new Surface());
        }

        List<Surface> surfaces = repository.getAll()
                .stream()
                .toList();

        assertNotNull(surfaces);
        assertEquals(3, surfaces.size());

        // won't get deleted
        repository.delete(mockSurface.getId());
        surfaces = repository.getAll()
                .stream()
                .filter(surface -> Objects.equals(surface.getId(), mockSurface.getId()))
                .toList();

        assertEquals(0, surfaces.size());
    }

    @Test
    void getAllIncludeDeleted() {
        // create mock surface
        SurfaceH2Repository repository = getRepository();
        Surface mockSurface = repository.create(new Surface());

        // delete
        repository.delete(mockSurface.getId());

        // try to obtain
        List<Surface> surfaces = repository
                .getAll(true)
                .stream()
                .filter(surface -> surface.getId().equals(mockSurface.getId()))
                .toList();

        assertNotNull(surfaces.get(0));
        assertEquals(mockSurface.getId(), surfaces.get(0).getId());
    }

    @Test
    void getOne() {
        // create mock surface
        SurfaceH2Repository repository = getRepository();
        Surface mockSurface = repository.create(new Surface());

        assertEquals(mockSurface.getId(), repository.getOne(mockSurface.getId()).getId());
        assertNull(mockSurface.getDeletedAt());

        // delete mock
        repository.delete(mockSurface.getId());

        // won't get deleted one
        assertNull(repository.getOne(mockSurface.getId()));
    }

    @Test
    void getOneIncludeDeleted() {
        // create mock surface
        SurfaceH2Repository repository = getRepository();
        Surface mockSurface = repository.create(new Surface());

        // delete created one
        repository.delete(mockSurface.getId());
        assertNotNull(mockSurface.getDeletedAt());

        // try to obtain deleted
        Surface updateSurface = repository.getOne(mockSurface.getId(), true);
        assertEquals(updateSurface.getId(), repository.getOne(mockSurface.getId(), true).getId());
        assertNotNull(updateSurface.getDeletedAt());
    }

    @Test
    void create() {
        Surface surface = getRepository().create(new Surface());
        assertNotNull(surface);
        assertNotNull(surface.getId());
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
        // create mock surface
        SurfaceH2Repository repository = getRepository();
        Surface surface = repository.create(new Surface());

        // try to delete
        repository.delete(surface.getId());
        assertNotNull(surface.getDeletedAt());
    }

    private SurfaceH2Repository getRepository() {
        return new SurfaceH2Repository(entityManager.getEntityManager());
    }
}