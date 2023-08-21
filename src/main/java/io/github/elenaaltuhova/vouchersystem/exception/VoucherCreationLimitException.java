package io.github.elenaaltuhova.vouchersystem.exception;

/**
 * Class that implements VoucherCreationLimitException in the API
 */
public class VoucherCreationLimitException extends Exception{
    public VoucherCreationLimitException(){
        super();
    }

    public VoucherCreationLimitException(String msg){
        super(msg);
    }

    public VoucherCreationLimitException(String msg, Throwable cause){
        super(msg, cause);
    }
}
