package com.twock.geproxy.parsers;

import java.io.StringWriter;

import com.google.inject.Inject;
import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class OverviewEnhancer {
  private static final Logger log = LoggerFactory.getLogger(OverviewEnhancer.class);
  private final Configuration freemarkerConfiguration;

  @Inject
  public OverviewEnhancer(Configuration freemarkerConfiguration) {
    this.freemarkerConfiguration = freemarkerConfiguration;
  }

  public String enhanceOverview(String html) throws Exception {
    StringWriter stringWriter = new StringWriter();
    freemarkerConfiguration.getTemplate("overview.ftl").process(null, stringWriter);
    String replaced = html.replace("</ul>", stringWriter.toString() + "</ul>");
    log.debug("New html: " + replaced);
    return replaced;
  }

  public String getOverviewAddition() {
    return "<li>Running through GEProxy! Moo Moo.</li>";
  }
}
