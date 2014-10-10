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
import org.openliberty.openaz.azapi.pep.PepResponse;
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
	public void testPermit() {
		PepResponse response = getPepAgent().simpleDecide("Julius Hibbert", "read",
				"http://medico.com/record/patient/BartSimpson");
		Assert.assertNotNull(response);
		Assert.assertEquals(true, response.allowed());
	}

	@Test
	public void testDeny() {
		PepResponse response = getPepAgent().simpleDecide("Seymour Skinner", "read",
				"http://medico.com/record/patient/BartSimpson");
		Assert.assertNotNull(response);
		Assert.assertEquals(false, response.allowed());
	}

}
