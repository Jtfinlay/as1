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

public class LogData {
	
	int id;
	String subject = "Subject";
	String date = "Date";
	String description = "This is a description.";
	
	/*
	 * Different constructors depending on object usage
	 */
	public LogData() {}
	public LogData(String subject, String date, String desc) {
		this.subject = subject;
		this.date = date;
		this.description = desc;
	}
	public LogData(int id, String subject, String date, String desc) {
		this.id = id;
		this.subject = subject;
		this.date = date;
		this.description = desc;
	}
}
