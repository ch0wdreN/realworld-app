package io.ch0wdren.infrastructure.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import java.util.concurrent.TimeUnit

fun Application.configureOpenTelemetry() {
  val openTelemetry = buildOpenTelemetry()

  install(KtorServerTelemetry) {
    setOpenTelemetry(openTelemetry)
  }
}

private fun buildOpenTelemetry(): OpenTelemetry {
  val serviceName = System.getenv("OTEL_SERVICE_NAME") ?: "realworld-app"
  val otlpEndpoint =
    System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT") ?: "http://localhost:4318"
  val useGrpc =
    System.getenv("OTEL_EXPORTER_OTLP_PROTOCOL")
      ?.equals("grpc", ignoreCase = true) ?: false

  val resource =
    Resource.getDefault()
      .merge(
        Resource.create(
          Attributes.builder()
            .put("service.name", serviceName)
            .put("service.version", "0.0.1")
            .build(),
        ),
      )

  val spanExporter =
    if (useGrpc) {
      OtlpGrpcSpanExporter.builder()
        .setEndpoint(otlpEndpoint)
        .setTimeout(30, TimeUnit.SECONDS)
        .build()
    } else {
      OtlpHttpSpanExporter.builder()
        .setEndpoint("$otlpEndpoint/v1/traces")
        .setTimeout(30, TimeUnit.SECONDS)
        .build()
    }

  val sdkTracerProvider =
    SdkTracerProvider.builder()
      .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
      .setResource(resource)
      .build()

  return OpenTelemetrySdk.builder()
    .setTracerProvider(sdkTracerProvider)
    .buildAndRegisterGlobal()
}
