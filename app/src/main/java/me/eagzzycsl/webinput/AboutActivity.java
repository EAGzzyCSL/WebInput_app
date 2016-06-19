package me.eagzzycsl.webinput;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AboutActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private View.OnClickListener fab_onClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        myFindView();
        myCreate();
        mySetView();


    }

    private void mySetView() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(fab_onClick);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void myFindView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

    }

    private void myCreate() {
        fab_onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.url_official_website)));
                startActivity(browserIntent);
            }
        };
    }

    /*菜单部分*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
                startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
