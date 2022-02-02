package gov.gsa.forms.service;

import gov.gsa.forms.payload.SignRequestDocumentResponse;

public interface IRSAPIService {
    boolean sendPayload(SignRequestDocumentResponse signRequestDocumentResponse);
    boolean verifyPayload(String docId);
}
