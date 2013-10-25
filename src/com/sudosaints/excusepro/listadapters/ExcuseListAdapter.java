package com.sudosaints.excusepro.listadapters;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.sudosaints.excusepro.ExcuseListActivity.ShareCallback;
import com.sudosaints.excusepro.ExcuseListActivity.ShareType;
import com.sudosaints.excusepro.R;
import com.sudosaints.excusepro.model.Excuse;

public class ExcuseListAdapter extends BaseAdapter {

    private List<Excuse> data;
    private Context context;
    private List<Integer> resIds;
    private ShareCallback shareCallback;
    private List<ViewHolder> holders;

    public ExcuseListAdapter(Context context, List<Excuse> data, List<Integer> resIds, ShareCallback shareCallback) {
        this.context = context;
        this.data = data;
        this.resIds = resIds;
        this.shareCallback = shareCallback;
        holders = new ArrayList<ExcuseListAdapter.ViewHolder>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Excuse getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Excuse item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.excuse_row_layout_new, parent, false);
            holder = new ViewHolder();
            holder.toonImage = (ImageView) convertView.findViewById(R.id.toonImage);
            holder.shareButton = (ImageView) convertView.findViewById(R.id.shareButton);
            holder.excuseText = (TextView) convertView.findViewById(R.id.excuseText);
            holder.fbShareButton = (ImageView) convertView.findViewById(R.id.fbShareButton);
            holder.twitterShareButton = (ImageView) convertView.findViewById(R.id.twitterShareButton);
            holder.backView = convertView.findViewById(R.id.swipelist_backview);
            holder.frontView = convertView.findViewById(R.id.swipelist_frontview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ((SwipeListView)parent).recycle(convertView, position);

        holder.toonImage.setImageDrawable(context.getResources().getDrawable(resIds.get(position)));
        holder.excuseText.setText(item.getExcuse());

        holder.id = item.getId();
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	Intent sendIntent = new Intent();
            	sendIntent.setAction(Intent.ACTION_SEND);
            	sendIntent.putExtra(Intent.EXTRA_TEXT, item.getExcuse());
            	sendIntent.setType("text/plain");
            	context.startActivity(sendIntent);
            }
        });

        holder.excuseText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(context)
								.setTitle("Excuse Details")
								.setMessage(item.getExcuse())
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.show();
			}
		});
        
        holder.fbShareButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shareCallback.execute(item.getExcuse(), ShareType.SHARE_FACEBOOK);
			}
		});
        
        holder.twitterShareButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shareCallback.execute(item.getExcuse(), ShareType.SHARE_TWITTER);
			}
		});
        if(!holders.contains(holder)) {
        	holders.add(holder);
        }
        return convertView;
    }
    
    public ViewHolder getViewHolder(int position, SwipeListView swipeListView) {
    	Log.d("ListAdapter", "Position - " + position);
    	Log.d("ListAdapter", "HolderSize - " + holders.size());
    	// Hack for getting correct viewholder according to position
    	if(position > holders.size()) {
    		 position = getPosition(position);
    	}
    	if(position == holders.size()) {
    		position = 0;
    	}
		return holders.get(position);
    }

    private int getPosition(int position) {
    	position = position - holders.size();
    	if(position > holders.size()) {
    		position = getPosition(position);
    	}
    	return position;
    }
    
    public static class ViewHolder {
        ImageView toonImage;
        ImageView shareButton;
        ImageView fbShareButton;
        ImageView twitterShareButton;
        TextView excuseText;
        long id;
        View frontView, backView;
        
        @Override
        public boolean equals(Object o) {
        	if(! (o instanceof ViewHolder)) {
        		return false;
        	}
        	ViewHolder holder = (ViewHolder) o;
        	if(this.id == holder.id) {
        		return true;
        	}
        	return false;
        }
        
        @Override
        public int hashCode() {
        	return Integer.valueOf(this.id + "");
        }

		public ImageView getToonImage() {
			return toonImage;
		}

		public void setToonImage(ImageView toonImage) {
			this.toonImage = toonImage;
		}

		public ImageView getShareButton() {
			return shareButton;
		}

		public void setShareButton(ImageView shareButton) {
			this.shareButton = shareButton;
		}

		public ImageView getFbShareButton() {
			return fbShareButton;
		}

		public void setFbShareButton(ImageView fbShareButton) {
			this.fbShareButton = fbShareButton;
		}

		public ImageView getTwitterShareButton() {
			return twitterShareButton;
		}

		public void setTwitterShareButton(ImageView twitterShareButton) {
			this.twitterShareButton = twitterShareButton;
		}

		public TextView getExcuseText() {
			return excuseText;
		}

		public void setExcuseText(TextView excuseText) {
			this.excuseText = excuseText;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public View getFrontView() {
			return frontView;
		}

		public void setFrontView(View frontView) {
			this.frontView = frontView;
		}

		public View getBackView() {
			return backView;
		}

		public void setBackView(View backView) {
			this.backView = backView;
		}
    }

}
