package mate.academy.rickandmorty.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalApiResponse {
    private List<ExternalCharacterDto> results;
}
