package com.viettel.ems;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext appContext = SpringApplication.run(MainApplication.class, args);
        SimulatorService bean = appContext.getBean(SimulatorService.class);
        new Thread(()->{
            bean.runOne();
        }).start();
        // System.out.println("Kaka");
        // String topic = "MQTT-Examples";
        // String content = "Message from MqttPublishSample";
        // int qos = 2;
        // String broker = "tcp://172.16.30.126:1884";
        // String clientId = "giangtm1";
        // MemoryPersistence persistence = new MemoryPersistence();
        // for (int i = 1; i <= 65000; i++){
        //     int finalI = i;
        //     new Thread(()->{
        //         try {
        //             MqttClient sampleClient = new MqttClient(broker, String.valueOf(finalI), persistence);
        //             MqttConnectOptions connOpts = new MqttConnectOptions();
        //             // connOpts.setCleanSession(true);
        //             // System.out.println("Connecting to broker: " + broker);
        //             sampleClient.connect(connOpts);
        //             System.out.println(finalI + " Connected");
        //             // System.out.println("Publishing message: "+content);
        //             // MqttMessage message = new MqttMessage(content.getBytes());
        //             // message.setQos(qos);
        //             // sampleClient.publish(topic, message);
        //             // System.out.println("Message published");
        //             // sampleClient.disconnect();
        //             // System.out.println("Disconnected");
        //             // System.exit(0);
        //         } catch (MqttException me) {
        //             System.out.println("reason " + me.getReasonCode());
        //             System.out.println("msg " + me.getMessage());
        //             System.out.println("loc " + me.getLocalizedMessage());
        //             System.out.println("cause " + me.getCause());
        //             System.out.println("excep " + me);
        //             me.printStackTrace();
        //         }
        //     }).start();
        // }

    }
}
