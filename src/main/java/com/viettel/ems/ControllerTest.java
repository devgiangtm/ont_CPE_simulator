package com.viettel.ems;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ControllerTest {
    @PostMapping({"/testMQTT"})
    public String testUpdateAlarms() throws Exception {
        IMqttClient client = new MqttClient("tcp://172.16.30.126:1883", "FM.gateway1");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("giangtm1");
        options.setPassword("123456a@".toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        client.connect(options);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                log.info("connectionLost" + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                log.info("messageArrived {}, {}",topic,message.getPayload());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                log.info("deliveryComplete");

            }
        });
        MqttMessage msg;
        // byte[] payload = item.getBytes();
        String sjon = "{\n" + "    \"DeviceId\": {\n" + "        \"Manufacturer\": \"abc\",\n"
            + "        \"ProductClass\": \"abc\",\n" + "        \"SerialNumber\": \"abc\"\n" + "    },\n"
            + "    \"Logs\": [\n"
            + "        \"<1>1 2003-10-11T22:14:15.003Z mymachine.example.com evntslog 5421 ID47 [exampleSDID@32473 iut=’3’ eventSource=’Application’ eventID=’1011’] BOMAn application event log entry...\"\n"
            + "    ]\n" + "}";
        byte[] payload = String.format(sjon)
            .getBytes();
        msg = new MqttMessage(payload);
        msg.setQos(0);
        msg.setRetained(true);
        try {
            client.publish("test", msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.disconnect();
        return "ok";
    }
}
