package com.blackypaw.mc.i18n.interceptors.sign;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.blackypaw.mc.i18n.chat.ChatComponent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class MapChunkHook implements I18NHook {

    private I18NUtilities i18n;

    /**
     * Create a new MapChunkHook.
     *
     * @param i18n
     */
    public MapChunkHook(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.Play.Server.MAP_CHUNK;
    }

    @Override
    public void on(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();
        final Locale locale = this.i18n.getLocale( player );

        List handleList = packet.getSpecificModifier( List.class ).read( 0 );
        for ( Object compoundHandle : handleList ) {
            NbtCompound compound = NbtFactory.fromNMSCompound( compoundHandle );
            if ( compound.getString( "id" ).equals( "Sign" ) ) {
                for ( int i = 1; i <= 4; ++i ) {
                    final String key = "Text" + i;
                    String message    = this.i18n.getGson().fromJson( compound.getString( key ), ChatComponent.class )
                            .getUnformattedText();
                    String translated = this.i18n.translateMessageIfAppropriate( locale, message );

                    if ( message != translated ) {
                        compound.put( key, WrappedChatComponent.fromText( translated ).getJson() );
                    }
                }
            }
        }
    }

}
