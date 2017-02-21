package com.cloudhome.listener;

public interface NetResultListener {
	/**
     * @param action 同一个activity里可能有多个网络请求，第一个参数用于区分到底是哪个请求的返回结果
     * @param flag  区分是请求成功，失败，还是其他原因
     * @param dataObj
     */
    void ReceiveData(int action, int flag, Object dataObj);
}
