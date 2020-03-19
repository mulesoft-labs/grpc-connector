package org.mule.modules.api;

import org.mule.runtime.api.util.MultiMap;

import java.util.Collections;
import java.util.Map;

import static org.mule.runtime.api.util.MultiMap.emptyMultiMap;
public class GrpcMessageAttributes {
    private String statusCode = GrpcConnectorConstants.STATUS_CODE;
    private String reasonPhrase = GrpcConnectorConstants.REASON_PHRASE;
    private Map<String, String> headers = Collections.emptyMap();

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
