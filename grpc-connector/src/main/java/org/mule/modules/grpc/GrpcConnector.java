package org.mule.modules.grpc;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.mule.api.MuleMessage;
import org.mule.api.annotations.*;
import org.mule.api.annotations.lifecycle.OnException;
import org.mule.api.annotations.lifecycle.Stop;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.grpc.config.GrpcConfiguration;
import org.mule.modules.grpc.error.ErrorHandler;
import org.mule.modules.grpc.impl.ExchangeMessageImpl;
import org.mule.modules.grpc.utils.GrpcUtils;
import org.mule.transformer.types.SimpleDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;


@Connector(name = "grpc", friendlyName = "Grpc")
@OnException(handler = ErrorHandler.class)
public class GrpcConnector {
    private Logger logger = LoggerFactory.getLogger(GrpcConnector.class);
    private org.mule.grpc.server.ExchangeMessageGrpc.ExchangeMessageBlockingStub blockingStub;
    private Server grpcServer;
    private ExchangeMessageImpl exchangeMessage;
    private boolean initialized = false;


    @Config
    GrpcConfiguration config;

    @Processor
    public void sendMessage(MuleMessage muleMessage, @Default("#[payload]") InputStream payload, @Optional Map<String, String> attributes) throws Exception {
        ManagedChannel plainTextChannel = createPlainTextChannel();
        org.mule.grpc.server.MuleMessage grpcResponse = blockingStub.onMuleMessage(GrpcUtils.createGrpcMuleMessageRequest(payload, attributes));
        GrpcUtils.copyOutboundProperties(muleMessage, grpcResponse);
        SimpleDataType<String> simpleDataType = new SimpleDataType<>(String.class, grpcResponse.getAttributesMap().get("mimeType"));
        muleMessage.setPayload(grpcResponse.getPaylaod().getValue().toByteArray(), simpleDataType);
        shutDownChannel(plainTextChannel);

    }

    @Processor
    public void generateError(MuleMessage muleMessage, @Default("#[payload]") InputStream payload,
                                @Optional Map<String, String> attributes) throws Exception {
        ManagedChannel plainTextChannel = createPlainTextChannel();
        org.mule.grpc.server.MuleMessage grpcResponse = blockingStub.onMuleError(GrpcUtils.createGrpcMuleMessageRequest(payload, attributes));
        GrpcUtils.copyOutboundProperties(muleMessage, grpcResponse);
        SimpleDataType<String> simpleDataType = new SimpleDataType<>(String.class, grpcResponse.getAttributesMap().get("mimeType"));
        muleMessage.setPayload(grpcResponse.getPaylaod().getValue().toByteArray(), simpleDataType);
        shutDownChannel(plainTextChannel);

    }

    @Processor
    public void refresh(MuleMessage muleMessage, @Default("#[payload]") InputStream payload,
                          @Optional Map<String, String> attributes) throws Exception {
        ManagedChannel plainTextChannel = createPlainTextChannel();
        org.mule.grpc.server.MuleMessage grpcResponse = blockingStub.onRefresh(GrpcUtils.createGrpcMuleMessageRequest(payload, attributes));
        GrpcUtils.copyOutboundProperties(muleMessage, grpcResponse);
        SimpleDataType<String> simpleDataType = new SimpleDataType<>(String.class, grpcResponse.getAttributesMap().get("mimeType"));
        muleMessage.setPayload(grpcResponse.getPaylaod().getValue().toByteArray(), simpleDataType);
        shutDownChannel(plainTextChannel);

    }

    @Processor
    public void loadCache(MuleMessage muleMessage, @Default("#[payload]") InputStream payload,
                            @Optional Map<String, String> attributes) throws Exception {
        ManagedChannel plainTextChannel = createPlainTextChannel();
        org.mule.grpc.server.MuleMessage grpcResponse = blockingStub.onLoadCache(GrpcUtils.createGrpcMuleMessageRequest(payload, attributes));
        GrpcUtils.copyOutboundProperties(muleMessage, grpcResponse);
        SimpleDataType<String> simpleDataType = new SimpleDataType<>(String.class, grpcResponse.getAttributesMap().get("mimeType"));
        muleMessage.setPayload(grpcResponse.getPaylaod().getValue().toByteArray(), simpleDataType);
        shutDownChannel(plainTextChannel);

    }

    @Source(sourceStrategy = SourceStrategy.NONE)
    public void receiveMessage(SourceCallback sourceCallback) throws Exception {
        startServerIfRequired();
        exchangeMessage.setOnMuleMessageSourceCallBack(sourceCallback);

    }

    @Source(sourceStrategy = SourceStrategy.NONE)
    public void onError(SourceCallback sourceCallback) throws Exception {
        startServerIfRequired();
        exchangeMessage.setOnErrorSourceCallBack(sourceCallback);

    }

    @Source(sourceStrategy = SourceStrategy.NONE)
    public void onRefresh(SourceCallback sourceCallback) throws Exception {
        startServerIfRequired();
        exchangeMessage.setOnRefreshMessageSourceCallBack(sourceCallback);

    }

    @Source(sourceStrategy = SourceStrategy.NONE)
    public void onLoadCache(SourceCallback sourceCallback) throws Exception {
        startServerIfRequired();
        exchangeMessage.setOnLoadCacheMessageSourceCallBack(sourceCallback);

    }

    public GrpcConfiguration getConfig() {
        return config;
    }


    private ManagedChannel createPlainTextChannel() {
        if (logger.isDebugEnabled()) {
            logger.debug("Connecting with host {}, port {} ", config.getHost(), config.getPort());

        }
        ManagedChannel channel = ManagedChannelBuilder.forAddress(config.getHost(), config.getPort())
                .usePlaintext(true)
                .maxInboundMessageSize(converteToByte())
                .build();
        blockingStub = org.mule.grpc.server.ExchangeMessageGrpc.newBlockingStub(channel);
        return channel;

    }

    private synchronized void startServerIfRequired() throws Exception {
        if (!initialized) {
            initialized = true;
            logger.info("Server is not on ..... starting");
            startServer();
        }


    }

    private void startServer() throws Exception {
        exchangeMessage = new ExchangeMessageImpl();
        logger.info("Starting server at port -----> {} ", config.getPort());
        grpcServer = ServerBuilder.forPort(config.getPort())
                .addService(exchangeMessage)
                .build()
                .start();
    }

    @Stop
    public synchronized void stopServer() {
        logger.info("Stopping GRPC server !!");
        stop();
        try {
            blockUntilShutdown();

        } catch (Exception e) {
            logger.error("Error in shutting down GRPC server !!! ", e);
        }
    }

    private void stop() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();

        }
    }

    private void shutDownChannel(ManagedChannel channel) {
        try {
            channel.shutdownNow();
        } catch (Exception e) {
            logger.error("Error in shutting down channel");
        }
    }

    private int converteToByte() {
        int frameSize = Integer.parseInt(config.getFrameSize());
        return frameSize * 1000000;
    }

    public void setConfig(GrpcConfiguration config) {
        this.config = config;
    }

}