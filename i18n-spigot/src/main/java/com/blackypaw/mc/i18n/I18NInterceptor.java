package com.blackypaw.mc.i18n;

import java.util.List;

public interface I18NInterceptor {

    /**
     * Retrieve the hooks used in this interceptor.
     *
     * @return
     */
    List<I18NHook> hooks();

}
