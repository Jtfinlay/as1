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

import java.util.Calendar;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class ReportActivity extends Activity {
	private static final String TAG = "ReportActivity";

	private TextView _CharSum, _WordSum, _LogSum, _CommonWords;

	/*
	 * Setup Views and behavior. Calculate report information.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_layout);

		// Views
		_CharSum = (TextView) findViewById(R.id.Report_CharSum);
		_WordSum = (TextView) findViewById(R.id.Report_WordSum);
		_LogSum = (TextView) findViewById(R.id.Report_LogSum);
		_CommonWords = (TextView) findViewById(R.id.Report_CommonWords);

		DatabaseHandler dbH = new DatabaseHandler(this);
		LogData[] rows = dbH.getAllEntries();

		_CharSum.setText("Total characters: " + countCharacters(rows));
		_WordSum.setText("Total words: " + countWords(rows));
		_LogSum.setText("Total logs: " + dbH.getEntryCount());

		String words = "";
		int counter = 0;
		// get common words & sort
		Map<String, Integer> commonWords = DatabaseHandler.CommonMapComparator(DatabaseHandler.getCommonWords(rows), false);
		for (String word : commonWords.keySet()) {
			if (++counter >= 100) break;
			words += word + ", ";
		}
		_CommonWords.setText("100 common words:\n" + words);
	}

	/*
	 * Count # of characters from LogData array
	 */
	private long countCharacters(LogData[] rows) {
		long sum = 0;
		for (LogData row : rows) {
			sum += row.description.length();
		}
		return sum;
	}
	/*
	 * Count words from LogData array
	 */
	private long countWords(LogData[] rows) {
		long sum = 0;
		for (LogData row : rows) {
			String[] words = row.description.split(" ");
			for (int i=0; i<words.length; i++) {
				
				// Formatting & regex
				words[i] = words[i].toLowerCase();
				words[i] = words[i].replaceAll("[^A-Za-z0-9()\\[\\]]", "");
				
				if (words[i].length() == 0)
					continue;

				sum++;
			}
		}
		return sum;
	}


}
