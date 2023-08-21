package io.github.elenaaltuhova.vouchersystem.exception;

/**
 * Class that implements VoucherNotValidException in the API
 */
public class VoucherNotValidException extends Exception{
    public VoucherNotValidException(){
        super();
    }

    public VoucherNotValidException(String msg){
        super(msg);
    }

    public VoucherNotValidException(String msg, Throwable cause){
        super(msg, cause);
    }
}
