package com.zendesk.tecna.sample.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zendesk.tecna.sample.Global;
import com.zendesk.tecna.sample.MainActivity;
import com.zendesk.tecna.sample.R;
import com.zendesk.util.StringUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textSettingsDescription;
    private TextInputEditText editSubdomainUrl;
    private EditText editApplicationId;
    private EditText editOAuthClientId;
    private TextView textIdentityDescription;
    private EditText editIdentityName;
    private EditText editIdentityEmail;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_settings);
        setTitle(R.string.action_settings);
        captureViews();
        load();
    }

    private void captureViews() {
        textSettingsDescription = findViewById(R.id.textSettingsDescription);
        editSubdomainUrl = findViewById(R.id.editSubdomainUrl);
        editApplicationId = findViewById(R.id.editApplicationId);
        editOAuthClientId = findViewById(R.id.editOAuthClientId);
        textIdentityDescription = findViewById(R.id.textIdentityDescription);
        editIdentityName = findViewById(R.id.editIdentityName);;
        editIdentityEmail = findViewById(R.id.editIdentityEmail);;
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
    }

    private void load() {
        textSettingsDescription.setText("Initialize the SDK with your Zendesk subdomain, mobile SDK app ID, and client ID. This is required.\n\nGet these details from your Zendesk dashboard: Admin -> Channels -> MobileSDK.");
        editSubdomainUrl.setText(Global.SUBDOMAIN_URL);
        editApplicationId.setText(Global.APPLICATION_ID);
        editOAuthClientId.setText(Global.OAUTH_CLIENT_ID);
        textIdentityDescription.setText("Set the anonymous identity name and email. It is optional, you can leave it blank.");
        editIdentityName.setText(Global.ANONYMOUS_IDENTITY_NAME);
        editIdentityEmail.setText(Global.ANONYMOUS_IDENTITY_EMAIL);
    }

    private void save() {
        if (isValid()) {
            Global.setZendeskEnviroment(
                    SettingsActivity.this,
                    editSubdomainUrl.getText().toString(),
                    editApplicationId.getText().toString(),
                    editOAuthClientId.getText().toString());
            Global.setZendeskAnonymousIdentity(
                    editIdentityName.getText().toString(),
                    editIdentityEmail.getText().toString());
            MainActivity.instance.setIdentityLabels(Global.ANONYMOUS_IDENTITY_NAME, Global.ANONYMOUS_IDENTITY_EMAIL);
            Toast.makeText(SettingsActivity.this, R.string.saved_settings_success, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(SettingsActivity.this, R.string.saved_settings_error_fill_required_fields, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        return !StringUtils.isEmpty(editSubdomainUrl.getText().toString()) &&
               !StringUtils.isEmpty(editApplicationId.getText().toString()) &&
               !StringUtils.isEmpty(editOAuthClientId.getText().toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                save();
                break;
            default:
        }
    }
}
