package org.mule.modules.internal.grpc.config;

import org.mule.modules.internal.grpc.connection.GrpcClientConnectionProvider;
import org.mule.modules.internal.grpc.operations.GrpcOperations;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(GrpcOperations.class)
@ConnectionProviders(GrpcClientConnectionProvider.class)
@Configuration(name = "client-config")
public class GrpcClientConfiguration {
}
