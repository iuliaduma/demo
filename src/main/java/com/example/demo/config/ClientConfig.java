package com.example.demo.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ClientConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    public ClientConfig(String sqsUrl) {

        ClientConfiguration basicConfig = new ClientConfiguration(sqsUrl,
                new DefaultAWSCredentialsProviderChain());
        basicConfig.setAwsCredentialsProvider(new ProfileCredentialsProvider());
        basicConfig.setEnableRawEventInfo(true);
        basicConfig.setThreadCount(4);
        basicConfig.setMaxEventsPerEmit(20);
    }
}
