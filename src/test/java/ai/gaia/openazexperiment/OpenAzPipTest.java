package ai.gaia.openazexperiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openliberty.openaz.azapi.pep.PepAgent;
import org.openliberty.openaz.azapi.pep.PepAgentFactory;
import org.openliberty.openaz.pdp.sunxacml.FileSystemPolicyLoader;
import org.openliberty.openaz.pdp.sunxacml.SunXacmlService;
import org.openliberty.openaz.pep.PepAgentFactoryImpl;
import org.openliberty.openaz.pip.AttributeRetriever;
import org.openliberty.openaz.pip.GenericAttributeAccessor;
import org.openliberty.openaz.pip.GenericDesignatorAttributeRetriever;

import ai.gaia.openazexperiment.dummypip.DummyRoleAccessor;

import com.sun.xacml.finder.PolicyFinderModule;

public class OpenAzPipTest {

	private static final String POLICY_FILE = "TestPolicy002.xml";
	private PepAgentFactory pepAgentFactory;

	@Before
	public void setUp() throws Exception {
		pepAgentFactory = new PepAgentFactoryImpl();
		SunXacmlService azService = new SunXacmlService();
		Set<PolicyFinderModule> finderModules = new HashSet<PolicyFinderModule>();
		FileSystemPolicyLoader finderModule = new FileSystemPolicyLoader();
		finderModule
				.setCombiningAlgorithm("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:permit-overrides");
		finderModule.setFileNamePattern(POLICY_FILE);
		finderModule.setPolicyLocation("resources/policies");
		finderModules.add(finderModule);
		azService.setPolicyLoaders(finderModules);

		Set<AttributeRetriever> retrievers = new HashSet<AttributeRetriever>();
		GenericDesignatorAttributeRetriever r = new GenericDesignatorAttributeRetriever();
		List<String> manifestFiles = new ArrayList<String>();
		manifestFiles.add("resources/TestManifest.xml");
		r.setManifestFiles(manifestFiles);
		r.setRetrieverId("roleRetriever");
		List<GenericAttributeAccessor> accessors = new ArrayList<GenericAttributeAccessor>();
		accessors.add(new DummyRoleAccessor());
		r.setRepositoryAttributeAccessors(accessors);
		retrievers.add(r);
		azService.setAttributeRetrievers(retrievers);

		azService.initialize();
		((PepAgentFactoryImpl) pepAgentFactory).setAzService(azService);
	}

	@Test
	public void createPepAgent_returnsNonNull() {
		Assert.assertNotNull(getPepAgent());

	}

	private PepAgent getPepAgent() {
		return pepAgentFactory.getPepAgent();
	}

	@Test
	public void requestAccess_UnknownPerson_Deny() {
		String unkownPerson = "Jimmy James Georgetown";
		Assert.assertEquals(false, getPepAgent().simpleDecide(unkownPerson, "GET","/api/v1/results").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(unkownPerson, "GET","/api/v1/specimens").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(unkownPerson, "GET","/api/v1/samplinglocations").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(unkownPerson, "PUT","/api/v1/results").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(unkownPerson, "POST","/api/v1/specimens").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(unkownPerson, "DELETE","/api/v1/samplinglocations").allowed());
	}

	@Test
	public void requestViewAccess_Beginner_Permit() {
		String beginnersName = "Friedbert Hamilton";
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/results").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/samplinglocations").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/specimens").allowed());
		
		Assert.assertEquals(false, getPepAgent().simpleDecide(beginnersName, "PUT","/api/v1/results").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(beginnersName, "POST","/api/v1/specimens").allowed());
		Assert.assertEquals(false, getPepAgent().simpleDecide(beginnersName, "DELETE","/api/v1/samplinglocations").allowed());
	}

	@Test
	public void requestViewTenants_Beginner_Deny() {
		String beginnersName = "Friedbert Hamilton";
		Assert.assertEquals(false, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/tenants").allowed());
	}
	
	@Test
	public void requestViewUsers_Beginner_Deny() {
		String beginnersName = "Friedbert Hamilton";
		Assert.assertEquals(false, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/users").allowed());
	}

	@Test
	public void requestViewAccess_Technician_Permit() {
		String beginnersName = "Sarah Edmonds";
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/results").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/specimens").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/samplinglocations").allowed());
	}

	@Test
	public void requestCRUDActivities_Technician_Permit() {
		String beginnersName = "Sarah Edmonds";
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "GET","/api/v1/activities").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "POST","/api/v1/activities").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "PUT","/api/v1/activities/UUID123").allowed());
		Assert.assertEquals(true, getPepAgent().simpleDecide(beginnersName, "DELETE","/api/v1/activities/UUIDabc").allowed());
	}

}
