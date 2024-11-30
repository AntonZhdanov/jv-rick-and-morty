package mate.academy.rickandmorty.service.impl;

import mate.academy.rickandmorty.dto.ExternalApiResponse;
import mate.academy.rickandmorty.dto.ExternalCharacterDto;
import java.util.List;
import mate.academy.rickandmorty.entity.Character;
import mate.academy.rickandmorty.exception.ExternalApiException;
import mate.academy.rickandmorty.mapper.CharacterMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ExternalCharacterServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CharacterMapper characterMapper;

    @InjectMocks
    private ExternalCharacterServiceImpl externalCharacterService;

    @DisplayName("Should return list of characters when API call is successful")
    @Test
    void fetchAllCharactersFromApi_shouldReturnListOfCharacters_whenApiCallIsSuccessful() {
        ExternalApiResponse mockResponse = new ExternalApiResponse();
        ExternalCharacterDto dto1 =
                new ExternalCharacterDto(1L, "Rick Sanchez", "Alive", "Male");
        ExternalCharacterDto dto2 =
                new ExternalCharacterDto(2L, "Morty Smith", "Alive", "Male");
        mockResponse.setResults(List.of(dto1, dto2));

        Character character1 = new Character(1L, 1L, "Rick Sanchez", "Alive", "Male");
        Character character2 = new Character(2L, 2L, "Morty Smith", "Alive", "Male");

        Mockito.when(restTemplate.exchange(
                        Mockito.eq("https://rickandmortyapi.com/api/character"),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.isNull(),
                        Mockito.eq(ExternalApiResponse.class)
                ))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        Mockito.when(characterMapper.toEntity(dto1)).thenReturn(character1);
        Mockito.when(characterMapper.toEntity(dto2)).thenReturn(character2);

        List<Character> result = externalCharacterService.fetchAllCharactersFromApi();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(character1));
        Assertions.assertTrue(result.contains(character2));
    }

    @DisplayName("Should return an empty list when API response contains no characters")
    @Test
    void fetchAllCharactersFromApi_shouldReturnEmptyList_whenApiResponseIsEmpty() {
        // Arrange
        ExternalApiResponse mockResponse = new ExternalApiResponse();
        mockResponse.setResults(List.of()); // Порожній список

        Mockito.when(restTemplate.exchange(
                Mockito.eq("https://rickandmortyapi.com/api/character"),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.eq(ExternalApiResponse.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        List<Character> result = externalCharacterService.fetchAllCharactersFromApi();

        Assertions.assertTrue(result.isEmpty(), "Expected empty list but got non-empty list.");
    }

    @DisplayName("Should throw ExternalApiException when API response body is null")
    @Test
    void fetchAllCharactersFromApi_shouldThrowExternalApiException_whenApiResponseBodyIsNull() {
        Mockito.when(restTemplate.exchange(
                Mockito.eq("https://rickandmortyapi.com/api/character"),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.eq(ExternalApiResponse.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        ExternalApiException exception = Assertions.assertThrows(
                ExternalApiException.class,
                () -> externalCharacterService.fetchAllCharactersFromApi()
        );

        Assertions.assertEquals("Failed to fetch data from external API.", exception.getMessage());
    }

    @DisplayName("Should throw ExternalApiException when API returns non-200 status")
    @Test
    void fetchAllCharactersFromApi_shouldThrowExternalApiException_whenApiReturnsNon200Status() {
        Mockito.when(restTemplate.exchange(
                Mockito.eq("https://rickandmortyapi.com/api/character"),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.eq(ExternalApiResponse.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        ExternalApiException exception = Assertions.assertThrows(
                ExternalApiException.class,
                () -> externalCharacterService.fetchAllCharactersFromApi()
        );

        Assertions.assertEquals("Failed to fetch data from external API.", exception.getMessage());
    }

    @DisplayName("Should throw ExternalApiException when API returns 404 status")
    @Test
    void fetchAllCharactersFromApi_shouldThrowExternalApiException_whenApiReturns404Status() {
        Mockito.when(restTemplate.exchange(
                Mockito.eq("https://rickandmortyapi.com/api/character"),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.eq(ExternalApiResponse.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        ExternalApiException exception = Assertions.assertThrows(
                ExternalApiException.class,
                () -> externalCharacterService.fetchAllCharactersFromApi()
        );

        Assertions.assertTrue(exception.getMessage().contains("Failed to fetch data from external API."));

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), 404);
    }
}
