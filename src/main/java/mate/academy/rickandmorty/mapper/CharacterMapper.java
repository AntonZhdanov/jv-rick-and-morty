package mate.academy.rickandmorty.mapper;

import java.util.Arrays;
import mate.academy.rickandmorty.dto.CharacterResponseDto;
import mate.academy.rickandmorty.dto.ExternalCharacterDto;
import mate.academy.rickandmorty.entity.Character;
import mate.academy.rickandmorty.entity.Gender;
import mate.academy.rickandmorty.entity.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CharacterMapper {
    @Mapping(source = "externalId", target = "externalId")
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(source = "gender", target = "gender", qualifiedByName = "stringToGender")
    CharacterResponseDto toDto(Character character);

    @Mapping(source = "id", target = "externalId")
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(source = "gender", target = "gender", qualifiedByName = "stringToGender")
    Character toEntity(ExternalCharacterDto dto);

    @Named("stringToStatus")
    default Status stringToStatus(String status) {
        return Arrays.stream(Status.values())
                .filter(s -> s.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .orElse(Status.UNKNOWN);
    }

    @Named("stringToGender")
    default Gender stringToGender(String gender) {
        return Arrays.stream(Gender.values())
                .filter(g -> g.getGender().equalsIgnoreCase(gender))
                .findFirst()
                .orElse(Gender.UNKNOWN);
    }
}
