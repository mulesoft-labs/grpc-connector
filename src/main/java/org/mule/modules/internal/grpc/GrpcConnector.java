package org.mule.modules.internal.grpc;

import org.mule.modules.internal.grpc.config.GrpcClientConfiguration;
import org.mule.modules.internal.grpc.config.GrpcServerConfiguration;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "grpc")
@Extension(name = "Grpc")
@Configurations({GrpcClientConfiguration.class, GrpcServerConfiguration.class})
public class GrpcConnector {

}
