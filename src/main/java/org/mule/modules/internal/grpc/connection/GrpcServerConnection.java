package org.mule.modules.internal.grpc.connection;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.mule.modules.internal.grpc.impl.ExchangeMessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class GrpcServerConnection {
    private Logger logger = LoggerFactory.getLogger(GrpcServerConnection.class);

    private String host;
    private int port;
    private ExchangeMessageImpl exchangeMessage;
    private Server grpcServer;

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


    public GrpcServerConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void invalidate() {
        logger.info("Stopping GRPC server!!");
        stop();
        try {
            blockUntilShutdown();

        } catch (Exception e) {
            logger.error("Error in shutting down GRPC server !!! ", e);
        }

    }

    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();

        }
    }

    private void stop() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }

    public void startServer() throws Exception {
        exchangeMessage = new ExchangeMessageImpl();
        logger.info("Starting server at port -----> {} ", port);
        grpcServer = ServerBuilder.forPort(port)
                .addService(exchangeMessage)
                .build()
                .start();
        logger.info("Running server on port {} ", grpcServer.getPort());
    }

    public ExchangeMessageImpl getExchangeMessage() {
        return exchangeMessage;
    }

    public void setExchangeMessage(ExchangeMessageImpl exchangeMessage) {
        this.exchangeMessage = exchangeMessage;
    }

    public Server getGrpcServer() {
        return grpcServer;
    }

    public void setGrpcServer(Server grpcServer) {
        this.grpcServer = grpcServer;
    }
}
