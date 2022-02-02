package gov.gsa.forms.service;

import java.security.Principal;

public interface SignRequestService {
    String executeSignRequest(
        String url,
        String pdfName,
        Principal principal,
        String firstName,
        String lastName,
        String email,
        String jointRequest
    );

    boolean getSignedDocumentData(String docUUID);
}
