package com.wptdxii.playground.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;

public class MyInvitationCodeActivity extends BaseActivity {


	private String erweima_url;

	private String user_id,recomend_code,token;
	boolean start = false;
	private ImageView erweima;
	private RelativeLayout invitation_code_back;
	private TextView title;
	private RelativeLayout rl_right;
	
	private TextView code_copy;
	private TextView code;
	private String mUserId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.my_invitation_code);

		token = sp.getString("Login_TOKEN", "");
		user_id = sp.getString("Login_UID", "");
		mUserId = sp.getString("Login_UID_ENCODE", "");
		init();
		initEvent();
	}



	void init() {

//	旧	erweima_url = IpConfig.getUri("getPreRecForWeb")+"user_id="+user_id;
//		erweima_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx64445853f60fe3e4&redirect_uri=http%3A%2F%2Fwww.baokeyun.com%2Fapi%2Factivity%2Findex.php&response_type=code&scope=snsapi_base&state=preRec_"+user_id+"&token="+token+"&connect_redirect=1#wechat_redirect";
		erweima_url = IpConfig.getIp4() + "/active/invite_friend/wx_page.html?state=" + mUserId + "&token=" + token;
		code_copy = (TextView) findViewById(R.id.code_copy);
		code= (TextView) findViewById(R.id.code);
		invitation_code_back = (RelativeLayout) findViewById(R.id.iv_back);
		title= (TextView) findViewById(R.id.tv_text);
		title.setText("我的邀请码");
		rl_right= (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		erweima = (ImageView) findViewById(R.id.erweima);
		 recomend_code = sp2.getString("recomend_code", "");
		code.setText(recomend_code);
	}

	void initEvent() {


		code_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 得到剪贴板管理器
				ClipboardManager cmb = (ClipboardManager) MyInvitationCodeActivity.this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText(null, recomend_code));
				Toast.makeText(MyInvitationCodeActivity.this, "复制成功", Toast.LENGTH_SHORT)
						.show();

			}
		});

		invitation_code_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});




		int width = 310, height = 310;

		try {

			if (erweima_url.length() < 1) {
				return;
			}
			start = true;
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(erweima_url,
					BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			erweima.setImageBitmap(bitmap);

			File f = new File(Environment.getExternalStorageDirectory()
					+ "/page/" + System.currentTimeMillis() + ".jpg");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);

				fos.flush();
				fos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}





}
