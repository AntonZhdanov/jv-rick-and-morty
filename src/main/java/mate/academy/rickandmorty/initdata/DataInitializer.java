package mate.academy.rickandmorty.initdata;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.rickandmorty.entity.Character;
import mate.academy.rickandmorty.service.ExternalCharacterService;
import mate.academy.rickandmorty.service.InternalCharacterService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final ExternalCharacterService externalCharacterService;
    private final InternalCharacterService internalCharacterService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        if (internalCharacterService.getAllCharacters().isEmpty()) {
            List<Character> characters = externalCharacterService.fetchAllCharactersFromApi();
            internalCharacterService.saveAllCharacters(characters);
        }
    }
}
