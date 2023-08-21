package io.github.elenaaltuhova.vouchersystem.service;

import io.github.elenaaltuhova.vouchersystem.dto.CampaignDTO;
import io.github.elenaaltuhova.vouchersystem.dto.CampaignResponseDTO;
import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import io.github.elenaaltuhova.vouchersystem.exception.CampaignAlreadyExistsException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherCreationLimitException;

import java.util.List;

/**
 * Interface that provides methods for manipulating Campaign entities.
 */
public interface CampaignService {
    /**
     * Method that adds new marketing campaign into the database.
     *
     * @param campaignDTO
     * @return <code>CampaignResponseDTO</code> object
     */
    CampaignResponseDTO create(CampaignDTO campaignDTO) throws CampaignAlreadyExistsException;

    /**
     * Method that searches for a Campaign by id.
     *
     * @param id
     * @return <code>CampaignResponseDTO</code> object
     */
    CampaignResponseDTO findById(Long id);

    /**
     * Method that adds vouchers for a selected campaign.
     *
     * @param campaignId
     * @param count
     * @return <code>CampaignResponseDTO</code> object
     */
    List<VoucherResponseDTO> createVouchers(Long campaignId, int count) throws VoucherCreationLimitException;
}
