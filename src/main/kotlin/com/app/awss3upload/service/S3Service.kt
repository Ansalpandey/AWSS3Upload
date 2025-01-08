package com.app.awss3upload.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class S3Service(
  private val client: S3Client
) {

  @Value("\${cloud.aws.s3.bucket}")
  lateinit var bucketName: String

  @Value("\${cloud.aws.region.static}")
  lateinit var awsRegion: String // Store the region explicitly

  fun uploadFile(image: MultipartFile): String {
    // Generate a unique file name
    val actualFileName: String = image.originalFilename ?: ""
    val fileExtension = actualFileName.substringAfterLast(".", "")
    val fileName = "${UUID.randomUUID()}.$fileExtension"

    // Include "images/" folder in the key
    val key = "images/$fileName"

    // Create PutObjectRequest
    val putObjectRequest = PutObjectRequest.builder()
      .bucket(bucketName)
      .key(key) // This key specifies the folder path and file name
      .contentType(image.contentType)
      .build()

    // Upload file to S3
    client.putObject(
      putObjectRequest,
      RequestBody.fromInputStream(image.inputStream, image.size)
    )

    // Construct the correct S3 URL manually
    val fileUrl = "https://${bucketName}.s3.${awsRegion}.amazonaws.com/$key"

    return fileUrl
  }

  fun getAllFiles(): List<String> {
    val fileUrls = mutableListOf<String>()

    // Build the request to list objects
    val listObjectsRequest = ListObjectsV2Request.builder()
      .bucket(bucketName)
      .prefix("images/") // Ensure we're only listing objects within the "images/" folder
      .build()

    // Fetch the list of objects
    val listObjectsResponse: ListObjectsV2Response = client.listObjectsV2(listObjectsRequest)

    // Iterate over objects and build their URLs
    listObjectsResponse.contents().forEach { s3Object ->
      // Only add file URLs that are actual files (exclude folders or empty keys)
      if (s3Object.key() != "images/") {
        val fileUrl = "https://${bucketName}.s3.${awsRegion}.amazonaws.com/${s3Object.key()}"
        fileUrls.add(fileUrl)
      }
    }

    return fileUrls
  }
}