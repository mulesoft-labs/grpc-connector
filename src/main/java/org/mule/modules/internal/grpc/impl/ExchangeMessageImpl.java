package org.mule.modules.internal.grpc.impl;

import io.grpc.stub.StreamObserver;
import org.mule.grpc.server.ExchangeMessageGrpc.ExchangeMessageImplBase;
import org.mule.grpc.server.MuleMessage;
import org.mule.modules.internal.grpc.source.GrpcResponseContext;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mule.modules.api.GrpcConnectorConstants.GRPC_RESPONSE_CONTEXT;
import static org.mule.modules.api.GrpcConnectorConstants.MEDIA_TYPE;

public class ExchangeMessageImpl extends ExchangeMessageImplBase {
    Logger logger = LoggerFactory.getLogger(ExchangeMessageImpl.class);


    private SourceCallback messageReceiverSourceCallBack;
    private SourceCallback onErrorMessageReceiverSourceCallBack;
    private SourceCallback onPingSourceCallBack;
    private SourceCallback onRefreshSourceCallBack;
    private SourceCallback onLoadCacheSourceCallBack;

    @Override
    public void onMuleMessage(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {

        Result<InputStream, Map<String, Object>> result = prepareMessageForFlows(request);
        SourceCallbackContext context = messageReceiverSourceCallBack.createContext();
        setSourceContext(responseObserver, context);
        messageReceiverSourceCallBack.handle(result, context);
    }

    @Override
    public void onPing(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {

        Result<InputStream, Map<String, Object>> result = prepareMessageForFlows(request);
        SourceCallbackContext context = onPingSourceCallBack.createContext();
        setSourceContext(responseObserver, context);
        onPingSourceCallBack.handle(result, context);
    }

    @Override
    public void onMuleError(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {

        Result<InputStream, Map<String, Object>> result = prepareMessageForFlows(request);
        SourceCallbackContext context = onErrorMessageReceiverSourceCallBack.createContext();
        setSourceContext(responseObserver, context);
        onErrorMessageReceiverSourceCallBack.handle(result, context);
    }

    @Override
    public void onRefresh(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        Result<InputStream, Map<String, Object>> result = prepareMessageForFlows(request);
        SourceCallbackContext context = onRefreshSourceCallBack.createContext();
        setSourceContext(responseObserver, context);
        onRefreshSourceCallBack.handle(result, context);
    }

    @Override
    public void onLoadCache(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        Result<InputStream, Map<String, Object>> result = prepareMessageForFlows(request);
        SourceCallbackContext context = onLoadCacheSourceCallBack.createContext();
        setSourceContext(responseObserver, context);
        onLoadCacheSourceCallBack.handle(result, context);
    }


    private Result<InputStream, Map<String, Object>> prepareMessageForFlows(MuleMessage request) {
        Map<String, Object> attributesMap = new HashMap<>();
        attributesMap.putAll(request.getAttributesMap());
        String mediaType = request.getAttributesMap().get(MEDIA_TYPE);
        InputStream payload = new ByteArrayInputStream(request.getPaylaod().getValue().toByteArray());
        return Result.<InputStream, Map<String, Object>>builder()
                .attributes(attributesMap)
                .attributesMediaType(MediaType.APPLICATION_JAVA)
                .mediaType(MediaType.parse(mediaType))
                .output(payload)
                .build();
    }

    public SourceCallback getMessageReceiverSourceCallBack() {
        return messageReceiverSourceCallBack;
    }

    public void setMessageReceiverSourceCallBack(SourceCallback messageReceiverSourceCallBack) {
        this.messageReceiverSourceCallBack = messageReceiverSourceCallBack;
    }

    public SourceCallback getOnErrorMessageReceiverSourceCallBack() {
        return onErrorMessageReceiverSourceCallBack;
    }

    public void setOnErrorMessageReceiverSourceCallBack(SourceCallback onErrorMessageReceiverSourceCallBack) {
        this.onErrorMessageReceiverSourceCallBack = onErrorMessageReceiverSourceCallBack;
    }

    public SourceCallback getOnPingSourceCallBack() {
        return onPingSourceCallBack;
    }

    public void setOnPingSourceCallBack(SourceCallback onPingSourceCallBack) {
        this.onPingSourceCallBack = onPingSourceCallBack;
    }

    public SourceCallback getOnRefreshSourceCallBack() {
        return onRefreshSourceCallBack;
    }

    public void setOnRefreshSourceCallBack(SourceCallback onRefreshSourceCallBack) {
        this.onRefreshSourceCallBack = onRefreshSourceCallBack;
    }

    public SourceCallback getOnLoadCacheSourceCallBack() {
        return onLoadCacheSourceCallBack;
    }

    public void setOnLoadCacheSourceCallBack(SourceCallback onLoadCacheSourceCallBack) {
        this.onLoadCacheSourceCallBack = onLoadCacheSourceCallBack;
    }

    private void setSourceContext(StreamObserver<MuleMessage> responseObserver, SourceCallbackContext context) {
        GrpcResponseContext grpcResponseContext = new GrpcResponseContext();
        grpcResponseContext.setResponseObserver(responseObserver);
        context.addVariable(GRPC_RESPONSE_CONTEXT, grpcResponseContext);
    }

}
