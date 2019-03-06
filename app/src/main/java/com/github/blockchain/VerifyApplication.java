package com.github.blockchain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuItem;

import com.github.blockchain.utils.BlockChainManager;

import io.github.rajdeep1008.apkwizard.R;


public class VerifyApplication extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_application);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Verify Application");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_application_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppCompatEditText editTextName =findViewById(R.id.name_et);
        AppCompatEditText editTextPackaga=findViewById(R.id.package_et);
        AppCompatEditText editTextPublic=findViewById(R.id.public_et);
        int appId=0;
        if(getIntent().getExtras()!=null)
         appId=getIntent().getExtras().getInt("app_id",0);
        if(item.getItemId()==R.id.action_add) {
            BlockChainManager.getInstance().verifyApplication(editTextPackaga.getText().toString(), editTextName.getText().toString(), editTextPublic.getText().toString(), appId);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}