/* Copyright 2016 Acquized
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            public void onPacketReceiving(PacketEvent event) {
                Locale locale = new Locale(event.getPacket().getStrings().readSafely(0).substring(0, 2));
                I18NUtilities.trySetPlayerLocale(event.getPlayer(), locale);
            }

        });

    }

}
