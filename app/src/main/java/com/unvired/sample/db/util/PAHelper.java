package com.unvired.sample.db.util;

import com.unvired.exception.ApplicationException;
import com.unvired.logger.Logger;
import com.unvired.sample.db.be.CONTACT_HEADER;
import com.unvired.sync.SyncConstants;
import com.unvired.sync.SyncEngine;
import com.unvired.sync.out.ISyncAppCallback;

/**
 * Created by nishchith on 21/07/17.
 */

/*
* Process Agent(PA) Helper
*/
public class PAHelper {

    public static void createContact(CONTACT_HEADER header, ISyncAppCallback callback) {

        try {
            SyncEngine.getInstance().submitInSyncMode(SyncConstants.MESSAGE_REQUEST_TYPE.RQST, header, "", Constants.PA_CREATE_CONTACT, true, callback);
        } catch (ApplicationException e) {
            Logger.e(e.getMessage());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public static void getContacts(CONTACT_HEADER header, ISyncAppCallback callback) {

        try {
            SyncEngine.getInstance().submitInSyncMode(SyncConstants.MESSAGE_REQUEST_TYPE.PULL, header, "", Constants.PA_GET_CONTACT, false, callback);
        } catch (ApplicationException e) {
            Logger.e(e.getMessage());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

    }
}
