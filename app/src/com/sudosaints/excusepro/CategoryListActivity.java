package com.sudosaints.excusepro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.sudosaints.excusepro.model.Category;
import com.sudosaints.excusepro.util.ApiRequestHelper;
import com.sudosaints.excusepro.util.ApiResponse;
import com.sudosaints.excusepro.util.IntentExtras;
import com.sudosaints.excusepro.util.PullToRefreshAttacher;
import com.sudosaints.excusepro.util.TypefaceLoader;

/**
 * 
 * @author Vishal
 *
 */
public class CategoryListActivity extends SherlockListActivity{

	private static final int MENU_CREDITS_ID = 1001;
	
	List<Category> categories;
	CategoriesListAdapter listAdapter;
	PullToRefreshAttacher pullToRefreshAttacher;
	PullToRefreshLayout pullToRefreshLayout;
	boolean isDestroyed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_layout);
		
		categories = new ArrayList<Category>();
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
		
		pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		pullToRefreshAttacher = PullToRefreshAttacher.get(this);
		pullToRefreshLayout.setPullToRefreshAttacher(pullToRefreshAttacher, new  PullToRefreshAttacher.OnRefreshListener() {
			
			@Override
			public void onRefreshStarted(View view) {
				// TODO Auto-generated method stub
				categories = new ArrayList<Category>();
				new DownloadCategories(CategoryListActivity.this).execute();
			}
		});
		new DownloadCategories(this).execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		SubMenu subMenu = menu.addSubMenu("Menu");
		subMenu.add(0, MENU_CREDITS_ID, 0, "Credits");
		MenuItem menuItem = subMenu.getItem();
		menuItem.setIcon(getResources().getDrawable(R.drawable.list_icon));
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MENU_CREDITS_ID:
		new AlertDialog.Builder(CategoryListActivity.this)
						.setTitle("Credits")
						.setMessage("Developed by SudoSaints.\nSpecial thanks to Sachin Gutte.")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class CategoriesListAdapter extends ArrayAdapter<Category> {

		public CategoriesListAdapter(Context context, int textViewResourceId, List<Category> objects) {
			super(context, textViewResourceId, objects);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View row = convertView;
			if(null == row) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = layoutInflater.inflate(R.layout.category_row_layout, null); 
			}
			TextView categoryNameTextView = (TextView) row.findViewById(R.id.categoryName);
			final Category category = categories.get(position);
			categoryNameTextView.setText(category.getName());
			
			row.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(CategoryListActivity.this, ExcuseListActivity.class);
					intent.putExtra(IntentExtras.CATEGORY_INDEX_EXTRA, position);
					startActivity(intent);
				}
			});
			return row;
		}		
	}
	
	private class DownloadCategories extends AsyncTask<Void, Void, ApiResponse> {
		
		Context context;
		ProgressDialog progressDialog;
		
		public DownloadCategories(Context context) {
			super();
			this.context = context;
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
			ApiResponse apiResponse = new ApiRequestHelper(context).getCategories();
			return apiResponse;
		}
		
		@Override
		protected void onPostExecute(ApiResponse result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!isDestroyed){
				progressDialog.dismiss();
				if(result.isSuccess()) {
					List<Map<String, Object>> maps= (List<Map<String, Object>>) result.getData();
					for (Map<String, Object> map : maps) {
						categories.add(new Category((String) map.get("name"), Long.valueOf(map.get("id") + "")));
					}
					DataCache.setCategories(categories);
					listAdapter = new CategoriesListAdapter(context, R.layout.category_row_layout, categories);
					setListAdapter(listAdapter);
				} else {
					Toast.makeText(context, result.getError().getMessage(), Toast.LENGTH_LONG).show();
				}
				if(pullToRefreshAttacher.isRefreshing()) {
					pullToRefreshAttacher.setRefreshComplete();
				}
			}
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isDestroyed = true;
		super.onDestroy();
	}
	
	
}
