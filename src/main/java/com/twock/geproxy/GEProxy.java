package com.twock.geproxy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.apache.commons.cli.*;
import org.littleshoot.proxy.HttpProxyServer;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GEProxy {
  public static void main(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("p", true, "Port to listen on (defaults to 8080)");
    options.addOption("h", "help", false, "Display this help message");
    CommandLineParser parser = new PosixParser();
    CommandLine cmd = parser.parse(options, args);
    if(cmd.hasOption("h")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java " + GEProxy.class.getName(), options);
      return;
    }
    int listenPort = cmd.hasOption("p") ? Integer.parseInt(cmd.getOptionValue("p")) : 8080;

    Injector injector = createInjector(listenPort);
    injector.getInstance(HttpProxyServer.class).start();
  }

  public static Injector createInjector(int listenPort) {
    Injector injector = Guice.createInjector(new GeProxyModule(listenPort), new JpaPersistModule("com.twock.geproxy"));
    injector.getInstance(PersistService.class).start();
    return injector;
  }
}
