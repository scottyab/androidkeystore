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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.keystore.R;
import com.example.keystore.common.logger.LogView;

/**
 * Simple fraggment which contains a LogView and uses is to output log data it
 * receives through the LogNode interface.
 */
public class LogFragment extends Fragment {

	private LogView mLogView;

	public LogFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View result = inflater.inflate(R.layout.log_fragment, container, false);

		mLogView = (LogView) result.findViewById(R.id.sample_output);

		// Wire up so when the text changes, the view scrolls down.
		final ScrollView scrollView = ((ScrollView) result
				.findViewById(R.id.log_scroll));

		mLogView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}

			@Override
			public void afterTextChanged(Editable s) {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

		return result;
	}

	public LogView getLogView() {
		return mLogView;
	}
}