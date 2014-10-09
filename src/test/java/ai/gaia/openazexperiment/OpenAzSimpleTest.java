package ai.gaia.openazexperiment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openliberty.openaz.azapi.pep.PepAgent;
import org.openliberty.openaz.azapi.pep.PepAgentFactory;
import org.openliberty.openaz.pdp.sunxacml.SunXacmlService;
import org.openliberty.openaz.pep.PepAgentFactoryImpl;

public class OpenAzSimpleTest {

	private PepAgentFactory pepAgentFactory;
	
	@Before
	public void setUp() throws Exception {
		pepAgentFactory = new PepAgentFactoryImpl();
		((PepAgentFactoryImpl)pepAgentFactory).setAzService(new SunXacmlService());
	}

	@Test
	public void createPepAgent_returnsNonNull() {
		Assert.assertNotNull(getPepAgent());
		
	}
	
	private PepAgent getPepAgent() {
		return pepAgentFactory.getPepAgent();
	}

}
