//package com.example.tazpitapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.snackbar.Snackbar;
//
//import androidx.appcompat.widget.Toolbar;
//
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.ActionBar;
//
//import android.view.MenuItem;
//import android.widget.Button;

/**
 * An activity representing a single Scenerios detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SceneriosListActivity}.
 */
//public class SceneriosDetailActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scenerios_detail);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        // Show the Up button in the action bar.
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//
//        // savedInstanceState is non-null when there is fragment state
//        // saved from previous configurations of this activity
//        // (e.g. when rotating the screen from portrait to landscape).
//        // In this case, the fragment will automatically be re-added
//        // to its container so we don"t need to manually add it.
//        // For more information, see the Fragments API guide at:
//        //
//        // http://developer.android.com/guide/components/fragments.html
//        //
//        if (savedInstanceState == null) {
//            // Create the detail fragment and add it to the activity
//            // using a fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putString(SceneriosDetailFragment.ARG_ITEM_ID,
//                    getIntent().getStringExtra(SceneriosDetailFragment.ARG_ITEM_ID));
//            SceneriosDetailFragment fragment = new SceneriosDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.scenerios_detail_container, fragment)
//                    .commit();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            // This ID represents the Home or Up button. In the case of this
//            // activity, the Up button is shown. For
//            // more details, see the Navigation pattern on Android Design:
//            //
//            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
//            //
//            navigateUpTo(new Intent(this, SceneriosListActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
//
package com.example.tazpitapp;

        import androidx.appcompat.app.AppCompatActivity;
        import android.widget.Button;
        import android.content.Intent;
        import android.view.View;

        import android.os.Bundle;

public class  SceneriosDetailActivity extends AppCompatActivity {
    public Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_detail);

        button = (Button)findViewById(R.id.buttonScenario);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SceneriosDetailActivity.this,fillReport.class);
                startActivity(intent);
            }
        });
    }
}