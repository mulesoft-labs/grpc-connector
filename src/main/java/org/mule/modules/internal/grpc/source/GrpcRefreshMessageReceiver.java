package org.mule.modules.internal.grpc.source;

import org.mule.modules.internal.grpc.config.GrpcServerConfiguration;
import org.mule.modules.internal.grpc.connection.GrpcServerConnection;
import org.mule.modules.internal.grpc.source.builder.GrpcListenerErrorResponseBuilder;
import org.mule.modules.internal.grpc.source.builder.GrpcSuccessResponseBuilder;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.runtime.source.SourceCompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

import static org.mule.modules.api.GrpcConnectorConstants.*;

@MediaType(MediaType.ANY)
@Alias("On-Refresh")
@EmitsResponse
public class GrpcRefreshMessageReceiver extends Source<InputStream, Map<String, String>> {
    Logger logger = LoggerFactory.getLogger(GrpcRefreshMessageReceiver.class);
    @Config
    GrpcServerConfiguration configuration;
    @Connection
    ConnectionProvider<GrpcServerConnection> sourceConn;


    @Override
    public void onStart(SourceCallback<InputStream, Map<String, String>> sourceCallback) throws MuleException {
        GrpcServerConnection connection = sourceConn.connect();
        connection.getExchangeMessage().setOnRefreshSourceCallBack(sourceCallback);
    }

    @Override
    public void onStop() {
        try {
            logger.info("Stopping server ......");
            sourceConn.connect().invalidate();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

    }

    @OnSuccess
    public void onSuccess(@ParameterGroup(name = SUCCESS_RESPONSE,
            showInDsl = true)
                                  GrpcSuccessResponseBuilder successResponse,
                          SourceCallbackContext callbackContext,
                          SourceCompletionCallback sourceCompletionCallback) //
            throws Exception {
        GrpcResponseContext grpcResponseContext = callbackContext.<GrpcResponseContext>getVariable(GRPC_RESPONSE_CONTEXT)
                .orElseThrow(() -> new MuleRuntimeException(I18nMessageFactory.createStaticMessage(RESPONSE_CONTEXT_NOT_FOUND)));
        grpcResponseContext.sendGrpcSuccessResponse(successResponse);
    }

    @OnError
    public void onError(@ParameterGroup(name = ERROR_RESPONSE,
            showInDsl = true) GrpcListenerErrorResponseBuilder errorResponse,
                        SourceCallbackContext callbackContext,
                        Error error,
                        SourceCompletionCallback sourceCompletionCallback) //
            throws Exception {
        GrpcResponseContext grpcResponseContext = callbackContext.<GrpcResponseContext>getVariable(GRPC_RESPONSE_CONTEXT)
                .orElseThrow(() -> new MuleRuntimeException(I18nMessageFactory.createStaticMessage(RESPONSE_CONTEXT_NOT_FOUND)));
        grpcResponseContext.sendGrpcErrorResponse(errorResponse, error);
    }

}
