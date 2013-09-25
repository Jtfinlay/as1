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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class NoteActivity extends FragmentActivity {
	private static final String TAG = "NoteActivity";

	private int _id;
	private EditText _vSubject, _vContent, _vDate;

	/*
	 * Setup View and behavior. Load intent data from parent
	 */
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_layout);

		// Get packaged data
		Intent intent = getIntent();
		_id = intent.getIntExtra("com.jtfinlay.as1.id", -1);
		String subject = intent.getStringExtra("com.jtfinlay.as1.subject");
		String date = intent.getStringExtra("com.jtfinlay.as1.date");
		String description = intent.getStringExtra("com.jtfinlay.as1.description");

		if (date.length() == 0) {
			Calendar c = Calendar.getInstance();
			String sMonth = ((c.get(c.MONTH)+1)<10) ? ("0"+(c.get(c.MONTH)+1)) : (""+(c.get(c.MONTH)+1));
			String sDay = (c.get(c.DAY_OF_MONTH)<10) ? ("0"+c.get(c.DAY_OF_MONTH)) : (""+c.get(c.DAY_OF_MONTH));
			date = c.get(c.YEAR) + "-" + sMonth + "-" + sDay; 
		}

		// View elements
		_vSubject = (EditText) findViewById(R.id.Notes_Subject);
		_vDate = (EditText) findViewById(R.id.Notes_Date);
		_vContent = (EditText) findViewById(R.id.Notes_Content);

		// Existing log
		_vSubject.setText(subject);
		_vDate.setText(date);
		_vContent.setText(description);
	}

	/*
	 * Opens dialog to change set date
	 */
	public void showDateClickerDialog(View v) {
		String sDate = _vDate.getText().toString();
		String[] values = sDate.split("-");
		int year = Integer.parseInt(values[0]);
		int month = Integer.parseInt(values[1]);
		int day = Integer.parseInt(values[2]);

		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				String sMonth = (monthOfYear<10) ? ("0"+(monthOfYear+1)) : (""+(monthOfYear+1));
				String sDay = (dayOfMonth<10) ? ("0"+dayOfMonth) : (""+dayOfMonth);
				String date = year + "-" + sMonth + "-" + sDay;
				_vDate.setText(date);
			}
		}, year, month-1, day).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.notes, menu);
		return true;
	}

	/*
	 * Item selected from Actionbar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String) item.getTitle();

		// If hitting 'Back', save.
		if (item.getItemId() == android.R.id.home) {

			// Ensure subject & date are set
			if (_vSubject.getText().toString().length() == 0) {
				new AlertDialog.Builder(this)
				.setTitle("Subject must be set")
				
				.setCancelable(false)
				.setPositiveButton("Okey-Dokey", new OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.create().show();
				return true;
			} else {

				DatabaseHandler db = new DatabaseHandler(this);
				
				// if row dne, add to db
				if (_id == -1) _id = (int) db.addEntry(new LogData("", "", ""));
				
				db.updateEntry(new LogData(_id, _vSubject.getText().toString(), _vDate.getText().toString(), _vContent.getText().toString()));
				db.close();
			}
		} else if (title.equals("Discard")) {

			DatabaseHandler db = new DatabaseHandler(this);
			db.deleteEntry(_id);
			finish();
			return true;
		}



		return super.onOptionsItemSelected(item);
	}



}
