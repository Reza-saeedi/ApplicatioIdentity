package com.github.blockchain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuItem;

import com.github.blockchain.utils.BlockChainManager;

import io.github.rajdeep1008.apkwizard.R;

public class AddApplicationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_application);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Add Application");
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
        AppCompatEditText editTextPrivate=findViewById(R.id.private_et);
        AppCompatEditText editTextN=findViewById(R.id.N_et);
        if(item.getItemId()==R.id.action_add) {
            BlockChainManager.getInstance().addApplications(editTextPackaga.getText().toString(), editTextName.getText().toString(), editTextPublic.getText().toString(), editTextN.getText().toString(), editTextPrivate.getText().toString());
        finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
