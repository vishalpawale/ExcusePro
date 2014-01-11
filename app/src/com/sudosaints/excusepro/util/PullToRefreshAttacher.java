package com.sudosaints.excusepro.util;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.Activity;
import android.content.Context;

public class PullToRefreshAttacher extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher {

    public static PullToRefreshAttacher get(Activity activity) {
        return get(activity, new Options());
    }

    public static PullToRefreshAttacher get(Activity activity, Options options) {
        return new PullToRefreshAttacher(activity, options);
    }

    protected PullToRefreshAttacher(Activity activity, Options options) {
        super(activity, options);
    }

    @Override
    protected EnvironmentDelegate createDefaultEnvironmentDelegate() {
        return new AbsEnvironmentDelegate();
    }

    @Override
    protected HeaderTransformer createDefaultHeaderTransformer() {
        return new AbsDefaultHeaderTransformer();
    }

    public static class AbsEnvironmentDelegate extends EnvironmentDelegate {
        /**
         * @return Context which should be used for inflating the header layout
         */
        public Context getContextForInflater(Activity activity) {
            if (activity instanceof SherlockActivity) {
                return ((SherlockActivity) activity).getSupportActionBar().getThemedContext();
            } else if (activity instanceof SherlockListActivity) {
                return ((SherlockListActivity) activity).getSupportActionBar().getThemedContext();
            } else if (activity instanceof SherlockFragmentActivity) {
                return ((SherlockFragmentActivity) activity).getSupportActionBar()
                        .getThemedContext();
            } else if (activity instanceof SherlockExpandableListActivity) {
                return ((SherlockExpandableListActivity) activity).getSupportActionBar()
                        .getThemedContext();
            } else if (activity instanceof SherlockPreferenceActivity) {
                return ((SherlockPreferenceActivity) activity).getSupportActionBar()
                        .getThemedContext();
            }
            return super.getContextForInflater(activity);
        }
    }
}
