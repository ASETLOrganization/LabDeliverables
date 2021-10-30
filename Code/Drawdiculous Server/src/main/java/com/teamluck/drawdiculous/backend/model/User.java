package com.teamluck.drawdiculous.backend.model;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * Stores user information.
 */
@Data
public class User {
    
    private int id;
    private String name;
    private Channel channel;
}
