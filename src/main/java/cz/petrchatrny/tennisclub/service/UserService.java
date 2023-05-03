package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.error.exception.ResourceAlreadyExistsException;
import cz.petrchatrny.tennisclub.model.User;
import cz.petrchatrny.tennisclub.model.UserRole;
import cz.petrchatrny.tennisclub.repository.user.IUserRepository;
import cz.petrchatrny.tennisclub.service.smsservice.ISmsService;
import cz.petrchatrny.tennisclub.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Service for manipulation with User entity.
 *
 * @see IUserRepository
 * @see ISmsService
 */
@Service
public class UserService {
    private final IUserRepository userRepository;
    private final ISmsService smsService;

    /**
     * @param userRepository injected repository
     * @param smsService     injected service
     */
    public UserService(IUserRepository userRepository, ISmsService smsService) {
        this.userRepository = userRepository;
        this.smsService = smsService;
    }

    /**
     * Gets collection of all users that are not deleted.
     *
     * @return collection of users
     */
    public Collection<User> getUsers() {
        return userRepository.getAll();
    }

    /**
     * Gets 1 user by unique phone number. If no user is found, returns null.
     *
     * @param phoneNumber unique phone number
     * @return user or null
     */
    public User getUser(String phoneNumber) {
        return userRepository.getOne(phoneNumber);
    }

    /**
     * Creates new user from provided params.
     *
     * <p>
     * Checks that the specified phone number is not in use.
     * If the number is OK, it generates a password and salt for the user and adds the user to the database.
     * The credentials are then sent to the user via SMS.
     * </p>
     *
     * @param phoneNumber provided phone number
     * @param fullName    provided full name
     * @return created user
     * @throws ResourceAlreadyExistsException if phone number is already in use
     */
    public User createUser(String phoneNumber, String fullName) throws ResourceAlreadyExistsException {
        // check already used phone number
        if (getUser(phoneNumber) != null) {
            throw new ResourceAlreadyExistsException("user with provided phone number already exists", null);
        }

        // create password
        String salt = SecurityUtil.generateRandomSalt(15);
        String password = SecurityUtil.generateRandomPassword(15);
        String hashedPassword = SecurityUtil.hash(password, salt);

        // create user
        User user = User.builder()
                .phoneNumber(phoneNumber)
                .fullName(fullName)
                .password(hashedPassword)
                .salt(salt)
                .role(UserRole.USER)
                .build();
        userRepository.create(user);

        // send SMS
        smsService.sendRegistrationMessage(phoneNumber, password);

        // return output
        return user;
    }

    /**
     * Authenticates user
     *
     * <p>
     * Finds the user by their phone number and if found, it takes their salt,
     * hashes the password and compares the entered password with the
     * user's saved password. If passwords match it returns user otherwise it returns null.
     * </p>
     *
     * @param phoneNumber unique phone number
     * @param password    plain text password
     * @return authenticated user or null
     */
    public User authenticateUser(String phoneNumber, String password) {
        User user = userRepository.getOne(phoneNumber);

        if (user != null && !SecurityUtil.hash(password, user.getSalt()).equals(user.getPassword())) {
            user = null;
        }

        return user;
    }
}
