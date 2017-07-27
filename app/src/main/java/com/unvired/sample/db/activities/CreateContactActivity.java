package com.unvired.sample.db.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.unvired.database.DBException;
import com.unvired.model.InfoMessage;
import com.unvired.sample.db.R;
import com.unvired.sample.db.be.CONTACT_HEADER;
import com.unvired.sample.db.util.Constants;
import com.unvired.sample.db.util.DBHelper;
import com.unvired.sample.db.util.PAHelper;
import com.unvired.sync.out.ISyncAppCallback;
import com.unvired.sync.response.ISyncResponse;
import com.unvired.sync.response.SyncBEResponse;

import java.util.Vector;

public class CreateContactActivity extends AppCompatActivity {

    private TextInputEditText name;
    private TextInputEditText phone;
    private TextInputEditText email;

    private String responseCode;
    private String responseText;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = (TextInputEditText) findViewById(R.id.name);
        phone = (TextInputEditText) findViewById(R.id.phone);
        email = (TextInputEditText) findViewById(R.id.email);

        AppCompatButton createButton = (AppCompatButton) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    createContact();
                }
            }
        });
    }

    private void createContact() {
        try {
            CONTACT_HEADER header = new CONTACT_HEADER();
            if (name.getText() != null && !name.getText().toString().isEmpty()) {
                header.setContactName(name.getText().toString());
            }

            if (phone.getText() != null && !phone.getText().toString().isEmpty()) {
                header.setPhone(phone.getText().toString());
            }

            if (email.getText() != null && !email.getText().toString().isEmpty()) {
                header.setEmail(email.getText().toString());
            }

            DBHelper.getInstance().insertOrUpdateContact(header);

            callCreate(header);

        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    private void callCreate(final CONTACT_HEADER header) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait..");
        }

        progressDialog.show();

        final ISyncAppCallback callback = new ISyncAppCallback() {
            @Override
            public void onResponse(ISyncResponse iSyncResponse) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                SyncBEResponse syncBEResponse;
                responseText = null;

                if (iSyncResponse == null) {
                    responseCode = Constants.RESPONSE_CODE_ERROR;
                    responseText = getResources().getString(R.string.invalidResponse);
                } else {

                    switch (iSyncResponse.getResponseStatus()) {
                        case SUCCESS:

                            if (iSyncResponse instanceof SyncBEResponse) {

                                syncBEResponse = (SyncBEResponse) iSyncResponse;

                                responseCode = Constants.RESPONSE_CODE_SUCCESSFUL;
                                Vector<InfoMessage> infoMessages = syncBEResponse.getInfoMessages();

                                if (infoMessages != null && infoMessages.size() > 0) {
                                    StringBuilder infoMsgs = new StringBuilder();

                                    for (int i = 0; i < infoMessages.size(); i++) {
                                        responseCode = infoMessages.get(i).getCategory().equals(InfoMessage.CATEGORY_SUCCESS) ? Constants.RESPONSE_CODE_SUCCESSFUL : Constants.RESPONSE_CODE_ERROR;

                                        if (infoMessages.get(i).getMessage() != null && !infoMessages.get(i).getMessage().equals("")) {
                                            infoMsgs.append(infoMessages.get(i).getMessage() + "\n");
                                        }
                                    }

                                    responseText = infoMsgs.toString();
                                }

                                if (responseText == null || responseText.trim().isEmpty()) {
                                    responseText = getResources().getString(R.string.contactCreateSuccess);
                                }
                            }
                            break;

                        case FAILURE:
                            responseCode = Constants.RESPONSE_CODE_ERROR;
                            if (iSyncResponse instanceof SyncBEResponse) {
                                syncBEResponse = (SyncBEResponse) iSyncResponse;
                                responseText = syncBEResponse.getErrorMessage();

                                if (syncBEResponse.getErrorMessage().contains(getResources().getString(R.string.invalidResponse))) {
                                    responseText = getResources().getString(R.string.invalidResponse);
                                } else {
                                    responseText = syncBEResponse.getErrorMessage();
                                }

                                Vector<InfoMessage> infoMessages = syncBEResponse.getInfoMessages();

                                if (infoMessages != null && infoMessages.size() > 0) {
                                    StringBuilder infoMsgs = new StringBuilder();

                                    for (int i = 0; i < infoMessages.size(); i++) {
                                        if (infoMessages.get(i).getMessage() != null && !infoMessages.get(i).getMessage().equals("")) {
                                            infoMsgs.append(infoMessages.get(i).getMessage() + "\n");
                                        }
                                    }

                                    responseText = infoMsgs.toString();
                                }

                                if (responseText == null || responseText.trim().equals("")) {
                                    responseText = getResources().getString(R.string.invalidResponse);
                                }

                            } else {
                                responseText = getResources().getString(R.string.invalidResponse);
                            }
                            break;
                    }

                    if (responseCode != null && responseCode.equalsIgnoreCase(Constants.RESPONSE_CODE_ERROR)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showInfo(true, responseText);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showInfo(false, responseText);
                            }
                        });
                    }
                }
            }
        };

        /*
        * Always execute Process Agent(PA) in thread
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                PAHelper.createContact(header, callback);
            }
        }).start();

    }

    private void showInfo(final boolean error, String msg) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!error) {
                            finish();
                        }

                    }
                }).create().show();
    }

    private boolean isValid() {
        if (name.getText() == null || name.getText().toString().isEmpty()) {
            ((TextInputEditText) name.getParentForAccessibility()).setError("Name cannot be empty.");
            return false;
        }

        if (phone.getText() == null || phone.getText().toString().isEmpty()) {
            ((TextInputEditText) phone.getParentForAccessibility()).setError("Phone number cannot be empty.");
            return false;
        }

        if (email.getText() == null || email.getText().toString().isEmpty()) {
            ((TextInputEditText) email.getParentForAccessibility()).setError("Email cannot be empty.");
            return false;
        }

        return true;
    }

}
