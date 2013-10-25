package com.sudosaints.excusepro.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.sudosaints.excusepro.Preferences;

public class UIHelper {

	Activity activity;
	Preferences preferences;
	
	public UIHelper(Activity activity) {
		super();
		this.activity = activity;
		this.preferences = new Preferences(activity);
	}
	
	public static interface Command {
		public void run();
	}
	
	public void showAlertDialog(String title, String message, final Command command) {
		
		new AlertDialog.Builder(activity)
						.setMessage(message)
						.setTitle(title)
						.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(null != command) {
									command.run();
								}
							}
						})
						.show();
	}
	
}
