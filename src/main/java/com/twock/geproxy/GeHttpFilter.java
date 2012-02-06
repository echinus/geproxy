package com.twock.geproxy;

import java.nio.charset.Charset;
import java.util.List;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.littleshoot.proxy.HttpFilter;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GeHttpFilter implements HttpFilter {
  private static final Logger log = Logger.getLogger(GeHttpFilter.class);
  private static final Charset UTF8 = Charset.forName("UTF8");
  private final GalaxyPageParser galaxyPageParser;

  @Inject
  public GeHttpFilter(GalaxyPageParser galaxyPageParser) {
    this.galaxyPageParser = galaxyPageParser;
  }

  @Override
  public HttpResponse filterResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
    try {
      QueryStringDecoder query = new QueryStringDecoder(httpRequest.getUri());
      log.info("Filtering " + query.getPath() + " params=" + query.getParameters() + " headers=" + httpResponse.getHeaders());
      log.debug("Page text: " + httpResponse.getContent().toString(UTF8));
      if(query.getPath().endsWith("/game.php")) {
        String pageParameter = getParameter(query, "page");
        String modeParameter = getParameter(query, "mode");
        if("overview".equals(pageParameter) && "showfleet".equals(modeParameter)) {
          log.info("Overview fleet " + query.getParameters());

        } else if("overview".equals(pageParameter)) {
          log.info("Main overview " + query.getParameters());

        } else if("buildings".equals(pageParameter)) {
          log.info("Buildings " + query.getParameters());

        } else if("fleet".equals(pageParameter) && "flying".equals(modeParameter)) {
          log.info("Flying fleet " + query.getParameters());

        } else if("fleet".equals(pageParameter)) {
          log.info("Ships " + query.getParameters());

        } else if("messages".equals(pageParameter) && "show".equals(modeParameter)) {
          log.info("Message list " + query.getParameters());

        } else if("messages".equals(pageParameter) && "read".equals(modeParameter)) {
          log.info("Read Message " + query.getParameters());
          log.info("Text: " + httpResponse.getContent().toString(UTF8));

        } else if("messages".equals(pageParameter)) {
          log.info("Message contents page " + query.getParameters());

        } else if("galaxy".equals(pageParameter)) {
          String actionParameter = getParameter(query, "action");
          if(actionParameter == null) {
            log.info("Galaxy page " + query.getParameters());
            galaxyPageParser.parse(httpResponse.getContent().toString(UTF8));

          } else if("profile".equals(actionParameter)) {
            log.info("Profile page " + query.getParameters());

          } else {
            log.warn("Unknown galaxy page " + query.getParameters());
          }

        } else {
          log.warn("Unknown game.php page: " + query.getParameters());
        }
      } else if(query.getPath().endsWith("/CombatReport.php")) {
        log.info("Combat report " + query.getParameters());
        log.info("Text: " + httpResponse.getContent().toString(UTF8));

      } else {
        log.warn("Unknown page: " + query.getPath() + " " + query.getParameters());
      }
    } catch(Exception e) {
      log.error("Failed to parse galaxy page request=" + httpRequest + " response=" + httpResponse, e);
    }
    return httpResponse;
  }

  private String getParameter(QueryStringDecoder query, String parameterName) {
    List<String> pageParameterList = query.getParameters().get(parameterName);
    return pageParameterList == null || pageParameterList.isEmpty() ? null : pageParameterList.get(0);
  }

  @Override
  public int getMaxResponseSize() {
    return 1048576;
  }

  @Override
  public boolean shouldFilterResponses(HttpRequest httpRequest) {
    return true;
  }
}
