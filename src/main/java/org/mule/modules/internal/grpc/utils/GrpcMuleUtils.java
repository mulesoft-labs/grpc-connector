package org.mule.modules.internal.grpc.utils;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import org.mule.grpc.server.MuleMessage;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mule.modules.api.GrpcConnectorConstants.*;

public class GrpcMuleUtils {
    private static Logger logger = LoggerFactory.getLogger(GrpcMuleUtils.class);

    /**
     *
     * @param payload
     * @param attributes
     * @return grpc MuleMessage Request
     * @throws Exception
     */
    public static MuleMessage createGrpcRequest(TypedValue<InputStream> payload, Map<String, String> attributes) throws Exception {
        Map<String, String> nullSafeAttributes = nullSafeAttributes(attributes);
        addPayloadMetaData(payload, nullSafeAttributes);
        MuleMessage grpcRequest = MuleMessage.newBuilder()
                .setPaylaod(createAnyPayload(payload.getValue()))
                .putAllAttributes(nullSafeAttributes)
                .build();
        return grpcRequest;
    }

    /**
     *
     * @param streamedPayload
     * @param outboundPropsMap
     * @return Grpc MuleMessage Response
     * @throws IOException
     */
    public static MuleMessage createGrpcResponse(InputStream streamedPayload, Map<String, String> outboundPropsMap) throws IOException {
        MuleMessage response = MuleMessage.newBuilder()
                .setPaylaod(createAnyPayload(streamedPayload))
                .putAllAttributes(nullSafeAttributes(outboundPropsMap))
                .build();
        return response;
    }

    /**
     *
     * @param payload
     * @return create empty or Grpc Payload
     * @throws IOException
     */
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

    /**
     *
     * @param attributes
     * @return NulSafe map, null values are replaced with empty string
     */
    public static Map<String, String> nullSafeAttributes(Map<String, String> attributes) {
        Map<String, String> nullSafeAttributes = new HashMap<>();
        if (attributes == null) {
            return nullSafeAttributes;
        }
        for (String key : attributes.keySet()) {

            if (attributes.get(key) == null) {
                nullSafeAttributes.put(key, "");
            } else {
                if (logger.isDebugEnabled()) {
                    logger.info("Outbound Attributes  [{}: {}] ", key, String.valueOf(attributes.get(key)));
                }
                nullSafeAttributes.put(key, String.valueOf(attributes.get(key)));
            }
        }

        return nullSafeAttributes;
    }

    /**
     * Adds Content-type, media-type to attributes for grpc server to process
     * @param payload
     * @param attributes
     */
    public static void addPayloadMetaData(TypedValue<InputStream> payload, Map<String, String> attributes) {
        attributes.put(MEDIA_TYPE, payload.getDataType().getMediaType().toString());
        attributes.put(CONTENT_TYPE, payload.getDataType().getMediaType().toString());
        attributes.put(PAYLOAD_TYPE, payload.getDataType().getType().getName());
    }

    /**
     *
     * @param grpcResponse
     * @return Response from grpc client to flows
     */
    public static Result<InputStream, Map<String, String>> prepareResponse(MuleMessage grpcResponse) {
        Map<String, String> attributes = new HashMap<>();
        attributes.putAll(grpcResponse.getAttributesMap());
        attributes.put(CONTENT_LENGTH, String.valueOf(grpcResponse.getPaylaod().getValue().toByteArray().length));
        InputStream stream = new ByteArrayInputStream(grpcResponse.getPaylaod().getValue().toByteArray());
        return Result.<InputStream, Map<String, String>>builder()
                .output(stream)
                .length(grpcResponse.getPaylaod().getValue().toByteArray().length)
                .mediaType(org.mule.runtime.api.metadata.MediaType.parse(grpcResponse.getAttributesMap().get(MEDIA_TYPE)))
                .attributes(attributes)
                .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
                .build();
    }

}
