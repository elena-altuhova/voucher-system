package io.github.elenaaltuhova.vouchersystem.exception;

/**
 * Class that implements VoucherAlreadyRedeemedException in the API
 */
public class VoucherAlreadyRedeemedException extends Exception{
    public VoucherAlreadyRedeemedException(){
        super();
    }

    public VoucherAlreadyRedeemedException(String msg){
        super(msg);
    }

    public VoucherAlreadyRedeemedException(String msg, Throwable cause){
        super(msg, cause);
    }
}
