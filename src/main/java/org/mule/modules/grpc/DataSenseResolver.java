package org.mule.modules.grpc;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.mule.api.annotations.MetaDataKeyRetriever;
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

    /**
     * Get MetaData given the Key the user selects
     * 
     * @param key The key selected from the list of valid keys
     * @return The MetaData model of that corresponds to the key
     * @throws Exception If anything fails
     */
    @MetaDataRetriever
    public MetaData getMetaData(MetaDataKey key) throws Exception {
        DefaultMetaDataBuilder builder = new DefaultMetaDataBuilder();
        
        String methodName = key.getId(); 
        
        Descriptor desc = GRPCReflectionUtil.getArgumentProtoDescriptor(connector.getConfig().getBlockingStub().getClass().getName(), methodName);
        
        if (desc != null) {
        	DynamicObjectBuilder<?> dynamicObject = builder.createDynamicObject(desc.getFullName());
        	
        	for (FieldDescriptor fd : desc.getFields()) {
        		String name = fd.getName();
        		DataType dt = decodeDataType(fd.getType());
        		dynamicObject.addSimpleField(name, dt);
        	}
        	return new DefaultMetaData(dynamicObject.build());
        } else {
        	DynamicObjectBuilder<?> dynamicObject = builder.createDynamicObject("Dummy object");
        	dynamicObject.addSimpleField("Somefield", DataType.STRING);
        	return new DefaultMetaData(dynamicObject.build());
        }
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
    		return DataType.POJO;
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
