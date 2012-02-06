package com.twock.geproxy;

import javax.xml.xpath.XPathFactory;

import com.google.inject.*;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.littleshoot.proxy.*;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GEProxy {
  private static final String GE_HOSTANDPORT = "ge.seazonegames.com";

  public static void main(String[] args) {
    Injector injector = createInjector();
    injector.getInstance(HttpProxyServer.class).start();
  }

  public static Injector createInjector() {
    Injector injector = Guice.createInjector(new AbstractModule() {
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
    }, new JpaPersistModule("com.twock.geproxy"));
    injector.getInstance(PersistService.class).start();
    return injector;
  }
}
