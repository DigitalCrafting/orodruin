package org.digitalcrafting.anvil.server;

import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

public class HttpSocketHandler implements Runnable {
    private Socket socket;
    private Map<String, Map<String, HttpPathHandler>> pathsMap;

    public HttpSocketHandler(Socket aSocket, Map<String, Map<String, HttpPathHandler>> aPathsMap) {
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

            System.out.println("Received connection from " + socket.getRemoteSocketAddress().toString());
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
                System.out.println("Received " + request.method + " request to path " + request.path);

                if (pathsMap.containsKey(request.method)) {
                    Map<String, HttpPathHandler> getHandlers = pathsMap.get(request.method);
                    if (getHandlers.containsKey(request.path)) {
                        response = new HttpResponse(200, "OK", out);
                        if ("GET".equals(request.method)) {
                            getHandlers.get(request.path).handle(request, response);
                        } else if ("POST".equals(request.method)) {
                            getHandlers.get(request.path).handle(request, response);
                        }
                    } else {
                        response = new HttpResponse(404, "NOT FOUND", out);
                    }
                } else {
                    response = new HttpResponse(404, "NOT FOUND", out);
                }

                response.send(done);
            }
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e);
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
                System.out.println("Connection has been closed");
            }
        }
    }
}
