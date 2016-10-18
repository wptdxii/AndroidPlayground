package com.wptdxii.playground.internal.di.component;

import android.app.Activity;

import com.wptdxii.playground.internal.di.module.ActivityModule;
import com.wptdxii.playground.internal.di.scope.PerActivity;

import dagger.Component;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@PerActivity
@Component(modules = ActivityModule.class, dependencies = AppComponent.class)
public interface ActivityComponent {
    Activity activity();
}
