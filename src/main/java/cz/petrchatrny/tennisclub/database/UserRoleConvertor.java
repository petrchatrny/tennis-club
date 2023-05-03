package cz.petrchatrny.tennisclub.database;

import cz.petrchatrny.tennisclub.model.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConvertor implements AttributeConverter<UserRole, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserRole attribute) {
        return attribute.getRepresentation();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer dbData) {
        for (UserRole r : UserRole.values()) {
            if (r.getRepresentation() == dbData) {
                return r;
            }
        }

        return null;
    }
}
