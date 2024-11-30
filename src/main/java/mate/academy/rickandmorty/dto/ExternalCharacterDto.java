package mate.academy.rickandmorty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCharacterDto {
    private Long id;
    private String name;
    private String status;
    private String gender;
}
