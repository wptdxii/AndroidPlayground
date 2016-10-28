package com.wptdxii.playground.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.view.sortlistview.CharacterParser;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.cloudhome.view.sortlistview.PinyinComparator;
import com.cloudhome.view.sortlistview.SideBar;
import com.cloudhome.view.sortlistview.SideBar.OnLetterChangedListener;
import com.cloudhome.view.sortlistview.SortAdapter;
import com.cloudhome.view.sortlistview.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("DefaultLocale")
public class CityselectActivity extends BaseActivity {

    private SideBar sideBar;
    private TextView dialog;
    private ListView sortListView;
    private ClearEditText mClearEditText;
    private SortAdapter adapter;


    private CharacterParser characterParser;

    private PinyinComparator pinyinComparator;
    private List<SortModel> SourceDateList;
    private ImageView selectcity_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cityselect);
        initViews();
    }

    private void initViews() {

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        selectcity_back = (ImageView) findViewById(R.id.selectcity_back);
        sideBar.setTextView(dialog);


        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        SourceDateList = filledData(getResources().getStringArray(R.array.date));

        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);


        sideBar.setOnLetterChangedListener(new OnLetterChangedListener() {

            @Override
            public void onTouchLetterChanged(String s) {

                System.out.println("1111111111" + s);
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });
        selectcity_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = getIntent();

                intent.putExtra("city", "");
                setResult(0, intent);
                CityselectActivity.this.finish();
            }
        });
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = getIntent();

                intent.putExtra("city", ((SortModel) adapter.getItem(position)).getName());
                setResult(0, intent);
                CityselectActivity.this.finish();
                Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();


            }
        });


        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {


        // 如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = getIntent();

            intent.putExtra("city", "");
            setResult(0, intent);
            CityselectActivity.this.finish();

        }
        return super.onKeyDown(keyCode, event);
    }


    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            //	sortModel.setName(date[i]);


            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();


            if (sortString.matches("[A-Z]")) {
                sortModel.setName(date[i]);
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {


                date[i] = date[i].substring(1);

                sortModel.setName(date[i]);
                sortModel.setSortLetters("热门");


            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }


    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr) != -1 || characterParser.getSelling(name).startsWith(filterStr)) {
                    filterDateList.add(sortModel);
                }
            }
        }


        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

}
