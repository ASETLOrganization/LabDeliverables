package com.teamluck.drawdiculous.backend.protocol;

import lombok.Data;

/**
 * Protocol. Extended by room protocol and game protocol.
 */
@Data
public class Protocol {
    
    // TODO: protocol id
    int type;
    int opCode;
    int roomId;
    int status;
    int senderId;
    String senderName;
    
}
