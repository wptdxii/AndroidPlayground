package com.wptdxii.androidpractice.internal.di.component;

import com.wptdxii.androidpractice.internal.di.module.ActivityModule;
import com.wptdxii.androidpractice.internal.di.module.GanksModule;
import com.wptdxii.androidpractice.internal.di.scope.PerActivity;
import com.wptdxii.androidpractice.ui.activity.ContentActivity;

import dagger.Component;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@PerActivity
@Component(modules = {ActivityModule.class, GanksModule.class}, dependencies = AppComponent.class)
public interface GanksComponent {

    void inject(ContentActivity activity);
}
