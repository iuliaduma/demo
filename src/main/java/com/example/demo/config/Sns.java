package com.example.demo.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sns {

  private static final Logger logger = LoggerFactory.getLogger(Sns.class);
  private static AmazonSNS snsclient = AmazonSNSClientBuilder.standard()
          .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAIVSNXVYQW4VTVNBQ","9wabkd/ZO9PA2U5VR67sM8WQpRbhr8txGtdmv4r9")))
          .withRegion(Regions.EU_CENTRAL_1)
        .build();
  /*
   * Send a notification email.
   */
  public static void sendNotification(String subject, String body) {
    String topicarn = System.getenv("NOTIFICATION_TOPIC");
    System.out.println(topicarn);
    PublishRequest publishRequest = new PublishRequest(topicarn, body, subject);
    System.out.println(publishRequest);
    PublishResult publishResult = snsclient.publish(publishRequest);
    logger.info(publishResult.getMessageId() + publishRequest.getMessage());
  }

  /*
   * Create an SNS subscription.
   */
  public static void createSubscription() {
    String topicarn = System.getenv("NOTIFICATION_TOPIC");
    String bucket = System.getenv("NOTIFICATION_BUCKET");
    SubscribeRequest subRequest = new SubscribeRequest(topicarn, "http", bucket);
    snsclient.subscribe(subRequest);
  }

}