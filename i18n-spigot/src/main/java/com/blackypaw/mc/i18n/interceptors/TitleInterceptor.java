package com.blackypaw.mc.i18n.interceptors;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.List;

public class TitleInterceptor implements I18NInterceptor {

    private I18NUtilities i18n;

    /**
     * Create a new TitleInterceptor.
     *
     * @param i18n
     */
    public TitleInterceptor(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public List<I18NHook> hooks() {
        I18NHook titleHook = new I18NHook() {
            @Override
            public PacketType getPacketType() {
                return PacketType.Play.Server.TITLE;
            }

            @Override
            public void on(PacketEvent event) {
                final Player player = event.getPlayer();
                final PacketContainer packet = event.getPacket();

                EnumWrappers.TitleAction action = packet.getTitleActions().read( 0 );
                if ( action == EnumWrappers.TitleAction.TITLE || action == EnumWrappers.TitleAction.SUBTITLE ) {
                    String message     = TitleInterceptor.this.i18n.restoreTextFromChatComponent( packet.getChatComponents().read( 0 ) );
                    String translation = TitleInterceptor.this.i18n.translateMessageIfAppropriate( TitleInterceptor.this.i18n.getLocale( player ), message );

                    if ( message != translation ) {
                        packet.getChatComponents().write( 0, WrappedChatComponent.fromText( translation ) );
                    }
                }
            }
        };

        return Lists.newArrayList(titleHook);
    }
}
