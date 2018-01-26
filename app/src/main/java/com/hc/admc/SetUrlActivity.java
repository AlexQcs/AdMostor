package com.hc.admc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hc.admc.util.SpUtils;

import java.util.regex.Pattern;

public class SetUrlActivity extends AppCompatActivity {

    private EditText mEtvUrl;
    private EditText mEtvPort;
    private Button mBtnConfirm;
    private Button mBtnCancel;
    private Pattern mPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_url);
        initaData();
        initView();
        initEvent();
    }

    void initaData() {

    }

    void initView() {
        mEtvUrl = (EditText) findViewById(R.id.edit_url);
        mEtvPort = (EditText) findViewById(R.id.edit_port);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mPattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
    }

    void initEvent() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mEtvUrl.getText().toString();
                String port = mEtvPort.getText().toString();
                if (url.equals("")) {
                    Toast.makeText(SetUrlActivity.this, "请输入服务器地址", Toast.LENGTH_SHORT).show();
                } else if (port.equals("")) {
                    Toast.makeText(SetUrlActivity.this, "请输入端口", Toast.LENGTH_SHORT).show();
                } else {
                    SpUtils.put(Constant.SP_BASEURL,"http://"+ url+":"+port);
                    finish();
                }

            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
