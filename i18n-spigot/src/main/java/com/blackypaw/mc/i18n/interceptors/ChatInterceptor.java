package com.blackypaw.mc.i18n.interceptors;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatInterceptor implements I18NInterceptor {

    private I18NUtilities i18n;

    /**
     * Create a new ChatInterceptor.
     *
     * @param i18n
     */
    public ChatInterceptor(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public List<I18NHook> hooks() {
        I18NHook chatHook = new I18NHook() {
            @Override
            public PacketType getPacketType() {
                return PacketType.Play.Server.CHAT;
            }

            @Override
            public void on(PacketEvent event) {
                final Player player = event.getPlayer();
                final PacketContainer packet = event.getPacket();

                String message = ChatInterceptor.this.i18n.restoreTextFromChatComponent(packet.getChatComponents().read(0));
                String translation = ChatInterceptor.this.i18n.translateMessageIfAppropriate(ChatInterceptor.this.i18n.getLocale(player), message);

                if (message != translation) {
                    // Yes, the test of instance equality is what I want to do here
                    // as it saves me time determining whether the message was actually
                    // translated or not:

                    // Issue #4:
                    //  https://github.com/BlackyPaw/I18N/issues/4
                    //
                    // Do this ugly conversion thing here in order to enforce usage of JSON color tags
                    // over the legacy chat format. Invoking WrappedChatComponent.fromText( ... ) for
                    // example, would result in a JSON such as {"text":"§cExample"} which can, if long enough
                    // and automatically put onto a new line by Minecraft, lose its formatting on the addtional
                    // line of chat. This issue does only arise for chat currently as it is the only place where
                    // multiple lines are even supported. In case this issue should arise again somewhere else
                    // one will simply have to do this conversion there, too:
                    packet.getChatComponents().write(0, ComponentConverter.fromBaseComponent(TextComponent.fromLegacyText(translation)));
                }
            }
        };

        return Lists.newArrayList(chatHook);
    }

}
