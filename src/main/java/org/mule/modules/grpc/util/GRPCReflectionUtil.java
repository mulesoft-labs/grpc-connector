package org.mule.modules.grpc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.mule.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.AbstractMessage;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.AbstractStub;

public class GRPCReflectionUtil {
	
	private static final String NEW_BLOCKING_STUB = "newBlockingStub";
	private static final Logger logger = LoggerFactory.getLogger(GRPCReflectionUtil.class);
	
	public static AbstractStub<?> getBlockingStub(String serviceClassName, ManagedChannel channel) {
		
		try {
			Class<?> cls = ClassUtils.getClass(serviceClassName); 
			return (AbstractStub<?>) MethodUtils.invokeStaticMethod(cls, NEW_BLOCKING_STUB, channel);
		} catch (ClassNotFoundException ex) {
			logClassNotFound(serviceClassName, ex);
		} catch (NoSuchMethodException ex) {
			logMethodNotFound(NEW_BLOCKING_STUB, ex);
		} catch (InvocationTargetException ex) {
			logInvocationError(NEW_BLOCKING_STUB, ex);
		} catch (Exception ex) {
			logger.error("Error while executing method by reflection", ex);
		}
		return null;
	}
	
	public static AbstractMessage invokeInStub(String method, AbstractMessage argument, AbstractStub<?> stub) throws StatusRuntimeException {
		try {
			Method m = stub.getClass().getMethod(method, argument.getClass());
			return (AbstractMessage) m.invoke(stub, argument);
		} catch (NoSuchMethodException ex) {
			logMethodNotFound(method, ex);
		} catch (InvocationTargetException ex) {
			logInvocationError(method, ex);
			
			if (ex.getCause() instanceof StatusRuntimeException) {
				StatusRuntimeException sre = (StatusRuntimeException) ex.getCause();
				throw sre;
			}
			
		} catch (Exception ex) {
			logger.error("Error while calling method " + method, ex);
		}
		return null;
	}
	
	private static void logClassNotFound(String name, Throwable cause) {
		logger.error("Could not load gRPC Service Class " + name, cause);
	}
	
	private static void logMethodNotFound(String name, Throwable cause) {
		logger.error("Could not find gRPC Service Method " + name, cause);
	}	
	
	private static void logInvocationError(String name, Throwable cause) {
		logger.error("Could not invoke gRPC Service Method " + name, cause);
	}
}
