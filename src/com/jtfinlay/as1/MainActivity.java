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

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";


	private RowArrayAdapter _adapter;
	private ListView _listView;
	private DatabaseHandler _db;

	/*
	 * Setup View items and their behavior
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_listView = (ListView) findViewById(R.id.ListView);
		_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.v(TAG, "Click");

				// Get selected item
				ListView listView = (ListView) parent;
				LogData row = (LogData) listView.getItemAtPosition(position);

				// TODO Launch Note-taking activity
				// Launch SetValueActivity
				Intent intent = new Intent(MainActivity.this, NoteActivity.class);

				intent.putExtra("com.jtfinlay.as1.id", row.id);
				intent.putExtra("com.jtfinlay.as1.subject", row.subject);
				intent.putExtra("com.jtfinlay.as1.date", row.date);
				intent.putExtra("com.jtfinlay.as1.description", row.description);

				startActivityForResult(intent, 0);


			}
		});
	}
	
	/*
	 * Setup DatabaseHandler, and load appropriate data
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		Log.v(TAG, "onResume");

		_db = new DatabaseHandler(this);

		//for (LogData row : _db.getAllEntries()) { _db.deleteEntry(row);}

		// TODO Send existing data into adapter
		_adapter = new RowArrayAdapter(this, R.layout.row_layout, _db.getAllEntries());
		_listView.setAdapter(_adapter);


		super.onResume();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * Item selected from Actionbar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String) item.getTitle();

		if (title.equals("New")) {

			Intent intent = new Intent(MainActivity.this, NoteActivity.class);

			intent.putExtra("com.jtfinlay.as1.id", -1);
			intent.putExtra("com.jtfinlay.as1.subject", "");
			intent.putExtra("com.jtfinlay.as1.date", "");
			intent.putExtra("com.jtfinlay.as1.description", "");

			startActivityForResult(intent, 0);

			return true;
		} else if (title.equals("Cloud")) {

			Intent intent = new Intent(MainActivity.this, CloudActivity.class);
			startActivityForResult(intent, 0);

			return true;
		} else if (title.equals("Report")) {

			Intent intent = new Intent(MainActivity.this, ReportActivity.class);
			startActivityForResult(intent, 0);
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * Custom view for logs
	 */
	private class RowArrayAdapter extends ArrayAdapter<LogData> {
		private static final String TAG = "RowArrayAdapter";

		private Context context;
		private int layoutResourceID;
		private LogData[] values;

		public RowArrayAdapter(Context context, int layoutResourceID, LogData[] values) {
			super(context, layoutResourceID, values);

			this.context = context;
			this.layoutResourceID = layoutResourceID;
			this.values = values;
		}

		/*
		 * Sets up view layout from given data
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView = inflater.inflate(R.layout.row_layout,  parent, false);
			TextView subject = (TextView) rowView.findViewById(R.id.subject);
			TextView description = (TextView) rowView.findViewById(R.id.date);

			subject.setText(values[position].subject);
			description.setText(values[position].date);

			return rowView;
		}

	}

}
