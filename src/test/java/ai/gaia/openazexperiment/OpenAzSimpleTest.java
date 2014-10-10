package ai.gaia.openazexperiment;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openliberty.openaz.azapi.pep.PepAgent;
import org.openliberty.openaz.azapi.pep.PepAgentFactory;
import org.openliberty.openaz.pdp.sunxacml.FileSystemPolicyLoader;
import org.openliberty.openaz.pdp.sunxacml.SunXacmlService;
import org.openliberty.openaz.pep.PepAgentFactoryImpl;

import com.sun.xacml.finder.PolicyFinderModule;

public class OpenAzSimpleTest {

	private static final String POLICY_FILE = "TestPolicy001.xml";
	private PepAgentFactory pepAgentFactory;
	
	@Before
	public void setUp() throws Exception {
		pepAgentFactory = new PepAgentFactoryImpl();
		SunXacmlService azService = new SunXacmlService();
		Set<PolicyFinderModule> finderModules = new HashSet<PolicyFinderModule>();
		FileSystemPolicyLoader finderModule = new FileSystemPolicyLoader();
		finderModule.setCombiningAlgorithm("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:permit-overrides");
		finderModule.setFileNamePattern(POLICY_FILE);
		finderModule.setPolicyLocation("resources/policies");
		finderModules.add(finderModule);
		azService.setPolicyLoaders(finderModules);
		azService.initialize();
		((PepAgentFactoryImpl)pepAgentFactory).setAzService(azService);
	}

	@Test
	public void createPepAgent_returnsNonNull() {
		Assert.assertNotNull(getPepAgent());
		
	}
	
	private PepAgent getPepAgent() {
		return pepAgentFactory.getPepAgent();
	}

	@Test
	public void testAllPermit(){
		String personWithAllAccess = "Amit Nath";
		Assert.assertEquals(true, getPepAgent().simpleDecide(personWithAllAccess,"GET", "/api/v1/projects").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(personWithAllAccess,"PUT", "/api/v1/projects/e5354e0e-1fa5-4749-ac6e-089fd43a0c31").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(personWithAllAccess,"POST", "/api/v1/projects").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(personWithAllAccess,"DELETE", "/api/v1/projects/e5354e0e-1fa5-4749-ac6e-089fd43a0c31").allowed());
	}
	
	@Test
	public void testAllDeny(){
		String personWithNoAccess = "Lisa S.";
		Assert.assertEquals(false, getPepAgent().simpleDecide(personWithNoAccess,"GET", "/api/v1/projects").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(personWithNoAccess,"PUT", "/api/v1/projects/e5354e0e-1fa5-4749-ac6e-089fd43a0c31").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(personWithNoAccess,"POST", "/api/v1/projects").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(personWithNoAccess,"DELETE", "/api/v1/projects/e5354e0e-1fa5-4749-ac6e-089fd43a0c31").allowed());
	}
	
	@Test
	public void testOnlyViewingAndEditingPermitted(){
		String personWithNoAccess = "Sarah Edmonds";
		Assert.assertEquals(true, getPepAgent().simpleDecide(personWithNoAccess,"GET", "/api/v1/projects").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(personWithNoAccess,"PUT", "/api/v1/projects/e5354e0e-1fa5-4749-ac6e-089fd43a0c31").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(personWithNoAccess,"POST", "/api/v1/projects").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(personWithNoAccess,"DELETE", "/api/v1/projects/e5354e0e-1fa5-4749-ac6e-089fd43a0c31").allowed());
	}

}
