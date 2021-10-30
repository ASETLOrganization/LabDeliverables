package com.teamluck.drawdiculous.backend.model;

import lombok.Data;

/**
 * Stores data of each draw action.
 */
@Data
public class DrawAction {
    private float x;
    private float y;
    private int data;
    private int op;
    
    /**
     * Constructor for draw action
     */
    public DrawAction() {
    }
    
    /**
     * Construct for draw action with parameters.
     */
    public DrawAction(float x, float y, int data, int op) {
        this.x = x;
        this.y = y;
        this.data = data;
        this.op = op;
    }
}
