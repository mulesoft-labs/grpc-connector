package org.mule.modules.grpc.config;

import java.util.concurrent.TimeUnit;

import org.mule.api.ConnectionException;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.modules.grpc.util.GRPCReflectionUtil;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;

@ConnectionManagement(friendlyName="gRPC Connection", configElementName="config")
public class GRPCConfiguration {
	
	
	//connection management
	private ManagedChannel channel;
	private AbstractStub<?> blockingStub;
	
	
	
	@Configurable
	@Default("true")
	private boolean usePlainText;
	
	@Configurable
	@Default("")
	private String serviceClass;
	
	private String host;
	private int port;
	
	
	@Connect
	@TestConnectivity
	public void connect(@ConnectionKey String host, @ConnectionKey Integer port) throws ConnectionException {		
		ManagedChannelBuilder<?> b = ManagedChannelBuilder.forAddress(host, port);
		b.usePlaintext(usePlainText);		
		channel = b.build();
		blockingStub = GRPCReflectionUtil.getBlockingStub(serviceClass, channel);
	}
	
	@Disconnect
	public void disconnect() {
		try {
			channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			channel.shutdownNow();
		}
	}
	
	@ValidateConnection
	public boolean isConnected() {
		
		if (channel == null) {
			return false;
		}
		
		ConnectivityState st = channel.getState(true);		
		return st == ConnectivityState.READY;
	}
	
	@ConnectionIdentifier
	public String connectionIdentifier() {
		return host + ":" + port; //one channel per host.
	}
	
	public boolean isUsePlainText() {
		return usePlainText;
	}

	public void setUsePlainText(boolean usePlainText) {
		this.usePlainText = usePlainText;
	}

	public ManagedChannel getChannel() {
		return channel;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public AbstractStub<?> getBlockingStub() {
		return blockingStub;
	}
	
}
