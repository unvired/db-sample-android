package com.unvired.sample.db.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.unvired.sample.db.R;
import com.unvired.sample.db.adapter.ContactAdapter;
import com.unvired.sample.db.be.CONTACT_HEADER;
import com.unvired.sample.db.util.Constants;
import com.unvired.sample.db.util.DBHelper;
import com.unvired.ui.Home;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private boolean searching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addNewButton = (FloatingActionButton) findViewById(R.id.addNewButton);
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCreate();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && searchView.hasFocus()) {
            searchView.clearFocus();
            return;
        }
        if (searchView != null && searching) {
            searchView.onActionViewCollapsed();
            searching = false;
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.home_activity_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(android.content.Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searching = false;
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.getContacts:
                startActivity(new Intent(this, GetContactActivity.class));
                return true;

            case R.id.settings:
                startActivity(new Intent(this, Home.class));
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void navigateToCreate() {
        startActivity(new Intent(this, CreateContactActivity.class));
    }

    private void loadList() {

        if (searching && searchView.getQuery() != null) {
            recyclerView.setAdapter(new ContactAdapter(getSearchList(searchView.getQuery().toString()), Constants.MODE_HOME));
        } else {
            ContactAdapter adapter = new ContactAdapter(DBHelper.getInstance().getContacts(), Constants.MODE_HOME);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searching = true;
        recyclerView.setAdapter(new ContactAdapter(getSearchList(newText), Constants.MODE_HOME));

        return true;
    }

    private List<CONTACT_HEADER> getSearchList(String searchText) {
        List<CONTACT_HEADER> tempList = DBHelper.getInstance().getContacts();
        List<CONTACT_HEADER> searchList = new ArrayList<>();

        for (CONTACT_HEADER header : tempList) {
            if (header.getContactName().toLowerCase().contains(searchText.toLowerCase())) {
                searchList.add(header);
            }
        }

        return searchList;
    }
}
