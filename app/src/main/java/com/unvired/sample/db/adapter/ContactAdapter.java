package com.unvired.sample.db.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unvired.sample.db.R;
import com.unvired.sample.db.be.CONTACT_HEADER;
import com.unvired.sample.db.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<CONTACT_HEADER> contact_headers;
    private List<String> sectionHeaders;
    private List<Integer> headersPosition;
    private int MODE;

    public ContactAdapter(List<CONTACT_HEADER> contact_headers, int mode) {
        this.contact_headers = contact_headers;
        MODE = mode;
        setHeaders();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_activity, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final CONTACT_HEADER contactHeader = contact_headers.get(position);

        if (contactHeader != null) {
            if (MODE == Constants.MODE_GET) {
                viewHolder.name.setText(contactHeader.getContactName());
                viewHolder.ph.setText(String.valueOf(contactHeader.getContactId()));
                viewHolder.email.setVisibility(View.GONE);
            } else {
                viewHolder.name.setText(contactHeader.getContactName());
                viewHolder.ph.setText(contactHeader.getPhone());
                viewHolder.email.setText(contactHeader.getEmail());
            }

            if (viewHolder.sectionHeader != null) {
                if (position == 0) {
                    viewHolder.sectionHeader.setText(sectionHeaders.get(0));
                    viewHolder.sectionHeader.setVisibility(View.VISIBLE);
                } else if (headersPosition.contains(position)) {
                    viewHolder.sectionHeader.setText(sectionHeaders.get(position));
                    viewHolder.sectionHeader.setVisibility(View.VISIBLE);
                } else
                    viewHolder.sectionHeader.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        if (contact_headers != null && contact_headers.size() > 0) {
            return contact_headers.size();
        } else
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView sectionHeader;
        public TextView name;
        public TextView ph;
        public TextView email;

        ViewHolder(View v) {
            super(v);
            sectionHeader = (TextView) v.findViewById(R.id.sectionHeader);
            name = (TextView) v.findViewById(R.id.name);
            ph = (TextView) v.findViewById(R.id.ph);
            email = (TextView) v.findViewById(R.id.email);
        }
    }

    private void setHeaders() {
        sectionHeaders = new ArrayList<>();
        headersPosition = new ArrayList<>();
        sectionHeaders.clear();
        headersPosition.clear();

        if (contact_headers != null && contact_headers.size() > 0) {
            for (int i = 0; i < contact_headers.size(); i++) {
                String current = "";

                if (!contact_headers.get(i).getContactName().isEmpty()) {
                    current = String.valueOf(contact_headers.get(i).getContactName().charAt(0));
                }

                if (i == 0) {
                    headersPosition.add(i);
                } else if (!current.equalsIgnoreCase(sectionHeaders.get(sectionHeaders.size() - 1))) {
                    headersPosition.add(i);
                }
                sectionHeaders.add(current.toUpperCase());
            }
        }
    }
}