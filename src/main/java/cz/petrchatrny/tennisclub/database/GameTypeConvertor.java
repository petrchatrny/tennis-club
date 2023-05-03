package cz.petrchatrny.tennisclub.database;

import cz.petrchatrny.tennisclub.model.GameType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GameTypeConvertor implements AttributeConverter<GameType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GameType attribute) {
        return attribute.getRepresentation();
    }

    @Override
    public GameType convertToEntityAttribute(Integer dbData) {
        for (GameType gt : GameType.values()) {
            if (gt.getRepresentation() == dbData) {
                return gt;
            }
        }

        return null;
    }
}
