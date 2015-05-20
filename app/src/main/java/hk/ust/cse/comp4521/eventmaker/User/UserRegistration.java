package hk.ust.cse.comp4521.eventmaker.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


import hk.ust.cse.comp4521.eventmaker.About;
import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Helper.ServerConnection;
import hk.ust.cse.comp4521.eventmaker.R;
import hk.ust.cse.comp4521.eventmaker.SearchFrag;

public class UserRegistration extends Activity {

    private boolean ismodify;
    private ProgressDialog pd;
    private Boolean networkIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        Spinner spinner = (Spinner) findViewById(R.id.RegInterestGroup1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.interest_array, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner = (Spinner) findViewById(R.id.RegInterestGroup2);
         // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        EditText phone_no = (EditText) findViewById(R.id.RegPhoneNo);
        phone_no.setText(UserModel.getUserModel().getPhoneNumber());

        Button confirmButton = (Button) findViewById(R.id.RegConfirmButton);
        Button resetButton = (Button) findViewById(R.id.RegResetButton);
        confirmButton.setOnClickListener(new pressButton());
        resetButton.setOnClickListener(new pressButton());

        EditText name = (EditText) findViewById(R.id.RegNameText);
        name.requestFocus();

        ismodify = false;
        Log.i(null, "onCreate of UserRegistration");

        if (getIntent().getIntExtra("Context", -1) == Constants.MODIFY_REG){
            ismodify = true;
            loadInfo();


        }


        pd = ProgressDialog.show(UserRegistration.this,"Network Access", "Connecting to the server", true);



//        myserver.updateInternalState();
//        while (myserver.connectionState == null){
//
//        }
//        pd.dismiss();
//        if (myserver.connectionState == false){
//
//        }

    }

    @Override
    protected void onStart() {


        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerConnection serverConn = new ServerConnection(UserRegistration.this, handle);
        serverConn.run();
        pd.dismiss();
    }

    public Handler handle = new Handler(){
        @Override
        public void handleMessage(Message inputMessage) {
            if (inputMessage.what == Constants.ConnectionError) {
                finish();
                pd.dismiss();
            }



        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadInfo(){
        Map<String, Object> data = UserModel.getUserModel().getAllInfo();
        ((EditText) findViewById(R.id.RegNameText)).setText((String)data.get("Name"));
        ((EditText) findViewById(R.id.RegAgeText)).setText(Integer.toString((Integer) data.get("Age")));
        RadioButton maleButton = (RadioButton) findViewById((R.id.RegMaleButton));
        RadioButton femaleButton = (RadioButton) findViewById((R.id.RegFemaleButton));
        if (((String)data.get("Gender")).equals("M")){
            maleButton.setChecked(true);
        }
        else {
            femaleButton.setChecked(true);
        }
        Spinner spinner = (Spinner) findViewById(R.id.RegInterestGroup1);
        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition((String) data.get("Interest")));
        spinner = (Spinner) findViewById(R.id.RegInterestGroup2);
        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition((String) data.get("Interest2")));
        ((EditText) findViewById(R.id.RegPhoneNo)).setText((String)data.get("Phone"));
        if (((String)data.get("NamePrivacy")).equals("Check")){
            ((CheckBox) findViewById(R.id.RegNameBox)).setChecked(true);
        }
        if (((String)data.get("AgePrivacy")).equals("Check")){
            ((CheckBox) findViewById(R.id.RegAgeBox)).setChecked(true);
        }
        if (((String)data.get("GenderPrivacy")).equals("Check")){
            ((CheckBox) findViewById(R.id.RegGenderBox)).setChecked(true);
        }
    }

