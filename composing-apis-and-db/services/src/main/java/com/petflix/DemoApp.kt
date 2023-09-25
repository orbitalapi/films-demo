package com.petflix

import com.orbitalhq.PackageMetadata
import com.orbitalhq.schema.publisher.SchemaPublisherService
import com.orbitalhq.schema.publisher.rsocket.RSocketSchemaPublisherTransport
import com.orbitalhq.schema.rsocket.TcpAddress
import lang.taxi.generators.java.spring.SpringTaxiGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class DemoApp {
   companion object {
      @JvmStatic
      fun main(args: Array<String>) {
         SpringApplication.run(DemoApp::class.java, *args)
      }
   }
}

@Component
class RegisterSchemaOnStartup(
   @Value("\${server.port}")
   private val serverPort: String,
) {

   init {
      val appName = "PetflixServices"
      val publisher = SchemaPublisherService(
         appName,
         RSocketSchemaPublisherTransport(TcpAddress("orbital", 7655))
      )
      val sources = SpringTaxiGenerator.forBaseUrl("http://demo-services:${serverPort}")
         .forPackage(DemoApp::class.java)
         .generate()
      publisher.publish(
         PackageMetadata.from("com.petflix", appName),
         sources
      ).subscribe()
   }
}
