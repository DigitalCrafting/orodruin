package org.digitalcrafting.anvil.common;

import java.io.IOException;

public interface HttpMessage {
    boolean parse();

    void send(boolean closeConnection) throws IOException;

    String toHttpString();
}
