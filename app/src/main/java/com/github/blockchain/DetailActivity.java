package com.github.blockchain;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;

import com.github.blockchain.models.Apk;
import com.github.blockchain.utils.BlockChainManager;

import io.github.rajdeep1008.apkwizard.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Apk apk=(Apk)getIntent().getExtras().get("apk_info");
        AppCompatImageView apk_icon_iv= findViewById(R.id.apk_icon_iv);
        AppCompatTextView apk_label_tv =findViewById(R.id.apk_label_tv);
        AppCompatTextView apk_package_tv=findViewById(R.id.apk_package_tv);
        CharSequence name = apk.getAppName();
        apk_icon_iv.setImageResource(R.drawable.ic_launcher);
        BlockChainManager.getInstance().getVerifiedApp(apk.getAppName(), apk.getPackageName(), apk.getPublicKey(), new BlockChainManager.VerifiedDelegate() {
            @Override
            public void OnSuccess(String packageName) {
                apk_package_tv.setText(apk.getPackageName() +"\n"+packageName);
            }

            @Override
            public void OnError() {

            }
        });
        try {
            ApplicationInfo app = getPackageManager().getApplicationInfo(apk.getPackageName(), 0);
            Drawable icon = getPackageManager().getApplicationIcon(app);

            apk_icon_iv.setImageDrawable(icon);

        } catch ( PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        apk_label_tv.setText(name);
        apk_package_tv.setText(apk.getPackageName());

        FloatingActionButton fab=findViewById(R.id.add_app_fab);
        if( !TextUtils.isEmpty(apk.getSourceDir()))
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VerifyApplication.class);
                intent.putExtra("app_id",0);
                startActivity(intent);
            }
        });

    }

}
