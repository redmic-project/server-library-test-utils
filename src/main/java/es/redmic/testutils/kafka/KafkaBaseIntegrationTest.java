package es.redmic.testutils.kafka;

import org.springframework.beans.factory.annotation.Value;

import es.redmic.testutils.schemaregistry.RestApp;
import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

public abstract class KafkaBaseIntegrationTest {

	@Value("${spring.kafka.properties.schema.registry.url}")
	protected String SCHEMA_REGISTRY_URL;

	protected RestApp restApp = null;

	protected void createSchemaRegistryRestApp(String zookeeperConnection, String brokers) throws Exception {

		restApp = new RestApp(Integer.parseInt(SCHEMA_REGISTRY_URL.split(":")[2]), SCHEMA_REGISTRY_URL,
				zookeeperConnection, brokers, AvroCompatibilityLevel.BACKWARD_TRANSITIVE.name);
		restApp.start();
	}
}
