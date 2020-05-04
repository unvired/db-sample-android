package com.unvired.sample.db.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.unvired.database.DBException;
import com.unvired.database.IDataStructure;
import com.unvired.model.InfoMessage;
import com.unvired.sample.db.R;
import com.unvired.sample.db.adapter.ContactAdapter;
import com.unvired.sample.db.be.CONTACT_HEADER;
import com.unvired.sample.db.util.Constants;
import com.unvired.sample.db.util.DBHelper;
import com.unvired.sample.db.util.PAHelper;
import com.unvired.sync.out.ISyncAppCallback;
import com.unvired.sync.response.ISyncResponse;
import com.unvired.sync.response.SyncBEResponse;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class GetContactActivity extends AppCompatActivity {

    private TextInputEditText name;
    private TextInputEditText num;

    private RecyclerView recyclerView;
    private ContactAdapter adapter;

    private String responseCode;
    private String responseText;

    private List<CONTACT_HEADER> headers = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        num = (TextInputEditText) findViewById(R.id.number);
        name = (TextInputEditText) findViewById(R.id.name);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        AppCompatButton createButton = (AppCompatButton) findViewById(R.id.searchButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    CONTACT_HEADER header = new CONTACT_HEADER();

                    if (num.getText() != null && !num.getText().toString().isEmpty()) {
                        header.setContactId(Long.parseLong(num.getText().toString()));
                    }

                    if (name.getText() != null && !name.getText().toString().isEmpty()) {
                        header.setContactName(name.getText().toString());
                    }


                    downloadContacts(header);


                } catch (DBException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (headers != null && !headers.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to save results?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (CONTACT_HEADER header : headers) {
                                DBHelper.getInstance().insertOrUpdateContact(header);
                            }
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .create().show();
        } else {
            finish();
        }

    }

    private void loadList() {
        adapter = new ContactAdapter(headers, Constants.MODE_GET);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void downloadContacts(final CONTACT_HEADER header) {
        headers = new ArrayList<>();

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
                                    responseText = getResources().getString(R.string.contactDownloadSuccess);
                                }

                                if (responseCode.equals(Constants.RESPONSE_CODE_SUCCESSFUL)) {

                                    Hashtable<String, Hashtable<IDataStructure, Vector<IDataStructure>>> dataBEs = syncBEResponse.getDataBEs();
                                    Hashtable<IDataStructure, Vector<IDataStructure>> tempCollectionOfHeaderAndItems = null;

                                    if (!dataBEs.isEmpty()) {
                                        Enumeration<String> beKeys = dataBEs.keys();

                                        if (beKeys.hasMoreElements()) {
                                            String customerBEName = beKeys.nextElement();
                                            tempCollectionOfHeaderAndItems = dataBEs.get(customerBEName);

                                            Enumeration<IDataStructure> contactHeaderKeys = tempCollectionOfHeaderAndItems.keys();

                                            while (contactHeaderKeys.hasMoreElements()) {
                                                CONTACT_HEADER contactHeader = (CONTACT_HEADER) contactHeaderKeys.nextElement();
                                                headers.add(contactHeader);
                                            }
                                        }
                                    }
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

                                if (responseText == null || responseText.trim().isEmpty()) {
                                    responseText = getResources().getString(R.string.invalidResponse);
                                }

                            } else {
                                responseText = getResources().getString(R.string.invalidResponse);
                            }
                            break;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo(responseText);
                        }
                    });
                }
            }
        };

        /*
        * Always execute Process Agent(PA) in thread
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                PAHelper.getContacts(header, callback);
            }
        }).start();

    }

    private void showInfo(String msg) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadList();
                    }
                }).create().show();
    }
}
