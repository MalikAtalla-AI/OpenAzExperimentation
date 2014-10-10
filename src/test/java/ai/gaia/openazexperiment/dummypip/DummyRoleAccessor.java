package ai.gaia.openazexperiment.dummypip;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pip.AttributeValue;
import org.openliberty.openaz.pip.GenericAttributeAccessor;
import org.openliberty.openaz.pip.manifest.AttributeDefinitionType;
import org.openliberty.openaz.pip.manifest.DomainContextType;

public class DummyRoleAccessor implements GenericAttributeAccessor {
	
	private static final String ROLE_ACCESSOR_KEY = "RoleAccessor"; 
	
	private static final Properties props = new Properties();
	
	private static final Log log = LogFactory.getLog(DummyRoleAccessor.class);
	
	static{
		props.setProperty("Julius Hibbert", "Physician");
		props.setProperty("Edna Krabappel", "Teacher");
	}
	
	public AttributeValue<String> retrieveAttribute(
			AttributeDefinitionType<DomainContextType> attributeDefinition,
			Map<String, List<String>> dependentAttributeMap){
		List<String> subjectIds = dependentAttributeMap.get("subjectId");
		AttributeValue<String> roles = AttributeValue.newInstance();
		roles.addValue((String)props.get(subjectIds.get(0)));
		return roles;
	}

	public String getAttributeAccessorKey() {
		return ROLE_ACCESSOR_KEY;
	}
}