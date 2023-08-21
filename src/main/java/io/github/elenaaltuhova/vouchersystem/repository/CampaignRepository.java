package io.github.elenaaltuhova.vouchersystem.repository;

import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface that implements the Campaign Repository, with JPA CRUD methods.
 *
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    /**
     * Method to search all Campaigns by Campaign title.
     *
     * @param campaignTitle
     * @return List<Optional<Campaign>>
     */
    Campaign findByTitle(String campaignTitle);
}
