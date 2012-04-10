package cz.silesnet.web.rest;

import com.sun.jersey.api.NotFoundException;
import cz.silesnet.service.CustomerManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10.4.12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */

@Path("/customers")
public class CustomerController {
    private CustomerManager customerManager;

    private final Log log = LogFactory.getLog(getClass());

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object customer(@PathParam("id") final long id) {
        try {
            return customerManager.get(id);
        } catch (RuntimeException e) {
            throw new NotFoundException("customer with id '" + id + "' not found");
        }
    }

}
