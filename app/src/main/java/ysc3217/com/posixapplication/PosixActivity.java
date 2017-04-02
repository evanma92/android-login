package ysc3217.com.posixapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PosixActivity extends AppCompatActivity {
    public static final String WELCOME_MESSAGE = "Hi there, ";

    private String filename;
    private EditText userName;
    private EditText userPassword;
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posix);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        /*
            Creates a register button:
            1) On click, the app gets the username and password and puts them in a string
            2) Then we check whether the user has input his/her username
                a) If not, we ask the user to input his/her username (using a snackbar)
            3) Then we check whether the password is of length 8 or more
                a) If not, we ask the user to input again (using a snackbar)
            4) If all the above conditions are met, we save the userdata
         */
        register = (Button) findViewById(R.id.RegisterButton);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try {
                    userPassword = (EditText) findViewById(R.id.Password);
                    userName = (EditText) findViewById(R.id.UserName);
                    String username = userName.getText().toString();
                    String password = userPassword.getText().toString();
                    if (username.length() < 1) {
                        Snackbar.make(view, "Please input your username", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else if (password.length() < 8) {
                        Snackbar.make(view, "Please input a password of at least 8 characters long", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        saveUserData(username, password);
                        userName.setText("");
                        userPassword.setText("");
                        Snackbar.make(view, "Registration Successful!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Intent intent = new Intent(PosixActivity.this, DisplayMessageActivity.class);

                        String message = "Registration Successful!";
                        intent.putExtra(PosixActivity.WELCOME_MESSAGE, message);
                        startActivity(intent);
                    }
                } catch (NullPointerException npe){
                    npe.printStackTrace();
                }
            }
        });


        /*
            Creates a login button:
            1) On click, the app gets the username and password and puts them in a string
            2) Then we hash it to get the hashed password (hash_pw)
            3) Next we read the data from file
            4) We then check the username and password accordingly by
                a) Splitting up the data into two components
                b) Checking for equality
            5) If login is successful, then we create a new intent and launch a new activity
               called DisplayMessageActivity, which displays a welcome message to the user
               a) Else we ask the user to register or input their username/password again (by the Snackbar)
         */
        login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try {
                    userPassword = (EditText) findViewById(R.id.Password);
                    userName = (EditText) findViewById(R.id.UserName);

                    String username = userName.getText().toString();
                    String pw = userPassword.getText().toString();
                    String hashed_pw = md5(pw);

                    String userdata = readUserData();

                    if (username.equals(userdata.substring(0, username.length()))
                            && hashed_pw.equals(userdata.substring(username.length()))) {

                        Snackbar.make(view, "Successfully Logged In!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Intent intent = new Intent(PosixActivity.this, DisplayMessageActivity.class);

                        String message = String.format("Hi there, %s!", username);
                        intent.putExtra(PosixActivity.WELCOME_MESSAGE, message);
                        startActivity(intent);

                    } else {
                        Snackbar.make(view, "Incorrect Username or Password. Have you registered with us?", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } catch (NullPointerException npe){
                    npe.printStackTrace();
                }
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        PosixActivity.this.userName = (EditText) findViewById(R.id.UserName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_posix, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Creates a new method to read information from the file
     */

    public String readUserData(){
        this.filename = getString(R.string.user_data);

        FileInputStream inputStream;
        InputStreamReader isr;
        BufferedReader buffreader;
        StringBuffer userData = new StringBuffer("");
        try{
            inputStream = openFileInput(this.filename);
            isr = new InputStreamReader(inputStream);
            buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while ((readString != null))
            {
                userData.append(readString);
                readString = buffreader.readLine();
            }

            isr.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return userData.toString();
    }



    /*
        Creates a new method to save information into a file
     */

    private void saveUserData(String username, String password) {
        this.filename = getString(R.string.user_data);

        FileOutputStream outputStream;
        final String hashed_password = md5(password);

        try {
            outputStream = openFileOutput(this.filename, 0);     // 0 Sets the file to private so that other apps may not access this data
            outputStream.write(username.getBytes());
            outputStream.write(hashed_password.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        md5 algorithm to hash the password
     */
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
