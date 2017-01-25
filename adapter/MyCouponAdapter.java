package com.cloudhome.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.MyCouponsActivity;
import com.cloudhome.bean.UserPrizeBean;
import com.cloudhome.utils.CircleImage;
import com.cloudhome.view.iosalertview.CustomDialog;

import java.util.List;

import static com.cloudhome.R.id.btn_bottom;
import static com.cloudhome.R.id.tv_2;
import static com.cloudhome.R.id.tv_3;
import static com.cloudhome.R.id.tv_5;

/**
 * Created by bkyj-005 on 2016/5/30.
 */
public class MyCouponAdapter extends BaseAdapter{
    private List<UserPrizeBean> list;
    private Context context;
    private LayoutInflater inflater;
    private MyCouponsActivity myCouponsActivity;
    private int index;
    public MyCouponAdapter(List<UserPrizeBean> list, Context context,int index) {
        this.list = list;
        this.context = context;
        if(index==1){
            myCouponsActivity= (MyCouponsActivity) context;
        }
        this.index=index;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            holder=new ViewHolder();
            view=inflater.inflate(R.layout.item_my_coupons,null);
            holder.iv_left_head= (CircleImage) view.findViewById(R.id.iv_left_head);
            holder.tv_1= (TextView) view.findViewById(R.id.tv_1);
            holder.tv_2= (TextView) view.findViewById(tv_2);
            holder.tv_3= (TextView) view.findViewById(tv_3);
            holder.rl_down= (RelativeLayout) view.findViewById(R.id.rl_down);
            holder.tv_5= (TextView) view.findViewById(tv_5);
            holder.btn_bottom= (Button) view.findViewById(btn_bottom);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        final UserPrizeBean bean=list.get(i);
        if(!bean.getGetType().equals("1")){
            holder.rl_down.setVisibility(View.VISIBLE);
            holder.tv_2.setVisibility(View.GONE);
            holder.btn_bottom.setText(bean.getStateName());
            if(bean.getGetType().equals("3")){
                holder.tv_5.setText("");
            }else{
                holder.tv_5.setText("有效期至"+bean.getEffectEnd());
            }
        }else{
            holder.rl_down.setVisibility(View.GONE);
            holder.tv_2.setVisibility(View.VISIBLE);
            holder.tv_2.setText("有效期至"+bean.getEffectEnd());
        }
        Glide.with(context)
                .load(bean.getImgUrl())
                .centerCrop()
                .error(R.drawable.sign_day)
                .crossFade()
                .into(holder.iv_left_head);
        holder.tv_1.setText(bean.getMouldName());
        holder.tv_3.setText(bean.getAddTime());
        if(index==2){
            holder.btn_bottom.setEnabled(false);
        }else{
            holder.btn_bottom.setEnabled(true);
        }
        holder.btn_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bean.getGetType().equals("3")){
                    showPromptDialog(context.getResources().getString(R.string.prompt_dialog));
                }else if(bean.getNeedMsg().equals("1")){
                    if(myCouponsActivity!=null)
                    myCouponsActivity.showGetCouponPop(bean);
                }
            }
        });
        return view;
    }

    class ViewHolder{
        private CircleImage iv_left_head;
        private TextView tv_1;
        private TextView tv_2;
        private TextView tv_3;
        private RelativeLayout rl_down;
        private TextView tv_5;
        private Button btn_bottom;
    }

    private void showPromptDialog(String message){
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


}
