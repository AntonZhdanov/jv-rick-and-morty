package mate.academy.rickandmorty.service.impl;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import mate.academy.rickandmorty.dto.CharacterResponseDto;
import mate.academy.rickandmorty.entity.Character;
import mate.academy.rickandmorty.exception.CharacterNotFoundException;
import mate.academy.rickandmorty.mapper.CharacterMapper;
import mate.academy.rickandmorty.repository.CharacterRepository;
import mate.academy.rickandmorty.service.InternalCharacterService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalCharacterServiceImpl implements InternalCharacterService {
    private final CharacterRepository characterRepository;
    private final CharacterMapper characterMapper;

    @Override
    public CharacterResponseDto getRandomCharacter() {
        List<Character> characters = characterRepository.findAll();
        if (characters.isEmpty()) {
            throw new CharacterNotFoundException("No characters found in the database.");
        }
        Character randomCharacter = characters.get(new Random().nextInt(characters.size()));

        return characterMapper.toDto(randomCharacter);
    }

    @Override
    public List<CharacterResponseDto> searchCharacterByName(String name) {
        return characterRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(characterMapper::toDto)
                .toList();
    }

    @Override
    public List<CharacterResponseDto> getAllCharacters() {
        return characterRepository.findAll()
                .stream()
                .map(characterMapper::toDto)
                .toList();
    }

    @Override
    public void saveAllCharacters(List<Character> characters) {
        List<Character> newCharacters = characters
                .stream()
                .filter(character ->
                        characterRepository.findByExternalId(character.getExternalId()).isEmpty())
                .toList();

        if (!newCharacters.isEmpty()) {
            characterRepository.saveAll(newCharacters);
        }
    }
}
