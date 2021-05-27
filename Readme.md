# Custom WebHook #

* This is a sample project for custom-webhook library which is written in `ballerina`.

## Prerequisites ##

* Ballerina SwanLake Beta 1+
* OpenJDK 11

## How to Buid

* Run following command.

```bash
    ./gradlew clean build
```

## Sample Webhook Implementation ##

* This is a sample project for writing webhook libraries using `ballerina`. 
* Following is the design of the webhook interface used here.
```ballerina
public type CustomWebhookService service object {
    remote function onStartup(StartupMessage message) returns Acknowledgement|StartupError?;
    
    remote function onEvent(EventNotification message) returns Acknowledgement?;
};
```

* You could use this library as follows.
```ballerina
import ballerinax/webhook;
import ballerina/websub;

listener webhook:Listener webhookListener = new (9090);

@websub:SubscriberServiceConfig {
    leaseSeconds: 36000
}
service /subscriber on webhookListener {
    isolated remote function onStartup(webhook:StartupMessage message) returns webhook:Acknowledgement|webhook:StartupError? {
        log:printInfo("Received startup-message ", startupMsg = message);
        // implement event related validations here
        return {};
    }
    
    isolated remote function onEvent(webhook:EventNotification message) returns webhook:Acknowledgement? {
        log:printInfo("Received event-notification-message ", notificationMsg = message);
        // implement event related validations here
        return {};
    }
}
```