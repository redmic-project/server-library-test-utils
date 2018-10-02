package es.redmic.testutils.schemaregistry;

import java.util.Properties;

import org.eclipse.jetty.server.Server;

import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.exceptions.SchemaRegistryException;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryRestApplication;
import io.confluent.kafka.schemaregistry.storage.SchemaRegistry;
import io.confluent.kafka.schemaregistry.storage.SchemaRegistryIdentity;

public class RestApp {

	public final Properties prop;
	public RestService restClient;
	public SchemaRegistryRestApplication restApp;
	public Server restServer;
	public String restConnect;

	public RestApp(int port, String url, String zkConnect, String bootstrapBrokers, String compatibilityType) {
		this(port, url, zkConnect, bootstrapBrokers, compatibilityType, null);
	}

	public RestApp(int port, String url, String zkConnect, String bootstrapBrokers, String compatibilityType,
			Properties schemaRegistryProps) {

		bootstrapBrokers = bootstrapBrokers.replaceAll("127", "PLAINTEXT://127");

		prop = new Properties();
		if (schemaRegistryProps != null) {
			prop.putAll(schemaRegistryProps);
		}
		prop.setProperty(SchemaRegistryConfig.PORT_CONFIG, ((Integer) port).toString());
		if (zkConnect != null) {
			prop.setProperty(SchemaRegistryConfig.KAFKASTORE_CONNECTION_URL_CONFIG, zkConnect);
		}
		if (bootstrapBrokers != null) {
			prop.setProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, bootstrapBrokers);
		}
		prop.put(SchemaRegistryConfig.KAFKASTORE_TOPIC_REPLICATION_FACTOR_CONFIG, 1);
		prop.put(SchemaRegistryConfig.KAFKASTORE_TOPIC_CONFIG, SchemaRegistryConfig.DEFAULT_KAFKASTORE_TOPIC);
		prop.put(SchemaRegistryConfig.COMPATIBILITY_CONFIG, compatibilityType);
		prop.put(SchemaRegistryConfig.MASTER_ELIGIBILITY, true);
		prop.put(SchemaRegistryConfig.LISTENERS_CONFIG, url);
	}

	public void start() throws Exception {
		restApp = new SchemaRegistryRestApplication(prop);
		restServer = restApp.createServer();
		restServer.start();
		restConnect = restServer.getURI().toString();
		if (restConnect.endsWith("/"))
			restConnect = restConnect.substring(0, restConnect.length() - 1);
		restClient = new RestService(restConnect);
	}

	public void stop() throws Exception {
		restClient = null;
		if (restServer != null) {
			restServer.stop();
			restServer.join();
		}
	}

	/**
	 * This method must be called before calling {@code RestApp.start()} for the
	 * additional properties to take affect.
	 *
	 * @param props
	 *            the additional properties to set
	 */
	public void addConfigs(Properties props) {
		prop.putAll(props);
	}

	public boolean isMaster() {
		return restApp.schemaRegistry().isMaster();
	}

	public void setMaster(SchemaRegistryIdentity schemaRegistryIdentity) throws SchemaRegistryException {
		restApp.schemaRegistry().setMaster(schemaRegistryIdentity);
	}

	public SchemaRegistryIdentity myIdentity() {
		return restApp.schemaRegistry().myIdentity();
	}

	public SchemaRegistryIdentity masterIdentity() {
		return restApp.schemaRegistry().masterIdentity();
	}

	public SchemaRegistry schemaRegistry() {
		return restApp.schemaRegistry();
	}
}
