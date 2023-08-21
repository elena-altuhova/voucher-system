package io.github.elenaaltuhova.vouchersystem.service;

import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import io.github.elenaaltuhova.vouchersystem.exception.CampaignExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.NoValidVouchersAvailableException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherAlreadyRedeemedException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherNotValidException;
import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import io.github.elenaaltuhova.vouchersystem.model.Voucher;
import io.github.elenaaltuhova.vouchersystem.repository.CampaignRepository;
import io.github.elenaaltuhova.vouchersystem.repository.StatusRepository;
import io.github.elenaaltuhova.vouchersystem.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus.ISSUED;
import static io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus.REDEEMED;

@Service
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    StatusRepository statusRepository;

    /**
     * @see VoucherService#check(String)
     */
    @Override
    public VoucherResponseDTO check(String code) throws VoucherExpiredException, VoucherAlreadyRedeemedException, VoucherNotValidException {
        Voucher foundVoucher = voucherRepository.findByCode(UUID.fromString(code));

        checkIfValid(foundVoucher);

        return foundVoucher.convertEntityToResponseDTO();
    }

    /**
     * @see VoucherService#redeem(Long)
     */
    @Override
    public VoucherResponseDTO redeem(Long voucherId) throws VoucherNotValidException, VoucherAlreadyRedeemedException, VoucherExpiredException {
        Optional<Voucher> redeemedVoucher = voucherRepository.findById(voucherId);

        checkIfValid(redeemedVoucher.get());

        voucherRepository.updateStatusById(voucherId, statusRepository.findByStatusName(REDEEMED));

        return voucherRepository.findById(voucherId).get().convertEntityToResponseDTO();
    }

    private void checkIfValid(Voucher voucher) throws VoucherNotValidException, VoucherExpiredException, VoucherAlreadyRedeemedException {
        if (voucher == null) {
            throw new VoucherNotValidException("Voucher not valid.");
        }
        LocalDate currentDate = LocalDate.now();
        Optional<LocalDate> expirationDate = Optional.ofNullable(voucher.getCampaign().getEndDate());

        if (currentDate.isBefore(voucher.getCampaign().getStartDate())) {
            throw new VoucherNotValidException("Voucher not valid.");
        }

        if (expirationDate.isPresent()) {
            if (currentDate.isAfter(expirationDate.get())) {
                throw new VoucherExpiredException("Voucher is expired.");
            }
        }

        if (voucher.getStatus().getStatusName() == REDEEMED) {
            throw new VoucherAlreadyRedeemedException("Voucher is already redeemed.");
        }
    }

    /**
     * @see VoucherService#findValidVoucherforACampaign(Long)
     */
    @Override
    public VoucherResponseDTO findValidVoucherforACampaign(Long campaignId) throws CampaignExpiredException, NoValidVouchersAvailableException {
        Optional<Campaign> foundCampaign = campaignRepository.findById(campaignId);

        if (!foundCampaign.isPresent()) {
            throw new NoSuchElementException(String.format("There is no campaign with ID=%d", campaignId));
        }

        LocalDate currentDate = LocalDate.now();
        Optional<LocalDate> expirationDate = Optional.ofNullable(foundCampaign.get().getEndDate());

        if (expirationDate.isPresent()) {
            if (currentDate.isAfter(expirationDate.get())) {
                throw new CampaignExpiredException("Campaign has already expired.");
            }
        }

        Optional<Voucher>
            validVoucher =
            voucherRepository.findFirstByCampaignAndStatus(foundCampaign.get(), statusRepository.findByStatusName(ISSUED));

        if (!validVoucher.isPresent()) {
            throw new NoValidVouchersAvailableException("There are no available vouchers available for this campaign.");
        }

        return validVoucher.get().convertEntityToResponseDTO();
    }
}
