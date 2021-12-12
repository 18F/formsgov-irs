package gov.gsa.forms.web.rest;

import gov.gsa.forms.service.SignRequestService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class SignRequestResource {

    private final SignRequestService signRequest;

    public SignRequestResource(SignRequestService signRequest) {
        this.signRequest = signRequest;
    }

    @GetMapping(value = "/sign")
    public String signRequest(
        @RequestParam(name = "pdfUrl") String pdfUrl,
        @RequestParam(name = "pdfName") String pdfName,
        @RequestParam(name = "taxpayerName2") String taxpayerName2,
        @RequestParam(name = "taxpayerLastName2") String taxpayerLastName2,
        @RequestParam(name = "taxpayer2Email") String taxpayer2Email,
        @RequestParam(name = "jointRequest") String jointRequest,
        Principal principal
    ) {
        log.info("Pdf Url :{} and Pdf Name :{}", pdfUrl, pdfName);
        return signRequest.executeSignRequest(pdfUrl, pdfName, principal, taxpayerName2, taxpayerLastName2, taxpayer2Email, jointRequest);
    }
}
