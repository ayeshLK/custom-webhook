package io.ballerinax.webhook;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.Future;
import io.ballerina.runtime.api.Module;
import io.ballerina.runtime.api.async.Callback;
import io.ballerina.runtime.api.async.StrandMetadata;
import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;

import static io.ballerina.runtime.api.utils.StringUtils.fromString;

public class NativeWebhookAdaptor {
    public static final String SERVICE_OBJECT = "WEBHOOK_SERVICE_OBJECT";

    public static void externInit(BObject adaptor, BObject service) {
        adaptor.addNativeData(SERVICE_OBJECT, service);
    }

    public static Object callOnStartupMethod(Environment env, BObject adaptor,
                                             BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message,
                "callOnStartupMethod", "onStartup");
    }

    public static Object callOnEventMethod(Environment env, BObject adaptor,
                                             BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message,
                "callOnEventMethod", "onEvent");
    }

    private static Object invokeRemoteFunction(Environment env, BObject bSubscriberService, Object message,
                                               String parentFunctionName, String remoteFunctionName) {
        Future balFuture = env.markAsync();
        Module module = ModuleUtils.getModule();
        StrandMetadata metadata = new StrandMetadata(module.getOrg(), module.getName(), module.getVersion(),
                parentFunctionName);
        Object[] args = new Object[]{message, true};
        env.getRuntime().invokeMethodAsync(bSubscriberService,
                remoteFunctionName, null, metadata, new Callback() {
            @Override
            public void notifySuccess(Object result) {
                balFuture.complete(result);
            }

            @Override
            public void notifyFailure(BError bError) {
                BString errorMessage = fromString("service method invocation failed: " + bError.getErrorMessage());
                BError invocationError = ErrorCreator.createError(module, "ServiceExecutionError",
                        errorMessage, bError, null);
                balFuture.complete(invocationError);
            }
        }, args);
        return null;
    }
}
