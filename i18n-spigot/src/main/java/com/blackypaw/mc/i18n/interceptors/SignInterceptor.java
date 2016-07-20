package com.blackypaw.mc.i18n.interceptors;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.blackypaw.mc.i18n.interceptors.sign.MapChunkHook;
import com.blackypaw.mc.i18n.interceptors.sign.TileEntityDataHook;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;

import java.util.List;

public class SignInterceptor implements I18NInterceptor {

    private I18NHook mapChunkHook, tileEntityDataHook;

    /**
     * Create a new SignInterceptor.
     *
     * @param i18n
     */
    public SignInterceptor(I18NUtilities i18n) {
        this.mapChunkHook = new MapChunkHook(i18n);
        this.tileEntityDataHook = new TileEntityDataHook(i18n);
    }

    @Override
    public List<I18NHook> hooks() {
        return Lists.newArrayList(
                this.mapChunkHook,
                this.tileEntityDataHook
        );
    }

}
