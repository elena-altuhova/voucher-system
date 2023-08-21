package io.github.elenaaltuhova.vouchersystem.enums;

public enum VoucherStatus {
    ISSUED("ISSUED"),
    SENT("SENT"),
    REDEEMED("REDEEMED");
    private String value;

    private VoucherStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
