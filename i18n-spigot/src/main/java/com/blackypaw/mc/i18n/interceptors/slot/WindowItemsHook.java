package com.blackypaw.mc.i18n.interceptors.slot;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Locale;

public class WindowItemsHook implements I18NHook {

    private I18NUtilities i18n;

    /**
     * Create a new WindowItemsHook.
     *
     * @param i18n
     */
    public WindowItemsHook(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.Play.Server.WINDOW_ITEMS;
    }

    @Override
    public void on(PacketEvent event) {
        final Player player   = event.getPlayer();
        final PacketContainer packet   = event.getPacket();
        final Locale language = this.i18n.getLocale( player );

        boolean     changed = false;
        ItemStack[] items   = packet.getItemArrayModifier().read( 0 );
        if ( items != null ) {
            for ( int i = 0; i < items.length; ++i ) {
                ItemStack stack = items[i];
                if ( stack == null ) {
                    continue;
                }
                ItemMeta meta = stack.getItemMeta();
                if ( meta == null ) {
                    continue;
                }
                String message = meta.getDisplayName();
                if ( message == null ) {
                    continue;
                }

                //self.getLogger().info( "#WindowItems: Message of Item = " + message );
                String translated = this.i18n.translateMessageIfAppropriate( language, message );

                if ( message != translated ) {
                    // Got to localize the item's display name:

                    if ( !changed ) {
                        // Construct a shallow clone of the array as we do NOT want
                        // to overwrite the original stack with the contents we modified:
                        items = Arrays.copyOf( items, items.length );
                    }

                    // Got to clone the item stack here in order not to modify its original
                    // reference as it might be in use by the actual inventory:
                    stack = stack.clone();
                    meta = stack.getItemMeta();
                    meta.setDisplayName( translated );
                    stack.setItemMeta( meta );
                    items[i] = stack;

                    changed = true;
                }
            }

            if ( changed ) {
                // Only write back when really needed:
                packet.getItemArrayModifier().write( 0, items );
            }
        }
    }

}
