package com.twock.geproxy;

import com.google.inject.*;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.littleshoot.proxy.*;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GEProxy {
  public static void main(String[] args) {
    Injector injector = createInjector();
    injector.getInstance(HttpProxyServer.class).start();
  }

  public static Injector createInjector() {
    Injector injector = Guice.createInjector(new GeProxyModule(), new JpaPersistModule("com.twock.geproxy"));
    injector.getInstance(PersistService.class).start();
    return injector;
  }

}
