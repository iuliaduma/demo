package com.example.demo.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.ElasticBeanstalkPlugin;
import com.amazonaws.xray.strategy.sampling.CentralizedSamplingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.net.URL;

@Configuration
public class WebConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public Filter TracingFilter() { return new AWSXRayServletFilter("demo");    }

    @Bean
    public Filter SimpleCORSFilter() { return new SimpleCORSFilter();}

    static {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard()
                .withPlugin(new EC2Plugin())
                .withPlugin(new ElasticBeanstalkPlugin());

        URL ruleFile = WebConfig.class.getResource("sampling-rules.json");
        builder.withSamplingStrategy(new CentralizedSamplingStrategy(ruleFile));

        AWSXRay.setGlobalRecorder(builder.build());

        AWSXRay.beginSegment("demo");
        if ( System.getenv("NOTIFICATION_BUCKET") != null ){
            try { Sns.createSubscription(); }
            catch (Exception e ) {
                logger.warn("Failed to create subscription "+  System.getenv("NOTIFICATION_BUCKET"));
            }
        }

        AWSXRay.endSegment();
    }

}
