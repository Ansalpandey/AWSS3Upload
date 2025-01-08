package com.app.awss3upload.controller

import com.app.awss3upload.model.ImageResponse
import com.app.awss3upload.service.S3Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImageController(
  private val s3Service: S3Service
) {

  @PostMapping("/uploads")
  fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<ImageResponse> {
    val fileUrl = s3Service.uploadFile(file)
    return ResponseEntity.ok(ImageResponse(fileUrl))
  }

  @GetMapping("/uploads")
  fun getAllFiles(): ResponseEntity<List<String>> {
    val files: List<String> = s3Service.getAllFiles()
    return ResponseEntity.ok(files)
  }
}