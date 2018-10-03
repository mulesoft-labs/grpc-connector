package org.mule.modules.grpc.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;

@Configuration(friendlyName = "Configuration")
public class GrpcConfiguration {

    @Configurable
    @Default("true")
   private boolean usePlainText;
    @Configurable
    @Default("localhost")
    private String host;
    @Configurable
    @Default("50051")
    private int port;
    /**
     * Frame size to configure grpc connector, the maximum number of bytes a single message can be e.g. 50 = 50MB
     */

    @Configurable
    @Default("50")

    private String frameSize;

    public boolean isUsePlainText() {
        return usePlainText;
    }

    public void setUsePlainText(boolean usePlainText) {
        this.usePlainText = usePlainText;
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

    public String getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(String frameSize) {
        this.frameSize = frameSize;
    }
}