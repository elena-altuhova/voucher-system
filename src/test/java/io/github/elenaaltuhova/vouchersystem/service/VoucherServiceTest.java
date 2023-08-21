package io.github.elenaaltuhova.vouchersystem.service;

import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus;
import io.github.elenaaltuhova.vouchersystem.exception.CampaignExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.NoValidVouchersAvailableException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherAlreadyRedeemedException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherNotValidException;
import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import io.github.elenaaltuhova.vouchersystem.model.Status;
import io.github.elenaaltuhova.vouchersystem.model.Voucher;
import io.github.elenaaltuhova.vouchersystem.repository.CampaignRepository;
import io.github.elenaaltuhova.vouchersystem.repository.StatusRepository;
import io.github.elenaaltuhova.vouchersystem.repository.VoucherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus.ISSUED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, MockitoTestExecutionListener.class})
public class VoucherServiceTest {
    @Autowired
    private VoucherService voucherService;

    @MockBean
    private VoucherRepository voucherRepository;

    @MockBean
    private CampaignRepository campaignRepository;

    @MockBean
    private StatusRepository statusRepository;

    @DisplayName("Check a valid voucher")
    @ParameterizedTest
    @ValueSource(strings = {"aafefde5-d7cf-474f-90c9-6957958456a1", "191e1f74-8c48-4099-9a49-717e8e5cf015"})
    public void checkAValidVoucher(String code) throws VoucherNotValidException, VoucherAlreadyRedeemedException, VoucherExpiredException {
        //Given
        BDDMockito.given(voucherRepository.findByCode(UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1")))
            .willReturn(getMockValidVoucher());
        BDDMockito.given(voucherRepository.findByCode(UUID.fromString("191e1f74-8c48-4099-9a49-717e8e5cf015")))
            .willReturn(getMockValidVoucherWithEndDate());

        //When
        VoucherResponseDTO voucherResponseDTO = voucherService.check(code);

        //Then
        assertThat(voucherResponseDTO, notNullValue());
        assertThat(voucherResponseDTO.getCode(), is(equalTo(code)));
        assertThat(voucherResponseDTO.getCampaignName(), is(equalTo("Test Campaign")));
        assertThat(voucherResponseDTO.getStatus(), is(equalTo("ISSUED")));
        assertThat(voucherResponseDTO.getId(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("Check an expired voucher")
    public void checkExpiredCampaignVoucher() {
        //Given
        BDDMockito.given(voucherRepository.findByCode(UUID.fromString("4ada2f1c-c129-44a2-b85c-6ad71fb0614e")))
            .willReturn(getMockExpiredVoucher());

        //When
        Exception exception = assertThrows(VoucherExpiredException.class, () -> {
            voucherService.check("4ada2f1c-c129-44a2-b85c-6ad71fb0614e");
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("Voucher is expired.")));
    }

    @Test
    @DisplayName("Check non existing voucher")
    public void checkNonExistingVoucher() {
        //Given
        BDDMockito.given(voucherRepository.findByCode(UUID.fromString("d490e225-8271-4093-a047-1598ee6b4c1b")))
            .willReturn(null);

        //When
        Exception exception = assertThrows(VoucherNotValidException.class, () -> {
            voucherService.check("d490e225-8271-4093-a047-1598ee6b4c1b");
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("Voucher not valid.")));
    }

    @Test
    @DisplayName("Check redeemed voucher")
    public void checkRedeemedVoucher() {
        //Given
        BDDMockito.given(voucherRepository.findByCode(UUID.fromString("000bbf73-0e4d-4e09-aaea-81c41f019fbd")))
            .willReturn(getMockRedeemedVoucher());

        //When
        Exception exception = assertThrows(VoucherAlreadyRedeemedException.class, () -> {
            voucherService.check("000bbf73-0e4d-4e09-aaea-81c41f019fbd");
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("Voucher is already redeemed.")));
    }

    @Test
    @DisplayName("Check voucher for not yet started campaign")
    public void checkVoucherForCampaignTatHasNotStarted() {
        //Given
        BDDMockito.given(voucherRepository.findByCode(UUID.fromString("72c72023-849b-4fe2-9731-bcb286ae405d")))
            .willReturn(getMockNotStartedVoucher());

        //When
        Exception exception = assertThrows(VoucherNotValidException.class, () -> {
            voucherService.check("72c72023-849b-4fe2-9731-bcb286ae405d");
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("Voucher not valid.")));
    }

    @Test
    @DisplayName("Redeem Valid Voucher")
    public void redeemValidVoucher() throws VoucherNotValidException, VoucherAlreadyRedeemedException, VoucherExpiredException {
        //Given
        BDDMockito.given(voucherRepository.findById((1L)))
            .willReturn(Optional.of(getMockValidVoucher()))
            .willReturn(Optional.of(getMockRedeemedValidVoucher()));

        //When
       VoucherResponseDTO voucherResponseDTO = voucherService.redeem(1L);

        //Then
        assertThat(voucherResponseDTO, notNullValue());
        assertThat(voucherResponseDTO.getCampaignName(), is(equalTo("Test Campaign")));
        assertThat(voucherResponseDTO.getStatus(), is(equalTo("REDEEMED")));
        assertThat(voucherResponseDTO.getId(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("Get Valid Voucher for a campaign")
    public void getValidVoucherForCampaign() throws CampaignExpiredException, NoValidVouchersAvailableException {
        //Given
        Status status = new Status(1L, VoucherStatus.ISSUED);
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);

        BDDMockito.given(campaignRepository.findById((1L)))
            .willReturn(Optional.of(campaign));
        BDDMockito.given(statusRepository.findByStatusName(ISSUED))
            .willReturn(status);

        BDDMockito.given(voucherRepository.findFirstByCampaignAndStatus(Mockito.eq(campaign), Mockito.eq(status)))
            .willReturn(Optional.of(getMockValidVoucher()));

        //When
        VoucherResponseDTO voucherResponseDTO = voucherService.findValidVoucherforACampaign(1L);

        //Then
        assertThat(voucherResponseDTO, notNullValue());
        assertThat(voucherResponseDTO.getCampaignName(), is(equalTo("Test Campaign")));
        assertThat(voucherResponseDTO.getStatus(), is(equalTo("ISSUED")));
        assertThat(voucherResponseDTO.getId(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("Get Valid Voucher for a campaign when no vouchers available")
    public void noValidVouchersForCampaign() throws CampaignExpiredException, NoValidVouchersAvailableException {
        //Given
        Status status = new Status(1L, VoucherStatus.ISSUED);
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);

        BDDMockito.given(campaignRepository.findById((1L)))
            .willReturn(Optional.of(campaign));
        BDDMockito.given(statusRepository.findByStatusName(ISSUED))
            .willReturn(status);

        BDDMockito.given(voucherRepository.findFirstByCampaignAndStatus(Mockito.eq(campaign), Mockito.eq(status)))
            .willReturn(Optional.ofNullable(null));

        //When
        Exception exception = assertThrows(NoValidVouchersAvailableException.class, () -> {
            voucherService.findValidVoucherforACampaign(1L);
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("There are no available vouchers available for this campaign.")));
    }

    @Test
    @DisplayName("Get Valid Voucher for inexisting campaign")
    public void noValidVouchersForNonExistingCampaign() {
        //Given
        Status status = new Status(1L, VoucherStatus.ISSUED);

        BDDMockito.given(campaignRepository.findById((1L)))
            .willReturn(Optional.ofNullable(null));
        BDDMockito.given(statusRepository.findByStatusName(ISSUED))
            .willReturn(status);

        //When
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            voucherService.findValidVoucherforACampaign(1L);
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("There is no campaign with ID=1")));
    }

    @Test
    @DisplayName("Get Valid Voucher for expired campaign")
    public void validVouchersForExpiredCampaign() {
        //Given
        Status status = new Status(1L, VoucherStatus.ISSUED);
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-02"));

        BDDMockito.given(campaignRepository.findById((1L)))
            .willReturn(Optional.of(campaign));
        BDDMockito.given(statusRepository.findByStatusName(ISSUED))
            .willReturn(status);

        //When
        Exception exception = assertThrows(CampaignExpiredException.class, () -> {
            voucherService.findValidVoucherforACampaign(1L);
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("Campaign has already expired.")));
    }

    private Voucher getMockValidVoucher() {
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);
        return new Voucher(1L, UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1"), campaign, new Status(1L, VoucherStatus.ISSUED));
    }
    private Voucher getMockRedeemedValidVoucher() {
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);
        return new Voucher(1L, UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1"), campaign, new Status(1L, VoucherStatus.REDEEMED));
    }

    private Voucher getMockValidVoucherWithEndDate() {
        Campaign campaign = new Campaign(2L, "Test Campaign", LocalDate.parse("2023-01-01"), LocalDate.parse("2025-01-01"));
        return new Voucher(1L, UUID.fromString("191e1f74-8c48-4099-9a49-717e8e5cf015"), campaign, new Status(1L, VoucherStatus.ISSUED));
    }

    private Voucher getMockExpiredVoucher() {
        Campaign campaign = new Campaign(2L, "Expired Test Campaign", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-02"));
        return new Voucher(1L, UUID.fromString("4ada2f1c-c129-44a2-b85c-6ad71fb0614e"), campaign, new Status(1L, VoucherStatus.ISSUED));
    }

    private Voucher getMockRedeemedVoucher() {
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);
        return new Voucher(1L, UUID.fromString("000bbf73-0e4d-4e09-aaea-81c41f019fbd"), campaign, new Status(1L, VoucherStatus.REDEEMED));
    }

    private Voucher getMockNotStartedVoucher() {
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2025-01-01"), null);
        return new Voucher(1L, UUID.fromString("72c72023-849b-4fe2-9731-bcb286ae405d"), campaign, new Status(1L, VoucherStatus.ISSUED));
    }
}
