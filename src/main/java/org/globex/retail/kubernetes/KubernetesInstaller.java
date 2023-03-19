package org.globex.retail.kubernetes;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class KubernetesInstaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesInstaller.class);

    @Inject
    KubernetesClient client;

    public int install() {

        String kafkaNamespace = System.getenv("KAFKA_NAMESPACE");
        String kafkaClientConnectionSecret = getEnv("Kafka_CLIENT_CONNECTION_SECRET", "kafka-client-secret");
        if (kafkaNamespace == null || kafkaNamespace.isBlank()) {
            LOGGER.error("Environment variable 'KAFKA_NAMESPACE' for kafka namespace not set. Exiting...");
            return -1;
        }

        String namespace = System.getenv("NAMESPACE");
        if (namespace == null || namespace.isBlank()) {
            LOGGER.error("Environment variable 'NAMESPACE' for namespace not set. Exiting...");
            return -1;
        }

        Secret clientConnectionSecret = client.secrets().inNamespace(kafkaNamespace).withName(kafkaClientConnectionSecret).get();
        if (clientConnectionSecret == null) {
            LOGGER.error("Secret " + kafkaClientConnectionSecret + " not found in namespace " + kafkaClientConnectionSecret);
            return -1;
        }

        Secret newSecret = new SecretBuilder().withNewMetadata().withName(kafkaClientConnectionSecret).endMetadata()
                .addToData(clientConnectionSecret.getData()).build();
        client.secrets().inNamespace(namespace).resource(newSecret).createOrReplace();
        return 0;
    }

    private String getEnv(String name, String defaultVal) {
        String env = System.getenv(name);
        if (env == null) {
            return defaultVal;
        }
        return env;
    }

}
