package com.zendesk.tecna.sample.ui.forms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zendesk.belvedere.Belvedere;
import com.zendesk.belvedere.BelvedereCallback;
import com.zendesk.belvedere.BelvedereResult;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;
import com.zendesk.tecna.sample.Global;
import com.zendesk.tecna.sample.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import zendesk.support.CreateRequest;
import zendesk.support.CustomField;
import zendesk.support.Request;
import zendesk.support.RequestProvider;
import zendesk.support.Support;
import zendesk.support.TicketField;
import zendesk.support.TicketFieldOption;
import zendesk.support.TicketForm;
import zendesk.support.UploadProvider;
import zendesk.support.UploadResponse;

public class FormCreditCardActivity extends AppCompatActivity {

    private static final String TAG = "FormCreditCardActivity";

    private static final long CREDIT_CARD_FORM_ID = 360000796452L;
    private static final long CREDIT_CARD_CUSTOM_FIELD_ASSUNTO = 360030183731L;
    private static final long CREDIT_CARD_CUSTOM_FIELD_DESCRICAO = 360030183751L;
    private static final long CREDIT_CARD_CUSTOM_FIELD_MOTIVO = 360030246932L;
    private static final long CREDIT_CARD_CUSTOM_FIELD_DATA_LANCAMENTO = 360030330931L;
    private static final long CREDIT_CARD_CUSTOM_FIELD_VALOR_LANCAMENTO = 360030330951L;

    private static final String DEFAULT_MIMETYPE = "application/octet-stream";

    private TicketForm ticketFormData;

    private TextView textDescription;

    private TextInputLayout editAssuntoLayout;
    private TextInputEditText editAssunto;

    private TextInputLayout editDescricaoLayout;
    private EditText editDescricao;

    private TextInputLayout dropMotivoLayout;
    private AutoCompleteTextView dropMotivo;

    private TextView textDataLancamento;
    private Button btnDataLancamento;

    private TextInputLayout editValorLancamentoLayout;
    private EditText editValorLancamento;

    private ProgressDialog progressDialog;
    private Belvedere belvedere;
    private RequestProvider requestProvider;
    private UploadProvider uploadProvider;

    private DatePickerDialog datePicker;
    private FloatingActionButton fab;

    private String motivo;
    private String dataLancamento;
    private int uploadRequestsInProgress;
    private List<String> attachmentsUploaded = new ArrayList<>();
    private boolean createRequestInProgress;

