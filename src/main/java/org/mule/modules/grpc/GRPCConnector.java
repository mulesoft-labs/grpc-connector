package org.mule.modules.grpc;

import java.util.Map;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.api.annotations.param.Payload;
import org.mule.modules.grpc.config.GRPCConfiguration;
import org.mule.modules.grpc.util.GRPCReflectionUtil;

import com.google.protobuf.AbstractMessage;

import io.grpc.stub.AbstractStub;

@Connector(name="grpc", friendlyName="GRPC")
@MetaDataScope( DataSenseResolver.class )
public class GRPCConnector {

    @Config
    GRPCConfiguration config;
    
    
    /**
     * Invoke gRPC service 
     */
    @Processor
    public AbstractMessage invokeBlocking(@MetaDataKeyParam String operation, @Default("#[payload]") Map<String, Object> data) {
        AbstractStub<?> stub = config.getBlockingStub();
    	AbstractMessage message = GRPCReflectionUtil.buildOperationArgument(stub.getClass(), operation, data);
    	return GRPCReflectionUtil.invokeInStub(operation, message, config.getBlockingStub());
    }

    public GRPCConfiguration getConfig() {
        return config;
    }

    public void setConfig(GRPCConfiguration config) {
        this.config = config;
    }

}