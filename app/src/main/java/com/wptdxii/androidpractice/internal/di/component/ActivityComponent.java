package com.wptdxii.androidpractice.internal.di.component;

import android.app.Activity;

import com.wptdxii.androidpractice.internal.di.module.ActivityModule;
import com.wptdxii.androidpractice.internal.di.scope.PerActivity;

import dagger.Component;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@PerActivity
@Component(modules = ActivityModule.class, dependencies = AppComponent.class)
public interface ActivityComponent {
    Activity activity();
}
