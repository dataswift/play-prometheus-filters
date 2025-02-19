package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.RequestMetric
import io.prometheus.client.Collector
import play.api.Configuration
import play.api.mvc.{ Filter, RequestHeader, Result }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.matching.Regex

/** Generic filter implementation to add metrics for a request.
  *
  * Subclasses only have to define the `metrics` property to apply metrics.
  *
  * {{{
  * @Singleton
  * class MyFilter @Inject()(registry: CollectorRegistry)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter {
  *
  *   override val metrics = List(
  *     LatencyOnlyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults)
  *   )
  * }
  * }}}
  *
  * Metrics can be created by using a [[RequestMetricBuilder RequestMetricBuilder]].
  * The builder creates and configures the metrics for the class instance.
  *
  * See [[CounterRequestMetrics CounterRequestMetrics]] and
  * [[LatencyRequestMetrics LatencyRequestMetrics]] for some provided
  * builders.
  *
  * @param mat
  * @param ec
  */
abstract class MetricsFilter(
    configuration: Configuration
  )(implicit
    val mat: Materializer,
    ec: ExecutionContext)
    extends Filter {

  val metrics: List[RequestMetric[_, RequestHeader, Result]]

  val excludePaths: Set[Regex] = {
    import collection.JavaConverters._
    Option(configuration.underlying)
      .map(_.getStringList("play-prometheus-filters.exclude.paths"))
      .map(_.asScala.toSet)
      .map(_.map(_.r))
      .getOrElse(Set.empty)
  }

  val metricResolution: String =
    Option(configuration.underlying)
      .map(_.getString("play-prometheus-filters.metric.resolution"))
      .getOrElse("milliseconds")

  def apply(
      nextFilter: RequestHeader => Future[Result]
    )(requestHeader: RequestHeader): Future[Result] = {

    // check if current uri is excluded from metrics
    def urlIsExcluded =
      excludePaths.exists(_.findFirstMatchIn(requestHeader.uri).isDefined)

    val resolution = metricResolution match {
      case "seconds"      => Collector.NANOSECONDS_PER_SECOND
      case "milliseconds" => Collector.MILLISECONDS_PER_SECOND
      case "nanoseconds"  => 1
      case _              => Collector.MILLISECONDS_PER_SECOND
    }

    val startTime = System.nanoTime

    nextFilter(requestHeader).map { implicit result =>
      implicit val rh = requestHeader

      if (!urlIsExcluded) {
        val endTime     = System.nanoTime
        val requestTime = (endTime - startTime) / resolution
        metrics.foreach(_.mark(requestTime))
      }

      result
    }
  }
}
