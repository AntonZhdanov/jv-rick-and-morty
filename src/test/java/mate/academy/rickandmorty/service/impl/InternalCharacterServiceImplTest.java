package mate.academy.rickandmorty.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.rickandmorty.dto.CharacterResponseDto;
import mate.academy.rickandmorty.entity.Character;
import mate.academy.rickandmorty.exception.CharacterNotFoundException;
import mate.academy.rickandmorty.mapper.CharacterMapper;
import mate.academy.rickandmorty.repository.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InternalCharacterServiceImplTest {
    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private CharacterMapper characterMapper;

    @InjectMocks
    private InternalCharacterServiceImpl internalCharacterService;

    private Character character;
    private CharacterResponseDto characterResponseDto;

    @BeforeEach
    void setUp() {
        character = new Character(1L, 1L,
                "Rick Sanchez", "A mad scientist", "rick-sanchez-id");

        characterResponseDto = new CharacterResponseDto();
        characterResponseDto.setName("Rick Sanchez");
        characterResponseDto.setStatus("A mad scientist");
    }

    @Test
    @DisplayName("Test that getRandomCharacter throws exception when no characters are found")
    void testGetRandomCharacterThrowsExceptionWhenNoCharacters() {
        when(characterRepository.findAll()).thenReturn(List.of());

        assertThrows(CharacterNotFoundException.class, () -> internalCharacterService.getRandomCharacter());
    }

    @Test
    @DisplayName("Test that getRandomCharacter returns a random character when characters are present")
    void testGetRandomCharacterReturnsRandomCharacter() {
        when(characterRepository.findAll()).thenReturn(List.of(character));
        when(characterMapper.toDto(character)).thenReturn(characterResponseDto);

        CharacterResponseDto result = internalCharacterService.getRandomCharacter();

        assertNotNull(result);
        assertEquals("Rick Sanchez", result.getName());
        assertEquals("A mad scientist", result.getStatus());
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns a list of characters that match the name")
    void testSearchCharacterByName() {
        when(characterRepository.findByNameContainingIgnoreCase("Rick")).thenReturn(List.of(character));
        when(characterMapper.toDto(character)).thenReturn(characterResponseDto);

        List<CharacterResponseDto> result = internalCharacterService.searchCharacterByName("Rick");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Rick Sanchez", result.get(0).getName());
    }

    @Test
    @DisplayName("Test that getAllCharacters returns a list of all characters")
    void testGetAllCharacters() {
        when(characterRepository.findAll()).thenReturn(List.of(character));
        when(characterMapper.toDto(character)).thenReturn(characterResponseDto);

        List<CharacterResponseDto> result = internalCharacterService.getAllCharacters();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test that saveAllCharacters saves only new characters")
    void testSaveAllCharacters() {
        List<Character> charactersToSave = List.of(character);
        when(characterRepository.findByExternalId(character.getExternalId())).thenReturn(Optional.empty());

        internalCharacterService.saveAllCharacters(charactersToSave);

        verify(characterRepository, times(1)).saveAll(charactersToSave);
    }

    @Test
    @DisplayName("Test that saveAllCharacters does not save already existing characters")
    void testSaveAllCharactersDoesNotSaveExisting() {
        List<Character> charactersToSave = List.of(character);
        when(characterRepository.findByExternalId(character.getExternalId())).thenReturn(Optional.of(character));

        internalCharacterService.saveAllCharacters(charactersToSave);

        verify(characterRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns an empty list when no characters match the name")
    void testSearchCharacterByNameNoMatch() {
        when(characterRepository.findByNameContainingIgnoreCase("Morty")).thenReturn(List.of());

        List<CharacterResponseDto> result = internalCharacterService.searchCharacterByName("Morty");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test that getAllCharacters returns an empty list when no characters are found")
    void testGetAllCharactersWhenNoCharactersFound() {
        when(characterRepository.findAll()).thenReturn(List.of());

        List<CharacterResponseDto> result = internalCharacterService.getAllCharacters();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test that Character is correctly mapped to CharacterResponseDto")
    void testCharacterToCharacterResponseDtoMapping() {
        when(characterMapper.toDto(character)).thenReturn(characterResponseDto);

        CharacterResponseDto result = characterMapper.toDto(character);

        assertNotNull(result);
        assertEquals("Rick Sanchez", result.getName());
        assertEquals("A mad scientist", result.getStatus());
    }

    @Test
    @DisplayName("Test that saveAllCharacters does not save characters with the same externalId")
    void testSaveAllCharactersDoesNotSaveExistingByExternalId() {
        List<Character> charactersToSave = List.of(character);
        when(characterRepository.findByExternalId(character.getExternalId())).thenReturn(Optional.of(character));

        internalCharacterService.saveAllCharacters(charactersToSave);

        verify(characterRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Test that saveAllCharacters saves new characters correctly")
    void testSaveAllCharactersSavesNewCharacters() {
        Character newCharacter = new Character(2L, 2L,
                "Morty Smith", "A confused teenager", "morty-smith-id");
        List<Character> charactersToSave = List.of(newCharacter);
        when(characterRepository.findByExternalId(newCharacter.getExternalId())).thenReturn(Optional.empty());

        internalCharacterService.saveAllCharacters(charactersToSave);

        verify(characterRepository, times(1)).saveAll(charactersToSave);
    }
}
