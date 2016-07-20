package com.blackypaw.mc.i18n.interceptors;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.blackypaw.mc.i18n.event.PlayerLanguageSettingEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class SettingsInterceptor implements I18NInterceptor {

    private I18NUtilities i18n;

    /**
     * Create a new SettingsInterceptor.
     *
     * @param i18n
     */
    public SettingsInterceptor(I18NUtilities i18n) {
        this.i18n = i18n;
    }

    @Override
    public List<I18NHook> hooks() {
        I18NHook settingsHook = new I18NHook() {
            @Override
            public PacketType getPacketType() {
                return PacketType.Play.Client.SETTINGS;
            }

            @Override
            public void on(PacketEvent event) {
                final Player player   = event.getPlayer();
                final Locale language = new Locale( event.getPacket().getStrings().read( 0 ).substring( 0, 2 ) );

                PlayerLanguageSettingEvent call = new PlayerLanguageSettingEvent( player, language );
                Bukkit.getServer().getPluginManager().callEvent( call );
            }
        };

        return Lists.newArrayList(settingsHook);
    }
}
