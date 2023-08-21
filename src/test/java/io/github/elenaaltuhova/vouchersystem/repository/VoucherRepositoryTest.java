package io.github.elenaaltuhova.vouchersystem.repository;

import io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus;
import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import io.github.elenaaltuhova.vouchersystem.model.Status;
import io.github.elenaaltuhova.vouchersystem.model.Voucher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class VoucherRepositoryTest extends BaseRepositoryClass{
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @BeforeAll
    public void setupDB(){
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);
        campaignRepository.save(campaign);
        List<Status> statusList = new ArrayList<>();
        statusList.add(new Status(1L, VoucherStatus.ISSUED));
        statusList.add(new Status(2L, VoucherStatus.REDEEMED));
        statusRepository.saveAll(statusList);
        List<Voucher> voucherList = new ArrayList<>();
        voucherList.add(new Voucher(1L, UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1"), campaign, new Status(1L, VoucherStatus.ISSUED)));
        voucherList.add(new Voucher(2L, UUID.fromString("4ada2f1c-c129-44a2-b85c-6ad71fb0614e"), campaign, new Status(1L, VoucherStatus.ISSUED)));
        voucherList.add(new Voucher(3L, UUID.fromString("77b760ec-5396-4e6e-878d-4dea79331f4a"), campaign, new Status(1L, VoucherStatus.ISSUED)));
        voucherRepository.saveAll(voucherList);
    }

    @Test
    @DisplayName("Find voucher by code when it exists")
    public void findByCodeShouldReturnVoucher() {
        //Given
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);
        Status status = new Status(1L, VoucherStatus.ISSUED);
        Voucher expectedVoucher = new Voucher(1L, UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1"), campaign, status);

        //When
        Voucher voucher = voucherRepository.findByCode(UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1"));

        //Then
        assertThat(voucher, notNullValue());
        assertThat(voucher.getCode(), is(equalTo(expectedVoucher.getCode())));
    }

    @Test
    @DisplayName("Null returned when voucher not found")
    public void findByCodeShouldReturnNullWhenCodeNotExisting() {
        //When
        Voucher voucher = voucherRepository.findByCode(UUID.fromString("aafefde5-d7cf-474f-90c9-6965458456a1"));

        //Then
        assertThat(null, is(equalTo(voucher)));
    }

    @Test
    @DisplayName("Successfully update voucher status")
    public void updateVoucherStatus() {
        //Given
        Status status = new Status(2L, VoucherStatus.REDEEMED);

        //When
        voucherRepository.updateStatusById(2L, status);
        Optional<Voucher> voucher = voucherRepository.findById(2L);

        //Then
        assertThat(voucher, notNullValue());
        assertThat(voucher.get().getStatus().getStatusName(), is(equalTo(VoucherStatus.REDEEMED)));
    }

    @Test
    @DisplayName("Find first voucher by campaign and status")
    public void findVoucherByCampaignAndStatus() {
        //Given
        Status status = new Status(1L, VoucherStatus.ISSUED);
        Campaign campaign = new Campaign(1L, "Test Campaign", LocalDate.parse("2023-01-01"), null);

        //When
        Optional<Voucher> voucher = voucherRepository.findFirstByCampaignAndStatus(campaign, status);

        //Then
        assertThat(voucher, notNullValue());
        assertThat(voucher.get().getStatus().getStatusName(), is(equalTo(VoucherStatus.ISSUED)));
        assertThat(voucher.get().getCode(), is(equalTo(UUID.fromString("aafefde5-d7cf-474f-90c9-6957958456a1"))));
    }
}
