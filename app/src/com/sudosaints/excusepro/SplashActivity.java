package com.sudosaints.excusepro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * 
 * @author Vishal
 *
 */
public class SplashActivity extends SherlockActivity {
	
	ProgressBar progressBar;
	Handler handler = new Handler();
	int progressStatus = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
		getSupportActionBar().hide();
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(progressStatus < 100) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						progressStatus = progressStatus + 1;
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								progressBar.setProgress(progressStatus);
							}
						});
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					startActivity(new Intent(SplashActivity.this, CategoryListActivity.class));
					finish();
				}
			}
		}).start();
	}


}
