package io.github.elenaaltuhova.vouchersystem.exception;

/**
 * Class that implements CampaignExpiredException in the API
 */
public class CampaignExpiredException extends Exception{
    public CampaignExpiredException(){
        super();
    }

    public CampaignExpiredException(String msg){
        super(msg);
    }

    public CampaignExpiredException(String msg, Throwable cause){
        super(msg, cause);
    }
}
