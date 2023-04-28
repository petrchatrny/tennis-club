package cz.petrchatrny.tennisclub.repository.user;

import cz.petrchatrny.tennisclub.model.User;
import cz.petrchatrny.tennisclub.repository.ICRUDSoftDeleteRepository;

/**
 * Interface for users manipulation.
 *
 * @see ICRUDSoftDeleteRepository
 */
public interface IUserRepository extends ICRUDSoftDeleteRepository<User, Long> {
    /**
     * Gets one user by their unique phone number.
     *
     * @param phoneNumber user's unique phone number
     * @return user entity
     */
    User getOne(String phoneNumber);
}
