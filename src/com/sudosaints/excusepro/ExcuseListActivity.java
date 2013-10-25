package com.sudosaints.excusepro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewListener;
import com.github.espiandev.showcaseview.ShowcaseView;
import com.github.espiandev.showcaseview.ShowcaseView.ConfigOptions;
import com.sudosaints.excusepro.listadapters.ExcuseListAdapter;
import com.sudosaints.excusepro.model.Category;
import com.sudosaints.excusepro.model.Excuse;
import com.sudosaints.excusepro.model.ShareObject;
import com.sudosaints.excusepro.util.ApiRequestHelper;
import com.sudosaints.excusepro.util.ApiResponse;
import com.sudosaints.excusepro.util.IntentExtras;
import com.sudosaints.excusepro.util.Logger;
import com.sudosaints.excusepro.util.PullToRefreshAttacher;
import com.sudosaints.excusepro.util.TypefaceLoader;
import com.sudosaints.excusepro.util.UIHelper;

/**
 * 
 * @author Vishal
 *
 */
public class ExcuseListActivity extends SherlockActivity{

	List<Excuse> excuses;
	ExcuseListAdapter listAdapter;
	List<Integer> resIds;
	UIHelper uiHelper;
	PullToRefreshAttacher pullToRefreshAttacher;
	PullToRefreshLayout pullToRefreshLayout;
	SwipeListView swipeListView;
	int index;
	Category category;
	List<String> permissions;
	Session.StatusCallback statusCallback = new SessionStatusCallback();
	Logger logger;
	ShowcaseView showcaseView;
	boolean isDestroyed= false;
	Preferences preferences;
	ImageView previous, next;
	TextView categoryName;
	ShareObject shareObject;
	
	public enum ShareType {
		SHARE_FACEBOOK,
		SHARE_TWITTER
	}
	
	public interface ShareCallback{
		public void execute(String shareText, ShareType shareType);
	}
	
