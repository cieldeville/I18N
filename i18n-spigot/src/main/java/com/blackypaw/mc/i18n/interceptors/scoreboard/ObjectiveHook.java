package com.blackypaw.mc.i18n.interceptors.scoreboard;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;

public class ObjectiveHook implements I18NHook {

    private I18NUtilities i18n;

    /**
     * Create a new ObjectiveHook.
     *
     * @param i18n
     */
    public ObjectiveHook(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
    }

    @Override
    public void on(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        int mode = packet.getIntegers().read( 0 );
        if ( mode == 0 || mode == 2 ) {
            String message     = packet.getStrings().read( 1 );
            String translation = this.i18n.translateMessageIfAppropriate( this.i18n.getLocale( player ), message );

            if ( message != translation ) {
                packet.getStrings().write( 1, translation );
            }
        }
    }

}
