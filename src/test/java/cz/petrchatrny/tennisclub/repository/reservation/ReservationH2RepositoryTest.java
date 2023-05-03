package cz.petrchatrny.tennisclub.repository.reservation;

import cz.petrchatrny.tennisclub.model.*;
import cz.petrchatrny.tennisclub.repository.court.CourtH2Repository;
import cz.petrchatrny.tennisclub.repository.court.ICourtRepository;
import cz.petrchatrny.tennisclub.repository.surface.ISurfaceRepository;
import cz.petrchatrny.tennisclub.repository.surface.SurfaceH2Repository;
import cz.petrchatrny.tennisclub.repository.user.IUserRepository;
import cz.petrchatrny.tennisclub.repository.user.UserH2Repository;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReservationH2RepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    private Court court;
    private User user;

    @BeforeEach
    void setUp() {
        ICourtRepository courtRepository = new CourtH2Repository(entityManager.getEntityManager());
        IUserRepository userRepository = new UserH2Repository(entityManager.getEntityManager());
        ISurfaceRepository surfaceRepository = new SurfaceH2Repository(entityManager.getEntityManager());

        Surface surface = surfaceRepository.create(new Surface());

        this.court = courtRepository.create(Court.builder().surface(surface).build());
        this.user = userRepository.create(
                User.builder()
                        .phoneNumber("123456")
                        .fullName("asdf")
                        .password("")
                        .role(UserRole.USER)
                        .salt("")
                        .build()
        );
    }

    @Test
    void getAll() {
        for (int i = 0; i < 3; i++) {
            Reservation r = getRepository().create(buildReservation());
            System.out.println(r);
        }

        assertEquals(3, getRepository().getAll().size());
    }

    @Test
    void getAllByProperties() {
        Reservation reservation = buildReservation();
        getRepository().create(reservation);

        assertEquals(1, getRepository().getAll(user.getPhoneNumber(), null, null).size());
        assertEquals(0, getRepository().getAll("x", null, null).size());

        assertEquals(1, getRepository().getAll(null, court.getId(), null).size());
        assertEquals(0, getRepository().getAll(null, -1L, null).size());

        assertEquals(1, getRepository().getAll(null, null, false).size());
        assertEquals(1, getRepository().getAll(null, null, true).size());
    }

    @Test
    void getOne() {
        Reservation reservation = buildReservation();
        getRepository().create(reservation);

        assertNotNull(getRepository().getOne(reservation.getId()));
    }

    @Test
    void isIntervalConflicting() {
        Reservation r1 = buildReservation();
        getRepository().create(r1);

        assertTrue(getRepository().isIntervalConflicting(r1.getCourt().getId(), r1.getHeldAt(), r1.getHeldUntil()));
        assertFalse(getRepository().isIntervalConflicting(r1.getCourt().getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }


    @Test
    void create() {
        assertThrows(ConstraintViolationException.class, () -> getRepository().create(
                Reservation.builder().build()
        ));

        assertThrows(ConstraintViolationException.class, () -> getRepository().create(
                Reservation.builder()
                        .heldAt(LocalDateTime.now())
                        .build()
        ));

        assertThrows(ConstraintViolationException.class, () -> getRepository().create(
                Reservation.builder()
                        .heldAt(LocalDateTime.now())
                        .heldUntil(LocalDateTime.now())
                        .build()
        ));

        assertThrows(PersistenceException.class, () -> getRepository().create(
                Reservation.builder()
                        .heldAt(LocalDateTime.now())
                        .heldUntil(LocalDateTime.now())
                        .gameType(GameType.SINGLES)
                        .build()
        ));

        assertThrows(PersistenceException.class, () -> getRepository().create(
                Reservation.builder()
                        .heldAt(LocalDateTime.now())
                        .heldUntil(LocalDateTime.now())
                        .gameType(GameType.SINGLES)
                        .user(user)
                        .build()
        ));

        Reservation reservation = buildReservation();
        getRepository().create(reservation);
        assertNotNull(reservation.getId());
        assertNull(reservation.getDeletedAt());
    }

    @Test
    void update() {
        Reservation reservation = buildReservation();
        getRepository().create(reservation);

    }

    private ReservationH2Repository getRepository() {
        return new ReservationH2Repository(entityManager.getEntityManager());
    }

    private Reservation buildReservation() {
        return Reservation.builder()
                .heldAt(LocalDateTime.now())
                .heldUntil(LocalDateTime.now().plusHours(5))
                .gameType(GameType.SINGLES)
                .user(user)
                .court(court)
                .build();
    }
}