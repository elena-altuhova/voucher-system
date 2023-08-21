package io.github.elenaaltuhova.vouchersystem.controller;

import io.github.elenaaltuhova.vouchersystem.dto.CampaignDTO;
import io.github.elenaaltuhova.vouchersystem.dto.CampaignResponseDTO;
import io.github.elenaaltuhova.vouchersystem.dto.VoucherResponseDTO;
import io.github.elenaaltuhova.vouchersystem.dto.response.Response;
import io.github.elenaaltuhova.vouchersystem.exception.CampaignAlreadyExistsException;
import io.github.elenaaltuhova.vouchersystem.exception.VoucherCreationLimitException;
import io.github.elenaaltuhova.vouchersystem.service.CampaignService;
import io.github.elenaaltuhova.vouchersystem.service.VoucherService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * SpringBoot RestController that creates all service end-points related to the marketing campaign.
 */
@Log4j2
@RestController
@RequestMapping("/v1/campaigns")
public class CampaignController {
    VoucherService voucherService;
    CampaignService campaignService;

    @Autowired
    public CampaignController(VoucherService voucherService, CampaignService campaignService) {
        this.voucherService = voucherService;
        this.campaignService = campaignService;
    }

    /**
     * Method that creates a new marketing campaign in the Voucher System.
     *
     * @param campaignDTO
     * @param result
     * @return ResponseEntity with a Response<CampaignDTO> object and the HTTP status
     * <p>
     * HTTP Status:
     * <p>
     * 201 - Created: Everything worked as expected.
     * 400 - Bad Request: The request was unacceptable, often due to missing a required parameter.
     * 401 - Unauthorized: No valid API key provided.
     * 500, 502, 503, 504 - Server Errors: something went wrong on API end (These are rare).
     */

    @PostMapping
    public ResponseEntity<Response<CampaignResponseDTO>> createCampaign(@Valid
                                                                        @RequestBody
                                                                        CampaignDTO campaignDTO, BindingResult result) {
        Response<CampaignResponseDTO> response = new Response<>();

        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> response.addErrorMsgToResponse(error.getDefaultMessage()));
            log.error(response.getErrors().toString());
            return ResponseEntity.badRequest().body(response);
        }

        CampaignResponseDTO campaignResponseDTO;
        try {
            campaignResponseDTO = campaignService.create(campaignDTO);
        } catch (CampaignAlreadyExistsException exception) {
            response.addErrorMsgToResponse(exception.getMessage());
            log.error(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.setData(campaignResponseDTO);
        createSelfLink(campaignResponseDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Method that returns Campaign by Id in the Voucher System.
     *
     * @param id
     * @return ResponseEntity with a Response<CampaignDTO> object and the HTTP status
     * <p>
     * HTTP Status:
     * <p>
     * 200 - Created: Everything worked as expected.
     * 401 - Unauthorized: No valid API key provided.
     * 404 - Not found: There is no campaign with such id.
     * 500, 502, 503, 504 - Server Errors: something went wrong on API end (These are rare).
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<CampaignResponseDTO>> findCampaign(
        @PathVariable("id")
        Long id) {
        Response<CampaignResponseDTO> response = new Response<>();

        CampaignResponseDTO campaignResponseDTO;

        try {
            campaignResponseDTO = campaignService.findById(id);
        } catch (NoSuchElementException exception) {
            response.addErrorMsgToResponse(exception.getMessage());
            log.error(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.setData(campaignResponseDTO);
        createSelfLink(campaignResponseDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Method that generate N vouchers for a Campaign in the Voucher System.
     *
     * @param id
     * @param count
     * @return ResponseEntity with a Response<CampaignDTO> object and the HTTP status
     * <p>
     * HTTP Status:
     * <p>
     * 200 - Created: Everything worked as expected.
     * 401 - Unauthorized: No valid API key provided.
     * 404 - Not found: There is no campaign with such id.
     * 500, 502, 503, 504 - Server Errors: something went wrong on API end (These are rare).
     */
    @PostMapping(value = "/{id}/vouchers")
    public ResponseEntity<Response<List<VoucherResponseDTO>>> addVouchers(
        @PathVariable("id")
        Long id,
        @RequestParam
        Integer count) {
        Response<List<VoucherResponseDTO>> response = new Response<>();

        List<VoucherResponseDTO> createdVouchers;
        try {
            createdVouchers = campaignService.createVouchers(id, count);
        } catch (NoSuchElementException exception) {
            response.addErrorMsgToResponse(exception.getMessage());
            log.error(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (VoucherCreationLimitException exception) {
            response.addErrorMsgToResponse(exception.getMessage());
            log.error(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.setData(createdVouchers);
        createdVouchers.forEach(this::createCheckLink);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Method that creates a self link to a marketing campaign object
     *
     * @param campaignResponseDTO
     */
    private void createSelfLink(CampaignResponseDTO campaignResponseDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(CampaignController.class).slash(campaignResponseDTO.getId()).withSelfRel();
        campaignResponseDTO.add(selfLink);
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
