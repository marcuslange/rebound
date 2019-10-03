package io.pileworx.rebound.common.velocity

import java.io.StringWriter

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity

class TemplateEngine {
  def process(template: String, params: Map[String, Any]): String = {
    val context = new VelocityContext
    val writer = new StringWriter
    params.foreach { case(k, v) => context.put(k, v) }
    Velocity.evaluate(context, writer, "request template", template)
    writer.toString
  }
}