package com.viettel.ems;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ScheduleJob implements Job {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
    String broker = "tcp://172.16.30.126:1883";
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("[jobName] : " + jobExecutionContext.getJobDetail().getKey() + " - running");
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String clientId = dataMap.getString("clientId");
        int deviceNumber = dataMap.getInt("deviceNumber");
        List<String> logs = (List<String>) dataMap.get("logs");
        String date = sdf.format(new Date());
        IMqttClient client = null;
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(broker, clientId,persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setMaxInflight(deviceNumber);
            IMqttToken token = client.connectWithResult(options);
            token.waitForCompletion();
            for (String log : logs) {
                pushToMQ("VIETTEL","ONT",clientId,String.format(log, date),client);
            }

        } catch (MqttException e) {
            log.error(clientId+"*: "+e.getMessage());

        }

    }

    public void pushToMQ(String manufacture,String productclass,String serial, String item,IMqttClient client){
        MqttMessage msg;
        // byte[] payload = item.getBytes();
        String sjon = "{\n" + "    \"DeviceId\": {\n" + "        \"Manufacturer\": \"%s\",\n"
            + "        \"ProductClass\": \"%s\",\n" + "        \"SerialNumber\": \"%s\"\n" + "    },\n" + "    \"Logs\": [\n"
            + "        \"%s\"\n" + "    ]\n" + "}";
        byte[] payload = String.format(sjon,manufacture,productclass,serial,item)
            .getBytes();
        msg = new MqttMessage(payload);
        msg.setQos(1);
        msg.setRetained(true);
        try {
            client.publish("test", msg);
        } catch (MqttException e) {
            log.error(serial+": "+e.getMessage());
        }
    }
}

