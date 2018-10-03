package org.mule.modules.grpc.impl;

import io.grpc.stub.StreamObserver;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.callback.SourceCallback;
import org.mule.grpc.server.ExchangeMessageGrpc;
import org.mule.grpc.server.MuleMessage;
import org.mule.modules.grpc.generated.sources.OnErrorMessageSource;
import org.mule.modules.grpc.generated.sources.OnLoadCacheMessageSource;
import org.mule.modules.grpc.generated.sources.OnRefreshMessageSource;
import org.mule.modules.grpc.generated.sources.ReceiveMessageMessageSource;
import org.mule.modules.grpc.utils.GrpcUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExchangeMessageImpl extends ExchangeMessageGrpc.ExchangeMessageImplBase {
    private SourceCallback onMuleMessageSourceCallBack;
    private SourceCallback onErrorSourceCallBack;
    private SourceCallback onRefreshMessageSourceCallBack;
    private SourceCallback onLoadCacheMessageSourceCallBack;


    @Override
    public void onMuleMessage(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        try {
            Map<String, Object> attributesMap = new HashMap<>();
            attributesMap.putAll(request.getAttributesMap());
            Object payload = request.getPaylaod().getValue().toByteArray();
            ReceiveMessageMessageSource messageMessageSource = (ReceiveMessageMessageSource) onMuleMessageSourceCallBack;
            org.mule.api.MuleMessage muleMessage = new DefaultMuleMessage(payload, attributesMap, null, null, messageMessageSource.getMuleContext());
            MuleEvent muleEvent = new DefaultMuleEvent(muleMessage, MessageExchangePattern.REQUEST_RESPONSE, messageMessageSource.getFlowConstruct());
            MuleEvent flowResponseEvent = onMuleMessageSourceCallBack.processEvent(muleEvent);
            InputStream streamedPayload = flowResponseEvent.getMessage().getPayload(InputStream.class);
            Map<String, String> outboundPropsMap = GrpcUtils.copyPropertiesToGrpcAttributes(flowResponseEvent);
            responseObserver.onNext(GrpcUtils.creaetGrpcMuleMessageResponse(streamedPayload, outboundPropsMap));
            responseObserver.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void onMuleError(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        try {
            Map<String, Object> attributesMap = new HashMap<>();
            attributesMap.putAll(request.getAttributesMap());
            Object payload = request.getPaylaod().getValue().toByteArray();
            OnErrorMessageSource messageMessageSource = (OnErrorMessageSource) onErrorSourceCallBack;
            org.mule.api.MuleMessage muleMessage = new DefaultMuleMessage(payload, attributesMap, null, null, messageMessageSource.getMuleContext());
            MuleEvent muleEvent = new DefaultMuleEvent(muleMessage, MessageExchangePattern.REQUEST_RESPONSE, messageMessageSource.getFlowConstruct());
            MuleEvent flowResponseEvent = onErrorSourceCallBack.processEvent(muleEvent);
            InputStream streamedPayload = flowResponseEvent.getMessage().getPayload(InputStream.class);
            Map<String, String> outboundPropsMap = GrpcUtils.copyPropertiesToGrpcAttributes(flowResponseEvent);
            responseObserver.onNext(GrpcUtils.creaetGrpcMuleMessageResponse(streamedPayload, outboundPropsMap));
            responseObserver.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void onRefresh(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        try {
            Map<String, Object> attributesMap = new HashMap<>();
            attributesMap.putAll(request.getAttributesMap());
            Object payload = request.getPaylaod().getValue().toByteArray();
            OnRefreshMessageSource messageMessageSource = (OnRefreshMessageSource) onRefreshMessageSourceCallBack;
            org.mule.api.MuleMessage muleMessage = new DefaultMuleMessage(payload, attributesMap, null, null, messageMessageSource.getMuleContext());
            MuleEvent muleEvent = new DefaultMuleEvent(muleMessage, MessageExchangePattern.REQUEST_RESPONSE, messageMessageSource.getFlowConstruct());
            MuleEvent flowResponseEvent = onRefreshMessageSourceCallBack.processEvent(muleEvent);
            InputStream streamedPayload = flowResponseEvent.getMessage().getPayload(InputStream.class);
            Map<String, String> outboundPropsMap = GrpcUtils.copyPropertiesToGrpcAttributes(flowResponseEvent);
            responseObserver.onNext(GrpcUtils.creaetGrpcMuleMessageResponse(streamedPayload, outboundPropsMap));
            responseObserver.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void onLoadCache(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        try {
            Map<String, Object> attributesMap = new HashMap<>();
            attributesMap.putAll(request.getAttributesMap());
            Object payload = request.getPaylaod().getValue().toByteArray();
            OnLoadCacheMessageSource messageMessageSource = (OnLoadCacheMessageSource) onLoadCacheMessageSourceCallBack;
            org.mule.api.MuleMessage muleMessage = new DefaultMuleMessage(payload, attributesMap, null, null, messageMessageSource.getMuleContext());
            MuleEvent muleEvent = new DefaultMuleEvent(muleMessage, MessageExchangePattern.REQUEST_RESPONSE, messageMessageSource.getFlowConstruct());
            MuleEvent flowResponseEvent = onLoadCacheMessageSourceCallBack.processEvent(muleEvent);
            InputStream streamedPayload = flowResponseEvent.getMessage().getPayload(InputStream.class);
            Map<String, String> outboundPropsMap = GrpcUtils.copyPropertiesToGrpcAttributes(flowResponseEvent);
            responseObserver.onNext(GrpcUtils.creaetGrpcMuleMessageResponse(streamedPayload, outboundPropsMap));
            responseObserver.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    // Getters and Setters


    public SourceCallback getOnMuleMessageSourceCallBack() {
        return onMuleMessageSourceCallBack;
    }

    public void setOnMuleMessageSourceCallBack(SourceCallback onMuleMessageSourceCallBack) {
        this.onMuleMessageSourceCallBack = onMuleMessageSourceCallBack;
    }

    public SourceCallback getOnErrorSourceCallBack() {
        return onErrorSourceCallBack;
    }

    public void setOnErrorSourceCallBack(SourceCallback onErrorSourceCallBack) {
        this.onErrorSourceCallBack = onErrorSourceCallBack;
    }

    public SourceCallback getOnRefreshMessageSourceCallBack() {
        return onRefreshMessageSourceCallBack;
    }

    public void setOnRefreshMessageSourceCallBack(SourceCallback onRefreshMessageSourceCallBack) {
        this.onRefreshMessageSourceCallBack = onRefreshMessageSourceCallBack;
    }

    public SourceCallback getOnLoadCacheMessageSourceCallBack() {
        return onLoadCacheMessageSourceCallBack;
    }

    public void setOnLoadCacheMessageSourceCallBack(SourceCallback onLoadCacheMessageSourceCallBack) {
        this.onLoadCacheMessageSourceCallBack = onLoadCacheMessageSourceCallBack;
    }
}
