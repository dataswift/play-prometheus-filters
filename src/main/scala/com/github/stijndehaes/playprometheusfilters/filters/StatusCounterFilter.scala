package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics.StatusCounterRequestMetricBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.{ CounterRequestMetric, DefaultPlayUnmatchedDefaults }
import io.prometheus.client.CollectorRegistry
import play.api.Configuration

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

/**
  * A [[MetricsFilter]] using a counter metric to count requests statuses.
  * Only adds a 'status' label containing the status codes.
  */
@Singleton
class StatusCounterFilter @Inject() (
    registry: CollectorRegistry,
    configuration: Configuration
  )(implicit mat: Materializer,
    ec: ExecutionContext)
    extends MetricsFilter(configuration) {

  override val metrics: List[CounterRequestMetric] = List(
    StatusCounterRequestMetricBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}
