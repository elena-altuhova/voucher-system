package io.github.elenaaltuhova.vouchersystem.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignResponseDTO extends RepresentationModel<CampaignResponseDTO> {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}
