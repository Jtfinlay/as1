/*
 * Jtfinlay_Notepad#. Note-taking application for Android.
 * Copyright (C) 2013 James Finlay
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jtfinlay.as1;

import java.util.Collections;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class CloudActivity extends Activity {
	public static final String TAG = "CloudActivity";
	
	private TextView _textCloud;
	private final static float MAX_FONT_SIZE = 5f;
	private final static float MIN_FONT_SIZE = 1f;
	
	/*
	 * Load View items and behavior. Create textcloud
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud);
		
		_textCloud = (TextView) findViewById(R.id.wordCloudText);
	}
	
	@Override
	protected void onResume() {
		
		DatabaseHandler dbH = new DatabaseHandler(this);
		LogData[] rows = dbH.getAllEntries();
		_textCloud.setText("");
		
		String words = "";
		int counter = 0;
		Map<String, Integer> commonWords = DatabaseHandler.getCommonWords(rows);
				
		float max_count = Collections.max(commonWords.values());
		float min_count = Collections.min(commonWords.values());
		
		Spannable span;
		float fontSize;
		for (String word : commonWords.keySet()) {
			//if (++counter >= 100) break;
			if (commonWords.get(word) == 1) continue;
			int start = _textCloud.getText().length();
			_textCloud.append(word + " ");
			int end = _textCloud.getText().length()-1;
			
			
			fontSize = MIN_FONT_SIZE+((float)(commonWords.get(word))-min_count)*(MAX_FONT_SIZE-MIN_FONT_SIZE)/(max_count-min_count);
			
			span = new SpannableString(_textCloud.getText());
			span.setSpan(new RelativeSizeSpan(fontSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			_textCloud.setText(span);
		}
		
		super.onResume();
	}

}
