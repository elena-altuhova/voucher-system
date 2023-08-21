package io.github.elenaaltuhova.vouchersystem.repository;

import io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus;
import io.github.elenaaltuhova.vouchersystem.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StatusRepositoryTest extends BaseRepositoryClass{

    @Autowired
    private StatusRepository statusRepository;

    @Test
    @DisplayName("Successfully found campaign by title")
    public void findCampaignByTitle() {
        //Given
        List<Status> statusList = new ArrayList<>();
        statusList.add(new Status(1L, VoucherStatus.ISSUED));
        statusRepository.saveAll(statusList);

        //When
        Status status = statusRepository.findByStatusName(VoucherStatus.ISSUED);

        //Then
        assertThat(status, notNullValue());
        assertThat(status.getId(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("Null is returned when status not found")
    public void nullReturnedWhenNoCampaignFound() {
        //When
        Status status = statusRepository.findByStatusName(VoucherStatus.REDEEMED);

        //Then
        assertThat(null, is(equalTo(status)));
    }
}
