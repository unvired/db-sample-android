package com.unvired.sample.db.be;

import com.unvired.database.DBException;
import com.unvired.model.DataStructure;

/*
This class is part of the BE "CONTACT".
*/
public class CONTACT_HEADER extends DataStructure {

    public static final String TABLE_NAME = "CONTACT_HEADER";

    // Contact Id
    public static final String FIELD_ContactId = "ContactId";

    // Contact Name
    public static final String FIELD_ContactName = "ContactName";

    // Phone
    public static final String FIELD_Phone = "Phone";

    // Email
    public static final String FIELD_Email = "Email";

    public CONTACT_HEADER() throws DBException {
        super(TABLE_NAME, true);
    }

    public Long getContactId() {
        return (Long) getField(FIELD_ContactId);
    }

    public void setContactId(Long value) {
        setField(FIELD_ContactId, value);
    }

    public String getContactName() {
        return (String) getField(FIELD_ContactName);
    }

    public void setContactName(String value) {
        setField(FIELD_ContactName, value);
    }

    public String getPhone() {
        return (String) getField(FIELD_Phone);
    }

    public void setPhone(String value) {
        setField(FIELD_Phone, value);
    }

    public String getEmail() {
        return (String) getField(FIELD_Email);
    }

    public void setEmail(String value) {
        setField(FIELD_Email, value);
    }
}