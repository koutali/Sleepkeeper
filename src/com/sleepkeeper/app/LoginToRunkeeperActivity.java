package com.sleepkeeper.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginToRunkeeperActivity extends Activity 
{
	private String password;
	private String email;
	
	private final String emailKey = "emailKey";
	private final String passwordKey = "passwordKey";
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_to_runkeeper_layout);
        
        if(savedInstanceState != null)
        {
    		email = savedInstanceState.getString(emailKey);
    		password = savedInstanceState.getString(passwordKey);
        }
    }
    
    /* Both onClick functions declared in the layout XML file */
    public void onLoginClick(View v) 
    {
    	EditText emailEditText = (EditText)findViewById(R.id.email_field);
    	EditText passwordEditText = (EditText)findViewById(R.id.password_field);
    	
    	email = emailEditText.getText().toString();
    	password = passwordEditText.getText().toString();
    }
    
    public void onCancelClick(View v) 
    {
    	EditText emailEditText = (EditText)findViewById(R.id.email_field);
    	EditText passwordEditText = (EditText)findViewById(R.id.password_field);

    	emailEditText.getEditableText().clear();
    	passwordEditText.getEditableText().clear();
    	
        Toast.makeText(LoginToRunkeeperActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
    }
    
    /* onSaveInstanceState and onSaveInstanceState needed so that we don't 
     * lose data when changing device orientation
     */
    public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		savedInstanceState.putString(emailKey, email);
		savedInstanceState.putString(passwordKey, password);
		super.onSaveInstanceState(savedInstanceState);
	}
}