package org.mule.modules.internal.grpc.source;

import io.grpc.stub.StreamObserver;
import org.mule.grpc.server.MuleMessage;
import org.mule.modules.internal.grpc.source.builder.GrpcListenerErrorResponseBuilder;
import org.mule.modules.internal.grpc.source.builder.GrpcSuccessResponseBuilder;
import org.mule.modules.internal.grpc.utils.GrpcMuleUtils;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.util.MultiMap;

import java.io.IOException;

import static org.mule.modules.api.GrpcConnectorConstants.*;
import static org.mule.runtime.api.util.MultiMap.emptyMultiMap;

public class GrpcResponseContext {
    StreamObserver<MuleMessage> responseObserver;

    public StreamObserver<MuleMessage> getResponseObserver() {
        return responseObserver;
    }

    public void setResponseObserver(StreamObserver<MuleMessage> responseObserver) {
        this.responseObserver = responseObserver;
    }

    public void sendGrpcSuccessResponse(GrpcSuccessResponseBuilder successResponse) throws IOException {
        String statusCode = successResponse.getStatusCode() == null ? "200" : String.valueOf(successResponse.getStatusCode());
        MultiMap<String, String> headers = successResponse.getHeaders() == null ? emptyMultiMap() : successResponse.getHeaders();
        headers.put(MEDIA_TYPE, successResponse.getBody().getDataType().getMediaType().toString());
        headers.put(PAYLOAD_TYPE, successResponse.getBody().getDataType().getType().getName());
        headers.put(STATUS_CODE, statusCode);
        responseObserver.onNext(GrpcMuleUtils.createGrpcResponse(successResponse.getBody().getValue(), headers));
        responseObserver.onCompleted();

    }

    public void sendGrpcErrorResponse(GrpcListenerErrorResponseBuilder errorResponse, Error error) throws IOException {
        String statusCode = errorResponse.getStatusCode() == null ? "500" : String.valueOf(errorResponse.getStatusCode());
        MultiMap<String, String> headers = errorResponse.getHeaders() == null ? emptyMultiMap() : errorResponse.getHeaders();
        headers.put(MEDIA_TYPE, errorResponse.getBody().getDataType().getMediaType().toString());
        headers.put(PAYLOAD_TYPE, errorResponse.getBody().getDataType().getType().getName());
        headers.put(STATUS_CODE, statusCode);
          headers.put(ERROR_DETAIL_DESCRIPTION, error.getDetailedDescription());
//        headers.put(ERROR_RESPONSE, error.getErrorMessage().toString());
        responseObserver.onNext(GrpcMuleUtils.createGrpcResponse(errorResponse.getBody().getValue(), headers));
        responseObserver.onCompleted();
    }

}
