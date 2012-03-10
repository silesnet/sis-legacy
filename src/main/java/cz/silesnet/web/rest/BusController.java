package cz.silesnet.web.rest;

import cz.silesnet.event.EventBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * User: der3k
 * Date: 9.3.12
 * Time: 17:37
 */
@Path("/bus")
public class BusController {
    private final Log log = LogFactory.getLog(getClass());
    private EventBus eventBus;

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Object status() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", eventBus.hashCode());
        map.put("name", "event bus for sis");
        return map;
    }

    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object publish(Map data) {
        log.info("published: " + data);
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("status", "OK");
        return map;
    }


}
