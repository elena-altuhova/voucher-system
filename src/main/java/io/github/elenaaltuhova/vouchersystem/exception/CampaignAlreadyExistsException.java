package io.github.elenaaltuhova.vouchersystem.exception;

/**
 * Class that implements CampaignAlreadyExistsException in the API
 */
public class CampaignAlreadyExistsException extends Exception{
    public CampaignAlreadyExistsException(){
        super();
    }

    public CampaignAlreadyExistsException(String msg){
        super(msg);
    }

    public CampaignAlreadyExistsException(String msg, Throwable cause){
        super(msg, cause);
    }
}
