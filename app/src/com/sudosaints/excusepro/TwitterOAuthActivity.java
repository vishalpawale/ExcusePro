package com.sudosaints.excusepro;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.neovisionaries.android.twitter.TwitterOAuthView;
import com.neovisionaries.android.twitter.TwitterOAuthView.Result;
import com.sudosaints.excusepro.model.ShareObject;
import com.sudosaints.excusepro.util.IntentExtras;
import com.sudosaints.excusepro.util.Logger;
import com.sudosaints.excusepro.util.ResultStatus;

/**
 * 
 * @author Vishal
 *
 */
public class TwitterOAuthActivity extends Activity implements TwitterOAuthView.Listener
{
    // Replace values of the parameters below with your own.
    private static final String CONSUMER_KEY = "CONSUMER_KEY";
    private static final String CONSUMER_SECRET = "CONSUMER_SECRET";
    private static final String CALLBACK_URL = "http://www.sudosaints.com";
    private static final boolean DUMMY_CALLBACK_URL = true;


    private TwitterOAuthView view;
    private boolean oauthStarted;
    Logger logger;
    Twitter twitter;
    ShareObject shareObject;
    boolean isDestroyed = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create an instance of TwitterOAuthView.
        view = new TwitterOAuthView(this);
        logger = new Logger(this);
        if(getIntent().hasExtra(IntentExtras.SHARE_OBJECT_EXTRA)) {
        	shareObject = (ShareObject) getIntent().getSerializableExtra(IntentExtras.SHARE_OBJECT_EXTRA);
        	setContentView(view);
        	oauthStarted = false;
        } else {
        	finish();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (oauthStarted)
        {
            return;
        }

        oauthStarted = true;

        // Start Twitter OAuth process. Its result will be notified via
        // TwitterOAuthView.Listener interface.
        view.start(CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL, DUMMY_CALLBACK_URL, this);
    }


    public void onSuccess(TwitterOAuthView view, AccessToken accessToken)
    {
        // The application has been authorized and an access token
        // has been obtained successfully. Save the access token
        // for later use.
        showMessage("Authorized by " + accessToken.getScreenName());
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
        configurationBuilder.setDebugEnabled(true);
        twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
        twitter.setOAuthAccessToken(accessToken);
        new ShareTweet().execute();
    }


    public void onFailure(TwitterOAuthView view, Result result)
    {
        // Failed to get an access token.
        showMessage("Failed due to " + result);
    }


    private void showMessage(String message)
    {
        // Show a popup message.
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private class ShareTweet extends AsyncTask<Void, Void, ResultStatus> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected ResultStatus doInBackground(Void... params) {
			try {
				StatusUpdate statusUpdate = new StatusUpdate(shareObject.getMessage());
				twitter4j.Status status = twitter.updateStatus(statusUpdate);
				logger.debug(status.toString());
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ResultStatus result) {
			super.onPostExecute(result);
			if(!isDestroyed) {
				finish();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isDestroyed = true;
	}
}