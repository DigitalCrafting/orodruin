package org.digitalcrafting.anvil.server;

import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

public class HttpSocketHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSocketHandler.class);
    private final Map<String, HttpPathHandler> pathsMap;
    private Socket socket;

    public HttpSocketHandler(Socket aSocket, Map<String, HttpPathHandler> aPathsMap) {
        this.socket = aSocket;
        this.pathsMap = aPathsMap;
    }

    @Override
    public void run() {
        OutputStream out = null;
        BufferedReader in = null;
        try {
            socket.setSoTimeout(10000);
            boolean done = false;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = socket.getOutputStream();

            LOGGER.info("Received connection from " + socket.getRemoteSocketAddress().toString());
            while (!done) {
                HttpRequest request = new HttpRequest(in);

                try {
                    if (!request.parse()) {
                        new HttpResponse(400, "Bad request", out).send(true);
                        return;
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }

                String connectionHeaderValue = request.getHeader("Connection");
                if ("close".equalsIgnoreCase(connectionHeaderValue)) {
                    done = true;
                }

                HttpResponse response;
                LOGGER.info("Received " + request.method + " request to path " + request.path);

                if (pathsMap.containsKey(request.path)) {
                    response = new HttpResponse(200, "OK", out);
                    pathsMap.get(request.path).handle(request, response);
                } else {
                    response = new HttpResponse(404, "NOT FOUND", out);
                }
                response.send(done);
            }
        } catch (IOException e) {
            LOGGER.error("Something went wrong: " + e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                LOGGER.info("Connection has been closed");
            }
        }
    }
}
