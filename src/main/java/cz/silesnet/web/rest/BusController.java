package cz.silesnet.web.rest;

import cz.silesnet.event.EventBus;
import cz.silesnet.event.Key;
import cz.silesnet.event.support.JsonPayload;
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
    @Path("/publish/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void publish(final Map data, @PathParam("key") final String key) {
        final JsonPayload payload = JsonPayload.of(data);
        eventBus.publish(payload, Key.of(key));
    }


}
