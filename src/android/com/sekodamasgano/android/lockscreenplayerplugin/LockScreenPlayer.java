package com.sekodamasgano.android.lockscreenplayerplugin;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class LockScreenPlayer extends CordovaPlugin {

    public static final String ACTION_UPDATE_INFOS = "updateInfos";
    public static final String ACTION_REMOVE_PLAYER = "removePlayer";

    private static final int PLAYBACK_NOTIFICATION_ID = 1;
    private static final int PLAYBACK_SERVICE_REQUEST_CODE = 10;
    private static final int UPDATE_PLAYBACK_SERVICE_REQUEST_CODE = 11;
    private static final String EXTRA_FROM_NOTIFICATION = "from_notification";
    public static final String EXTRA_TRACK_TO_PLAY_INDEX = "track_to_play_index";

    public static final String UPDATE_PLAYBACK_SERVICE_ACTION_PAUSE = "ActionPause";
    public static final String UPDATE_PLAYBACK_SERVICE_ACTION_PLAY = "ActionPlay";
    public static final String UPDATE_PLAYBACK_SERVICE_ACTION_PREV = "ActionPrev";
    public static final String UPDATE_PLAYBACK_SERVICE_ACTION_NEXT = "ActionNext";

    private NotificationManager notificationManager;
    private Intent notificationIntent;
    private PendingIntent pausePlaybackPendingIntent;
    private PendingIntent startPlaybackPendingIntent;
    private PendingIntent prevPlaybackPendingIntent;
    private PendingIntent nextPlaybackPendingIntent;


    private static final String TAG = "LockScreenPlayerPlugin";

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "PluginInitialize");

        Activity cordovaActivity = this.cordova.getActivity();

        this.notificationManager = (NotificationManager) cordovaActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create base intent to launch activity from notification
        notificationIntent = new Intent(cordovaActivity, cordovaActivity.getClass());
        notificationIntent.putExtra(EXTRA_FROM_NOTIFICATION, true);

        // Create PendingIntents for Notification actions -- will be assigned to actions by buildNotification()
        Intent updatePlaybackIntent = new Intent();
        // Pause playback
        updatePlaybackIntent.setAction(UPDATE_PLAYBACK_SERVICE_ACTION_PAUSE);
        this.pausePlaybackPendingIntent = PendingIntent.getBroadcast(cordovaActivity,
                UPDATE_PLAYBACK_SERVICE_REQUEST_CODE, updatePlaybackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Start playback
        updatePlaybackIntent.setAction(UPDATE_PLAYBACK_SERVICE_ACTION_PLAY);
        this.startPlaybackPendingIntent = PendingIntent.getBroadcast(cordovaActivity,
                UPDATE_PLAYBACK_SERVICE_REQUEST_CODE, updatePlaybackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Previous track
        updatePlaybackIntent.setAction(UPDATE_PLAYBACK_SERVICE_ACTION_PREV);
        this.prevPlaybackPendingIntent = PendingIntent.getBroadcast(cordovaActivity,
                UPDATE_PLAYBACK_SERVICE_REQUEST_CODE, updatePlaybackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Next track
        updatePlaybackIntent.setAction(UPDATE_PLAYBACK_SERVICE_ACTION_NEXT);
        this.nextPlaybackPendingIntent = PendingIntent.getBroadcast(cordovaActivity,
                UPDATE_PLAYBACK_SERVICE_REQUEST_CODE, updatePlaybackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Register receiver for Notification actions
        cordovaActivity.registerReceiver(this.notificationActionReceiver,
                new IntentFilter(UPDATE_PLAYBACK_SERVICE_ACTION_PAUSE));
        cordovaActivity.registerReceiver(this.notificationActionReceiver,
                new IntentFilter(UPDATE_PLAYBACK_SERVICE_ACTION_PLAY));
        cordovaActivity.registerReceiver(this.notificationActionReceiver,
                new IntentFilter(UPDATE_PLAYBACK_SERVICE_ACTION_PREV));
        cordovaActivity.registerReceiver(this.notificationActionReceiver,
                new IntentFilter(UPDATE_PLAYBACK_SERVICE_ACTION_NEXT));
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.d(TAG, "CordovaPlugin: execute " + action);

        try {

            if (ACTION_UPDATE_INFOS.equals(action)) {

                Log.d(TAG, "CordovaPlugin: load " + action);

                JSONObject arg_object = args.getJSONObject(0);

                this.setInfos(
                        arg_object.getString("title"),
                        arg_object.getString("artistName"),
                        arg_object.getString("albumName"),
                        arg_object.getString("cover"),
                        arg_object.getBoolean("isPlaying")
                );
            } else if (ACTION_REMOVE_PLAYER.equals(action)) {

                Log.d(TAG, "CordovaPlugin: load " + action);

                this.notificationManager.cancel(PLAYBACK_NOTIFICATION_ID);

            } else{
                Log.d(TAG, "CordovaPlugin: unknown action");
            }

            return true;

        } catch (Exception e) {

            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());

            return false;
        }
    }

    private void setInfos(String trackName, String artistName, String albumName, String cover, Boolean isPlaying)
    {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        //NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(trackName)
                .addLine(artistName)
                .addLine(albumName);

        Bitmap imageCover = null;
        if (cover != null && cover.length() > 0) {
            byte[] imageAsBytes = Base64.decode(cover, Base64.DEFAULT);
            imageCover = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }

        this.notificationManager.notify(PLAYBACK_NOTIFICATION_ID, buildNotification(
                trackName,
                trackName + " - " + artistName,
                imageCover,
                isPlaying,
                inboxStyle));
    }


    private Notification buildNotification(String title, String content,
                                           Bitmap image, Boolean isPlaying,
                                           NotificationCompat.InboxStyle style) {
        // Build intent with track list and current playing track
        //this.notificationIntent.putExtra(EXTRA_TRACK_TO_PLAY_INDEX, this.currentTrackIndex);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this.cordova.getActivity(), PLAYBACK_SERVICE_REQUEST_CODE,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (image == null) {
            image = BitmapFactory.decodeResource(this.cordova.getActivity().getResources(), getResourceId("no_image"));
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.cordova.getActivity());

        notificationBuilder.setSmallIcon(getResourceId("ic_notification_icon"))
                .setTicker(content)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(notificationPendingIntent)
                .setLargeIcon(image)
                .setStyle(style)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX);

        //if (playbackMode == PlayerActivity.Mode.TRACK)
        // Set action according to current state and playing track index
        notificationBuilder.addAction(android.R.drawable.ic_media_previous, "Prev", this.prevPlaybackPendingIntent);
        // Set play/pause action according to state
        if (isPlaying) {
            notificationBuilder.addAction(android.R.drawable.ic_media_pause, "Pause", this.pausePlaybackPendingIntent);
        } else {
            notificationBuilder.addAction(android.R.drawable.ic_media_play, "Play", this.startPlaybackPendingIntent);
        }
        // Set action according to current state and playing track index
        notificationBuilder.addAction(android.R.drawable.ic_media_next, "Next", this.nextPlaybackPendingIntent);

        return notificationBuilder.build();
    }

    private BroadcastReceiver notificationActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Request received for action '" + action + "'");

            if (action.equals(LockScreenPlayer.UPDATE_PLAYBACK_SERVICE_ACTION_PAUSE)) {
                //send event notification pause;
            } else if (action.equals(LockScreenPlayer.UPDATE_PLAYBACK_SERVICE_ACTION_PLAY)) {
                //send event notification play;
            } else if (action.equals(LockScreenPlayer.UPDATE_PLAYBACK_SERVICE_ACTION_PREV)) {
                //send event notification prev;
            } else if (action.equals(LockScreenPlayer.UPDATE_PLAYBACK_SERVICE_ACTION_NEXT)) {
                //send event notification next;
            } else {
                Log.e(TAG, "Unrecognized action: '" + action + "'");
            }
        }
    };

    private Drawable getDrawable(String resourceName)
    {
        Resources activityRes = this.cordova.getActivity().getResources();
        int drawableResId = activityRes.getIdentifier(resourceName, "drawable", this.cordova.getActivity().getPackageName());

        return activityRes.getDrawable(drawableResId);
    }

    private int getResourceId(String resourceName)
    {
        Resources activityRes = this.cordova.getActivity().getResources();
        return activityRes.getIdentifier(resourceName, "drawable", this.cordova.getActivity().getPackageName());
    }

}