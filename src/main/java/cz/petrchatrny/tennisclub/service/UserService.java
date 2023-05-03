package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.error.exception.ResourceAlreadyExistsException;
import cz.petrchatrny.tennisclub.model.User;
import cz.petrchatrny.tennisclub.model.UserRole;
import cz.petrchatrny.tennisclub.repository.user.IUserRepository;
import cz.petrchatrny.tennisclub.service.smsservice.ISmsService;
import cz.petrchatrny.tennisclub.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserService {
    private final IUserRepository userRepository;
    private final ISmsService smsService;

    public UserService(IUserRepository userRepository, ISmsService smsService) {
        this.userRepository = userRepository;
        this.smsService = smsService;
    }

    public Collection<User> getUsers() {
        return userRepository.getAll();
    }

    public User getUser(String phoneNumber) {
        return userRepository.getOne(phoneNumber);
    }

    public User createUser(String phoneNumber, String fullName) {
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

    public User authenticateUser(String phoneNumber, String password) {
        User user = userRepository.getOne(phoneNumber);

        if (user != null && !SecurityUtil.hash(password, user.getSalt()).equals(user.getPassword())) {
            user = null;
        }

        return user;
    }
}