    public void saveInfo(){
        Map<String,Object> data = new HashMap<>();
        data.put("Name", ((EditText) findViewById(R.id.RegNameText)).getText().toString());
        data.put("Age", Integer.parseInt(((EditText) findViewById(R.id.RegAgeText)).getText().toString()));
        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.GenderradioGroup);
        if (genderGroup.getCheckedRadioButtonId() == R.id.RegMaleButton) //M as male
            data.put("Gender", "M");
        else
            data.put("Gender", "F");
        data.put("Interest", ((Spinner) findViewById(R.id.RegInterestGroup1)).getSelectedItem().toString());
        data.put("Interest2", ((Spinner) findViewById(R.id.RegInterestGroup2)).getSelectedItem().toString());
        data.put("Phone", ((EditText) findViewById(R.id.RegPhoneNo)).getText().toString());
        data.put("NamePrivacy", (((CheckBox) findViewById(R.id.RegNameBox)).isChecked())?"Check":"Uncheck");
        data.put("AgePrivacy", (((CheckBox) findViewById(R.id.RegAgeBox)).isChecked())?"Check":"Uncheck");
        data.put("GenderPrivacy", (((CheckBox) findViewById(R.id.RegGenderBox)).isChecked())?"Check":"Uncheck");

        UserModel.getUserModel().saveAllInfo(data, ismodify);
    }

    public void clearInfo(){
        EditText nameField = (EditText) findViewById(R.id.RegNameText);
        nameField.setText("");
        EditText ageField = (EditText) findViewById(R.id.RegAgeText);
        ageField.setText("");
        RadioButton maleButton = (RadioButton) findViewById((R.id.RegMaleButton));
        RadioButton femaleButton = (RadioButton) findViewById((R.id.RegFemaleButton));
        maleButton.setChecked(false);
        femaleButton.setChecked(false);
        ((EditText) findViewById(R.id.RegPhoneNo)).setText("");
        Spinner spinner = (Spinner) findViewById(R.id.RegInterestGroup1);
        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition(""));
        spinner = (Spinner) findViewById(R.id.RegInterestGroup2);
        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition(""));
        ((CheckBox) findViewById(R.id.RegNameBox)).setChecked(false);
        ((CheckBox) findViewById(R.id.RegAgeBox)).setChecked(false);
        ((CheckBox) findViewById(R.id.RegGenderBox)).setChecked(false);
    }

    public boolean validation(){
        EditText nameField = (EditText) findViewById(R.id.RegNameText);

        if (nameField.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input your name!", Toast.LENGTH_SHORT).show();
            return false;
        };
        EditText ageField = (EditText) findViewById(R.id.RegAgeText);
        if (ageField.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input your age!", Toast.LENGTH_SHORT).show();
            return false;
        };
        RadioButton maleButton = (RadioButton) findViewById((R.id.RegMaleButton));
        RadioButton femaleButton = (RadioButton) findViewById((R.id.RegFemaleButton));
        if (!maleButton.isChecked() && !femaleButton.isChecked()){
            Toast.makeText(getApplicationContext(), "Please select your gender!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (((EditText) findViewById(R.id.RegPhoneNo)).getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input your phone number!", Toast.LENGTH_SHORT).show();
            return false;
        }
        Spinner spinner = (Spinner) findViewById(R.id.RegInterestGroup1);
        if (spinner.getSelectedItem().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please select your interest in the first box!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public class pressButton implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.RegConfirmButton){
                if (!validation()){
                    return;
                }
                pd = ProgressDialog.show(UserRegistration.this,"Network Access", "Connecting to the server", true);
                ServerConnection serverConn = new ServerConnection(UserRegistration.this, handle);
                serverConn.run(); //test network connection
                saveInfo();
                pd.dismiss();
                UserModel.getUserModel().saveSetting(true);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                if (getIntent().getIntExtra("Context", -1) ==Constants.NEW_REGISTRATION) {

                    Intent intent = new Intent(getApplicationContext(), SearchFrag.class);
                    startActivity(intent);
                    finish();
                }
                else if (getIntent().getIntExtra("Context",-1)==Constants.MODIFY_REG){
                    Intent intent = new Intent(getApplicationContext(), SearchFrag.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(intent);
                    finish();
                }
            }
            else if (view.getId() == R.id.RegResetButton){
                clearInfo();
                Toast.makeText(getApplicationContext(), "Reset", Toast.LENGTH_SHORT).show();
            }

        }
    }

//    public class ErrorDialogFragment extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the Builder class for convenient dialog construction
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage("Cannot connect to the server!")
//                    .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // FIRE ZE MISSILES!
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User cancelled the dialog
//                        }
//                    });
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }
//    }
}
