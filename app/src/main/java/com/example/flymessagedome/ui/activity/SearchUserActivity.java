package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SearchUserActivity extends BaseActivity {

    @BindView(R.id.search_person)
    View person;
    @BindView(R.id.search_person_tv)
    TextView personTv;
    @BindView(R.id.search_group)
    View group;
    @BindView(R.id.search_group_tv)
    TextView groupTv;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.cancel)
    ImageView cancel;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_user;
    }

    @SuppressLint("StaticFieldLeak")
    @OnClick({R.id.cancel, R.id.cancel_btn, R.id.search_group, R.id.search_person})
    public void onViewClick(View view) {
        String words = searchEt.getText().toString();
        switch (view.getId()) {
            case R.id.cancel:
                person.setVisibility(View.GONE);
                group.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                searchEt.setText("");
                break;
            case R.id.cancel_btn:
                finish();
                break;
            case R.id.search_person:
                new AsyncTask<Void, Void, Users>() {
                    @Override
                    protected Users doInBackground(Void... voids) {
                        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
                        if (loginToken == null) {
                            return null;
                        }
                        return HttpRequest.getUserMsg(words, loginToken);
                    }

                    @Override
                    protected void onPostExecute(Users users) {
                        try {
                            if (users != null && users.code == Constant.SUCCESS) {
                                if (users.getUser() != null) {
                                    if (users.getUser().getU_id() != LoginActivity.loginUser.getU_id()) {
                                        Intent intent = new Intent(SearchUserActivity.this, ShowUserActivity.class);
                                        intent.putExtra("userName", users.getUser().getU_name());
                                        startActivity(intent);
                                    } else {
                                        startActivity(new Intent(mContext, LoginUserMsgActivity.class));
                                    }
                                }
                            } else if (users == null || users.code == Constant.NOT_LOGIN || users.code == Constant.TOKEN_EXCEED) {
                                ToastUtils.showToast("获取登录用户信息失败");
                                finish();
                            } else {
                                Intent intent = new Intent(SearchUserActivity.this, SearchResultActivity.class);
                                intent.putExtra("searchWords", words);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
                break;
            case R.id.search_group:
                Intent intent = new Intent(SearchUserActivity.this, SearchGroupResultActivity.class);
                intent.putExtra("searchWords", words);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initDatas() {
        person.setVisibility(View.GONE);
        group.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    personTv.setText("找人" + s);
                    groupTv.setText("找群" + s);
                    person.setVisibility(View.VISIBLE);
                    group.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                } else {
                    person.setVisibility(View.GONE);
                    group.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void configViews() {

    }
}
