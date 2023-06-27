package org.digitalcrafting.anvil.common;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpResponse implements HttpMessage {
    public int statusCode;
    public String message;
    public String version;
    public String body;
    public Map<String, String> headers = new HashMap<>();

    private BufferedReader in;
    private OutputStream out;

    public HttpResponse(BufferedReader in) {
        this.in = in;
    }

    public HttpResponse(int statusCode, String message, OutputStream out) {
        this(statusCode, message);
        this.out = out;
    }

    public HttpResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.version = "HTTP/1.1";
    }

    private String getBodyAsString() throws IOException {
        StringBuffer buf = new StringBuffer();
        InputStream in = getBody();
        int c;
        while ((c = in.read()) != -1) {
            buf.append((char) c);
        }
        return buf.toString();
    }

    private InputStream getBody() throws IOException {
        return new HttpInputStream(in, headers);
    }

    public boolean parse() {
        try {
            String initialLine = in.readLine();

            // We expect first line to consist of 3 token: METHOD, URL, HTTP_VERSION
            StringTokenizer tok = new StringTokenizer(initialLine);
            String[] components = new String[3];

            for (int i = 0; i < components.length; i++) {
                if (tok.hasMoreTokens()) {
                    components[i] = tok.nextToken();
                } else {
                    return false;
                }
            }

            this.version = components[0];
            this.statusCode = Integer.parseInt(components[1]);
            this.message = components[2];

            // Consume headers, for now let's assume single values
            while (true) {
                String headerLine = in.readLine();
                if (headerLine.length() == 0) {
                    break;
                }

                int separator = headerLine.indexOf(":");
                if (separator == -1) {
                    break;
                }

                String[] nameValuePair = headerLine.split(":");
                headers.put(nameValuePair[0], nameValuePair[1].strip());
            }

            System.out.println(headers.toString());

            this.body = this.getBodyAsString();

            return true;
        } catch (Exception e) {
            System.out.println("Could not parse response.");
        }

        return false;
    }

    public void send(boolean closeConnection) throws IOException {
        this.headers.put("Connection", closeConnection ? "Close" : "Keep-Alive");

        this.out.write(this.toHttpString().getBytes());
        this.out.flush();
    }

    public String toHttpString() {
        StringBuilder builder = new StringBuilder(version)
                .append(" ")
                .append(statusCode).append(" ")
                .append(message).append("\r\n");

        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
            }
        }

        if (StringUtils.isNotBlank(body)) {
            builder.append("Content-Length").append(": ").append(body.length()).append("\r\n")
                    .append("\r\n")
                    .append(body);
        } else {
            builder.append("\r\n");
        }

        return builder.toString();
    }
}
