package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.PhoneInfo;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.ui.adapter.ContractAdapter;
import com.example.flymessagedome.ui.contract.AddContract;
import com.example.flymessagedome.ui.presenter.ContractAddPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("NonConstantResourceId")
public class ContractAddActivity extends BaseActivity implements AddContract.View {
    private ArrayList<PhoneInfo> phoneInfos = new ArrayList<>();
    private ArrayList<SearchUserModel.ResultBean> resultBeans = new ArrayList<>();

    @BindView(R.id.contract_list)
    RecyclerView recyclerView;
    @BindView(R.id.none)
    TextView none;
    ContractAdapter adapter;
    @Inject
    ContractAddPresenter contractAddPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_contract_add;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }

    @Override
    public void initDatas() {
        if (!NetworkUtils.isConnected(mContext)) {
            ToastUtils.showToast("请检查网络连接!");
            finish();
            return;
        }
        adapter = new ContractAdapter(resultBeans, mContext);
        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            if (view.getId() == R.id.add_btn) {
                Intent rq_intent = new Intent(mContext, RequestFriendActivity.class);
                rq_intent.putExtra("uName", resultBeans.get(position).getU_name());
                startActivity(rq_intent);
            } else {
                Intent intent = new Intent(mContext, ShowUserActivity.class);
                intent.putExtra("userName", resultBeans.get(position).getU_name());
                startActivity(intent);
            }
        });
        StaggeredGridLayoutManager msgGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(msgGridLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        getContract();
    }

    @SuppressLint("StaticFieldLeak")
    @AfterPermissionGranted(Constant.READ_CONTRACT)
    private void getContract() {
        phoneInfos = new ArrayList<>();
        String[] perms = {Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            showLoadingDialog(true, "读取联系人信息中");
            new AsyncTask<Void, Void, ArrayList<PhoneInfo>>() {
                @Override
                protected ArrayList<PhoneInfo> doInBackground(Void... voids) {
                    Cursor cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, null, null, null);
                    //moveToNext方法返回的是一个boolean类型的数据
                    while
                    (cursor.moveToNext()) {
                        //读取通讯录的姓名
                        String name = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        //读取通讯录的号码
                        String number = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneInfos.add(new PhoneInfo(name, number));
                    }
                    HashSet<PhoneInfo> phoneInfoSet = new HashSet<>(phoneInfos);
                    phoneInfos.clear();
                    phoneInfos.addAll(phoneInfoSet);
                    contractAddPresenter.checkPhone(phoneInfos);
                    return phoneInfos;
                }

                @Override
                protected void onPostExecute(ArrayList<PhoneInfo> phoneInfos) {
                    try {
                        if (phoneInfos.size() == 0) {
                            dismissLoadingDialog();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (phoneInfos.size() == 0) {
                        dismissLoadingDialog();
                    }
                }
            }.execute();

        } else {
            EasyPermissions.requestPermissions(this, "请赋予读取联系人权限，以使用此功能", Constant.READ_CONTRACT, perms);
        }
    }

    @Override
    public void configViews() {
        contractAddPresenter.attachView(this);
    }

    @Override
    public void initResult(SearchUserModel.ResultBean resultBean, PhoneInfo info, boolean last) {
        try {
            if (last)
                dismissLoadingDialog();
            else
                showLoadingDialog(true, "读取联系人信息中");
            if (resultBean != null) {
                none.setVisibility(View.GONE);
                resultBean.setU_sign(info.getName());
                resultBeans.add(resultBean);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {
        try {
            dismissLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tokenExceed() {

    }
}
