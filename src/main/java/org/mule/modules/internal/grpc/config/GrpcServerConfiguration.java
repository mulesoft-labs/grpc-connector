package org.mule.modules.internal.grpc.config;

import org.mule.modules.internal.grpc.connection.GrpcServerConnectionProvider;
import org.mule.modules.internal.grpc.source.*;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

@Sources({GrpcMessageReceiver.class,
        GrpcErrorMessageReceiver.class,
        GrpcPingMessageReciever.class,
        GrpcLoadCacheMessageReceiver.class,
        GrpcRefreshMessageReceiver.class})
@ConnectionProviders(GrpcServerConnectionProvider.class)
@Configuration(name = "server-config")
public class GrpcServerConfiguration {
}
