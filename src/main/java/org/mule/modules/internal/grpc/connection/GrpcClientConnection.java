package org.mule.modules.internal.grpc.connection;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class GrpcClientConnection {
    Logger logger = LoggerFactory.getLogger(GrpcClientConnection.class);

    private String host;
    private int port;
    private boolean usePlainText;
    private boolean serverStarted = false;
    private int frameSize;
    private ManagedChannel managedChannel;

    public boolean isServerStarted() {
        return serverStarted;
    }

    public void setServerStarted(boolean serverStarted) {
        this.serverStarted = serverStarted;
    }

    public String getHost() {
        return host;
    }


    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isUsePlainText() {
        return usePlainText;
    }

    public void setUsePlainText(boolean usePlainText) {
        this.usePlainText = usePlainText;
    }

    public GrpcClientConnection(String host, int port, int frameSize) {
        this.host = host;
        this.port = port;
        this.frameSize = frameSize;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public void invalidate() {
        try {
            if (managedChannel != null) {
                managedChannel.shutdownNow();
            }
        } catch (Exception e) {
            logger.error("Error in shutting down channel");
        }
    }

    public ManagedChannel getManagedChannel() {
        if (managedChannel == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Channel is null, creating new connection for host {}, port {}  ", host, port);
            }
            managedChannel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()

                    .maxInboundMessageSize(frameSize * 1000000)
                    .build();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Channel created successfully, state {} ", managedChannel.getState(true));
        }
        return managedChannel;
    }

}
