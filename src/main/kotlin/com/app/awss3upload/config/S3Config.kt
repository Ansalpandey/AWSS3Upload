package com.app.awss3upload.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config {
  @Value("\${cloud.aws.credentials.access-key}")
  lateinit var awsAccessKey: String // Added type annotation

  @Value("\${cloud.aws.credentials.secret-key}")
  lateinit var awsSecretKey: String // Added type annotation

  @Value("\${cloud.aws.region.static}")
  lateinit var awsRegion: String // Added type annotation

  @Bean
  fun awsS3Client(): S3Client {
    return S3Client.builder()
      .region(Region.of(awsRegion)) // Use the region from configuration
      .credentialsProvider(
        StaticCredentialsProvider.create(
          AwsBasicCredentials.create(awsAccessKey, awsSecretKey) // Provide access and secret keys
        )
      )
      .build()
  }
}
