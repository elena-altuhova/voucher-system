package io.github.elenaaltuhova.vouchersystem.exception;

/**
 * Class that implements NoValidVouchersAvailable in the API
 */
public class NoValidVouchersAvailableException extends Exception{
    public NoValidVouchersAvailableException(){
        super();
    }

    public NoValidVouchersAvailableException(String msg){
        super(msg);
    }

    public NoValidVouchersAvailableException(String msg, Throwable cause){
        super(msg, cause);
    }
}
