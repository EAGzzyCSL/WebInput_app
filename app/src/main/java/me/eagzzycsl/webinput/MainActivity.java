package me.eagzzycsl.webinput;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private View.OnClickListener fab_onClick;
    private Button button_enable;
    private Button button_pick;

    private AlertDialog.Builder dialogBuilder;
    private int requestEnableInputMethodCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myFindView();
        myCreate();
        mySetView();
    }

    private void myFindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        button_enable = (Button) findViewById(R.id.button_enable);
        button_pick = (Button) findViewById(R.id.button_pick);

    }


    private void mySetView() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(fab_onClick);
        button_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS),
                        requestEnableInputMethodCode);
            }
        });
        button_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imeManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imeManager != null) {
                    imeManager.showInputMethodPicker();
                }
            }
        });
        setTitle(R.string.title_activity_main);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void myCreate() {
        dialogBuilder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.visitLinkInNoteBook))
                .setMessage(getString(R.string.webLink))
                .setPositiveButton(R.string.ok, null);
        fab_onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.show();

            }
        };

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestEnableInputMethodCode) {
            if (isMyInputMethodEnabled()) {
                button_pick.setEnabled(true);

                button_pick.setBackgroundTintList(
                        ContextCompat.getColorStateList(
                                MainActivity.this, R.color.button_tint));
            } else {
                button_pick.setEnabled(false);
            }

        }

    }

    private boolean isMyInputMethodEnabled() {
        InputMethodManager imeManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> list = imeManager.getEnabledInputMethodList();
        for (InputMethodInfo i : list) {
            if (i.getPackageName().equals(MainActivity.this.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /*菜单部分*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting: {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            case R.id.action_about: {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
