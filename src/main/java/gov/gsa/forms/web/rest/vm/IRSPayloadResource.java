package gov.gsa.forms.web.rest.vm;

import gov.gsa.forms.payload.SignRequestDocumentResponse;
import gov.gsa.forms.payload.SignRequestEventCallbackResponse;
import gov.gsa.forms.service.SignRequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("send-irs-payload")
@Slf4j
public class IRSPayloadResource {

    public static final String SIGNED = "signed";
    private final SignRequestService signRequest;

    public IRSPayloadResource(SignRequestService signRequest) {
        this.signRequest = signRequest;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> sendIrsPayload(@RequestBody SignRequestEventCallbackResponse response) {
        log.info("Sign request event call back response: {}", response);
        if (StringUtils.equals(SIGNED, response.getEventType())) {
            boolean signedDocument = signRequest.getSignedDocumentData(response.getDocument().getUuid());
            return new ResponseEntity<>(String.valueOf(signedDocument), HttpStatus.OK);
        }
        return new ResponseEntity<>("false", HttpStatus.OK);
    }
}
