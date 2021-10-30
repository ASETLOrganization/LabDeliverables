package com.teamluck.drawdiculous.backend.client.handler;

import com.teamluck.drawdiculous.backend.protocol.Protocol;

/**
 * Client protocol handler, extended by client room handler and client game handler.
 */
public abstract class ClientProtocolHandler {
    /**
     * Handle incoming server packages.
     */
    public abstract void handle(Protocol protocol);
    
}
