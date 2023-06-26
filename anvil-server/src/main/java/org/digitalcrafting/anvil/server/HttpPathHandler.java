package org.digitalcrafting.anvil.server;

import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;

/* This *very* basic implementation of path handler */
public interface HttpPathHandler {
    void handle(HttpRequest request, HttpResponse response);
}
