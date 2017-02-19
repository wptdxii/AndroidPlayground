package com.cloudhome.network.retrofit.entity;

/**
 * Created by wptdxii on 2017/2/16 0016.
 *
 * 获取版本的Entity
 */

public class AppVersionEntity {

    /**
     * appName : 保客云集
     * appVersion : 1.0.0
     * clientType : android
     * createdBy : zhangchuanfu
     * createdDate : 2017-02-15T09:44:47.000+0000
     * downloadUrl : http://d.baokeyun.com
     * fileSize : 11000
     * lastModifiedBy : zhangchuanfu
     * lastModifiedDate : 2017-02-15T09:44:47.000+0000
     * updateDesc : 更新内容
     * updateMode : 1
     * updateNotice : 您的版本太旧了,请下载最新的版本。新的版本操作起来更加流畅，功能更加完善。
     */

    private String appName;
    private String appVersion;
    private String clientType;
    private String createdBy;
    private String createdDate;
    private String downloadUrl;
    private int fileSize;
    private String lastModifiedBy;
    private String lastModifiedDate;
    private String updateDesc;
    private String updateMode;
    private String updateNotice;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public String getUpdateNotice() {
        return updateNotice;
    }

    public void setUpdateNotice(String updateNotice) {
        this.updateNotice = updateNotice;
    }
}
