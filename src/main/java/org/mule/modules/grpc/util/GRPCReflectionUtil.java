package org.mule.modules.grpc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.mule.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors.Descriptor;

import io.grpc.ManagedChannel;
import io.grpc.ServiceDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.AbstractStub;

public class GRPCReflectionUtil {
	
	private static final String BUILD = "build";
	private static final String NEW_BUILDER = "newBuilder";
	private static final String GET_DESCRIPTOR = "getDescriptor";
	private static final String GET_SERVICE_DESCRIPTOR = "getServiceDescriptor";
	private static final String NEW_BLOCKING_STUB = "newBlockingStub";
	private static final Logger logger = LoggerFactory.getLogger(GRPCReflectionUtil.class);
	
	/**
	 * Get the blocking sub for a service using the given channel. 
	 * @param serviceClassName
	 * @param channel
	 * @return
	 */
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
	
	
	/**
	 * Invoke the given operation in the provided stub. 
	 * @param method
	 * @param argument
	 * @param stub
	 * @return
	 * @throws StatusRuntimeException
	 */
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
	
	
	/**
	 * Get the public methods of an object.
	 * @param o
	 * @return
	 */
	public static List<String> listPublicMethods(Object o) {
		
		Class<?> cls = o.getClass();
		
		List<String> ret = new LinkedList<>();
		
		try {
			
			for(Method m : cls.getDeclaredMethods()) {
				
				if (Modifier.isPublic(m.getModifiers())) {
					ret.add(m.getName());
				}
			}
			
		} catch (Exception ex) {
			logger.error("Error while introspecting class " + cls, ex);
		}
		
		return ret;
	}
	
	/**
	 * Get the service descriptor for a particular service class name.
	 * @param serviceClassName
	 * @return
	 */
	public static ServiceDescriptor getServiceDescriptor(String serviceClassName) {
		try {
			Class<?> cls = ClassUtils.getClass(serviceClassName);
			Method m = cls.getMethod(GET_SERVICE_DESCRIPTOR);
			return (ServiceDescriptor) m.invoke(null);
		} catch (ClassNotFoundException ex) {
			logClassNotFound(serviceClassName, ex);
		} catch (NoSuchMethodException ex) {
			logMethodNotFound(GET_SERVICE_DESCRIPTOR, ex);
		} catch (InvocationTargetException ex) {
			logInvocationError(GET_SERVICE_DESCRIPTOR, ex);
		} catch (Exception ex) {
			logger.error("Could not retrieve service description", ex);
		}
		return null;
	}
	
	
	/**
	 * Get the descriptor of the protocol buffer that serves as argument of a service method..
	 * @param serviceClassName
	 * @param methodName
	 * @return
	 */
	public static Descriptor getArgumentProtoDescriptor(String serviceClassName, String methodName) {
		
		Class<?> serviceClass = null;
		try {
			serviceClass = ClassUtils.getClass(serviceClassName);
		} catch (ClassNotFoundException ex) {
			logClassNotFound(serviceClassName, ex);
			return null;
		}

		try {
			Class<?> argument = retrieveMethodArgument(serviceClass, methodName);
			if (argument == null) {
				return null;
			}
			Method metadata = argument.getMethod(GET_DESCRIPTOR);
			return (Descriptor) metadata.invoke(null);
		} catch (NoSuchMethodException ex) {
			logMethodNotFound(GET_SERVICE_DESCRIPTOR, ex);
		} catch (InvocationTargetException ex) {
			logInvocationError(GET_SERVICE_DESCRIPTOR, ex);
		} catch (Exception ex) {
			logger.error("Could not retrieve service description", ex);
		}
		return null;
	}


	private static Class<?> retrieveMethodArgument(Class<?> serviceClass, String methodName) {
				
		Method m = null;
		
		for (Method dm : serviceClass.getDeclaredMethods()) {
			if (dm.getName().equals(methodName)) {
				m = dm;
				break;
			}
		}
		
		if (m == null) { 
			return null;
		}
		
		Class<?> argument = m.getParameterTypes().length == 1 ? m.getParameterTypes()[0] : null;
		
		if (argument == null) { 
			logger.info("gRPC method does not take argument");
			return null;
		}
		
		return argument;
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

	public static AbstractMessage buildOperationArgument(String serviceClassName, String methodName, Map<String, Object> data) {
		Class<?> cls = null;
		try {
			cls = ClassUtils.getClass(serviceClassName);
		} catch (ClassNotFoundException ex) {
			logClassNotFound(serviceClassName, ex);
			return null;
		}
		
		return buildOperationArgument(cls, methodName, data);
	}
	
	
	public static AbstractMessage buildOperationArgument(Class<?> serviceClass, String methodName, Map<String, Object> data) {
		
		Class<?> arg = retrieveMethodArgument(serviceClass, methodName);
		
		if (arg == null) {
			return null;
		}
		
		try {
			Object builder = MethodUtils.invokeStaticMethod(arg, NEW_BUILDER);
			
			//go through all the fields and set them in the builder.
			for (Entry<String, Object> e : data.entrySet()) {
				//blindly call the setter.
				builder = MethodUtils.invokeMethod(builder, "set" + StringUtils.capitalize(e.getKey()), e.getValue());	
			}
			
			
			return (AbstractMessage) MethodUtils.invokeMethod(builder, BUILD, new Object[0]);
		} catch (Exception ex) {
			logger.error("Error while populating operation argument", ex);
		}
		
		return null;
	}
}
