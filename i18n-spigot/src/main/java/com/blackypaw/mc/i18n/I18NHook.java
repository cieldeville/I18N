package com.blackypaw.mc.i18n;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

public interface I18NHook {

    /**
     * The packet type to hook into.
     *
     * @return
     */
    PacketType getPacketType();

    /**
     * Hook into the specified PacketEvent.
     *
     * @param event
     */
    void on(PacketEvent event);

}
