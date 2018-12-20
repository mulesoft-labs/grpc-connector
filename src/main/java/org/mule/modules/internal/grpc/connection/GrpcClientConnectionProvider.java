package org.mule.modules.internal.grpc.connection;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import org.mule.runtime.api.connection.*;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */
@Alias("Client-Connection")
public class GrpcClientConnectionProvider implements ConnectionProvider<GrpcClientConnection> {

    private final Logger LOGGER = LoggerFactory.getLogger(GrpcClientConnectionProvider.class);

    @Parameter
    @DisplayName("Grpc Server Host")
    @Optional(defaultValue = "localhost")
    private String host;
    @Parameter
    @Optional(defaultValue = "50051")
    private int port;
    @DisplayName("Frames per message")
    @Parameter
    @Optional(defaultValue = "50")
    private int frames;
    @DisplayName("Use Text Plain")
    @Parameter
    @Optional(defaultValue = "true")
    private boolean useTextPlain;

    private boolean initialized = false;
    @Override
    public GrpcClientConnection connect() throws ConnectionException {
        return new GrpcClientConnection(host,port,frames);
    }

    @Override
    public void disconnect(GrpcClientConnection connection) {
        try {
            connection.invalidate();
        } catch (Exception e) {
            LOGGER.error("Error while disconnecting [" + connection.getHost() + "]: " + e.getMessage(), e);
        }
    }

    @Override
    public ConnectionValidationResult validate(GrpcClientConnection connection) {
        LOGGER.info("Establishing connection with grpcServer at host {}, port {}", connection.getHost(), connection.getPort());
        ManagedChannel managedChannel = connection.getManagedChannel();
        ConnectivityState state = managedChannel.getState(true);
        LOGGER.info("Grpc Connection State {} ", state);
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

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }
}
