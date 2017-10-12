package org.mule.modules.grpc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
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
		return retrieveMethodArgument(serviceClass, methodName, null, 0);
	}
	
	private static Class<?> retrieveMethodArgument(Class<?> serviceClass, String methodName, Class<?> filter, int index) {
				
		List<Method> candidates = new LinkedList<>();
		
		for (Method dm : serviceClass.getDeclaredMethods()) {
			if (dm.getName().equals(methodName)) {
				candidates.add(dm);
			}
		}
		
		if (candidates.isEmpty()) { 
			logger.info("Could not find method " + methodName + " in class " + serviceClass.getName());
			return null;
		}
		
		for (Method m : candidates) {		
			Class<?> argument = m.getParameterTypes().length >= index + 1 ? m.getParameterTypes()[index] : null;			
			
			if (argument == null) { 
				//not the right one.
				continue;
			}
			
			if (filter == null) {
				//blindly return whatever we found first.
				return argument;
			}
			
			//this means we have a filter
			if (filter.isAssignableFrom(argument)) {
				return argument;
			} else {
				continue;
			}
		}
		logger.info("method " + methodName + " not found or does not take at least " + index + " argument(s).");
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
		return populateFromMap(arg, data);
	}
	
	@SuppressWarnings("unchecked")
	private static AbstractMessage populateFromMap(Class<?> msgClass, Map<String, Object> data) {
		if (msgClass == null) {
			return null;
		}

		try {
			Object builder = MethodUtils.invokeStaticMethod(msgClass, NEW_BUILDER);
			
			//go through all the fields and set them in the builder.
			for (Entry<String, Object> e : data.entrySet()) {
				String setterName = "set" + StringUtils.capitalize(e.getKey());
				Object value = e.getValue();
				//if we find a map
				if (e.getValue() instanceof Map) {
					Class<?> argClass = retrieveMethodArgument(builder.getClass(), setterName, AbstractMessage.class, 0);
					value = populateFromMap(argClass, (Map<String,Object>) value);
				} else if (e.getValue() instanceof Collection) {
					//should be a collection of maps.
					Collection<Map<String, Object>> c = (Collection<Map<String, Object>>) e.getValue();
					Class<?> argClass = retrieveMethodArgument(builder.getClass(),  setterName, AbstractMessage.class, 1);
					for (Map<String, Object> m : c) {
						value = populateFromMap(argClass, m);
						//invoke the setter for each one
						setterName = "add" + StringUtils.capitalize(e.getKey());
						MethodUtils.invokeMethod(builder, setterName, value);
					}
					continue;
				}
				
				//blindly call the setter and hope it works.
				MethodUtils.invokeMethod(builder, setterName, value);
				
			}
			
			
			return (AbstractMessage) MethodUtils.invokeMethod(builder, BUILD, new Object[0]);
		} catch (Exception ex) {
			logger.error("Error while populating operation argument", ex);
			throw new RuntimeException("Could not build GRPC Message from Map", ex);
		}
	}
	
	
	

	private static Class<?> getReturnType(Class<?> serviceClass, String methodName) {
		List<Method> candidates = new LinkedList<>();
		
		for (Method dm : serviceClass.getDeclaredMethods()) {
			if (dm.getName().equals(methodName)) {
				candidates.add(dm);
			}
		}
		
		if (candidates.isEmpty()) { 
			logger.info("Could not find method " + methodName + " in class " + serviceClass.getName());
			return null;
		}
		
		for (Method m : candidates) {
			return m.getReturnType();
		}
		
		return null;
	}


	public static Descriptor getReturnProtoDescriptor(String serviceClassName, String methodName) {
		Class<?> serviceClass = null;
		try {
			serviceClass = ClassUtils.getClass(serviceClassName);
		} catch (ClassNotFoundException ex) {
			logClassNotFound(serviceClassName, ex);
			return null;
		}		
		
		Class<?> returnType = getReturnType(serviceClass, methodName);
		
		try {
			return (Descriptor) MethodUtils.invokeStaticMethod(returnType, GET_DESCRIPTOR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error while getting descriptor for operation " + methodName);
			return null;
		}
	}
	
}
