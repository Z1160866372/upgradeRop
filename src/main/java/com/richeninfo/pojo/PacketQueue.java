/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/16 10:46
 */
public class PacketQueue {
    private Packet packet;
    private String handleCode;

    public PacketQueue(Packet packet, String handleCode) {
        super();
        this.packet = packet;
        this.handleCode = handleCode;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public String getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(String handleCode) {
        this.handleCode = handleCode;
    }

}

