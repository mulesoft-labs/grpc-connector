/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.modules.api;

/**
 * Constants of the Grpc Connector.
 *
 * @since 1.0
 */
public interface GrpcConnectorConstants {


    String RESPONSE = "Response";
    String SUCCESS_RESPONSE = "Success Response";
    String ERROR_RESPONSE = "Error Response";
    String REQUEST = "Request";
    String RESPONSES = "Responses";
    String CONNECTOR_OVERRIDES = "Connector Overrides";
    String GRPC_RESPONSE_CONTEXT = "grpcResponseContext";
    String RESPONSE_CONTEXT_NOT_FOUND = "Response Context is not present. Could not send response.";
    String REMOTELY_CLOSED = "Remotely closed";
    String MEDIA_TYPE = "mediaType";
    String PAYLOAD_TYPE = "payloadType";
    String CONTENT_TYPE = "content-type";
    String ERROR_DETAIL_DESCRIPTION = "errorDetailDescription";
    String ERROR_MESSAGE = "errorMessage";
    String STATUS_CODE = "statusCode";
    String CONTENT_LENGTH = "content-length";
    String HEADERS = "headers";
    String REASON_PHRASE = "reasonPhrase";
    int DEFAULT_RETRY_ATTEMPTS = 3;

}
