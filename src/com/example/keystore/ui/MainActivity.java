/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.keystore.ui;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.keystore.KeyStoreHelper;
import com.example.keystore.R;
import com.example.keystore.common.SimpleTextFragment;
import com.example.keystore.common.logger.Log;
import com.example.keystore.common.logger.LogWrapper;
import com.example.keystore.common.logger.MessageOnlyLogFilter;

public class MainActivity extends FragmentActivity {

	public final static String TAG = "MainActivity";
	public LogFragment mLogFragment;

	public KeyStoreHelper mKeyStoreHelper;

	// You can store multiple key pairs in the Key Store. The string used to
	// refer to the Key you
	// want to store, or later pull, is referred to as an "alias" in this case,
	// because calling it
	// a key, when you use it to retrieve a key, would just be irritating.
	public static final String ALIAS = "myKey";

	// Some sample data to sign, and later verify using the generated signature.
	public static final String SAMPLE_INPUT = "Hello, Android!";

	// Just a handy place to store the signature in between signing and
	// verifying.
	public String mSignatureStr = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SimpleTextFragment actionFragment = (SimpleTextFragment) getSupportFragmentManager()
				.findFragmentById(R.id.intro_fragment);
		actionFragment.setText(R.string.intro_message);

		mKeyStoreHelper = new KeyStoreHelper();
		mKeyStoreHelper.setAlias(ALIAS);
		initializeLogging();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_create_keys:
			try {
				mKeyStoreHelper.createKeys(this);
				Log.d(TAG, "Keys created");
				return true;
			} catch (NoSuchAlgorithmException e) {
				Log.w(TAG, "RSA not supported", e);
			} catch (InvalidAlgorithmParameterException e) {
				Log.w(TAG, "No such provider: AndroidKeyStore");
			} catch (NoSuchProviderException e) {
				Log.w(TAG, "Invalid Algorithm Parameter Exception", e);
			}
			return true;
		case R.id.btn_sign_data:
			try {
				mSignatureStr = mKeyStoreHelper.signData(SAMPLE_INPUT);
			} catch (KeyStoreException e) {
				Log.w(TAG, "KeyStore not Initialized", e);
			} catch (UnrecoverableEntryException e) {
				Log.w(TAG, "KeyPair not recovered", e);
			} catch (NoSuchAlgorithmException e) {
				Log.w(TAG, "RSA not supported", e);
			} catch (InvalidKeyException e) {
				Log.w(TAG, "Invalid Key", e);
			} catch (SignatureException e) {
				Log.w(TAG, "Invalid Signature", e);
			} catch (IOException e) {
				Log.w(TAG, "IO Exception", e);
			} catch (CertificateException e) {
				Log.w(TAG, "Error occurred while loading certificates", e);
			}
			Log.d(TAG, "Signature: " + mSignatureStr);
			return true;

		case R.id.btn_verify_data:
			boolean verified = false;
			try {
				if (mSignatureStr != null) {
					verified = mKeyStoreHelper.verifyData(SAMPLE_INPUT,
							mSignatureStr);
				}
			} catch (KeyStoreException e) {
				Log.w(TAG, "KeyStore not Initialized", e);
			} catch (CertificateException e) {
				Log.w(TAG, "Error occurred while loading certificates", e);
			} catch (NoSuchAlgorithmException e) {
				Log.w(TAG, "RSA not supported", e);
			} catch (IOException e) {
				Log.w(TAG, "IO Exception", e);
			} catch (UnrecoverableEntryException e) {
				Log.w(TAG, "KeyPair not recovered", e);
			} catch (InvalidKeyException e) {
				Log.w(TAG, "Invalid Key", e);
			} catch (SignatureException e) {
				Log.w(TAG, "Invalid Signature", e);
			}
			if (verified) {
				Log.d(TAG, "Data Signature Verified");
			} else {
				Log.d(TAG, "Data not verified.");
			}
			return true;
		}
		return false;
	}

	/** Create a chain of targets that will receive log data */
	public void initializeLogging() {
		// Using Log, front-end to the logging chain, emulates android.util.log
		// method signatures.
		// Wraps Android's native log framework
		LogWrapper logWrapper = new LogWrapper();
		Log.setLogNode(logWrapper);

		// A filter that strips out everything except the message text
		MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
		logWrapper.setNext(msgFilter);

		// On screen logging via a fragment with a TextView
		mLogFragment = (LogFragment) getSupportFragmentManager()
				.findFragmentById(R.id.log_fragment);
		msgFilter.setNext(mLogFragment.getLogView());
		Log.i(TAG, "Ready");
	}
}
