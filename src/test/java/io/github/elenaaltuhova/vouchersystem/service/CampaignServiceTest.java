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
import io.github.elenaaltuhova.vouchersystem.repository.VoucherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, MockitoTestExecutionListener.class })
public class CampaignServiceTest {
    @Autowired
    private CampaignService campaignService;

    @MockBean
    private CampaignRepository campaignRepository;

    @MockBean
    private VoucherRepository voucherRepository;

    @Test
    @DisplayName("Successfully create a campaign")
    public void createCampaign() throws CampaignAlreadyExistsException {
        //Given
        BDDMockito.given(campaignRepository.findByTitle("Test Campaign"))
            .willReturn(getMockValidCampaign());
        BDDMockito.given(campaignRepository.save(Mockito.any(Campaign.class)))
            .willReturn(getMockSecondValidCampaign());
        CampaignDTO campaignDTO = new CampaignDTO("Test Campaign 2", LocalDate.parse("2023-05-01"), null);

        //When
        CampaignResponseDTO campaignResponseDTO = campaignService.create(campaignDTO);

        //Then
        assertThat(campaignResponseDTO, notNullValue());
        assertThat(campaignResponseDTO.getTitle(), is(equalTo("Test Campaign 2")));
        assertThat(campaignResponseDTO.getEndDate(), is(equalTo(null)));
        assertThat(campaignResponseDTO.getStartDate(), is(equalTo(LocalDate.parse("2023-05-01"))));
        assertThat(campaignResponseDTO.getId(), is(equalTo(2L)));
    }

    @Test
    @DisplayName("Find existing campaign by ID")
    public void findExistingCampaign() {
        //Given
        BDDMockito.given(campaignRepository.findById(1L))
            .willReturn(Optional.of(getMockValidCampaign()));

        //When
        CampaignResponseDTO campaignResponseDTO = campaignService.findById(1L);

        //Then
        assertThat(campaignResponseDTO.getTitle(), is(equalTo("Test Campaign")));
        assertThat(campaignResponseDTO.getStartDate(), is(equalTo(LocalDate.parse("2023-01-01"))));
        assertThat(campaignResponseDTO.getEndDate(), is(equalTo(null)));
        assertThat(campaignResponseDTO.getId(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("Validate exception when campaign not found")
    public void exceptionisThrownWhenCampaignNotExist() {
        //Given
        BDDMockito.given(campaignRepository.findById(1L))
            .willReturn(Optional.ofNullable(null));

        //When
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            campaignService.findById(3L);
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("There is no campaign with ID=3")));
    }

    @Test
    @DisplayName("Exception is thrown when campaign already exist")
    public void createCampaignWhereItAlreadyExist() {
        //Given
        BDDMockito.given(campaignRepository.findByTitle("Test Campaign"))
            .willReturn(getMockValidCampaign());
        CampaignDTO campaignDTO = new CampaignDTO("Test Campaign", LocalDate.parse("2023-01-01"), null);

        //When
        Exception exception = assertThrows(CampaignAlreadyExistsException.class, () -> {
            campaignService.create(campaignDTO);
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("Marketing campaign with 'Test Campaign' name already exists")));
    }

    @Test
    @DisplayName("Create valid amount of vouchers for a campaign")
    public void createValidAmountOfVouchers() throws VoucherCreationLimitException {
        //Given
        BDDMockito.given(campaignRepository.findById(1L))
            .willReturn(Optional.of(getMockValidCampaign()));

        BDDMockito.given(voucherRepository.saveAll(Mockito.anyList()))
            .willReturn(getMockGeneratedVouchers());

        //When
        List<VoucherResponseDTO>  voucherResponseDTOS = campaignService.createVouchers(1L, 3);

        //Then
        assertThat(voucherResponseDTOS.isEmpty(), is(false));
        assertThat(voucherResponseDTOS.size(), is(3));
    }

    @Test
    @DisplayName("Try to create more than 500 vouchers for a campaign")
    public void createInvalidAmountOfVouchers() {
        //Given
        BDDMockito.given(campaignRepository.findById(1L))
            .willReturn(Optional.of(getMockValidCampaign()));

        BDDMockito.given(voucherRepository.saveAll(Mockito.anyList()))
            .willReturn(getMockGeneratedVouchers());

        //When
        Exception exception = assertThrows(VoucherCreationLimitException.class, () -> {
            campaignService.createVouchers(1L, 501);
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("Limit of possible vouchers created at once exceeded. Should be less than 500")));
    }

    @Test
    @DisplayName("Try to create vouchers for not existing campaign")
    public void createVouchersForInvalidCampaign() {
        //Given
        BDDMockito.given(campaignRepository.findById(2L))
            .willReturn(Optional.ofNullable(null));

        BDDMockito.given(voucherRepository.saveAll(Mockito.anyList()))
            .willReturn(getMockGeneratedVouchers());

        //When
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            campaignService.createVouchers(2L, 3);
        });

        //Then
        assertThat(exception.getMessage(), is(equalTo("There is no campaign with ID=2")));
    }

    private Campaign getMockValidCampaign() {
        return new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);
    }

    private Campaign getMockSecondValidCampaign() {
        return new Campaign(2L, "Test Campaign 2", LocalDate.parse("2023-05-01"), null);
    }

    private List<Voucher> getMockGeneratedVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);

        vouchers.add(new Voucher(1L, UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1"), campaign, new Status(1L, VoucherStatus.ISSUED)));
        vouchers.add(new Voucher(2L, UUID.fromString("4ada2f1c-c129-44a2-b85c-6ad71fb0614e"), campaign, new Status(1L, VoucherStatus.ISSUED)));
        vouchers.add(new Voucher(3L, UUID.fromString("77b760ec-5396-4e6e-878d-4dea79331f4a"), campaign, new Status(1L, VoucherStatus.ISSUED)));

        return vouchers;
    }
}
