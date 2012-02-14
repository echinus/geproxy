package com.twock.geproxy;

import javax.xml.xpath.XPathFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import freemarker.template.Configuration;
import org.littleshoot.proxy.*;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GeProxyModule extends AbstractModule {
  private static final String GE_HOSTANDPORT = "ge.seazonegames.com";
  private static final String DEFAULT_USERNAME = "geproxy";
  private static final String DEFAULT_PASSWORD = "geproxy";
  private final int listenPort;

  public GeProxyModule(int listenPort) {
    this.listenPort = listenPort;
  }

  @Override
  protected void configure() {
    bind(XPathFactory.class).toInstance(XPathFactory.newInstance());
  }

  @Provides
  @Singleton
  public HttpProxyServer getProxyServer(final GeHttpFilter geFilter) {
    DefaultHttpProxyServer httpProxyServer = new DefaultHttpProxyServer(listenPort, new HttpResponseFilters() {
      @Override
      public HttpFilter getFilter(String hostAndPort) {
        return GE_HOSTANDPORT.equals(hostAndPort) ? geFilter : null;
      }
    });
    httpProxyServer.addProxyAuthenticationHandler(new ProxyAuthorizationHandler() {
      @Override
      public boolean authenticate(String user, String pass) {
        return DEFAULT_USERNAME.equals(user) && DEFAULT_PASSWORD.equals(pass);
      }
    });
    return httpProxyServer;
  }

  @Provides
  public Configuration getFreemarkerConfiguration() {
    Configuration cfg = new Configuration();
    cfg.setClassForTemplateLoading(getClass(), "/templates");
    return cfg;
  }
}
