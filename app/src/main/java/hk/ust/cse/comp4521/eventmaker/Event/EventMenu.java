package hk.ust.cse.comp4521.eventmaker.Event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import hk.ust.cse.comp4521.eventmaker.R;

/**
 * Created by LiLokHim on 14/5/15.
 */
public class EventMenu extends Activity {
    private String TAG="eventMenu";
    private Event event=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        Intent intent = getIntent();
        String easyPuzzle = intent.getExtras().getString("epuzzle");
    }





}
