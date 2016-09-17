package com.wptdxii.androidpractice.internal.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {

}
