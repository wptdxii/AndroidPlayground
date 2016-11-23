package com.wptdxii.playground.test.bean;

import java.io.Serializable;

/**
 * Created by bkyj-005 on 2016/8/26.
 */
public class MyClientBean implements Serializable {
    private String name;
    private String nameFirstWord;
    private String clientImg;
    private String clientId;
    private String underId;
    private String underState;
    private String underAvatar;
    private String underType;
    private String underIsAuthentication;

    private String clientSex;
    private int clientPolicyNum;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameFirstWord() {
        return nameFirstWord;
    }

    public void setNameFirstWord(String nameFirstWord) {
        this.nameFirstWord = nameFirstWord;
    }



    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientImg() {
        return clientImg;
    }

    public void setClientImg(String clientImg) {
        this.clientImg = clientImg;
    }

    public String getUnderId() {
        return underId;
    }

    public void setUnderId(String underId) {
        this.underId = underId;
    }

    public String getUnderState() {
        return underState;
    }

    public void setUnderState(String underState) {
        this.underState = underState;
    }

    public String getUnderAvatar() {
        return underAvatar;
    }

    public void setUnderAvatar(String underAvatar) {
        this.underAvatar = underAvatar;
    }

    public String getUnderType() {
        return underType;
    }

    public void setUnderType(String underType) {
        this.underType = underType;
    }

    public String getUnderIsAuthentication() {
        return underIsAuthentication;
    }

    public void setUnderIsAuthentication(String underIsAuthentication) {
        this.underIsAuthentication = underIsAuthentication;
    }

    public String getClientSex() {
        return clientSex;
    }

    public void setClientSex(String clientSex) {
        this.clientSex = clientSex;
    }

    public int getClientPolicyNum() {
        return clientPolicyNum;
    }

    public void setClientPolicyNum(int clientPolicyNum) {
        this.clientPolicyNum = clientPolicyNum;
    }
}
