package com.example.testqrscanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view){
        EditText userName = (EditText) findViewById(R.id.txt_login);
        EditText password = (EditText) findViewById(R.id.txt_password);
        if(DataBaseController.checkLogin(this, userName.getText().toString(), password.getText().toString())) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
        else{
            setStatus("Неправильный логин или пароль!", true);
        }
    }

    public void register(View view){
        EditText userName = (EditText) findViewById(R.id.txt_login);
        EditText password = (EditText) findViewById(R.id.txt_password);
        if(!DataBaseController.checkLogin(this, userName.getText().toString(), password.getText().toString()) &&
                DataBaseController.addUser(this, userName.getText().toString(), password.getText().toString())){
            setStatus("Регистрация успешна!", false);
        }
        else{
            setStatus("Ошибка регистрации!", true);
        }
    }


    private void setStatus(String text, boolean bad){
        TextView status = (TextView) findViewById(R.id.txt_status);
        status.setText(text);
        status.setVisibility(View.VISIBLE);
        if(bad) status.setTextColor(Color.RED);
        else status.setTextColor(Color.GREEN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1500);
                    status.setVisibility(View.INVISIBLE);
                }
                catch (InterruptedException exc){
                    System.out.println(exc.getMessage());
                }
            }
        }).start();
    }

}
