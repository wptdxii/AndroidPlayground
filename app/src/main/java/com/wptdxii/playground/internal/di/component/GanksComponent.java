package com.wptdxii.playground.internal.di.component;

import com.wptdxii.playground.internal.di.module.ActivityModule;
import com.wptdxii.playground.internal.di.module.GanksModule;
import com.wptdxii.playground.internal.di.scope.PerActivity;
import com.wptdxii.playground.ui.sample.home.ContentActivity;

import dagger.Component;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@PerActivity
@Component(modules = {ActivityModule.class, GanksModule.class}, dependencies = AppComponent.class)
public interface GanksComponent {

    void inject(ContentActivity activity);
}
