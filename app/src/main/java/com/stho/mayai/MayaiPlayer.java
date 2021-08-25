package com.stho.mayai;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;

public class MayaiPlayer {
    private final Context context;
    private Ringtone ringtone;

    public static MayaiPlayer build(final @NonNull Context context) {
        return new MayaiPlayer(context);
    }

    private MayaiPlayer(final @NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    void ring() {
        try {
            if (ringtone != null) {
                ringtone.stop();
            }
            final Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(context, notification);
            ringtone.play();
        } catch (Exception ex) {
            Logger.log("Error in MayaiPlayer: " + ex.toString());
            // ignore
        }
    }

    void silence() {
        if (ringtone != null) {
            ringtone.stop();
        }
    }
}
