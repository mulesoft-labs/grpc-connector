package org.mule.modules.grpc;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.api.transformer.TransformerException;
import org.mule.modules.grpc.config.GRPCConfiguration;
import org.mule.modules.grpc.util.GRPCReflectionUtil;
import org.mule.transformer.types.SimpleDataType;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

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
    
    @Processor
    public void convertMessageToJson(MuleMessage mmsg) throws InvalidProtocolBufferException, TransformerException {
    	 String payload = JsonFormat.printer().print(mmsg.getPayload(AbstractMessage.class));
    	 mmsg.setPayload(payload, new SimpleDataType<String>(java.lang.String.class, "application/json"));
    }

    public GRPCConfiguration getConfig() {
        return config;
    }

    public void setConfig(GRPCConfiguration config) {
        this.config = config;
    }

}