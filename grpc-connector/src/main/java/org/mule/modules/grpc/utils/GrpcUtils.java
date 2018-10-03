package org.mule.modules.grpc.utils;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;

import org.mule.api.MuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.mule.grpc.server.MuleMessage;
public class GrpcUtils {
    static Logger logger = LoggerFactory.getLogger(GrpcUtils.class);

    /**
     * Copies inbound properties to outbound properties
     *
     * @param mmsg         muleMessage
     * @param grpcResponse grpcResponse
     */
    public static void copyOutboundProperties(org.mule.api.MuleMessage mmsg, org.mule.grpc.server.MuleMessage grpcResponse) {
        Map<String, String> responseAttributesMap = grpcResponse.getAttributesMap();
        if (!responseAttributesMap.isEmpty()) {
            Iterator<String> iterator = responseAttributesMap.keySet().iterator();
            while (iterator.hasNext()) {
                String propertyName = iterator.next();
                if (logger.isDebugEnabled()) {
                    logger.debug("Setting outbound properties {},  value {} ", propertyName, responseAttributesMap.get(propertyName));
                }
                mmsg.setOutboundProperty(propertyName, responseAttributesMap.get(propertyName));
            }
        }
        if (mmsg.getOutboundProperty("content-length") == null) {
            int contentLenghth = grpcResponse.getPaylaod().getValue().toByteArray().length;
            if (logger.isDebugEnabled()) {
                logger.debug("Content Length is null, setting in grpc connector {} ", contentLenghth);
            }
            mmsg.setOutboundProperty("content-length", contentLenghth);
        }

    }

    /**
     * @param payload    incoming payload from muleflow
     * @param attributes attributes in Map<String, String></String,>
     * @return grpc MuleMessage
     * @throws IOException
     */
    public static MuleMessage createGrpcMuleMessageRequest(InputStream payload, Map<String, String> attributes) throws IOException {
        MuleMessage grpcRequest = MuleMessage.newBuilder()
                .setPaylaod(createAnyPayload(payload))
                .putAllAttributes(nullSafeAttributes(attributes))
                .build();
        return grpcRequest;
    }

    public static MuleMessage creaetGrpcMuleMessageResponse(InputStream streamedPayload, Map<String, String> outboundPropsMap) throws IOException {
        MuleMessage response = MuleMessage.newBuilder()
                .setPaylaod(Any.newBuilder().setValue(ByteString.readFrom(streamedPayload)))
                .putAllAttributes(outboundPropsMap)
                .build();
        return response;
    }

    public static Map<String, String> nullSafeAttributes(Map<String, String> attributes) {
        Map<String, String> nullSafeAttributes = new HashMap<>();
        if (attributes == null) {
            return nullSafeAttributes;
        }
        for (String key : attributes.keySet()) {
            if (attributes.get(key) == null) {
                nullSafeAttributes.put(key, "");
            } else {
                nullSafeAttributes.put(key, String.valueOf(attributes.get(key)));
            }
        }

        return nullSafeAttributes;
    }

    public static Any.Builder createAnyPayload(InputStream payload) throws IOException {

        Any.Builder anyPayload = null;
        if (payload == null) {
            logger.info("Payload is null, setting empty payload ");
            anyPayload = Any.newBuilder().setValue(ByteString.EMPTY);
        } else {
            anyPayload = Any.newBuilder().setValue(ByteString.readFrom(payload));
        }
        return anyPayload;
    }

    public static Map<String, String> copyPropertiesToGrpcAttributes(MuleEvent flowResponseEvent) {
        Set<String> outboundPropertyNames = flowResponseEvent.getMessage().getOutboundPropertyNames();
        Iterator<String> iterator = outboundPropertyNames.iterator();
        Map<String, String> outboundPropsMap = new HashMap<>();
        outboundPropsMap.put("mimeType", flowResponseEvent.getMessage().getDataType().getMimeType());
        while (iterator.hasNext()) {
            String propName = iterator.next();
            outboundPropsMap.put(propName, String.valueOf(flowResponseEvent.getMessage().getOutboundProperty(propName)));
        }
        return outboundPropsMap;
    }
}

