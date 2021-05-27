import ballerina/test;
import ballerina/websub;
import ballerina/log;
import ballerina/http;

listener Listener webhookListener = new (9090);

@websub:SubscriberServiceConfig {
    leaseSeconds: 36000
}
service /subscriber on webhookListener {
    isolated remote function onStartup(StartupMessage message) returns Acknowledgement|StartupError? {
        log:printInfo("[TEST] Received startup-message ", startupMsg = message);
        return {};
    }
    
    isolated remote function onEvent(EventNotification message) returns Acknowledgement? {
        log:printInfo("[TEST] Received event-notification-message ", notificationMsg = message);
        return {};
    }
}

http:Client httpClient = checkpanic new("http://localhost:9090/subscriber");

@test:Config {}
function testStartupMessage() returns @tainted error? {
    http:Request request = new;
    json payload =  {"eventType": "start", "eventData" : { "hubName": "hub1", "subscriberId": "sub1" } };
    request.setPayload(payload);
    http:Response response = check httpClient->post("/", request);
    test:assertEquals(response.statusCode, 202);
}

@test:Config {}
function testEventNotificationMessage() returns @tainted error? {
    http:Request request = new;
    json payload =  {"eventType": "notify", "eventData" : { "hubName": "hub1", "eventId": "event1", "message": "This is a simpl notification" } };
    request.setPayload(payload);
    http:Response response = check httpClient->post("/", request);
    test:assertEquals(response.statusCode, 202);
}
