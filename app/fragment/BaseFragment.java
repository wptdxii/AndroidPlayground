package com.cloudhome.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.cloudhome.activity.BaseActivity;
import com.cloudhome.listener.PermissionListener;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment {

    /**
     * when activity is recycled by system, isFirstTimeStartFlag will be reset to default true,
     * when activity is recreated because a configuration change for example screen rotate, isFirstTimeStartFlag will stay false
     */
    private boolean isFirstTimeStartFlag = true;

    private final static int FIRST_TIME_START = 0; //when activity is first time start
    private final static int SCREEN_ROTATE = 1;    //when activity is destroyed and recreated because a configuration change, see setRetainInstance(boolean retain)
    private final static int ACTIVITY_DESTROY_AND_CREATE = 2;  //when activity is destroyed because memory is too low, recycled by android system

    protected int getCurrentState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            isFirstTimeStartFlag = false;
            return ACTIVITY_DESTROY_AND_CREATE;
        }


        if (!isFirstTimeStartFlag) {
            return SCREEN_ROTATE;
        }

        isFirstTimeStartFlag = false;
        return FIRST_TIME_START;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setRetainInstance(false);
	}

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    protected void requestPermissions(String[] permissions, PermissionListener listener) {

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).requestPermissions(permissions, listener);
        }
    }

    protected void showPermissionSettingDialog(String msg) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showPermissionSettingDialog(msg);
        }
    }

    public void showRequestPermissionRationale(String msg) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showRequestPermissionRationale(msg);
        }
    }
}
