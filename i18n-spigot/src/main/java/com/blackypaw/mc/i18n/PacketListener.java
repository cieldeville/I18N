/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */
 
 package com.blackypaw.mc.i18n;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;

import java.util.Locale;

public class PacketListener {

    public PacketListener() {

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Bukkit.getPluginManager().getPlugin("I18N"), PacketType.Play.Client.SETTINGS) {

            @Override
            public void onPacketReceiving( PacketEvent event ) {
                Locale locale = new Locale( event.getPacket().getStrings().readSafely(0).substring(0, 2) );
                I18NUtilities.trySetPlayerLocale( event.getPlayer(), locale );
            }

        });

    }

}
