package org.digitalcrafting.testing.server;

import org.digitalcrafting.anvil.common.HttpMethod;
import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;
import org.digitalcrafting.anvil.server.HttpPathHandler;

import java.io.IOException;

public class TestHandler implements HttpPathHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        if (HttpMethod.GET.equals(request.method)) {
            response.body = "Called GET method on path '/api/'";
        } else if (HttpMethod.POST.equals(request.method)) {
            try {
                response.body = "Called POST method on path '/api/' with body " + request.getBodyAsString();
            } catch (IOException e) {
                response.statusCode = 400;
                response.message = "Bad Request";
            }
        } else {
            response.statusCode = 405;
            response.message = "METHOD NOT ALLOWED";
        }
    }
}
