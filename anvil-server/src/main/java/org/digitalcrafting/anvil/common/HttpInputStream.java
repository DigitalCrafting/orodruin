package org.digitalcrafting.anvil.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

public class HttpInputStream extends InputStream {
    private Reader source;
    private int bytesRemaining;

    public HttpInputStream(Reader source, Map<String, String> headers) throws IOException  {
        this.source = source;

        try  {
            bytesRemaining = Integer.parseInt(headers.get("Content-Length"));
        } catch (NumberFormatException e)  {
            throw new IOException("Malformed or missing Content-Length header");
        }
    }

    public int read() throws IOException {
        if (bytesRemaining == 0)  {
            return -1;
        } else  {
            bytesRemaining -= 1;
            return source.read();
        }
    }
}
