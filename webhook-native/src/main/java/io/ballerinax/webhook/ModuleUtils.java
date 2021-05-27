package io.ballerinax.webhook;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.Module;


public class ModuleUtils {

    private static Module webhookModule;

    private ModuleUtils() {}

    public static void setModule(Environment environment) {
        webhookModule = environment.getCurrentModule();
    }

    public static Module getModule() {
        return webhookModule;
    }
}
