package gov.gsa.forms.web.rest;

import gov.gsa.forms.service.SignRequestService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        Principal principal
    ) {
        log.info("Pdf Url :{} and Pdf Name :{}", pdfUrl, pdfName);
        return signRequest.executeSignRequest(pdfUrl, pdfName, principal);
    }

    @GetMapping(value = "/send-irs-payload")
    public boolean sendIrsPayload() {
        return signRequest.getSignedDocumentData();
    }
}
