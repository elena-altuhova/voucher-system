package io.github.elenaaltuhova.vouchersystem.exception;

/**
 * Class that implements VoucherExpiredException in the API
 */
public class VoucherExpiredException extends Exception{
    public VoucherExpiredException(){
        super();
    }

    public VoucherExpiredException(String msg){
        super(msg);
    }

    public VoucherExpiredException(String msg, Throwable cause){
        super(msg, cause);
    }
}
