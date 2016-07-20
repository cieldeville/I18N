package com.blackypaw.mc.i18n.interceptors.scoreboard;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class TeamHook implements I18NHook {

    private I18NUtilities i18n;

    /**
     * Create a new TeamHook.
     *
     * @param i18n
     */
    public TeamHook(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.Play.Server.SCOREBOARD_TEAM;
    }

    @Override
    public void on(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();
        final Locale locale = this.i18n.getLocale( player );

        int mode = packet.getIntegers().read( 1 );
        if ( mode == 0 || mode == 2 ) {
            String displayName = packet.getStrings().read( 1 );
            String prefix      = packet.getStrings().read( 2 );
            String suffix      = packet.getStrings().read( 3 );

            String translatedDisplayName = this.i18n.translateMessageIfAppropriate( locale, displayName );
            String translatedPrefix      = this.i18n.translateMessageIfAppropriate( locale, prefix );
            String translatedSuffix      = this.i18n.translateMessageIfAppropriate( locale, suffix );

            if ( displayName != translatedDisplayName ) {
                packet.getStrings().write( 1, translatedDisplayName );
            }

            if ( prefix != translatedPrefix ) {
                packet.getStrings().write( 2, translatedPrefix );
            }

            if ( suffix != translatedSuffix ) {
                packet.getStrings().write( 3, translatedSuffix );
            }
        }

        if ( mode == 0 || mode == 3 || mode == 4 ) {
            List<String> entries = (List<String>) packet.getSpecificModifier( Collection.class ).read( 0 );
            if ( entries.size() > 0 ) {
                for ( int i = 0; i < entries.size(); ++i ) {
                    String entry           = entries.get( i );
                    String translatedEntry = this.i18n.translateMessageIfAppropriate( locale, entry );

                    if ( entry != translatedEntry ) {
                        entries.set( i, translatedEntry );
                    }
                }
            }
        }
    }

}
