package io.github.elenaaltuhova.vouchersystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VoucherDTO extends RepresentationModel<VoucherDTO> {
    @NotNull(message = "Code cannot be null.")
    @Length(min=36, max=36, message="Voucher code must be a UUID 36 symbols long.")
    private String code;

    @NotNull(message = "Campaign name cannot be null")
    @Length(min=5, max=100, message="Campaign name should nbe between 5 and 100 symbols.")
    private String campaignName;
}
