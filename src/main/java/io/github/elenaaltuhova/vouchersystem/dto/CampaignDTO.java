package io.github.elenaaltuhova.vouchersystem.dto;

import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignDTO extends RepresentationModel<CampaignDTO> {
    @NotNull(message = "Campaign title cannot be null.")
    @Length(min=5, max=50, message="Title should be in between 5 and 50 symbols long.")
    private String title;

    @NotNull(message = "Start date of campaign cannot be null.")
    private LocalDate startDate;

    @Future(message = "End date should be in the future.")
    private LocalDate endDate;

    /**
     * Method to convert a CampaignDTO to a campaign entity.
     *
     * @return a <code>Campaign</code> object
     */
    public Campaign convertDTOtoEntity() {
        Campaign campaign = new Campaign();
        campaign.setTitle(this.getTitle());
        campaign.setStartDate(this.getStartDate());
        campaign.setEndDate(this.getEndDate());
        return campaign;
    }
}
