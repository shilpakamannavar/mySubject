package com.study.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class CognitoClientConfig {

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(
            @Value("${app.cognito.region}")                   String region,
            @Value("${app.cognito.access-key-id:}")           String accessKeyId,
            @Value("${app.cognito.secret-access-key:}")       String secretAccessKey) {



        return CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        (!accessKeyId.isBlank() && !secretAccessKey.isBlank())
                            ? StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKeyId, secretAccessKey))
                            : DefaultCredentialsProvider.create())  // env vars → ~/.aws/credentials → IAM role
                .build();
    }
}
