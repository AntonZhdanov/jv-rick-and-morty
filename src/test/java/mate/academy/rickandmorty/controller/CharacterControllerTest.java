package mate.academy.rickandmorty.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;
import java.util.List;
import mate.academy.rickandmorty.dto.CharacterResponseDto;
import mate.academy.rickandmorty.exception.CharacterNotFoundException;
import mate.academy.rickandmorty.exception.ExternalApiException;
import mate.academy.rickandmorty.service.InternalCharacterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CharacterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InternalCharacterService internalCharacterService;

    @Test
    @DisplayName("Test that getRandomCharacter returns a character from the service")
    void testGetRandomCharacter() throws Exception {
        CharacterResponseDto mockCharacterResponse = new CharacterResponseDto();
        mockCharacterResponse.setName("Rick Sanchez");
        when(internalCharacterService.getRandomCharacter()).thenReturn(mockCharacterResponse);

        mockMvc.perform(get("/api/characters/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rick Sanchez"));
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns a list of characters")
    void testSearchCharacterByName() throws Exception {
        CharacterResponseDto mockCharacterResponse1 = new CharacterResponseDto();
        mockCharacterResponse1.setName("Rick Sanchez");
        CharacterResponseDto mockCharacterResponse2 = new CharacterResponseDto();
        mockCharacterResponse2.setName("Morty Smith");

        when(internalCharacterService.searchCharacterByName("Rick"))
                .thenReturn(List.of(mockCharacterResponse1, mockCharacterResponse2));

        mockMvc.perform(get("/api/characters/search?name=Rick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rick Sanchez"))
                .andExpect(jsonPath("$[1].name").value("Morty Smith"));
    }

    @Test
    @DisplayName("Test that getRandomCharacter throws exception when no characters exist")
    void testGetRandomCharacterThrowsExceptionWhenNoCharactersExist() throws Exception {
        when(internalCharacterService.getRandomCharacter())
                .thenThrow(new CharacterNotFoundException("No characters found"));

        mockMvc.perform(get("/api/characters/random"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No characters found"));
    }


    @Test
    @DisplayName("Test that searchCharacterByName returns an empty list when no matches are found")
    void testSearchCharacterByNameReturnsEmptyListWhenNoMatchesFound() throws Exception {
        when(internalCharacterService.searchCharacterByName("Alien"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/characters/search?name=Alien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns correct characters when name contains mixed case")
    void testSearchCharacterByNameReturnsCorrectCharactersWithCaseInsensitiveSearch() throws Exception {
        CharacterResponseDto mockCharacterResponse = new CharacterResponseDto();
        mockCharacterResponse.setName("Rick Sanchez");

        when(internalCharacterService.searchCharacterByName("rick"))
                .thenReturn(List.of(mockCharacterResponse));

        mockMvc.perform(get("/api/characters/search?name=rick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rick Sanchez"));
    }

    @Test
    @DisplayName("Test that getRandomCharacter returns a random character from the service")
    void testGetRandomCharacterReturnsRandomCharacter() throws Exception {
        CharacterResponseDto mockCharacterResponse = new CharacterResponseDto();
        mockCharacterResponse.setName("Morty Smith");
        when(internalCharacterService.getRandomCharacter()).thenReturn(mockCharacterResponse);

        mockMvc.perform(get("/api/characters/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morty Smith"));
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns 400 Bad Request when name parameter is missing")
    void testSearchCharacterByNameReturnsBadRequestWhenNameParameterIsMissing() throws Exception {
        mockMvc.perform(get("/api/characters/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing required parameter: name"));
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns 500 Internal Server Error when an external API fails")
    void testSearchCharacterByNameReturnsInternalServerErrorWhenApiFails() throws Exception {
        when(internalCharacterService.searchCharacterByName("Rick"))
                .thenThrow(new ExternalApiException("External API error"));

        mockMvc.perform(get("/api/characters/search?name=Rick"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("External API error"));
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns 400 Bad Request for invalid name format")
    void testSearchCharacterByNameReturnsBadRequestForInvalidName() throws Exception {
        mockMvc.perform(get("/api/characters/search?name=123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid name format"));
    }

    @Test
    @DisplayName("Test that searchCharacterByName returns empty "
            + "list when no characters are found for a different query")
    void testSearchCharacterByNameReturnsEmptyListForNoMatch() throws Exception {
        when(internalCharacterService.searchCharacterByName("NoMatch"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/characters/search?name=NoMatch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}