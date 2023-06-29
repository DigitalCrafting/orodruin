package org.digitalcrafting.testing.server;

import org.digitalcrafting.anvil.common.HttpMethod;
import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;
import org.digitalcrafting.anvil.server.HttpPathHandler;

public class TestHandler implements HttpPathHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        if (HttpMethod.GET.equals(request.method)) {
            response.body = "Called GET method on path '/api/'";
        } else if (HttpMethod.POST.equals(request.method)) {
            response.body = "Called POST method on path '/api/' with body " + request.body;
        } else {
            response.statusCode = 405;
            response.message = "METHOD NOT ALLOWED";
        }
    }
}
