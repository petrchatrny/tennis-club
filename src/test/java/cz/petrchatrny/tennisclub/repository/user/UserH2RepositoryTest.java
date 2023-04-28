package cz.petrchatrny.tennisclub.repository.user;

import cz.petrchatrny.tennisclub.model.User;
import cz.petrchatrny.tennisclub.model.UserRole;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserH2RepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    private final String PHONE_NUMBER = "111";

    @Test
    void getAll() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());

        // will get created
        List<User> users = repository.getAll()
                .stream()
                .toList();

        assertNotNull(users);
        users = users.stream()
                .filter(user -> user.getId().equals(mockUser.getId()))
                .toList();

        assertEquals(mockUser.getId(), users.get(0).getId());

        // won't get deleted
        repository.delete(mockUser.getId());
        users = repository.getAll()
                .stream()
                .toList();
        assertEquals(0, users.size());
    }

    @Test
    void getAllIncludeDeleted() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());

        // delete
        repository.delete(mockUser.getId());

        // try to obtain
        List<User> users = repository
                .getAll(true)
                .stream()
                .filter(surface -> surface.getId().equals(mockUser.getId()))
                .toList();

        assertNotNull(users.get(0));
        assertEquals(mockUser.getId(), users.get(0).getId());
    }

    @Test
    void getOne() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());
        repository.create(mockUser);

        // try to obtain
        assertEquals(mockUser.getId(), repository.getOne(mockUser.getId()).getId());

        // won't get deleted
        repository.delete(mockUser.getId());
        assertThrows(NoResultException.class, () -> repository.getOne(mockUser.getId()));
    }

    @Test
    void getOneIncludeDeleted() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());
        repository.create(mockUser);

        // delete mock user
        repository.delete(mockUser.getId());

        assertNotNull(repository.getOne(mockUser.getId(), true));
    }

    @Test
    void getOneByPhoneNumber() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());
        repository.create(mockUser);

        assertEquals(mockUser, repository.getOne(PHONE_NUMBER));
        assertThrows(NoResultException.class, () -> repository.getOne("nonsence"));

        // won't get deleted one
        repository.delete(mockUser.getId());
        assertThrows(NoResultException.class, () -> repository.getOne(PHONE_NUMBER));
    }

    @Test
    void create() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());
        repository.create(mockUser);

        assertNotNull(mockUser.getId());
        assertNotNull(mockUser.getCreatedAt());
        assertNull(mockUser.getDeletedAt());
    }

    @Test
    void update() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());
        repository.create(mockUser);

        String newStringValue = "SOMETHING NEW";

        // update mock user
        mockUser.setFullName(newStringValue);
        mockUser.setPhoneNumber(newStringValue);
        mockUser.setPassword(newStringValue);
        mockUser.setSalt(newStringValue);
        mockUser.setRole(UserRole.ADMIN);
        repository.update(mockUser);

        User updatedUser = repository.getOne(mockUser.getId());
        assertEquals(newStringValue, updatedUser.getFullName());
        assertEquals(newStringValue, updatedUser.getPhoneNumber());
        assertEquals(newStringValue, updatedUser.getPassword());
        assertEquals(newStringValue, updatedUser.getSalt());
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
    }

    @Test
    void delete() {
        // create mock user
        UserH2Repository repository = getRepository();
        User mockUser = repository.create(getMockUser());
        repository.create(mockUser);

        // delete mock user
        repository.delete(mockUser.getId());

        assertNotNull(mockUser.getDeletedAt());
    }

    private UserH2Repository getRepository() {
        return new UserH2Repository(entityManager.getEntityManager());
    }

    private User getMockUser() {
        return User.builder()
                .phoneNumber(PHONE_NUMBER)
                .fullName("test")
                .role(UserRole.USER)
                .password("test")
                .salt("test")
                .build();
    }
}