package es.redmic.testutils.kafka;

/*-
 * #%L
 * Test utils
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.springframework.beans.factory.annotation.Value;

import es.redmic.testutils.schemaregistry.RestApp;
import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

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
