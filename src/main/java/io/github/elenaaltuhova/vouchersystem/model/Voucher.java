package io.github.elenaaltuhova.vouchersystem.model;

import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Class that implements Voucher entity.
 *
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private UUID code;
    @JoinColumn(name = "campaign_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Campaign campaign;

    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Status status;

    /**
     * Method to convert a Voucher entity to a Voucher Response DTO.
     *
     * @return a <code>VoucherResponseDTO</code> object
     */
    public VoucherResponseDTO convertEntityToResponseDTO() {
        VoucherResponseDTO dto = new VoucherResponseDTO();
        dto.setId(this.getId());
        dto.setCode(String.valueOf(this.getCode()));
        dto.setCampaignName(this.getCampaign().getTitle());
        dto.setStatus(String.valueOf(this.getStatus().getStatusName()));
        return dto;
    }
}
