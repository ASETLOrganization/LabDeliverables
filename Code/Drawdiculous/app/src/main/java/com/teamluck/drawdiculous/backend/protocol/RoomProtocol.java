package com.teamluck.drawdiculous.backend.protocol;

import com.teamluck.drawdiculous.backend.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

/**
 * Room protocol.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoomProtocol extends Protocol {
    
    private int kickPlayerId;
    private int roomStatus;
    private String password;
    private ArrayList<String> users;
    
    /**
     * Constructor for RoomProtocol.
     */
    public RoomProtocol() {
        setType(Const.TYPE_ROOM);
    }
}
