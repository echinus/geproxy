package com.twock.geproxy;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import com.google.inject.Inject;
import com.twock.geproxy.entity.Fleet;
import com.twock.geproxy.entity.FleetMovement;
import com.twock.geproxy.entity.Planet;
import com.twock.geproxy.parsers.*;
import org.jboss.netty.buffer.ByteBufferBackedChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.littleshoot.proxy.HttpFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GeHttpFilter implements HttpFilter {
  private static final Logger log = LoggerFactory.getLogger(GeHttpFilter.class);
  private static final Charset UTF8 = Charset.forName("UTF8");
  private final GalaxyPageParser galaxyPageParser;
  private final GeProxyDao geProxyDao;
  private final Fleet3PageParser fleet3PageParser;
  private final FleetPageParser fleetPageParser;
  private final OverviewEnhancer overviewEnhancer;

  @Inject
  public GeHttpFilter(GalaxyPageParser galaxyPageParser, GeProxyDao geProxyDao, Fleet3PageParser fleet3PageParser, FleetPageParser fleetPageParser, OverviewEnhancer overviewEnhancer) {
    this.galaxyPageParser = galaxyPageParser;
    this.geProxyDao = geProxyDao;
    this.fleet3PageParser = fleet3PageParser;
    this.fleetPageParser = fleetPageParser;
    this.overviewEnhancer = overviewEnhancer;
  }

  @Override
  public HttpResponse filterResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
    try {
      QueryStringDecoder query = new QueryStringDecoder(httpRequest.getUri());
      log.info("Filtering " + query.getPath() + " params=" + query.getParameters() + " headers=" + httpResponse.getHeaders());
      if(log.isDebugEnabled()) {
        log.debug("Request URI: " + httpRequest.getUri());
        log.debug("Request headers: " + httpRequest.getHeaders());
        String requestBody = httpRequest.getContent().toString(UTF8);
        log.debug("Request text: " + requestBody);
        QueryStringDecoder request = new QueryStringDecoder("/?" + requestBody);
        log.debug("Request parameters: " + request.getParameters());

        log.debug("Response text: " + httpResponse.getContent().toString(UTF8));
      }
      if(query.getPath().endsWith("/game.php")) {
        String pageParameter = getParameter(query, "page");
        String modeParameter = getParameter(query, "mode");
        if("overview".equals(pageParameter) && "showfleet".equals(modeParameter)) {
          log.info("Overview fleet " + query.getParameters());

        } else if("overview".equals(pageParameter)) {
          byte[] newOverview = overviewEnhancer.enhanceOverview(httpResponse.getContent().toString(UTF8)).getBytes(UTF8);
          httpResponse.setContent(new ByteBufferBackedChannelBuffer(ByteBuffer.wrap(newOverview)));
          httpResponse.setHeader("Content-Length", newOverview.length);
          log.info("Main overview " + query.getParameters());

        } else if("buildings".equals(pageParameter)) {
          log.info("Buildings " + query.getParameters());

        } else if("fleet".equals(pageParameter) && "flying".equals(modeParameter)) {
          log.info("Flying fleet " + query.getParameters());

        } else if("fleet".equals(pageParameter)) {
          Fleet fleet = fleetPageParser.parse(httpResponse.getContent().toString(UTF8));
          geProxyDao.updateFleet(fleet);
          log.info("Added fleet " + fleet);

        } else if("fleet1".equals(pageParameter)) {
          log.info("Fleet1 " + query.getParameters());

        } else if("fleet2".equals(pageParameter)) {
          log.info("Fleet2 " + query.getParameters());

        } else if("fleet3".equals(pageParameter)) {
          FleetMovement fleetMovement = fleet3PageParser.parse(httpResponse.getContent().toString(UTF8), httpRequest.getContent().toString(UTF8));
          geProxyDao.addFleetMovement(fleetMovement);
          log.info("Added fleet movement " + fleetMovement);
          // gone! fleet deployed, request contains details of what was sent

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
            List<Planet> planets = galaxyPageParser.parse(httpResponse.getContent().toString(UTF8));
            geProxyDao.updatePlanets(planets);

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