    HashMap<String ,String> motivoMap = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Global.isMissingCredentials()) {
            Toast.makeText(this, R.string.missing_credentials, Toast.LENGTH_LONG);
            return;
        }

        setContentView(R.layout.activity_form_credit_card);

        captureViews();
        addTextWatchersToForm();
        initializeZendeskProviders();
        initializeBelvedereFilePicker();
        configureForm();
    }

    private void captureViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textDescription = findViewById(R.id.textDescription);

        editAssuntoLayout = findViewById(R.id.editAssuntoLayout);
        editAssunto = findViewById(R.id.editAssunto);

        editDescricaoLayout = findViewById(R.id.editDescricaoLayout);
        editDescricao = findViewById(R.id.editDescricao);

        dropMotivoLayout = findViewById(R.id.dropMotivoLayout);
        dropMotivo = findViewById(R.id.dropMotivo);

        textDataLancamento = findViewById(R.id.textDataLancamento);

        btnDataLancamento = findViewById(R.id.btnDataLancamento);

        editValorLancamentoLayout = findViewById(R.id.editValorLancamentoLayout);
        editValorLancamento = findViewById(R.id.editValorLancamento);

        fab = findViewById(R.id.fab);
    }

    private void addTextWatchersToForm() {
        TextWatcher notEmptyTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editAssunto.addTextChangedListener(notEmptyTextWatcher);
        editDescricao.addTextChangedListener(notEmptyTextWatcher);
    }

    private void initializeBelvedereFilePicker() {
        belvedere = Belvedere.from(this)
                .withContentType("image/*")
                .withAllowMultiple(true)
                .build();
    }

    private void initializeZendeskProviders() {
        uploadProvider = Support.INSTANCE.provider().uploadProvider();
        requestProvider = Support.INSTANCE.provider().requestProvider();
    }

    private void configureForm() {
        progressDialog(getString(R.string.loading_form)).show();

        textDescription.setText(R.string.send_ticket);

        requestProvider.getTicketFormsById(new ArrayList(Arrays.asList(CREDIT_CARD_FORM_ID)), new ZendeskCallback<List<TicketForm>>() {
            @Override
            public void onSuccess(List<TicketForm> ticketForms) {
                ticketFormData = ticketForms.get(0);

                setTitle(ticketFormData.getName());

                for (TicketField field : ticketFormData.getTicketFields()) {

                    if (field.getId() == CREDIT_CARD_CUSTOM_FIELD_ASSUNTO) {
                        editAssuntoLayout.setHint(field.getTitle());
                    } else if (field.getId() == CREDIT_CARD_CUSTOM_FIELD_DESCRICAO) {
                        editDescricaoLayout.setHint(field.getTitle());
                    } else if (field.getId() == CREDIT_CARD_CUSTOM_FIELD_MOTIVO) {
                        List<String> motivoOptions = new ArrayList();

                        motivoMap.put("Selecione", null);
                        motivoOptions.add("Selecione");

                        for (TicketFieldOption option : field.getTicketFieldOptions()) {
                            motivoMap.put(option.getName(), option.getValue());
                            motivoOptions.add(option.getName());
                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(FormCreditCardActivity.this, android.R.layout.simple_spinner_item, motivoOptions);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        dropMotivoLayout.setHint(field.getTitle());
                        dropMotivo.setAdapter(spinnerAdapter);
                        dropMotivo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                motivo = adapterView.getItemAtPosition(i).toString();
                                invalidateOptionsMenu();
                            }
                        });
                    } else if (field.getId() == CREDIT_CARD_CUSTOM_FIELD_DATA_LANCAMENTO) {
                        textDataLancamento.setText(field.getTitle());

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        btnDataLancamento.setText(dateFormat.format(new Date()));

                        btnDataLancamento.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Calendar cldr = Calendar.getInstance();
                                int day = cldr.get(Calendar.DAY_OF_MONTH);
                                int month = cldr.get(Calendar.MONTH);
                                int year = cldr.get(Calendar.YEAR);

                                datePicker = new DatePickerDialog(FormCreditCardActivity.this,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                int month = monthOfYear +1;
                                                String strMonth = String.valueOf(month);
                                                if (month < 10) {
                                                    strMonth = "0" + String.valueOf(month);
                                                }

                                                int day = dayOfMonth;
                                                String strDay = String.valueOf(day);
                                                if (day < 10) {
                                                    strDay = "0" + String.valueOf(day);
                                                }

                                                dataLancamento = year + "-" + strMonth + "-" + strDay;

                                                btnDataLancamento.setText(dataLancamento);
                                            }
                                        }, year, month, day);
                                datePicker.show();
                            }
                        });
                    } else if (field.getId() == CREDIT_CARD_CUSTOM_FIELD_VALOR_LANCAMENTO) {
                        editValorLancamentoLayout.setHint(field.getTitle());
                    }
                }

                final FragmentManager fragmentManager = getSupportFragmentManager();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        belvedere.showDialog(fragmentManager);
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.loading_form_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Boolean isRequestFormValid() {

        return !editAssunto.getText().toString().isEmpty() &&
                !editDescricao.getText().toString().isEmpty() &&
                motivo != null &&
                !motivo.isEmpty();
    }

    private CreateRequest buildCreateRequest() {
        CreateRequest request = new CreateRequest();

        request.setTicketFormId(CREDIT_CARD_FORM_ID);
        request.setSubject(editAssunto.getText().toString());
        request.setDescription(editDescricao.getText().toString());
        request.setAttachments(attachmentsUploaded);

        request.setCustomFields(buildCustomFieldsList());

        return request;
    }

    private List<CustomField> buildCustomFieldsList() {
        List<CustomField> list = new ArrayList<>();

        list.add(new CustomField(CREDIT_CARD_CUSTOM_FIELD_ASSUNTO, editAssunto.getText().toString()));
        list.add(new CustomField(CREDIT_CARD_CUSTOM_FIELD_DESCRICAO, editDescricao.getText().toString()));

        if (motivo != null) {
            list.add(new CustomField(CREDIT_CARD_CUSTOM_FIELD_MOTIVO, motivo));
        }

        if (dataLancamento != null && dataLancamento.length() > 0) {
            list.add(new CustomField(CREDIT_CARD_CUSTOM_FIELD_DATA_LANCAMENTO, dataLancamento));
        }

        list.add(new CustomField(CREDIT_CARD_CUSTOM_FIELD_VALOR_LANCAMENTO, editValorLancamento.getText().toString()));

        return list;
    }

    private ZendeskCallback<Request> buildCallback() {
        return new ZendeskCallback<Request>() {
            @Override
            public void onSuccess(Request createRequest) {
                invalidateOptionsMenu();

                progressDialog().dismiss();
                Toast.makeText(getApplicationContext(), R.string.ticket_success, Toast.LENGTH_SHORT).show();

                clearForm();
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                progressDialog().dismiss();
                Toast.makeText(getApplicationContext(), "Error! " + errorResponse.getReason(), Toast.LENGTH_SHORT).show();
                createRequestInProgress = false;
            }
        };
    }

    void clearForm() {
        editAssunto.setText("");
        editDescricao.setText("");
        //spinMotivo.setSelection(0);
        btnDataLancamento.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        editValorLancamento.setText("");

        // Clear list of uploaded attachments
        attachmentsUploaded.clear();

        createRequestInProgress = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        belvedere.getFilesFromActivityOnResult(requestCode, resultCode, data, new BelvedereCallback<List<BelvedereResult>>() {
            @Override
            public void success(List<BelvedereResult> belvedereResults) {

                if (belvedereResults != null && belvedereResults.size() > 0) {
                    progressDialog("Uploading your attachments...").show();
                } else {
                    return;
                }

                for (int i = 0, limit = belvedereResults.size(); i < limit; i++) {

                    BelvedereResult file = belvedereResults.get(i);

                    uploadProvider.uploadAttachment(
                            file.getFile().getName(),
                            file.getFile(),
                            getMimeType(getApplicationContext(), file.getUri()),
                            new ZendeskCallback<UploadResponse>() {
                                @Override
                                public void onSuccess(UploadResponse uploadResponse) {
                                    if (uploadResponse != null && uploadResponse.getAttachment() != null) {
                                        attachmentsUploaded.add(uploadResponse.getToken());
                                        Log.d(TAG, String.format("onSuccess: Image successfully uploaded: %s",
                                                uploadResponse.getAttachment().getContentUrl()));
                                    }
                                    // Make sure to keep track of how many requests are in progress
                                    uploadRequestsInProgress--;
                                    checkUploadRequestsInProgress();
                                }

                                @Override
                                public void onError(ErrorResponse errorResponse) {
                                    // Make sure to keep track of how many requests are in progress
                                    uploadRequestsInProgress--;
                                    checkUploadRequestsInProgress();
                                }
                            });

                    uploadRequestsInProgress++;
                }
            }
        });
    }

    private static String getMimeType(Context context, Uri file) {
        final ContentResolver cr = context.getContentResolver();
        return (file != null) ? cr.getType(file) : DEFAULT_MIMETYPE;
    }

    private void checkUploadRequestsInProgress() {
        if (noUploadRequestsInProgress() && progressDialog().isShowing()) {
            progressDialog().dismiss();
        }
    }

    private boolean noUploadRequestsInProgress() {
        return uploadRequestsInProgress == 0;
    }

    private ProgressDialog progressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

        return progressDialog;
    }

    private ProgressDialog progressDialog(String dialogText) {
        progressDialog().setMessage(dialogText);
        return progressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_attachment, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_send).setEnabled(isRequestFormValid());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send) {

            progressDialog("Creating your request...").show();

            createRequestInProgress = true;

            CreateRequest request = buildCreateRequest();

            ZendeskCallback<Request> callback = buildCallback();
            requestProvider.createRequest(request, callback);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!createRequestInProgress) {
            for (String attachment : attachmentsUploaded) {
                uploadProvider.deleteAttachment(attachment, null);
            }
        }
    }
}
