package com.unvired.sample.db.util;

import com.unvired.core.ApplicationManager;
import com.unvired.database.DBException;
import com.unvired.database.IDataManager;
import com.unvired.database.IDataStructure;
import com.unvired.logger.Logger;
import com.unvired.sample.db.be.CONTACT_HEADER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nishchith on 21/07/17.
 */

public class DBHelper {

    private IDataManager iDataManager = null;
    private static DBHelper dbHelper = null;

    DBHelper() {
        try {
            iDataManager = ApplicationManager.getInstance().getDataManager();
        } catch (DBException e) {
            Logger.e(e.getMessage());
        }
    }

    public static DBHelper getInstance() {
        if (dbHelper == null) {
            dbHelper = new DBHelper();
        }

        return dbHelper;
    }

    public void insertOrUpdateContact(CONTACT_HEADER header) {
        try {
            iDataManager.insertOrUpdateBasedOnGID(header);
        } catch (DBException e) {
            Logger.e(e.getMessage());
        }
    }

    public List<CONTACT_HEADER> getContacts() {

        List<CONTACT_HEADER> contactHeaders = new ArrayList<>();

        try {
            IDataStructure[] structures = iDataManager.get(CONTACT_HEADER.TABLE_NAME);

            if (structures != null && structures.length > 0) {
                for (IDataStructure structure : structures) {
                    contactHeaders.add((CONTACT_HEADER) structure);
                }
            }
        } catch (DBException e) {
            Logger.e(e.getMessage());
            return null;
        }

        Collections.sort(contactHeaders, new Comparator<CONTACT_HEADER>() {
            @Override
            public int compare(CONTACT_HEADER top, CONTACT_HEADER next) {
                return top.getContactName().compareToIgnoreCase(next.getContactName());
            }
        });

        return contactHeaders;
    }
}
