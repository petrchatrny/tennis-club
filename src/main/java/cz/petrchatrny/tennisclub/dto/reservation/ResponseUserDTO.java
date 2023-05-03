package cz.petrchatrny.tennisclub.dto.reservation;

/**
 * Response form of User
 *
 * @param phoneNumber of user
 * @param fullName    of user
 */
public record ResponseUserDTO(
        String phoneNumber,
        String fullName
) {
}