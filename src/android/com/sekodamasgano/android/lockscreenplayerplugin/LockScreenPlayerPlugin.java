package com.sekodamasgano.android.lockscreenplayerplugin;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.graphics.Bitmap;
import android.util.Base64;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class LockScreenPlayerPlugin extends CordovaPlugin {

    public static final String ACTION_UPDATE_INFOS = "updateInfos";
    private static final int PLAYBACK_NOTIFICATION_ID = 1;
    private static final int PLAYBACK_SERVICE_REQUEST_CODE = 10;
    private static final String EXTRA_FROM_NOTIFICATION = "from_notification";
    public static final String EXTRA_TRACK_TO_PLAY_INDEX = "track_to_play_index";

    private NotificationManager notificationManager;
    private Intent notificationIntent;

    @Override
    protected void pluginInitialize() {
        this.notificationManager = (NotificationManager) this.cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create base intent to launch activity from notification
        notificationIntent = new Intent(this, this.cordova.getActivity().class);
        notificationIntent.putExtra(EXTRA_FROM_NOTIFICATION, true);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {

            if (ACTION_UPDATE_INFOS.equals(action)) {

                JSONObject arg_object = args.getJSONObject(0);

                this.setInfos(
                    arg_object.getString("title"),
                    arg_object.getString("artistName"),
                    arg_object.getString("albumName"),
                    arg_object.getString("cover"),
                    arg_object.getBoolean("isPlaying")
                );
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
            image = BitmapFactory.decodeResource(this.cordova.getActivity().getResources(), R.drawable.no_image);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.cordova.getActivity());
        notificationBuilder.setSmallIcon(R.drawable.ic_notification_icon)
                .setTicker(content)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(notificationPendingIntent)
                .setLargeIcon(image)
                .setStyle(style);

        //if (playbackMode == PlayerActivity.Mode.TRACK)
            // Set action according to current state and playing track index
        notificationBuilder.addAction(android.R.drawable.ic_media_previous, "Prev", null);
        // Set play/pause action according to state
        if (isPlaying) {
            notificationBuilder.addAction(android.R.drawable.ic_media_pause, "Pause", null);
        } else {
            notificationBuilder.addAction(android.R.drawable.ic_media_play, "Play", null);
        }
            // Set action according to current state and playing track index
        notificationBuilder.addAction(android.R.drawable.ic_media_next, "Next", null);

        return notificationBuilder.build();
    }
}