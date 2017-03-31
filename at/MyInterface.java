package com.cloudhome.activity;


public class MyInterface {


    public static interface OnProposal_SelectActivityChangeListener {
        void CloseRightMenu();


        void onActivityChange(String companyname, String companycode);


    }


    public static interface MenuMainFragmentListener {
        void openLeftMenu();
    }

    public static interface MainFraToShopFraListener {

        void openShopFra();
    }


}
