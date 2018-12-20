package org.mule.modules.internal.grpc.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alias("source-connection")
public class GrpcServerConnectionProvider implements ConnectionProvider<GrpcServerConnection> {
    private final Logger logger = LoggerFactory.getLogger(GrpcServerConnectionProvider.class);

    /**
     * A parameter that is always required to be configured.
     */
    @Parameter
    @DisplayName("Grpc Server Host")
    @Optional(defaultValue = "localhost")
    private String host;
    @Parameter
    @Optional(defaultValue = "50051")
    private int port;

    private boolean initialized = false;
    GrpcServerConnection serverConnection;

    @Override
    public GrpcServerConnection connect() throws ConnectionException {
        if (!initialized && serverConnection == null) {
            logger.info("Starting grpc server with host " + host);
            initialized = true;
            serverConnection = new GrpcServerConnection(host, port);
            try {
                serverConnection.startServer();
            } catch (Exception e) {
                logger.error("Error in starting grpc server ", e.getMessage(), e);
                throw new ConnectionException(e.getMessage());
            }
        } else {
            logger.debug("Server is already up ----------------> ");
        }
        return serverConnection;
    }

    @Override
    public void disconnect(GrpcServerConnection grpcServerConnection) {
        try {
            //grpcServerConnection.invalidate();
        } catch (Exception e) {
            logger.error("Error while disconnecting [" + grpcServerConnection.getHost() + "]: " + e.getMessage(), e);
        }
    }

    @Override
    public ConnectionValidationResult validate(GrpcServerConnection grpcServerConnection) {

        return ConnectionValidationResult.success();
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


}
