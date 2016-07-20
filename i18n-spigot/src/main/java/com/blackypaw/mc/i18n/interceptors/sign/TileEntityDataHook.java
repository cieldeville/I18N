package com.blackypaw.mc.i18n.interceptors.sign;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.blackypaw.mc.i18n.chat.ChatComponent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import org.bukkit.entity.Player;

import java.util.Locale;

public class TileEntityDataHook implements I18NHook {

    private I18NUtilities i18n;

    /**
     * Create a new TileEntityDataHook.
     *
     * @param i18n
     */
    public TileEntityDataHook(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.Play.Server.TILE_ENTITY_DATA;
    }

    @Override
    public void on(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();
        final Locale locale = this.i18n.getLocale( player );

        if ( packet.getIntegers().read( 0 ) == 0x09 ) {
            // Update Block Entity -> Set Sign Text:
            NbtBase<?> nbt = packet.getNbtModifier().read( 0 );
            if ( nbt.getType() != NbtType.TAG_COMPOUND ) {
                // Malformed Sign Entity:
                return;
            }

            boolean changed = false;
            NbtCompound compound = (NbtCompound) nbt;
            for ( int i = 1; i <= 4; ++i ) {
                final String key = "Text" + i;
                String message    = this.i18n.getGson().fromJson( compound.getString( key ), ChatComponent.class )
                        .getUnformattedText();
                String translated = this.i18n.translateMessageIfAppropriate( locale, message );

                if ( message != translated ) {
                    if ( !changed ) {
                        nbt = compound.deepClone();
                        compound = (NbtCompound) nbt;
                    }
                    compound.put( key, WrappedChatComponent.fromText( translated ).getJson() );
                    changed = true;
                }
            }

            if ( changed ) {
                packet.getNbtModifier().write( 0, nbt );
            }
        }
    }

}
