package com.example.demo.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsResult;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.EnableAlarmActionsRequest;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cloudwatch")
public class CloudwatchController {

    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @GetMapping("/add/{alarm_name}")
    public Object addMetricAlarm(@PathVariable String alarm_name){
        String instanceId = "1";

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        Dimension dimension = new Dimension().withName("InstanceId").withValue(instanceId);

        PutMetricAlarmRequest request = new PutMetricAlarmRequest().withAlarmName(alarm_name)
                .withComparisonOperator(ComparisonOperator.GreaterThanThreshold)
                .withEvaluationPeriods(1)
                .withMetricName("CPUUtilization")
                .withNamespace("AWS/EC2")
                .withPeriod(60)
                .withStatistic(Statistic.Average)
                .withThreshold(70.0)
                .withActionsEnabled(false)
                .withAlarmDescription(
                        "Alarm when server CPU utilization exceeds 70%")
                .withUnit(StandardUnit.Seconds)
                .withDimensions(dimension);

        return cw.putMetricAlarm(request);
    }

    @GetMapping("/enable/{alarm_name}")
    public Object enableAlarm(@PathVariable String alarm_name){

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        EnableAlarmActionsRequest request = new EnableAlarmActionsRequest().withAlarmNames(alarm_name);

        return cw.enableAlarmActions(request);
    }

    @GetMapping("/alarms")
    public List<MetricAlarm> getAlarms(){

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        boolean done = false;
        DescribeAlarmsRequest request = new DescribeAlarmsRequest();

        List<MetricAlarm> metricAlarms = new ArrayList<>();

        while (!done) {
            DescribeAlarmsResult response = cw.describeAlarms();
            for (MetricAlarm alarm: response.getMetricAlarms()){
                metricAlarms.add(alarm);
            }

            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null){
                done = true;
            }
        }

        return metricAlarms;
    }
}
