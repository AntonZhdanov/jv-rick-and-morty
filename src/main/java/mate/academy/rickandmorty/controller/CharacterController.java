package mate.academy.rickandmorty.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.rickandmorty.dto.CharacterResponseDto;
import mate.academy.rickandmorty.service.InternalCharacterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/characters")
public class CharacterController {
    private final InternalCharacterService internalCharacterService;

    @Operation(summary = "Get a random character",
            description = "Returns a randomly selected character from the database, "
            + "including all available details such as: id, name, gender, and status")
    @GetMapping("/random")
    public CharacterResponseDto getRandomCharacter() {
        return internalCharacterService.getRandomCharacter();
    }

    @Operation(summary = "Search for characters by name",
            description = "Retrieves a list of characters whose names contain "
                    + "the provided search string.")
    @GetMapping("/search")
    public List<CharacterResponseDto> searchCharacterByName(@RequestParam String name) {
        if (!name.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Invalid name format");
        }

        return internalCharacterService.searchCharacterByName(name);
    }
}
