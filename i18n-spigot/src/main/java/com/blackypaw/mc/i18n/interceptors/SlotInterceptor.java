package com.blackypaw.mc.i18n.interceptors;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.blackypaw.mc.i18n.interceptors.slot.SetSlotHook;
import com.blackypaw.mc.i18n.interceptors.slot.WindowItemsHook;
import com.google.common.collect.Lists;

import java.util.List;

public class SlotInterceptor implements I18NInterceptor {

    private I18NHook setSlotHook, windowItemsHook;

    /**
     * Create a new SlotInterceptor.
     *
     * @param i18n
     */
    public SlotInterceptor(I18NUtilities i18n) {
        this.setSlotHook = new SetSlotHook(i18n);
        this.windowItemsHook = new WindowItemsHook(i18n);
    }

    @Override
    public List<I18NHook> hooks() {
        return Lists.newArrayList(
                this.setSlotHook,
                this.windowItemsHook
        );
    }

}
