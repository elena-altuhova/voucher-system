package io.github.elenaaltuhova.vouchersystem.model;

import io.github.elenaaltuhova.vouchersystem.dto.CampaignResponseDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
/**
 * Class that implements Campaign entity.
 *
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;
    @Column(name = "start_date")
    @NotNull
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Method to convert a campaign to a CampaignResponseDTO entity.
     *
     * @return a <code>CampaignResponseDTO</code> object
     */
    public CampaignResponseDTO convertEntityToDTO() {
        CampaignResponseDTO dto = new CampaignResponseDTO();
        dto.setId(this.getId());
        dto.setTitle(this.getTitle());
        dto.setStartDate(this.getStartDate());
        dto.setEndDate(this.getEndDate());
        return dto;
    }
}
