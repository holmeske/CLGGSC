package com.sc.clgg.activity.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ReplacementTransformationMethod;
import android.widget.EditText;
import android.widget.Toast;

import com.sc.clgg.R;
import com.sc.clgg.activity.TakePhotoActivity;
import com.sc.clgg.bean.Check;
import com.sc.clgg.retrofit.RetrofitHelper;
import com.sc.clgg.tool.helper.LogHelper;
import com.sc.clgg.util.PotatoKt;

import org.devio.takephoto.model.TResult;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author lvke
 */
public class AddVehicleActivity extends TakePhotoActivity {
    private EditText etCarNo;
    private EditText etCarVin;

    private Call<Check> addVehicleHttp;
    private Call<Map<String, Object>> vehicleLicenseInfoHttp;

    private String scan = "0";
    private String carType = "";
    private String carOwner = "";
    private String address = "";
    private String engineNumber = "";
    private String registrationDate = "";
    private String carLicenceDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        initTitle("添加车辆");

        etCarNo = findViewById(R.id.et_car_no);
        etCarVin = findViewById(R.id.et_car_vin);

        etCarNo.setTransformationMethod(new AllCapTransformationMethod());
        etCarVin.setTransformationMethod(new AllCapTransformationMethod());

        findViewById(R.id.tv_add).setOnClickListener(v -> add());
        findViewById(R.id.iv_scan).setOnClickListener(v -> PotatoKt.showTakePhoto(AddVehicleActivity.this, getTakePhoto(), 1));
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        if (result != null && result.getImage() != null) {
            scan(result.getImage().getCompressPath());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 识别行驶证
     */
    private void scan(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        showProgressDialog();
        vehicleLicenseInfoHttp = new RetrofitHelper().scan(new File(filePath));
        vehicleLicenseInfoHttp.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NotNull Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                hideProgressDialog();
                try {
                    Map<String, Object> allMap = response.body();
                    if (allMap.containsKey("success") && (boolean) allMap.get("success")) {
                        Map<String, Object> identifyMap = (Map<String, Object>) allMap.get("identify");

                        if (identifyMap.containsKey("words_result")) {
                            Map<String, Object> resultMap = (Map<String, Object>) identifyMap.get("words_result");

                            etCarNo.setText(((Map<String, String>) resultMap.get("号牌号码")).get("words"));
                            etCarVin.setText(((Map<String, String>) resultMap.get("车辆识别代号")).get("words"));

                            scan = "1";
                            carType = ((Map<String, String>) resultMap.get("车辆类型")).get("words");
                            carOwner = ((Map<String, String>) resultMap.get("所有人")).get("words");
                            address = ((Map<String, String>) resultMap.get("住址")).get("words");
                            engineNumber = ((Map<String, String>) resultMap.get("发动机号码")).get("words");
                            registrationDate = ((Map<String, String>) resultMap.get("注册日期")).get("words");
                            carLicenceDate = ((Map<String, String>) resultMap.get("发证日期")).get("words");
                        }

                    }
                } catch (Exception e) {
                    LogHelper.e(e);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Map<String, Object>> call, Throwable t) {
                hideProgressDialog();
            }
        });
    }

    /**
     * 添加车辆
     */
    private void add() {
        String carno = etCarNo.getText().toString();
        String vin = etCarVin.getText().toString();

        if (TextUtils.isEmpty(carno)) {
            Toast.makeText(this, "请输入车牌号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(vin)) {
            Toast.makeText(this, "请输入VIN码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vin.length() != 17) {
            Toast.makeText(this, "请输入17位VIN码", Toast.LENGTH_SHORT).show();
            return;
        }

        addVehicleHttp = new RetrofitHelper().vehicleAdd(carno, vin, scan, carType, carOwner, address, engineNumber, registrationDate, carLicenceDate);
        addVehicleHttp.enqueue(new Callback<Check>() {

            @Override
            public void onResponse(Call<Check> call, Response<Check> response) {
                if (response.body().getSuccess()) {
                    Toast.makeText(AddVehicleActivity.this, "添加车辆成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddVehicleActivity.this, String.valueOf(response.body().getMsg()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Check> call, Throwable t) {
                Toast.makeText(AddVehicleActivity.this, R.string.network_anomaly, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (addVehicleHttp != null) {
            addVehicleHttp.cancel();
        }
        if (vehicleLicenseInfoHttp != null) {
            vehicleLicenseInfoHttp.cancel();
        }
    }

    /**
     * 小写字母自动转换为大写
     */
    private class AllCapTransformationMethod extends ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            return new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        }

        @Override
        protected char[] getReplacement() {
            return new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        }

    }

}