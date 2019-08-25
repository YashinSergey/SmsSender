package com.example.smssender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private final String KEY = "com.example.smssender.key";

    private TextInputEditText textInputField;
    private MaterialButton sendBtn;
    private String smsText;
    private String phoneNumber = "89875896521";

    private EditText etPhoneNumber;
    private MaterialButton phoneNumBtn;
    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        init();
    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textInputField = findViewById(R.id.textInputField);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        phoneNumBtn = findViewById(R.id.btnPhoneNum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.myMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, myMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onClickMenuItem(item);
        return super.onOptionsItemSelected(item);
    }

    private void onClickMenuItem(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setNumber) {
            etPhoneNumber.setVisibility(View.VISIBLE);
            phoneNumBtn.setVisibility(View.VISIBLE);
            phoneNumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phoneNumber = etPhoneNumber.getText().toString()
                            .replaceAll("-", "")
                            .replaceAll("\\(", "")
                            .replaceAll("\\)", "");
                    etPhoneNumber.setText("");
                    etPhoneNumber.setVisibility(View.INVISIBLE);
                    phoneNumBtn.setVisibility(View.INVISIBLE);
                    myMenu.close();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        sendSms();
    }

    private void sendSms() {
        smsText = Objects.requireNonNull(textInputField.getText()).toString();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null,smsText,null,null);
        textInputField.setText("");
    }
}
