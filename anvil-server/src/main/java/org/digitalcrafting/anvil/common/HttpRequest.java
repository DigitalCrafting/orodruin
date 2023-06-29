package org.digitalcrafting.anvil.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest implements HttpMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    public String method;
    public String path;
    public String fullUrl;
    public String version;
    public String body;
    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> queryParams = new HashMap<>();

    public BufferedReader in;
    public OutputStream out;

    public HttpRequest(BufferedReader in) {
        this.in = in;
    }

    public HttpRequest(String method, String path, OutputStream out) {
        this(method, path);
        this.out = out;
    }

    public HttpRequest(String method, String path) {
        this.method = method;
        this.path = path;
        this.version = "HTTP/1.1";
    }

    /* TODO multi-value headers */
    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public String getQueryParam(String paramName) {
        return queryParams.get(paramName);
    }

    private void parseQueryParameters(String queryString)  {
        for (String parameter : queryString.split("&"))  {
            int separator = parameter.indexOf('=');
            if (separator > -1)  {
                queryParams.put(parameter.substring(0, separator),
                        parameter.substring(separator + 1));
            } else  {
                queryParams.put(parameter, null);
            }
        }
    }

    public String getBodyAsString() throws IOException {
        StringBuffer buf = new StringBuffer();
        InputStream in = getBody();
        int c;
        while ((c = in.read()) != -1) {
            buf.append((char) c);
        }
        return buf.toString();
    }

    public InputStream getBody() throws IOException {
        return new HttpInputStream(in, headers);
    }

    public boolean parse() {
        try {
            String initialLine = in.readLine();
            if (initialLine == null) {
                return false;
            }

            // We expect first line to consist of 3 token: METHOD, URL, HTTP_VERSION
            String[] components = initialLine.split(" ");
            if (components.length != 3) {
                return false;
            }

            // Version is not important for us in this simple implementation
            this.method = components[0];
            this.fullUrl = components[1];

            int queryParamsIndex = components[1].indexOf("?");
            if (queryParamsIndex == -1) {
                this.path = this.fullUrl;
            } else {
                this.path = components[1].substring(0, queryParamsIndex);
                this.parseQueryParameters(components[1].substring(queryParamsIndex) + 1);
            }

            if ("/".equals(this.path)) {
                this.path = "/index.html";
            }

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
                headers.put(nameValuePair[0].strip(), nameValuePair[1].strip());
            }

            LOGGER.info(headers.toString());

            this.body = getBodyAsString();

            return true;
        } catch (Exception e) {
            LOGGER.error("Could not parse request");
        }

        return false;
    }

    public void send(OutputStream out, boolean closeConnection) throws IOException {
        this.out = out;
        this.send(closeConnection);
    }

    public void send(OutputStream out) throws IOException {
        this.out = out;
        this.send(true);
    }

    public void send(boolean closeConnection) throws IOException {
        this.headers.put("Connection", closeConnection ? "Close" : "Keep-Alive");

        this.out.write(this.toHttpString().getBytes());
        this.out.flush();
    }

    public String toHttpString() {
        StringBuilder builder = new StringBuilder(method)
                .append(" ")
                .append(path).append(" ")
                .append(version).append("\r\n");

        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header: headers.entrySet()) {
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
