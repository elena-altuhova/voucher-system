package io.github.elenaaltuhova.vouchersystem.repository;

import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CampaignRepositoryTest extends BaseRepositoryClass{

    @Autowired
    private CampaignRepository campaignRepository;

    @BeforeAll
    public void setupDB() {
        List<Campaign> campaigns = new ArrayList<>();
        campaigns.add(new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null));
        campaigns.add(new Campaign(2L, "Test Campaign 2", LocalDate.parse("2023-05-05"), null));
        campaignRepository.saveAll(campaigns);
    }

    @Test
    @DisplayName("Successfully find campaign by title")
    public void findCampaignByTitle() {
        //When
        Campaign campaign = campaignRepository.findByTitle("Test Campaign");

        //Then
        assertThat(campaign, notNullValue());
        assertThat(campaign.getId(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("Null is returned when campaign not found")
    public void nullReturnedWhenNoCampaignFound() {
        //When
        Campaign campaign = campaignRepository.findByTitle("Test Campaign 3");

        //Then
        assertThat(null, is(equalTo(campaign)));
    }
}
