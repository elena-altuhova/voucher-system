package io.github.elenaaltuhova.vouchersystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VoucherResponseDTO extends RepresentationModel<VoucherResponseDTO> {

    @NotNull
    private Long Id;
    @NotNull(message = "Code cannot be null.")
    private String code;

    @NotNull(message = "Campaign name cannot be null")
    private String campaignName;

    @NotNull(message = "Voucher status cannot be null.")
    private String status;
}
