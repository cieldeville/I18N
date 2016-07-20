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

import java.util.Locale;

public class SetSlotHook implements I18NHook {

    private I18NUtilities i18n;

    /**
     * Create a new SetSlotHook.
     *
     * @param i18n
     */
    public SetSlotHook(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.Play.Server.SET_SLOT;
    }

    @Override
    public void on(PacketEvent event) {
        final Player player   = event.getPlayer();
        final PacketContainer packet   = event.getPacket();
        final Locale language = this.i18n.getLocale( player );

        ItemStack stack = packet.getItemModifier().read( 0 );
        if ( stack != null ) {
            ItemMeta meta = stack.getItemMeta();
            if ( meta == null ) {
                return;
            }
            String message = meta.getDisplayName();
            if ( message == null ) {
                return;
            }

            //self.getLogger().info( "#SetSlot: Message of Item = " + message );
            String translated = this.i18n.translateMessageIfAppropriate( language, message );

            if ( message != translated ) {
                // Only write back when really needed:

                // Got to clone here as otherwise we might be modifying an instance that
                // is actually also used by the inventory:
                stack = stack.clone();
                meta = stack.getItemMeta();
                meta.setDisplayName( translated );
                stack.setItemMeta( meta );
                packet.getItemModifier().write( 0, stack );
            }
        }
    }

}
