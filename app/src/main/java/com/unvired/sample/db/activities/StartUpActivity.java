package com.unvired.sample.db.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.unvired.database.DBException;
import com.unvired.exception.ApplicationException;
import com.unvired.logger.Logger;
import com.unvired.login.AuthenticationService;
import com.unvired.login.LoginListener;
import com.unvired.login.LoginParameters;
import com.unvired.login.LoginParameters.LOGIN_TYPE;
import com.unvired.login.UnviredAccount;
import com.unvired.model.ApplicationVersion;
import com.unvired.sample.db.R;
import com.unvired.sample.db.util.Constants;
import com.unvired.sample.db.util.PermissionHelper;
import com.unvired.utils.FrameworkHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StartUpActivity extends AppCompatActivity implements LoginListener {
	
	private static final String URL = "https://sandbox.unvired.io/UMP";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_up);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initialize();
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PermissionHelper.GENERAL_PERMISSION) {
			boolean permitted = true;
			for (int i : grantResults) {
				if (i == PackageManager.PERMISSION_DENIED) {
					permitted = false;
				}
			}
			
			if (permitted) {
				initializeFramework(this);
			} else {
				finish();
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	
	private void initialize() {
		/*
		 * Check and obtain basic user permissions
		 */
		
		List<String> permissionList = new ArrayList<>();
		
		if (!PermissionHelper.hasPhonePermission(this)) {
			permissionList.add(Manifest.permission.READ_PHONE_STATE);
		}
		if (!PermissionHelper.hasStoragePermission(this)) {
			permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if (permissionList.size() > 0) {
			PermissionHelper.requestPermissions(this, permissionList);
		} else {
			initializeFramework(StartUpActivity.this);
		}
	}
	
	public void initializeFramework(Context context) {
		/*
		 * Initialize Framework parameters as per Documentation
		 */
		
		String metaDataXml = null;
		
		try {
			InputStream inputStream = this.getResources().openRawResource(R.raw.metadata);
			metaDataXml = FrameworkHelper.getString(inputStream);
		} catch (ApplicationException e) {
			Logger.e(e.getMessage());
		}
		
		LoginParameters.setUrl(URL);
		LoginParameters.setAppTitle(Constants.APPLICATION_TITLE);
		LoginParameters.setAppName(Constants.APPLICATION_NAME);
		LoginParameters.setMetaDataXml(metaDataXml);
		LoginParameters.setLoginListener(this);
		LoginParameters.setLoginTypes(new LoginParameters.LOGIN_TYPE[]{LOGIN_TYPE.UNVIRED_ID});
		LoginParameters.setDemoModeRequired(false);
		LoginParameters.showCompanyField(true);
		LoginParameters.setContext(context);
		
		LoginParameters.setLoginTypes(new LOGIN_TYPE[]{LOGIN_TYPE.UNVIRED_ID});
		
		ApplicationVersion.setBUILD_NUMBER("1");
		
		try {
			AuthenticationService.login(this.getApplicationContext());
		} catch (ApplicationException e) {
			Logger.e(e.getMessage());
		} catch (DBException e) {
			Logger.e(e.getMessage());
		} catch (Exception e) {
			Logger.e(e.getMessage());
		}
		
		this.finish();
	}
	
	private void navigateToHomeActivity() {
		startActivity(new Intent(this, HomeActivity.class));
		
	}
	
	@Override
	public void loginSuccessful() {
		navigateToHomeActivity();
		
	}
	
	@Override
	public void loginCancelled() {
	
	}
	
	@Override
	public void loginFailure(String s) {
	
	}
	
	@Override
	public void authenticateAndActivationSuccessful() {
		navigateToHomeActivity();
	}
	
	@Override
	public void authenticateAndActivationFailure(String s) {
	
	}
	
	@Override
	public void invokeAppLoginActivity(boolean b) {
	
	}
	
	@Override
	public void noSDCardFound(String s) {
	
	}
	
	@Override
	public void invokeMultiAccountActivity(List<UnviredAccount> list) {
	
	}
}
