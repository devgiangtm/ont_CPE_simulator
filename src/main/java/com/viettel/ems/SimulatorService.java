package com.viettel.ems;

import com.viettel.ems.helper.AppConst;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Service
@Slf4j
public class SimulatorService {
    @Value("${alarm.number}")
    private int alarmNumber;

    @Value("${device.number}")
    private int deviceNumber;


    public void runOne() {
        // Get all current schedule
        for (int i = 0; i < deviceNumber; i++) {
            int finalI = i;
            Properties p = new Properties();
            p.put("org.quartz.threadPool.threadCount", String.valueOf(deviceNumber));
            try {
                Scheduler scheduler = new StdSchedulerFactory(p).getScheduler();
                scheduler.start();
                new Thread(()->{
                    String ont = startNewJobSchedule(scheduler,"CPE " + finalI + " FM", "ONT"+finalI, "0 0/1 * * * ?", "CPE " + finalI);
                    log.info("start job for "+ont+" done.");
                }).start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }

        }
    }

    public List<String> genAlarms() {
        ArrayList<String> logs = new ArrayList<>();
        Random random = new Random();
        List<Integer> lstKey = new ArrayList<>(AppConst.GENDATA_ALARM.keySet());
        for (int j = 0; j < alarmNumber; j++) {
            Integer key = lstKey.get(random.nextInt(lstKey.size()));
            logs.add(AppConst.GENDATA_ALARM.get(key));
        }
        return logs;
    }

    public String startNewJobSchedule( Scheduler scheduler,
        String jobName, String groupName, String cronString, String clientId
    ) {
        String jn = null;
        try {
            JobDataMap m = new JobDataMap();
            m.put("clientId", clientId);
            m.put("deviceNumber", deviceNumber);
            m.put("logs", genAlarms());
            JobDetail job = JobBuilder.newJob(ScheduleJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(m)
                .build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("cron_" + jobName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronString))
                .forJob(jobName, groupName)
                .startNow()
                .build();
            scheduler.scheduleJob(job, trigger);
            jn = jobName;
        } catch (SchedulerException e) {
            e.printStackTrace();
        } finally {
            return jn;
        }
    }
}
