package es.redmic.testutils.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;

import es.redmic.testutils.schemaregistry.RestApp;
import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

@DirtiesContext
public abstract class KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	@Value("${spring.kafka.properties.schema.registry.url}")
	protected String SCHEMA_REGISTRY_URL;

	protected static RestApp restApp = null;

	protected void createSchemaRegistryRestApp(String zookeeperConnection, String brokers) {

		logger.info("Arrancando schema registry rest app en la url " + SCHEMA_REGISTRY_URL);

		restApp = new RestApp(Integer.parseInt(SCHEMA_REGISTRY_URL.split(":")[2]), SCHEMA_REGISTRY_URL,
				zookeeperConnection, brokers, AvroCompatibilityLevel.BACKWARD_TRANSITIVE.name);

		try {
			restApp.start();
		} catch (Exception e) {

			logger.debug("Servidor arrancado. Descartando acción");
		}
	}

	@AfterClass
	public static void removeSchemaRegistryRestApp() throws Exception {

		logger.info("Parando schema registry rest app");

		// restApp.stop();
	}
}
