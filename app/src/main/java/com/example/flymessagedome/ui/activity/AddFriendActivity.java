package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.ToastUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
public class AddFriendActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.my_name)
    TextView my_name;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_friend;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.my_qr_view, R.id.search, R.id.back, R.id.sys_add, R.id.phone_contarct_add})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.my_qr_view:
                startActivity(new Intent(AddFriendActivity.this, MyQrCodeActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(AddFriendActivity.this, SearchUserActivity.class));
                break;
            case R.id.sys_add:
                sys();
                break;
            case R.id.phone_contarct_add:
                startActivity(new Intent(mContext, ContractAddActivity.class));
                break;
        }
    }

    @Override
    public void initDatas() {
        my_name.setText("我的飞讯名:" + LoginActivity.loginUser.getU_name());
    }

    @Override
    public void configViews() {

    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    public void sys() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            new IntentIntegrator(((AddFriendActivity) mContext))
                    .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                    //.setPrompt("请对准二维码")// 设置提示语
                    .setCameraId(0)// 选择摄像头,可使用前置或者后置
                    .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                    .setCaptureActivity(QrCodeActivity.class)//自定义扫码界面
                    .initiateScan();// 初始化扫码
        } else {
            EasyPermissions.requestPermissions(this, "扫码需要以下权限:\n\n1.摄像头权限", Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO) {
            sys();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO) {
            ToastUtils.showToast("你拒绝了摄像头权限");
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                String content = intentResult.getContents();//返回值
                Log.e("TAG", content);
                if (content == null) {
                    ToastUtils.showToast("扫描失败");
                } else if (content.contains("http")) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("URLString", content);
                    startActivity(intent);
                } else if (content.contains("[") && content.contains("]")) {
                    String fileContent = content.substring(content.indexOf("[") + 1, content.indexOf("]"));
                    if (fileContent.contains(":")) {
                        String[] fileParams = fileContent.split(":");
                        fileContent = fileParams[0];
                        if (fileContent.equals("FlyMessage-addFriend")) {
                            String u_name = fileParams[1];
                            Intent intent = new Intent(mContext, ShowUserActivity.class);
                            intent.putExtra("userName", u_name);
                            startActivity(intent);
                        } else if (fileContent.equals("FlyMessage-addGroup")) {
                            String g_id = fileParams[1];
                            try {
                                int gId = Integer.parseInt(g_id);
                                new AsyncTask<Void, Void, GroupModel>() {
                                    @Override
                                    protected GroupModel doInBackground(Void... voids) {
                                        return HttpRequest.getGroupMsg(gId);
                                    }

                                    @Override
                                    protected void onPostExecute(GroupModel groupModel) {
                                        try {
                                            if (groupModel != null && groupModel.code == Constant.SUCCESS) {
                                                Bundle bundle = new Bundle();
                                                bundle.putParcelable("group", groupModel.getGroup());
                                                Intent intent = new Intent(mContext, GroupMsgActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            } else if (groupModel != null) {
                                                ToastUtils.showToast(groupModel.msg);
                                            } else {
                                                ToastUtils.showToast("获取群聊信息失败");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
