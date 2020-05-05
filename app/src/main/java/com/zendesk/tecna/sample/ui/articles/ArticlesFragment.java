package com.zendesk.tecna.sample.ui.articles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.zendesk.tecna.sample.R;

import zendesk.support.guide.HelpCenterActivity;

public class ArticlesFragment extends Fragment implements View.OnClickListener {

    private TextView textDescription;
    private Switch switchFabTicket;
    private Button btnOpen;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_articles, container, false);
        captureViews(root);
        configureViews();
        return root;
    }

    private void captureViews(View root) {
        textDescription = root.findViewById(R.id.textDescription);
        switchFabTicket = root.findViewById(R.id.switchFabTicket);
        btnOpen = root.findViewById(R.id.btnOpen);
    }

    private void configureViews() {
        textDescription.setText("Zendesk Help Center");
        switchFabTicket.setText("Show the button on the article list view that allows the user to create a ticket.");
        btnOpen.setOnClickListener(this);
    }

    private void openHelpCenterActivity() {
        HelpCenterActivity.builder()
                .withContactUsButtonVisible(switchFabTicket.isChecked())
                .show(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOpen:
                openHelpCenterActivity();
                break;
            default:
        }
    }
}