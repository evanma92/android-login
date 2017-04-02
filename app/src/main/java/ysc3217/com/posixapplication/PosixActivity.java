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
    private TextView result;
    private TextView searchedUser;
    private TextView searchedPass;
    private TextView userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posix);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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
                    }
                } catch (NullPointerException npe){
                    npe.printStackTrace();
                }
            }
        });

        login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try {
                    userPassword = (EditText) findViewById(R.id.Password);
                    userName = (EditText) findViewById(R.id.UserName);
                    searchedUser = (TextView) findViewById(R.id.searchedUser);
                    searchedPass = (TextView) findViewById(R.id.searchedPass);

                    String username = userName.getText().toString();
                    String pw = userPassword.getText().toString();
                    String hashed_pw = md5(pw);

                    String userdata = readUserData();

                    PosixActivity.this.result.setText(userdata);
                    PosixActivity.this.searchedUser.setText(userdata.substring(0, username.length()));
                    PosixActivity.this.searchedPass.setText(userdata.substring(username.length()));
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

        /*
            Just to test whether the password is hashed correctly.
         */
        this.result = (TextView) findViewById(R.id.result);
        this.result.setText(hashed_password);

        try {
            outputStream = openFileOutput(this.filename, 0);     // 0 Sets the file to private so that other apps may not access this data
            outputStream.write(username.getBytes());
            outputStream.write(hashed_password.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
