package com.blackypaw.mc.i18n.interceptors;

import com.blackypaw.mc.i18n.I18NHook;
import com.blackypaw.mc.i18n.I18NInterceptor;
import com.blackypaw.mc.i18n.I18NUtilities;
import com.blackypaw.mc.i18n.interceptors.scoreboard.ObjectiveHook;
import com.blackypaw.mc.i18n.interceptors.scoreboard.ScoreHook;
import com.blackypaw.mc.i18n.interceptors.scoreboard.TeamHook;
import com.blackypaw.mc.i18n.interceptors.sign.MapChunkHook;
import com.blackypaw.mc.i18n.interceptors.sign.TileEntityDataHook;
import com.google.common.collect.Lists;

import java.util.List;

public class ScoreboardInterceptor implements I18NInterceptor {

    private I18NHook objectiveHook, scoreHook, teamHook;

    /**
     * Create a new ScoreboardInterceptor.
     *
     * @param i18n
     */
    public ScoreboardInterceptor(I18NUtilities i18n) {
        this.objectiveHook = new ObjectiveHook(i18n);
        this.scoreHook = new ScoreHook(i18n);
        this.teamHook = new TeamHook(i18n);
    }

    @Override
    public List<I18NHook> hooks() {
        return Lists.newArrayList(
                this.objectiveHook,
                this.scoreHook,
                this.teamHook
        );
    }

}
