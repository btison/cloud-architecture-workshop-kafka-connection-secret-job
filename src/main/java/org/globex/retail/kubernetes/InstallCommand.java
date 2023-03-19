package org.globex.retail.kubernetes;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;

@QuarkusMain
public class InstallCommand implements QuarkusApplication {

    @Inject
    KubernetesInstaller installer;

    @Override
    public int run(String... args) throws Exception {
        return installer.install();
    }
}
