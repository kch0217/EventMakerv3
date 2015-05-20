package hk.ust.cse.comp4521.eventmaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import hk.ust.cse.comp4521.eventmaker.Event.Event;
import hk.ust.cse.comp4521.eventmaker.Event.EventMenu;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.Event.Map;
import hk.ust.cse.comp4521.eventmaker.Event.Matching;
import hk.ust.cse.comp4521.eventmaker.PassiveSearch.SearchHelper;
import hk.ust.cse.comp4521.eventmaker.PassiveSearch.ServerConnection;
import hk.ust.cse.comp4521.eventmaker.User.UserInfo;
import hk.ust.cse.comp4521.eventmaker.User.UserModel;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;


public class SearchFrag extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;



    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Intent mainloc;
    private Object lock;
    private ProgressDialog pd2;
    private static Boolean networkIO;
    public static Handler handle = new Handler(){
        @Override
        public void handleMessage(Message inputMessage) {
            if (inputMessage.what == Constants.ConnectionError) {
//                    pd2.dismiss();
//                    if (pd != null)
//                        pd.dismiss();
                networkIO = false;
            }
            else {
//                    pd2.dismiss();
//                    if (pd != null)
//                        pd.dismiss();
                networkIO = true;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_frag);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        pd2= ProgressDialog.show(SearchFrag.this, "Loading", "Downloading important info from the Internet.", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(mainloc);
    }

    @Override
    protected void onResume() {

        super.onResume();
        UserServer userServer = new UserServer();


        ServerConnection serverConn = new ServerConnection(SearchFrag.this, handle);
        serverConn.run();


        if (userServer.connectionState ==true) {
            lock = new Object();
            userServer.lock = lock;
            UserInfo user = userServer.getAUser(UserModel.getUserModel().getPhoneNumberFromSP());

            if (UserServer.returnInfo == null) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

            Event_T event_t = new Event_T();
            event_t.getAllEvent();


            while (Event_T.test == null) {


            }

        }
        pd2.dismiss();

        mainloc = new Intent(getApplicationContext(), SearchHelper.class);
        mainloc.putExtra("Mode", "Voluntary");
        MainSearchFragment.getloc = mainloc;
        startService(mainloc);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchfrag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(null, "Setting Button is clicked");
            Intent intent = new Intent(getApplicationContext(), Setting.class);
            startActivity(intent);
            return true;
        }
        if (id ==R.id.action_about){
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a MainSearchFragment (defined as a static inner class below).
//            return MainSearchFragment.newInstance(position + 1);
            if (position ==0)
                return MainSearchFragment.newInstance(position + 1);
            else
                return PassiveSearchFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainSearchFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "main search";
        private static String [] activity;



        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MainSearchFragment newInstance(int sectionNumber) {
            MainSearchFragment fragment = new MainSearchFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public MainSearchFragment() {
        }

        private static Intent getloc;
        private ProgressDialog pd;



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search, container, false);
            activity = getResources().getStringArray(R.array.interest_array2);

            final ListView list = (ListView) rootView.findViewById(R.id.searchselectionList);
            list.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_single_choice, activity));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    if (SearchHelper.mCurrentLocation == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        //  Chain together various setter methods to set the dialog characteristics
                        builder.setMessage("No location detected!!")
                                .setTitle("Error")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }

                                });

                        // Get the AlertDialog from create()
                        builder.create().show();
                        return;
                    }
                    pd = ProgressDialog.show(getActivity(), "Network Access", "Connecting to the server", true);
                    ServerConnection serverConn = new ServerConnection(getActivity(), handle);
                    serverConn.run(); //test network connection
                    pd.dismiss();

                    while (UserServer.connectionState == null) {

                    }
                    if (!UserServer.connectionState) {
                        return;
                    }


                    double lat = SearchHelper.mCurrentLocation.getLatitude();
                    double lon = SearchHelper.mCurrentLocation.getLongitude();
                    Log.i("SearchFrag", (String) list.getAdapter().getItem(i) + " " + lat + " " + lon);
                    String id = Matching.checking((String) list.getAdapter().getItem(i), lat, lon);
                    getActivity().stopService(getloc);


                    Log.i(ARG_SECTION_NUMBER, "Successfully get the location");
                    if (id == null) {
                        Intent intent2 = new Intent(getActivity(), Map.class);
                        intent2.putExtra("Interest", (String) list.getAdapter().getItem(i));
                        intent2.putExtra("lat", lat);
                        intent2.putExtra("lon", lon);
                        intent2.putExtra(Constants.eventCode, 100);
                        Log.i(ARG_SECTION_NUMBER, "Create new event");

                        startActivity(intent2);

                    } else {
                        Intent intent2 = new Intent(getActivity(), EventMenu.class);
                        intent2.putExtra(Constants.eventId, id);
                        Log.i(ARG_SECTION_NUMBER, "Go to the existing event");
                        startActivity(intent2);
                    }
                }
            });







//            getloc = new Intent(getActivity(), SearchHelper.class);
//            getloc.putExtra("Mode", "Voluntary");
//            getActivity().startService(getloc);


//            Log.i(ARG_SECTION_NUMBER, UserServer.returnInfo._id);



            return rootView;
        }
    }

    public static class PassiveSearchFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "passive search";
        private static String [] activity;
        private ListAdapter adapter;
        private ArrayList<String> tempPassive;
        private boolean enableButton;
        private Button enablepassive;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PassiveSearchFragment newInstance(int sectionNumber) {
            PassiveSearchFragment fragment = new PassiveSearchFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PassiveSearchFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_passivesearch, container, false);
            activity = getResources().getStringArray(R.array.interest_array2);

            final ListView list = (ListView) rootView.findViewById(R.id.passivesearchselectionList);
            adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_single_choice, activity);

            list.setAdapter(adapter);
            tempPassive = new ArrayList<>();
            enableButton = false;


//            list.setSelection(((ArrayAdapter)list.getAdapter()).getPosition(UserModel.getUserModel().getInterest()));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selected = (String) list.getItemAtPosition(i);
                    Log.i(ARG_SECTION_NUMBER, selected + " selected!");
                    if (tempPassive.contains(selected)) {
                        tempPassive.remove(selected);
                    }
                    else
                        tempPassive.add(selected);
                }

            });

            enablepassive = (Button) rootView.findViewById(R.id.passiveSearchEnabler);
            enablepassive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SearchHelper.class);
                    if (enableButton){

                        enableButton = false;
                        getActivity().stopService(intent);

                        return;
                    }
                    String result = "Selected interests:";
                    for (int i= 0 ; i< tempPassive.size(); i++){
                        result = result +" "+ tempPassive.get(i);
                    }
                    Log.i(ARG_SECTION_NUMBER, result);

                    intent.putExtra("Mode", "Passive");
                    intent.putStringArrayListExtra("Interest", tempPassive);
                    getActivity().startService(intent);

                    enableButton = true;


                }
            });

            return rootView;
        }



    }

}
