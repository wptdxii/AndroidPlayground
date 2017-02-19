package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class AuthLogin1 {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();

	private int action;
	private String errmsg;
	private String url;
	private UserBean bean;

	public AuthLogin1(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		url=IpConfig.getUri("getMemberLogin");
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		key_value.put("mobile", params[0].toString());
		key_value.put("password", params[1].toString());
		action=(Integer) params[2];
		bean= (UserBean) params[3];
		Log.i("login----url",url);
		Log.i("login----mobile",params[0].toString());
		Log.i("login----password",params[1].toString());
		builder.params(key_value).build().execute(new StringCallback(){
			@Override
			public void onError(Call call, Exception e, int id) {

				receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				try {
					if(jsonString.equals("") || jsonString.equals("null"))
					{
						receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
					}
					else{
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						String errMsg = jsonObject.getString("errmsg");
						if(errcode.equals("0"))
						{
							String data = jsonObject.getString("data");
							JSONObject dataObject = new JSONObject(data);
							String sex = dataObject.getString("sex");
							String birthday = dataObject.getString("birthday");
							String mobile = dataObject.getString("mobile");
							String user_id = dataObject.getString("user_id");
							String user_id_encode= URLEncoder.encode(user_id);
							String token = dataObject.getString("token");
							String cert_b = dataObject.getString("cert_b");
							String cert_a = dataObject.getString("cert_a");
							String licence = dataObject.getString("licence");
							String assessment="";

							JSONObject typeObj=dataObject.getJSONObject("type");
							String type_code = typeObj.getString("code");
							JSONObject stateObj=dataObject.getJSONObject("state");
							String state_code = stateObj.getString("code");

							String nickname = dataObject.getString("nickname");
							String avatar = dataObject.getString("avatar");
							String truename = dataObject.getString("name");
							String idno = dataObject.getString("idno");
							String company_name = dataObject.getString("company_name");
							String company = dataObject.getString("company");
							String mobile_area = dataObject.getString("mobile_area");
							String bank_name = dataObject.getString("bank_name");
							String bank_no = dataObject.getString("bank_no");
							String  ccyj_reference_flag = dataObject.getString("ccyj_reference_flag");
							String referral = dataObject.getString("referral");
							JSONObject referralObject = new JSONObject(referral);
							String refer_name = referralObject.getString("name");
							String refer_user_id=referralObject.getString("user_id");
							String recomend_code = dataObject.getString("recomend_code");
							if (state_code.equals("00")) {
								String personal_specialty = dataObject.getString("personal_specialty");
								String good_count = dataObject.getString("good_count");
								String cert_num_isShowFlg = dataObject.getString("cert_num_isShowFlg");
								String isShow_in_expertlist = dataObject.getString("isShow_in_expertlist");
								String isshow_card = dataObject.getString("isshow_card");
								String mobile_num_short = dataObject.getString("mobile_num_short");
								String personal_context = dataObject.getString("personal_context");
								bean.setPersonal_specialty(personal_specialty);
								bean.setGood_count(good_count);
								bean.setCert_num_isShowFlg(cert_num_isShowFlg);
								bean.setIsShow_in_expertlist(isShow_in_expertlist);
								bean.setIsshow_card(isshow_card);
								bean.setMobile_num_short(mobile_num_short);
								bean.setPersonal_context(personal_context);
							}

							bean.setMobile(mobile);
							bean.setUser_id(user_id);
							bean.setUser_id_encode(user_id_encode);
							bean.setToken(token);
							bean.setIdno(idno);
							bean.setType(type_code);
							bean.setState(state_code);
							bean.setAvatar(avatar);
							bean.setCert_a(cert_a);
							bean.setCert_b(cert_b);
							bean.setLicence(licence);
							bean.setAssessment(assessment);
							bean.setNickname(nickname);
							bean.setName(truename);
							bean.setSex(sex);
							bean.setBirthday(birthday);
							bean.setCompany_name(company_name);
							bean.setCompany(company);
							bean.setMobile_area(mobile_area);
							bean.setBank_name(bank_name);
							bean.setBank_no(bank_no);
							bean.setReferral_name(refer_name);
							bean.setReferral_user_id(refer_user_id);
							bean.setRecomend_code(recomend_code);
							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errMsg);
						}else{
							receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, errMsg);
						}
					}
				} catch (Exception e) {
					receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
				}
			}
		});
	}
}
