package io.github.elenaaltuhova.vouchersystem.repository;

import io.github.elenaaltuhova.vouchersystem.model.Campaign;
import io.github.elenaaltuhova.vouchersystem.model.Status;
import io.github.elenaaltuhova.vouchersystem.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Voucher Repository, with JPA CRUD methods.
 *
 */

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    /**
     * Method to search Voucher by Voucher Code.
     *
     * @param code
     * @return Voucher
     */
    Voucher findByCode(UUID code);

    /**
     * Method to change Voucher status by id.
     *
     * @param id
     * @return Voucher
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Voucher voucher SET voucher.status = :status WHERE voucher.id = :id")
    void updateStatusById(Long id, Status status);

    /**
     * Method to find a voucher by status and specific campaign id.
     *
     * @param campaign
     * @param status
     * @return Voucher
     */
    Optional<Voucher> findFirstByCampaignAndStatus(Campaign campaign, Status status);
}
