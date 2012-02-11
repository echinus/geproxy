package com.twock.geproxy;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import com.google.inject.*;
import freemarker.template.Configuration;
import org.littleshoot.proxy.*;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GeProxyModule extends AbstractModule {
  private static final String GE_HOSTANDPORT = "ge.seazonegames.com";

  @Override
  protected void configure() {
    bind(XPathFactory.class).toInstance(XPathFactory.newInstance());
  }

  @Provides
  @Singleton
  public HttpProxyServer getProxyServer(final GeHttpFilter geFilter) {
    return new DefaultHttpProxyServer(8080, new HttpResponseFilters() {
      @Override
      public HttpFilter getFilter(String hostAndPort) {
        return GE_HOSTANDPORT.equals(hostAndPort) ? geFilter : null;
      }
    });
  }

  @Provides
  public Configuration getFreemarkerConfiguration() {
    Configuration cfg = new Configuration();
    cfg.setClassForTemplateLoading(getClass(), "/templates");
    return cfg;
  }
}
