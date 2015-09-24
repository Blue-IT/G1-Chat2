package com.blueit.g1_chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.blueit.g1_chat.parseobjects.Newsflash;

public class NewsflashActivity extends AppCompatActivity implements View.OnClickListener {


    static final int CREATE_NEWSFLASH_REQUEST = 1;  // The request code

    private EditText newsflashText;
    private ArrayList<String> TEMP_newsflashTable;
    private ArrayAdapter<String> TEMP_newsflashTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set default layout
        setContentView(R.layout.activity_newsflash);

        // Get logged in user
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

            // Append admin view
            if(currentUser.getBoolean("isAdmin")) {

                // Get reference to our current layout
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.newsflash_layout);

                // "Inflate" the admin view, attaching it as a child to our layout through the layout reference
                // Then immediately set our content view to the returned combined view, so that other function calls do not need a reference.
                setContentView(getLayoutInflater().inflate(R.layout.activity_newsflash_admin, layout, true));

                // Initialize admin text field
                newsflashText = (EditText) findViewById(R.id.newsflash_text);
                newsflashText.setInputType(InputType.TYPE_CLASS_TEXT
                               | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        
                // Setup admin submit button
                setClick(R.id.newsflash_submit);
            }

        }
        else {
            Log.e("G1CHAT", "No currentUser");
        }

        
        // Initialize placeholder database
        TEMP_newsflashTable = new ArrayList<String>();
        TEMP_newsflashTable.add("Lorem");
        TEMP_newsflashTable.add("Ipsum");

        // Configure the adapter which feeds database info into the list
        TEMP_newsflashTableAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, TEMP_newsflashTable);
        ListView listView = (ListView) findViewById(R.id.newsflash_list);
        listView.setAdapter(TEMP_newsflashTableAdapter);

        // Setup submit button
        setClick(R.id.newsflash_submit);

        // Setup parse
        ParseObject.registerSubclass(Newsflash.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_newsflash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.d("G1CHAT", "Settings button pressed");
            return true;
        }
        else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.newsflash_submit)
        {
            createNewsflash();
        }

    }

    public void createNewsflash()
    {
        // Get input
        String input = newsflashText.getText().toString();

        // Validate
        if (input.equals("") ) {
            return;
        }

        // Start create newsflash activity
        Intent intent = new Intent(NewsflashActivity.this, CreateNewsflashActivity.class);
        startActivityForResult(intent, CREATE_NEWSFLASH_REQUEST);

        // Create and insert
        TEMP_newsflashTable.add(input);
        TEMP_newsflashTableAdapter.notifyDataSetChanged();
        Log.d("G1CHAT", "Newsflash created!");

        // Reset input
        newsflashText.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Request
        if (requestCode == CREATE_NEWSFLASH_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String id = data.getStringExtra("id");

                ParseQuery<Newsflash> query = ParseQuery.getQuery(Newsflash.class);
                query.whereEqualTo("id", id);
                query.getFirstInBackground(new GetCallback<Newsflash>() {
                    @Override
                    public void done(Newsflash parseObject, ParseException e) {
                        TEMP_newsflashTable.add(parseObject.getTitle());
                        TEMP_newsflashTableAdapter.notifyDataSetChanged();
                    }
                });

            }

            else {

            }
        }
    }

    public void logout() {
        ParseUser.getCurrentUser().logOut();
        Intent intent = new Intent(NewsflashActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Sets the click listener for a view with given id.
     *
     * @param id
     *            the id
     * @return the view on which listener is applied
     */
    public View setClick(int id)
    {
        View v = findViewById(id);
        if (v != null)
            v.setOnClickListener(this);
        return v;
    }

}
