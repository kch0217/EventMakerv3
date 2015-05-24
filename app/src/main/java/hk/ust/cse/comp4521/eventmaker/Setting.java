package hk.ust.cse.comp4521.eventmaker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import hk.ust.cse.comp4521.eventmaker.Helper.ServerConnection;
import hk.ust.cse.comp4521.eventmaker.User.UserModel;
import hk.ust.cse.comp4521.eventmaker.User.UserRegistration;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;


public class Setting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        EditText DetectionRange = (EditText) findViewById(R.id.DetectionRange);

        DetectionRange.setText(Constants.DEFAULT_RANGE_DETECTION+"");
        DetectionRange.setEnabled(false); //range detection is not allowed to modify in the current version

        Button confirm = (Button) findViewById(R.id.SettingConfirmButton);
        confirm.setOnClickListener(new pressButton());

        Button clearButton = (Button) findViewById(R.id.SettingClearButton);
        clearButton.setOnClickListener(new pressButton());

        Button  modifyButton = (Button) findViewById(R.id.SettingInfoButton);
        modifyButton.setOnClickListener(new pressButton());


//        UserServer myServer  = new UserServer();
        UserServer.updateInternalState(); //update all user data from the server

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_about) { //show the about page if the item on the menu is selected
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class pressButton implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (view.getId()== R.id.SettingConfirmButton){

                finish(); //terminate the current setting activity so that the user could go back to the search activity

            }
            if (view.getId() ==R.id.SettingClearButton){ //remove the user data on the device
                ServerConnection serverConnection = new ServerConnection(Setting.this, null);
                serverConnection.run();

                

                Log.i(null, "Press Clear");
                UserModel.getUserModel().wipeAlldata();
                Toast.makeText(getApplicationContext(), "All Data are removed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP ); //clear the back stack
                startActivity(intent);
                finish();

            }

            if (view.getId()== R.id.SettingInfoButton){ //go to the registration class for user to modify his/her data
                Log.i(null, "Press Modify");
                Intent intent = new Intent(getApplicationContext(), UserRegistration.class);
                intent.putExtra("Context", Constants.MODIFY_REG); //pass the message to the registration activity so that it knows it is currently modifying
                startActivity(intent);
            }
        }
    }
}
