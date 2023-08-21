package io.github.elenaaltuhova.vouchersystem.service;

import io.github.elenaaltuhova.vouchersystem.dto.CampaignDTO;
import io.github.elenaaltuhova.vouchersystem.dto.CampaignResponseDTO;
import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus;
import io.github.elenaaltuhova.vouchersystem.exception.CampaignAlreadyExistsException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherCreationLimitException;
import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import io.github.elenaaltuhova.vouchersystem.model.Status;
import io.github.elenaaltuhova.vouchersystem.model.Voucher;
import io.github.elenaaltuhova.vouchersystem.repository.CampaignRepository;
import io.github.elenaaltuhova.vouchersystem.repository.StatusRepository;
import io.github.elenaaltuhova.vouchersystem.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    VoucherCodesGeneratorService voucherCodesGeneratorService;

    @Value(value = "${voucher.creation.limit}")
    int voucherCreationLimit;

    /**
     * @see CampaignService#create(CampaignDTO)
     */
    @Override
    public CampaignResponseDTO create(CampaignDTO campaignDTO) throws CampaignAlreadyExistsException {

        if (campaignRepository.findByTitle(campaignDTO.getTitle()) != null) {
            throw new CampaignAlreadyExistsException(String.format("Marketing campaign with '%s' name already exists", campaignDTO.getTitle()));
        }

        Campaign createdCampaign = campaignRepository.save(campaignDTO.convertDTOtoEntity());
        return createdCampaign.convertEntityToDTO();
    }

    /**
     * @see CampaignService#findById(Long)
     */
    @Override
    public CampaignResponseDTO findById(Long id) {
        Optional<Campaign> foundCampaign = campaignRepository.findById(id);

        if (!foundCampaign.isPresent()) {
            throw new NoSuchElementException(String.format("There is no campaign with ID=%d", id));
        }

        return foundCampaign.get().convertEntityToDTO();
    }

    /**
     * @see CampaignService#createVouchers(Long, int)
     */
    @Override
    public List<VoucherResponseDTO> createVouchers(Long campaignId, int count) throws VoucherCreationLimitException {
        List<UUID> codes = voucherCodesGeneratorService.generateUUIDCodes(count);
        Optional<Campaign> foundCampaign = campaignRepository.findById(campaignId);
        Status status = statusRepository.findByStatusName(VoucherStatus.ISSUED);
        List<Voucher> vouchers = new ArrayList<>();

        if (count > voucherCreationLimit) {
            throw new VoucherCreationLimitException(String.format("Limit of possible vouchers created at once exceeded. Should be less than %d", voucherCreationLimit));
        }

        if (!foundCampaign.isPresent()) {
            throw new NoSuchElementException(String.format("There is no campaign with ID=%d", campaignId));
        }

        codes.forEach(code -> {
            Voucher voucher = new Voucher();
            voucher.setCampaign(foundCampaign.get());
            voucher.setStatus(status);
            voucher.setCode(code);
            vouchers.add(voucher);
        });

        List<Voucher> createdVouchers = voucherRepository.saveAll(vouchers);

        List<VoucherResponseDTO> createdVouchersDTOs = new ArrayList<>();
        createdVouchers.forEach(voucher -> {
            createdVouchersDTOs.add(voucher.convertEntityToResponseDTO());
        });

        return createdVouchersDTOs;
    }
}
