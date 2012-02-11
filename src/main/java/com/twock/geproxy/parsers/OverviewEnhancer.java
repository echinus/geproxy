package com.twock.geproxy.parsers;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import com.google.inject.Inject;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class OverviewEnhancer {
  private final ThreadLocal<XPath> xPath;

  @Inject
  public OverviewEnhancer(final XPathFactory xPathFactory) {
    this.xPath = new ThreadLocal<XPath>() {
      @Override
      protected XPath initialValue() {
        return xPathFactory.newXPath();
      }
    };
  }

  public String enhanceOverview(String html) throws Exception {
    return html.replace("</ul>", getOverviewAddition() + "</ul>");
  }

  public String getOverviewAddition() {
    return "<li>Running through GEProxy! Moo Moo.</li>";
  }
}
