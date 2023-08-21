package io.github.elenaaltuhova.vouchersystem.controller;

import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import io.github.elenaaltuhova.vouchersystem.dto.response.Response;
import io.github.elenaaltuhova.vouchersystem.exception.CampaignExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.NoValidVouchersAvailableException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherAlreadyRedeemedException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherExpiredException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherNotValidException;
import io.github.elenaaltuhova.vouchersystem.service.VoucherService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

/**
 * SpringBoot RestController that creates all service end-points related to the voucher.
 *
 */
@Log4j2
@RestController
@RequestMapping("/v1/vouchers")
public class VoucherController {
    VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    /**
     * Method that checks voucher validity in the Travels Java API.
     *
     *
     * @return ResponseEntity with a Response<VoucherResponseDTO> object and the HTTP status
     *
     * HTTP Status:
     *
     * 200 - Voucher is valid and can be redeemed.
     * 400 - Bad Request: Voucher is invalid. It's either expired, redeemed.
     * 401 - Unauthorized: No valid API key provided.
     * 404 - Not Found: Voucher with this code was not found or marketing company hasn't started yet.
     * 500, 502, 503, 504 - Server Errors: something went wrong on API end (These are rare).
     */
    @GetMapping(value = "/{code}/check")
    public ResponseEntity<Response<VoucherResponseDTO>> check(@PathVariable("code") String voucherCode) {

        Response<VoucherResponseDTO> response = new Response<>();
        VoucherResponseDTO voucherDTO = null;

        try {
             voucherDTO = voucherService.check(voucherCode);
        } catch (VoucherNotValidException exception) {
            log.error(exception.getMessage());
            response.addErrorMsgToResponse(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (VoucherExpiredException | VoucherAlreadyRedeemedException exception) {
            log.error(exception.getMessage());
            response.addErrorMsgToResponse(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.setData(voucherDTO);

        createRedeemLink(voucherDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Method that redeems voucher in the Travels Java API.
     *
     *
     * @return ResponseEntity with a Response<VoucherResponseDTO> object and the HTTP status
     *
     * HTTP Status:
     *
     * 200 - Voucher was successfully redeemed.
     * 400 - Bad Request: Voucher is invalid. It's either expired, redeemed.
     * 401 - Unauthorized: No valid API key provided.
     * 404 - Not Found: Voucher with this code was not found.
     * 500, 502, 503, 504 - Server Errors: something went wrong on API end (These are rare).
     */
    @PostMapping(value = "/{id}/redeem")
    public ResponseEntity<Response<VoucherResponseDTO>> redeem(@PathVariable("id") Long voucherId) {

        Response<VoucherResponseDTO> response = new Response<>();
        VoucherResponseDTO voucherDTO = null;

        try {
            voucherDTO = voucherService.redeem(voucherId);
        } catch (VoucherNotValidException | VoucherExpiredException | VoucherAlreadyRedeemedException exception) {
            log.error(exception.getMessage());
            response.addErrorMsgToResponse(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.setData(voucherDTO);
        createCheckLink(voucherDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Method that sends back a valid voucher for a specific campaign in the Travels Java API.
     *
     *
     * @return ResponseEntity with a Response<VoucherResponseDTO> object and the HTTP status
     *
     * HTTP Status:
     *
     * 200 - Valid voucher is returned.
     * 400 - Bad Request: Campaign not valid or expired or hasn't started yet.
     * 401 - Unauthorized: No valid API key provided.
     * 404 - Not Found: Valid voucher was not found or marketing company hasn't started yet.
     * 500, 502, 503, 504 - Server Errors: something went wrong on API end (These are rare).
     */
    @GetMapping(value = "/send")
    public ResponseEntity<Response<VoucherResponseDTO>> sendValidVoucher(@RequestParam Long campaignId) {

        Response<VoucherResponseDTO> response = new Response<>();
        VoucherResponseDTO voucherDTO = null;

        try {
            voucherDTO = voucherService.sendValidVoucherForACampaign(campaignId);
        } catch (CampaignExpiredException exception) {
            log.error(exception.getMessage());
            response.addErrorMsgToResponse(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException | NoValidVouchersAvailableException exception) {
            log.error(exception.getMessage());
            response.addErrorMsgToResponse(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }


        response.setData(voucherDTO);
        createCheckLink(voucherDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Method that creates a redeem link for a voucher
     *
     * @param voucherResponseDTO
     */
    private void createRedeemLink(VoucherResponseDTO voucherResponseDTO) {
        Link redeemLink = WebMvcLinkBuilder.linkTo(VoucherController.class).slash(voucherResponseDTO.getId()).slash("/redeem").withRel("redeem");
        voucherResponseDTO.add(redeemLink);
    }

    /**
     * Method that creates a check link for Voucher
     *
     * @param voucherResponseDTO
     */
    private void createCheckLink(VoucherResponseDTO voucherResponseDTO) {
        Link redeemLink = WebMvcLinkBuilder.linkTo(VoucherController.class).slash(voucherResponseDTO.getCode()).slash("/check").withRel("check");
        voucherResponseDTO.add(redeemLink);
    }
}
