package dev.ammar.checkout.api;

import dev.ammar.checkout.entity.Checkout;
import dev.ammar.checkout.repository.CheckoutRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/checkout")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Checkout", description = "Checkout operations")
public class CheckoutResource {

    @Inject
    CheckoutRepository checkoutRepository;

    @GET
    @Operation(summary = "Health check", description = "Returns OK if the service is running")
    public Response health() {
        return Response.ok("{\"status\":\"OK\"}").build();
    }

    @GET
    @Path("/{orderId}")
    @Operation(summary = "Get checkout by order ID", description = "Retrieves a checkout record by its order ID")
    public Response getByOrderId(@PathParam("orderId") String orderId) {
        return checkoutRepository.findByOrderId(orderId)
                .map(checkout -> Response.ok(checkout).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Checkout not found for orderId: " + orderId + "\"}")
                        .build());
    }
}
