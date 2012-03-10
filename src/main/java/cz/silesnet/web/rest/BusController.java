package cz.silesnet.web.rest;

import cz.silesnet.event.EventBus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * User: der3k
 * Date: 9.3.12
 * Time: 17:37
 */
@Path("/bus")
public class BusController {
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
}
