package io.github.elenaaltuhova.vouchersystem.service;

import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import io.github.elenaaltuhova.vouchersystem.exception.CampaignExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.NoValidVouchersAvailableException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherAlreadyRedeemedException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherNotValidException;

/**
 * Interface that provides methods for manipulating Voucher entities.
 */
public interface VoucherService {

    /**
     * Method that checks Voucher status.
     *
     * @param code
     * @return <code>VoucherResponseDTO</code> object
     */
    VoucherResponseDTO check(String code) throws VoucherExpiredException, VoucherAlreadyRedeemedException, VoucherNotValidException;

    /**
     * Method that redeems Voucher based on voucher id.
     *
     * @param voucherId
     * @return <code>VoucherResponseDTO</code> object
     */
    VoucherResponseDTO redeem(Long voucherId) throws VoucherNotValidException, VoucherAlreadyRedeemedException, VoucherExpiredException;

    /**
     * Method that returns valid Voucher for campaign id.
     *
     * @param campaignId
     * @return <code>VoucherResponseDTO</code> object
     */
    VoucherResponseDTO sendValidVoucherForACampaign(Long campaignId) throws CampaignExpiredException, NoValidVouchersAvailableException;
}
