package org.mule.modules.internal.grpc.operations;

import io.grpc.ManagedChannel;
import io.grpc.internal.GrpcAttributes;
import org.mule.grpc.server.ExchangeMessageGrpc;
import org.mule.grpc.server.MuleMessage;
import org.mule.modules.api.GrpcMessageAttributes;
import org.mule.modules.internal.grpc.config.GrpcClientConfiguration;
import org.mule.modules.internal.grpc.connection.GrpcClientConnection;
import org.mule.modules.internal.grpc.utils.GrpcMuleUtils;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
@Configurations(GrpcClientConfiguration.class)
public class GrpcOperations {
    Logger logger = LoggerFactory.getLogger(GrpcOperations.class);

    @MediaType(value = ANY, strict = false)
    @DisplayName("Send-Message")
    public Result<InputStream, GrpcMessageAttributes> sendMessage(@Config GrpcClientConfiguration configuration,
                                                                  @Connection GrpcClientConnection connection,
                                                                  @Content TypedValue<InputStream> payload,
                                                                  Map<String, String> attributes) throws Exception {
        MuleMessage grpcRequest = GrpcMuleUtils.createGrpcRequest(payload, attributes);
        ManagedChannel managedChannel = connection.getManagedChannel();
        ExchangeMessageGrpc.ExchangeMessageBlockingStub blockingStub = ExchangeMessageGrpc.newBlockingStub(managedChannel);
        MuleMessage grpcResponse = blockingStub.onMuleMessage(grpcRequest);
        connection.invalidate();
        return GrpcMuleUtils.prepareResponse(grpcResponse);

    }


    @MediaType(value = ANY, strict = false)
    @DisplayName("Ping-Grpc-Server")
    @Summary("Pings Grpc Server to verify health of connection")
    public Result<InputStream, GrpcMessageAttributes>  pingServer(@Config GrpcClientConfiguration configuration,
                                                               @Connection GrpcClientConnection connection,
                                                               @Content(primary = true) TypedValue<InputStream> payload,
                                                               Map<String, String> attributes) throws Exception {

        MuleMessage grpcRequest = GrpcMuleUtils.createGrpcRequest(payload, attributes);
        ManagedChannel managedChannel = connection.getManagedChannel();
        ExchangeMessageGrpc.ExchangeMessageBlockingStub blockingStub = ExchangeMessageGrpc.newBlockingStub(managedChannel);
        MuleMessage grpcResponse = blockingStub.onPing(grpcRequest);
        connection.invalidate();
        return GrpcMuleUtils.prepareResponse(grpcResponse);

    }

    @MediaType(value = ANY, strict = false)
    @DisplayName("Send Error")
    public Result<InputStream, GrpcMessageAttributes>  sendError(@Config GrpcClientConfiguration configuration,
                                                               @Connection GrpcClientConnection connection,
                                                               @Content(primary = true) TypedValue<InputStream> payload,
                                                               Map<String, String> attributes) throws Exception {

        MuleMessage grpcRequest = GrpcMuleUtils.createGrpcRequest(payload, attributes);
        ManagedChannel managedChannel = connection.getManagedChannel();
        ExchangeMessageGrpc.ExchangeMessageBlockingStub blockingStub = ExchangeMessageGrpc.newBlockingStub(managedChannel);
        MuleMessage grpcResponse = blockingStub.onMuleError(grpcRequest);
        connection.invalidate();
        return GrpcMuleUtils.prepareResponse(grpcResponse);

    }

    @MediaType(value = ANY, strict = false)
    @DisplayName("Load Cache")
    public Result<InputStream, GrpcMessageAttributes>  loadCache(@Config GrpcClientConfiguration configuration,
                                                              @Connection GrpcClientConnection connection,
                                                              @Content(primary = true) TypedValue<InputStream> payload,
                                                              Map<String, String> attributes) throws Exception {

        MuleMessage grpcRequest = GrpcMuleUtils.createGrpcRequest(payload, attributes);
        ManagedChannel managedChannel = connection.getManagedChannel();
        ExchangeMessageGrpc.ExchangeMessageBlockingStub blockingStub = ExchangeMessageGrpc.newBlockingStub(managedChannel);
        MuleMessage grpcResponse = blockingStub.onLoadCache(grpcRequest);
        connection.invalidate();
        return GrpcMuleUtils.prepareResponse(grpcResponse);

    }
    @MediaType(value = ANY, strict = false)
    @DisplayName("Refresh Cache")
    public Result<InputStream, GrpcMessageAttributes>  refreshCache(@Config GrpcClientConfiguration configuration,
                                                              @Connection GrpcClientConnection connection,
                                                              @Content(primary = true) TypedValue<InputStream> payload,
                                                              Map<String, String> attributes) throws Exception {

        MuleMessage grpcRequest = GrpcMuleUtils.createGrpcRequest(payload, attributes);
        ManagedChannel managedChannel = connection.getManagedChannel();
        ExchangeMessageGrpc.ExchangeMessageBlockingStub blockingStub = ExchangeMessageGrpc.newBlockingStub(managedChannel);
        MuleMessage grpcResponse = blockingStub.onRefresh(grpcRequest);
        connection.invalidate();
        return GrpcMuleUtils.prepareResponse(grpcResponse);

    }

}
