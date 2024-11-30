package mate.academy.rickandmorty.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mate.academy.rickandmorty.dto.ExternalApiResponse;
import mate.academy.rickandmorty.entity.Character;
import mate.academy.rickandmorty.exception.ExternalApiException;
import mate.academy.rickandmorty.mapper.CharacterMapper;
import mate.academy.rickandmorty.service.ExternalCharacterService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalCharacterServiceImpl implements ExternalCharacterService {
    private static final String RICK_AND_MORTY_URL = "https://rickandmortyapi.com/api/character";
    private final RestTemplate restTemplate;
    private final CharacterMapper characterMapper;

    @Override
    public List<Character> fetchAllCharactersFromApi() {
        try {
            ResponseEntity<ExternalApiResponse> response =
                    restTemplate.exchange(RICK_AND_MORTY_URL, HttpMethod.GET,
                            null, ExternalApiResponse.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Failed to fetch data from external API. Status code: {}",
                        response.getStatusCode());
                throw new ExternalApiException("Failed to fetch data from external API.");
            }

            ExternalApiResponse responseBody = response.getBody();
            if (responseBody == null) {
                log.error("Failed to fetch data from external API. Response body is null.");
                throw new ExternalApiException("Failed to fetch data from external API.");
            }

            return responseBody.getResults()
                    .stream()
                    .map(characterMapper::toEntity)
                    .toList();
        } catch (RestClientException e) {
            log.error("Error occurred while fetching data from API: {}", e.getMessage(), e);
            throw new ExternalApiException("Failed to fetch data from external API.");
        }
    }
}
