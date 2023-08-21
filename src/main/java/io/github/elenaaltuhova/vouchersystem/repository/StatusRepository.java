package io.github.elenaaltuhova.vouchersystem.repository;

import io.github.elenaaltuhova.vouchersystem.enums.VoucherStatus;
import io.github.elenaaltuhova.vouchersystem.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface that implements the Status Repository, with JPA CRUD methods.
 *
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    /**
     * Method to search all Statuses by Status Name.
     *
     * @param statusName
     * @return Status
     */
    Status findByStatusName(VoucherStatus statusName);
}
