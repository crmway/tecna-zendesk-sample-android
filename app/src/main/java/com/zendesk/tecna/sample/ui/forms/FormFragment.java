package com.zendesk.tecna.sample.ui.forms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.zendesk.tecna.sample.MainActivity;
import com.zendesk.tecna.sample.R;
import com.zendesk.tecna.sample.ui.settings.SettingsActivity;

public class FormFragment extends Fragment implements View.OnClickListener {

    private TextView textDescription;
    private Button btnCreditCard;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form, container, false);
        captureViews(root);
        configureViews();
        return root;
    }

    private void captureViews(View root) {
        textDescription = root.findViewById(R.id.textDescription);
        btnCreditCard = root.findViewById(R.id.btnCreditCard);
    }

    private void configureViews() {
        textDescription.setText("Credit Card form");
        btnCreditCard.setOnClickListener(this);
    }

    private void openCreditCardFormActivity() {
        Intent settingsIntent = new Intent(getActivity(), FormCreditCardActivity.class);
        getActivity().startActivity(settingsIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreditCard:
                openCreditCardFormActivity();
                break;
            default:
        }
    }
}