	ShareCallback shareCallback = new ShareCallback() {
		
		@Override
		public void execute(final String shareText, final ShareType shareType) {
			// TODO Auto-generated method stub
			final EditText editText = new EditText(ExcuseListActivity.this);
			LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			editText.setLayoutParams(layoutParams);
			new AlertDialog.Builder(ExcuseListActivity.this)
							.setView(editText)
							.setTitle("Say something about the excuse")
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									String userText = "";
									if(editText.getText().length() > 0) {
										userText = editText.getText().toString();
									}
									shareObject.setMessage(userText + "\n" + shareText);
									switch (shareType) {
									case SHARE_FACEBOOK:
										shareObject.setCaption("SudoSaints - Product");
										shareObject.setName("ExcusePro");
										shareObject.setDescription(ExcuseListActivity.this.getResources().getString(R.string.post_description));
										shareObject.setLink("http://excusepro.sudosaints.com");
										publishStory();
										break;

									case SHARE_TWITTER:
										Intent intent = new Intent(ExcuseListActivity.this, TwitterOAuthActivity.class);
										intent.putExtra(IntentExtras.SHARE_OBJECT_EXTRA, shareObject);
										startActivity(intent);
										break;
									}
								}
							})
							.show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.excuse_layout);
		excuses = new ArrayList<Excuse>();
		resIds = new ArrayList<Integer>();
		uiHelper = new UIHelper(this);
		logger = new Logger(this);
		preferences = new Preferences(this);
		
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0C8AC8")));
		getSupportActionBar().setIcon(android.R.color.transparent);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		Typeface akashiTypeface = TypefaceLoader.get(this, "fonts/akashi.ttf");
		Typeface berlinTypeface = TypefaceLoader.get(this, "fonts/berlin_sans_serif.ttf");
		View view = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
		((TextView) view.findViewById(R.id.excuseText)).setTypeface(berlinTypeface);
		((TextView) view.findViewById(R.id.proText)).setTypeface(akashiTypeface);
		getSupportActionBar().setCustomView(view);
	
		swipeListView = (SwipeListView) findViewById(R.id.swipeListView);
		previous = (ImageView) findViewById(R.id.arrowLeft);
		next = (ImageView) findViewById(R.id.arrowRight);
		categoryName = (TextView) findViewById(R.id.categoryName);
		showcaseView = new ShowcaseView(this);
		
		previous.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(0 == index) {
					Toast.makeText(ExcuseListActivity.this, "This is first Excuse Category", Toast.LENGTH_LONG).show();
				} else {
					index--;
					refreshList();
				}
			}
		});
		
		next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(DataCache.getCategories().size() - 1 <= index) {
					Toast.makeText(ExcuseListActivity.this, "This is last Excuse Category", Toast.LENGTH_LONG).show();
				} else {
					index++;
					refreshList();
				}
			}
		});
		
		pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		pullToRefreshAttacher = PullToRefreshAttacher.get(this);
		pullToRefreshLayout.setPullToRefreshAttacher(pullToRefreshAttacher, new  PullToRefreshAttacher.OnRefreshListener() {
			
			@Override
			public void onRefreshStarted(View view) {
				// TODO Auto-generated method stub
				refreshList();
			}
		});
		
		shareObject = new ShareObject();
		
		/**
		* Facebook Permissions
		*/
		permissions = new ArrayList<String>();
		permissions.add("publish_actions");
		
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		swipeListView.setOffsetLeft(displayMetrics.widthPixels / 2);
		swipeListView.setOffsetRight(displayMetrics.widthPixels / 2);
		swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		
		swipeListView.setSwipeListViewListener(new SwipeListViewListener() {
			
			@Override
			public void onStartOpen(int arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				//logger.debug("Detected Open Event");
			}
			
			@Override
			public void onStartClose(int arg0, boolean arg1) {
				// TODO Auto-generated method stub
				//logger.debug("Detected Close Event");
			}
			
			@Override
			public void onOpened(int arg0, boolean arg1) {
				// TODO Auto-generated method stub
				((ExcuseListAdapter) swipeListView.getAdapter()).getViewHolder(arg0, swipeListView).getFrontView().setBackgroundColor(ExcuseListActivity.this.getResources().getColor(R.color.frontview_bg));
				((ExcuseListAdapter) swipeListView.getAdapter()).getViewHolder(arg0, swipeListView).getExcuseText().setTextColor(Color.WHITE);
			}
			
			@Override
			public void onMove(int arg0, float arg1) {
				// TODO Auto-generated method stub
				//logger.debug("Detected Move Event");
			}
			
			@Override
			public void onListChanged() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLastListItem() {
				// TODO Auto-generated method stub
				logger.debug("On Last Item");
			}
			
			@Override
			public void onFirstListItem() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDismiss(int[] arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClosed(int arg0, boolean arg1) {
				// TODO Auto-generated method stub
				((ExcuseListAdapter) swipeListView.getAdapter()).getViewHolder(arg0, swipeListView).getFrontView().setBackgroundColor(Color.parseColor("#F6F7F8"));
				((ExcuseListAdapter) swipeListView.getAdapter()).getViewHolder(arg0, swipeListView).getExcuseText().setTextColor(Color.BLACK);
			}
			
			@Override
			public void onClickFrontView(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClickBackView(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChoiceStarted() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onChoiceEnded() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onChoiceChanged(int arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int onChangeSwipeMode(int arg0) {
				// TODO Auto-generated method stub
				return SwipeListView.SWIPE_MODE_DEFAULT;
			}
		});
		
		if(getIntent().hasExtra(IntentExtras.CATEGORY_INDEX_EXTRA)) {
			index = getIntent().getIntExtra(IntentExtras.CATEGORY_INDEX_EXTRA, 0);
			category = DataCache.getCategories().get(index);
			refreshList();
			/**
			* Facebook Session Initialization
			*/
			Session session = Session.getActiveSession();
			if (session == null) {
				if (savedInstanceState != null) {
					session = Session.restoreSession(this, null, statusCallback,
							savedInstanceState);
				}
				if (session == null) {
					session = new Session(this);
				}
				session.addCallback(statusCallback);
				Session.setActiveSession(session);
			}
			logger.debug("Session State - " + session.getState());
			/** End **/
		} else {
			finish();
		}
		
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			// Check if Session is Opened or not, if open & clicked on share
			// button publish the story
			if (session != null && state.isOpened()) {
				logger.debug("Session is opened");
				if (session.getPermissions().contains("publish_actions")) {
					logger.debug("Starting share");
					publishAction(session);
				} else {
					logger.debug("Session dont have permissions");
					publishStory();
				}
			} else {
				logger.debug("Invalid fb Session");
			}
		}
	}
	
	private void refreshList() {
		excuses = new ArrayList<Excuse>();
		resIds = new ArrayList<Integer>();
		category = DataCache.getCategories().get(index);
		new DownloadExcuses(ExcuseListActivity.this, category.getId()).execute();
		categoryName.setText(category.getName());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	private class DownloadExcuses extends AsyncTask<Void, Void, ApiResponse> {
		
		Context context;
		Long categoryId;
		ProgressDialog progressDialog;
		
		public DownloadExcuses(Context context, Long categoryId) {
			super();
			this.context = context;
			this.categoryId = categoryId;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}
		
		@Override
		protected ApiResponse doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ApiResponse apiResponse = new ApiRequestHelper(context).getExcuses(categoryId);
			return apiResponse;
		}
		
		@Override
		protected void onPostExecute(ApiResponse result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!isDestroyed) {
				progressDialog.dismiss();
				if(result.isSuccess()) {
					
					List<Map<String, Object>> maps = (List<Map<String, Object>>) result.getData();
					int i = 0;
					for (Map<String, Object> map : maps) {
						Excuse excuse = new Excuse();
						excuse.setExcuse((String) map.get("excuse"));
						excuse.setId(Long.valueOf(map.get("id") + ""));
						excuse.setDate(new Date(Long.valueOf(map.get("date") + "")));
						excuses.add(excuse);
						resIds.add(getResId(i % 5));
						i++;
					}
					listAdapter = new ExcuseListAdapter(context, excuses, resIds, shareCallback);
					swipeListView.setAdapter(listAdapter);
					if(!preferences.getIsDemoDone() && maps.size() > 0) {
						showDemo();
					}
				} else {
					listAdapter = new ExcuseListAdapter(context, excuses, resIds, shareCallback);
					swipeListView.setAdapter(listAdapter);
					Toast.makeText(context, result.getError().getMessage(), Toast.LENGTH_LONG).show();
				}
				if(pullToRefreshAttacher.isRefreshing()) {
					pullToRefreshAttacher.setRefreshComplete();
				}
			}
		}

		private void showDemo() {
			final Activity activity = ExcuseListActivity.this;
			Handler handler = new Handler();
			WindowManager manager = activity.getWindowManager();
			DisplayMetrics displayMetrics = new DisplayMetrics();
			manager.getDefaultDisplay().getMetrics(displayMetrics);
			handler.postDelayed(new Runnable() {
				
				@SuppressLint("Recycle")
				@Override
				public void run() {
					// TODO Auto-generated method stub
					showcaseView = new ShowcaseView(activity);
					View view = ((ExcuseListAdapter)swipeListView.getAdapter()).getViewHolder(0, swipeListView).getFrontView();
					int[] location = new int[2];
					view.getLocationInWindow(location);
					logger.debug("Coords - " + location[0] + "--" + location[1]);
					int viewHeight = view.getHeight()/2;
					MotionEvent motionEvent1 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100, MotionEvent.ACTION_DOWN, location[0] + view.getWidth(), location[1] + viewHeight, 1, 1, 0, 1, 1, 0, 0);
					MotionEvent motionEvent2 = MotionEvent.obtain(SystemClock.uptimeMillis() + 100, SystemClock.uptimeMillis() + 150, MotionEvent.ACTION_MOVE, location[0] + view.getWidth(), location[1] + viewHeight, 1, 1, 0, 1, 1, 0, 0);
					MotionEvent motionEvent3 = MotionEvent.obtain(SystemClock.uptimeMillis() + 500, SystemClock.uptimeMillis() + 1000, MotionEvent.ACTION_UP, location[0]+ 100, location[1] + viewHeight, 1, 1, 0, 1, 1, 0, 0);
					swipeListView.dispatchTouchEvent(motionEvent1);
					logger.debug("Dispatched first event");
					swipeListView.dispatchTouchEvent(motionEvent2);
					logger.debug("Dispatched second event");
					swipeListView.dispatchTouchEvent(motionEvent3);
					logger.debug("Dispatched third event");
					ConfigOptions configOptions = new ConfigOptions();
					configOptions.hideOnClickOutside = true;
					configOptions.block = true;
					showcaseView = ShowcaseView.insertShowcaseView(view, activity);
					showcaseView.animateGesture(location[0] + view.getWidth(), location[1] + viewHeight, 0, location[1] + viewHeight);
					preferences.setIsDemoDone(true);
				}
			}, 500);
		}
	}
	
	private int getResId(int index) {
		switch (index) {
		case 0:
			return R.drawable.toon1;
			
		case 1:
			return R.drawable.toon2;
			
		case 2:
			return R.drawable.toon3;
			
		case 3:
			return R.drawable.toon4;
			
		case 4:
			return R.drawable.toon5;
		}
		return R.drawable.toon1;
	}
	
	/**
	 * Facebook Methods
	 */
	private void publishStory() {
		Session session = Session.getActiveSession();
		if (session != null && session.getState().isOpened()) {
			checkSessionAndPost();
		} else {
			logger.debug("Session is null");
			session = new Session(ExcuseListActivity.this);
			Session.setActiveSession(session);
			session.addCallback(statusCallback);
			logger.debug("Session info - " + session);
			try {
				logger.debug("Opening session for read");
				session.openForRead(new Session.OpenRequest(ExcuseListActivity.this));
			} catch (UnsupportedOperationException exception) {
				exception.printStackTrace();
				logger.debug("Exception Caught");
				Toast.makeText(ExcuseListActivity.this, "Unable to post excuse on facebook", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private void checkSessionAndPost() {
		Session session = Session.getActiveSession();
		session.addCallback(statusCallback);
		logger.debug("Session permissions are - " + session.getPermissions());
		if (session.getPermissions().contains("publish_actions")) {
			publishAction(session);
		} else {
			session.requestNewPublishPermissions(new Session.NewPermissionsRequest(ExcuseListActivity.this, permissions));
		}
	}
	
	private void publishAction(Session session) {
		logger.debug("Inside publishAction()");
		final ProgressDialog dialog = new ProgressDialog(ExcuseListActivity.this);
		dialog.setMessage("Please wait...Posting the status");
		dialog.show();
		Bundle postParams = new Bundle();
		postParams.putString("name", shareObject.getName());
		postParams.putString("caption", shareObject.getCaption());
		postParams.putString("message", shareObject.getMessage());
		postParams.putString("description", shareObject.getDescription());
		postParams.putString("link", shareObject.getLink());
		postParams.putString("picture", "http://sudosaints.com/excuse.jpg");

		Request.Callback callback = new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				dialog.dismiss();
				FacebookRequestError error = response.getError();
				if (error != null) {
					logger.debug("Facebook error - " + error.getErrorMessage());
					logger.debug("Error code - " + error.getErrorCode());
					logger.debug("JSON Response - " + error.getRequestResult());
					logger.debug("Error Category - " + error.getCategory());
					Toast.makeText(ExcuseListActivity.this, "Failed to share the excuse.Please try again", Toast.LENGTH_SHORT).show();
					//fbButton.setEnabled(true);
				} else {
					Toast.makeText(ExcuseListActivity.this, "Successfully shared the excuse", Toast.LENGTH_SHORT).show();
					//fbButton.setEnabled(false);
				}
			}
		};
		Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
		RequestAsyncTask asyncTask = new RequestAsyncTask(request);
		asyncTask.execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		logger.debug("Resut code is - " + resultCode + "");
		Session.getActiveSession().addCallback(statusCallback);
		Session.getActiveSession().onActivityResult(ExcuseListActivity.this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Save current session
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onStart() {
		// TODO Add status callback
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	protected void onStop() {
		// TODO Remove callback
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isDestroyed = true;
		super.onDestroy();
	}
}
