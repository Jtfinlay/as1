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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final String TAG = "ResourceManager";
	
	private static final String DATABASE_NAME = "velociraptor.db";
	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_NAME = "entries";
	
	private static final String KEY_ID = "id";
	private static final String KEY_SUBJECT = "subject";
	private static final String KEY_DATE = "date";
	private static final String KEY_CONTENT = "content";
	
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
			+ KEY_ID + " INTEGER PRIMARY KEY, " 
			+ KEY_SUBJECT + " TEXT,"
			+ KEY_DATE + " TEXT,"
			+ KEY_CONTENT + " TEXT)";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * Create table (assuming DNE)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	/*
	 * If DATABASE_VERSION has increased, drop table and recreate.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		
		// Create tables again
		onCreate(db);
	}
	
	/*
	 * Add an entry to the SQLite table
	 */
	public long addEntry(LogData data) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_SUBJECT, data.subject);
		values.put(KEY_DATE, data.date);
		values.put(KEY_CONTENT, data.description);
		
		long id = db.insert(TABLE_NAME, null, values);
		db.close();
		
		return id;
	}
	
	/*
	 * Get an entry from the SQLite table
	 */
	public LogData getEntry(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_NAME, 
				new String[] { KEY_ID, KEY_SUBJECT, KEY_DATE, KEY_CONTENT},
				KEY_ID + "=?",
				new String[] { String.valueOf(id) },
				null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		LogData entry = new LogData(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3));
		
		return entry;
	}
	
	/*
	 * Return all entries from the SQLite table
	 */
	public LogData[] getAllEntries() {
		List<LogData> rowList = new ArrayList<LogData>();
		
		String selectQuery = "SELECT * FROM " + TABLE_NAME
				+ " ORDER BY " + KEY_ID + " DESC";
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			do {
				rowList.add(new LogData(
						Integer.parseInt(cursor.getString(0)),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3)));
			} while (cursor.moveToNext());
		}
		
		// Sort by date
		//Collections.sort(rowList, new DateComparator());
		
		
		// Convert from List to Array
		LogData[] result = new LogData[rowList.size()];
		for (int i=0; i<rowList.size(); i++)
			result[i] = rowList.get(i);
		
		return result;
	}
	
	/*
	 * Get # of entries in table
	 */
	public int getEntryCount() {
		String countQuery = "SELECT * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(countQuery, null);
		
		return cursor.getCount();
	}
	
	/*
	 * Update the information of a given entry
	 */
	public int updateEntry(LogData data) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_SUBJECT, data.subject);
		values.put(KEY_DATE, data.date);
		values.put(KEY_CONTENT, data.description);
		
		return db.update(TABLE_NAME, values, KEY_ID + " = ?",
				new String[] { String.valueOf(data.id) });
	}
	/*
	 * Delete given entry from table
	 */
	public void deleteEntry(LogData data) {
		deleteEntry(data.id);
	}
	
	/*
	 * Delete entry with matching id from table
	 */
	public  void deleteEntry(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}
	/*
	 * Get all words from entries, along with # of occurences for each
	 */
	public static Map<String, Integer> getCommonWords(LogData[] rows) {
		
		Map<String, Integer> wordList = new HashMap<String, Integer>();
		for (LogData row : rows) {

			String[] words = row.description.split(" ");
			
			for (int i=0; i<words.length; i++) {
				
				// Formatting & regex
				words[i] = words[i].toLowerCase();
				words[i] = words[i].replaceAll("[^A-Za-z0-9()\\[\\]]", "");
				
				if (words[i].length() == 0) // Ensure valid word
					continue;
				
				if (wordList.containsKey(words[i]))
					wordList.put(words[i], wordList.get(words[i])+1);
				else
					wordList.put(words[i], 1);
			}
		}
		return wordList;
		
	}

	/*
	 * Class to compare dates
	 */
	private class DateComparator implements Comparator<LogData> {
		@Override
		public int compare(LogData o1, LogData o2) {
			return o2.date.compareTo(o1.date);
		}
	}
	/*
	 * Sorts Map<String, Integer>. Useful for finding most commonly used words.
	 */
	public static Map<String, Integer> CommonMapComparator(Map<String, Integer> unsortMap, final boolean order) {
		// http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());
		
		Collections.sort(list, new Comparator<Entry<String, Integer>>()
				{
					public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
					{
						return (order) ? o1.getValue().compareTo(o2.getValue())
								: o2.getValue().compareTo(o1.getValue());
					}
				});
		
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list)
			sortedMap.put(entry.getKey(), entry.getValue());
		
		return sortedMap;
		
	}
	

	
	

}
