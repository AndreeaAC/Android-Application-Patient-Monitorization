package com.example.patientmonitorization;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {

    public SendNotification(String message, String heading, String notificationKey){

        try {
            OneSignal.postNotification(new JSONObject("{'contents':{'en':'" + message + "'},"+
                     "'include_player_ids':['" + notificationKey + "'],"+
                     "'headings':{'en': '" + heading + "'}}"), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
