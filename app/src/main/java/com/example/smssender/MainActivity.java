package com.example.smssender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    public static String address = null;

    private TextInputEditText textInputField;
    private ListView textListView;

    private EditText etPhoneNumber;
    private MaterialButton phoneNumBtn;

    private final List<String> messageList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private IntentFilter filter = new IntentFilter(SMS_RECEIVED);
    private BroadcastReceiver receiver = new SmsMonitor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        init();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messageList);
        textListView.setAdapter(adapter);

        filter.setPriority(100);
        registerReceiver(receiver, filter);
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
        MaterialButton sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        phoneNumBtn = findViewById(R.id.btnPhoneNum);
        textListView = findViewById(R.id.listViewSms);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                    MainActivity.address = etPhoneNumber.getText().toString()
                            .replaceAll("-", "")
                            .replaceAll("\\(", "")
                            .replaceAll("\\)", "");
                    etPhoneNumber.setText("");
                    etPhoneNumber.setVisibility(View.INVISIBLE);
                    phoneNumBtn.setVisibility(View.INVISIBLE);
                    hideSoftKeyboard(view);
                }
            });
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        if (MainActivity.address == null) {
            Toast.makeText(getApplicationContext(), "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Objects.requireNonNull(textInputField.getText()).toString().equals("")) {
            sendSms();
            hideSoftKeyboard(view);
        }
    }

    private void sendSms() {
        String smsText = Objects.requireNonNull(textInputField.getText()).toString();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address, null, smsText, null, null);
        messageList.add(0, textInputField.getText().toString());
        adapter.notifyDataSetChanged();
        textInputField.setText("");
    }
}
