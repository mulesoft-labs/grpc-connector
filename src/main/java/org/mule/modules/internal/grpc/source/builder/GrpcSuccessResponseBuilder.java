/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.modules.internal.grpc.source.builder;

import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import java.io.InputStream;

import static org.mule.modules.api.GrpcConnectorConstants.RESPONSES;
import static org.mule.runtime.api.util.MultiMap.emptyMultiMap;

/**
 *  returns success responses
 *
 * @since 1.0
 */
public class GrpcSuccessResponseBuilder {

  /**
   * The body of the response message.
   */
  @Parameter
  @Content(primary = true)
  @Placement(tab = RESPONSES, order = 1)
  private TypedValue<InputStream> body;

  /**
   * HTTP headers the message should include.
   */
  @Parameter
  @Optional(defaultValue = "#[vars.outboundHeaders default {}]")
  @Content
  @Placement(tab = RESPONSES, order = 2)
  protected MultiMap<String, String> headers = emptyMultiMap();

  /**
   * HTTP status code the response should have.
   */
  @Parameter
  @Optional(defaultValue = "#[vars.httpStatusCode default 200]")
  @Placement(tab = RESPONSES, order = 3)
  private Integer statusCode;

  /**
   * HTTP reason phrase the response should have.
   */
  @Parameter
  @Optional
  @Placement(tab = RESPONSES, order = 4)
  private String reasonPhrase;

  public TypedValue<InputStream> getBody() {
    return body;
  }

  public void setBody(TypedValue<InputStream> body) {
    this.body = body;
  }

  public MultiMap<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(MultiMap<String, String> headers) {
    this.headers = headers;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }

  public void setReasonPhrase(String reasonPhrase) {
    this.reasonPhrase = reasonPhrase;
  }
}
