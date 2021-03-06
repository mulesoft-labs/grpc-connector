package org.mule.modules.grpc;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.mule.api.annotations.MetaDataKeyRetriever;
import org.mule.api.annotations.MetaDataOutputRetriever;
import org.mule.api.annotations.MetaDataRetriever;
import org.mule.api.annotations.components.MetaDataCategory;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.builder.DefaultMetaDataBuilder;
import org.mule.common.metadata.builder.DynamicObjectBuilder;
import org.mule.common.metadata.datatype.DataType;
import org.mule.modules.grpc.util.GRPCReflectionUtil;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;

/**
 * Category which can differentiate between input or output MetaDataRetriever
 */
@MetaDataCategory
public class DataSenseResolver {

    @Inject
    private GRPCConnector connector;

    /**
     * Retrieves the list of keys
     */
    @MetaDataKeyRetriever
    public List<MetaDataKey> getMetaDataKeys() throws Exception {
        List<MetaDataKey> keys = new ArrayList<MetaDataKey>();
        
        List<String> methods = GRPCReflectionUtil.listPublicMethods(connector.getConfig().getBlockingStub());
        
        for(String s : methods) {
        	keys.add(new DefaultMetaDataKey(s, s));
        }

        return keys;
    }
    
    
    @MetaDataOutputRetriever
    public MetaData getMetaDataOutput(MetaDataKey key) throws Exception {
        String methodName = key.getId(); 
        Descriptor desc = GRPCReflectionUtil.getReturnProtoDescriptor(getConnector().getConfig().getBlockingStub().getClass().getName(), methodName);
        return getMetaData(desc);
    }
    
    
    
    /**
     * Get MetaData given the Key the user selects
     * 
     * @param key The key selected from the list of valid keys
     * @return The MetaData model of that corresponds to the key
     * @throws Exception If anything fails
     */
    @MetaDataRetriever
    public MetaData getMetaData(MetaDataKey key) throws Exception {
        String methodName = key.getId(); 
        Descriptor desc = GRPCReflectionUtil.getArgumentProtoDescriptor(getConnector().getConfig().getBlockingStub().getClass().getName(), methodName);
        return getMetaData(desc);
    }
    
    private MetaData getMetaData(Descriptor desc) throws Exception {
        DefaultMetaDataBuilder builder = new DefaultMetaDataBuilder();
                
        if (desc != null) {
        	DefaultMetaData md = new DefaultMetaData(createDynamicObjectFromDescriptor(builder.createDynamicObject(desc.getFullName()), desc));
        	return md;
        } else {
        	DynamicObjectBuilder<?> dynamicObject = builder.createDynamicObject("Unknown object");
        	return new DefaultMetaData(dynamicObject.build());
        }
    }
    
    private MetaDataModel createDynamicObjectFromDescriptor(DynamicObjectBuilder<?> dynamicObject, Descriptor desc) {
    	
    	for (FieldDescriptor fd : desc.getFields()) {
    		String name = fd.getName();
    		DataType dt = decodeDataType(fd.getType());
    		if (dt == DataType.MAP) {
    			if (fd.isRepeated()) {
    				createDynamicObjectFromDescriptor(dynamicObject.addList(name).ofDynamicObject(name), fd.getMessageType());
    			} else {
    				createDynamicObjectFromDescriptor(dynamicObject.addDynamicObjectField(name), fd.getMessageType());
    			}
    		} else {
    			dynamicObject.addSimpleField(name, dt);
    		}    		
    	}
    	return dynamicObject.build();
    }
    
    
    private DataType decodeDataType(Type type) {
		
    	switch (type) {
    	case BOOL:
    		return DataType.BOOLEAN;
    	case BYTES:
    		return DataType.BYTE;
    	case DOUBLE:
    		return DataType.DOUBLE;
    	case ENUM:
    		return DataType.ENUM;
    	case FIXED32:
    		return DataType.INTEGER;
    	case FIXED64:
    		return DataType.LONG;
    	case FLOAT:
    		return DataType.FLOAT;
    	case INT32:
    	case INT64:
    		return DataType.INTEGER;
    	case MESSAGE:
    		return DataType.MAP;
    	case STRING:
    		return DataType.STRING;
    	default:
    		break;
    	}
    	
		return DataType.POJO;
	}

	public GRPCConnector getConnector() {
        return connector;
    }

    public void setConnector(GRPCConnector connector) {
        this.connector = connector;
    }
}
