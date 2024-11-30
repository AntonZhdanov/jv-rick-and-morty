package mate.academy.rickandmorty.service;

import java.util.List;
import mate.academy.rickandmorty.dto.CharacterResponseDto;
import mate.academy.rickandmorty.entity.Character;

public interface InternalCharacterService {
    CharacterResponseDto getRandomCharacter();

    List<CharacterResponseDto> searchCharacterByName(String name);

    List<CharacterResponseDto> getAllCharacters();

    void saveAllCharacters(List<Character> characters);
}
