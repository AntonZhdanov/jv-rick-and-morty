package mate.academy.rickandmorty.service;

import java.util.List;
import mate.academy.rickandmorty.entity.Character;

public interface ExternalCharacterService {
    List<Character> fetchAllCharactersFromApi();
}
