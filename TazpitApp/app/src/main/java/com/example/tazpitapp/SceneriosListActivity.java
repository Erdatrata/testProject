package com.example.tazpitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Scenarios. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SceneriosDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SceneriosListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static boolean gpsState; //gps on off
    private static String getlatofgps;//lat of gps
    private static String getlongofgps; //long of gps
    private  static  int bit=0;
    static boolean isInit = true;
    static boolean isInit1 = true;
    private  List <String> list = new ArrayList<>();
   CollectionReference itemRef;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_list);
        FirebaseFirestore firestoreRootRef = FirebaseFirestore.getInstance();
        itemRef = firestoreRootRef.collection("Scenarios");
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(List<String> list) {
                View recyclerView = findViewById(R.id.scenerios_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);
            }
        });

        gpsState=getStateOfGps();//start_Scen12
        if(gpsState){
            //get lat and long
            Log.d("onComplet","start_Scen_indx1");
            getlatofgps=getlatOfGps();
            getlongofgps=getlongOfGps();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onComplet","start_Scen01");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.scenerios_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Log.d("onComplet","start_Scen_indx2");
            mTwoPane = true;
        }
        Log.d("onComplet","start_Scen+indx="+DummyContent.ITEMS);
        Log.d("onComplet","start_Scen0");

    }
        private  void readData( FirestoreCallback firestoreCallback){
        itemRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    firestoreCallback.onCallback(list);
                }
                else{
                    Log.d("onComplet","inside onComplete () ERRORR");
                }
            }
        });
    }
    private  interface  FirestoreCallback{
        void onCallback(List<String>list);
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("onComplet","start_Scen110="+DummyContent.ITEMS);

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));

    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final SceneriosListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues; //items of the list showing
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("onComplet","start_Scen1");
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Log.d("onComplet","start_Scen2");
                    Bundle arguments = new Bundle();
                    arguments.putString(SceneriosDetailFragment.ARG_ITEM_ID, item.id);
                    SceneriosDetailFragment fragment = new SceneriosDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.scenerios_detail_container, fragment)
                            .commit();
                } else {//clicking on item send intent
                    Log.d("onComplet","start_Scen3");
                    Context context = view.getContext();
                    Intent intent = new Intent(context, SceneriosDetailActivity.class);
                    intent.putExtra(SceneriosDetailFragment.ARG_ITEM_ID, item.id); //sent trow intent the id[number in list] ,and content [name of scenerio]
                    intent.putExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT, item.content);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(SceneriosListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane) {
            Log.d("onComplet","start_Scen4");
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("onComplet","start_Scen5");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scenerios_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Log.d("onComplet","start_Scen6");

            DocumentReference mDocRef= FirebaseFirestore.getInstance().document("Scenarios/"+mValues.get(position).content);
            mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("onComplet","start_Scen6_0="+mValues.get(position));
                    holder.mIdView.setText(mValues.get(position).id);

                    holder.mContentView.setText(mValues.get(position).content+"-Range-"+Range((GeoPoint)documentSnapshot.getData().get("מיקום")));//add range with the name of the sceneriro

                    holder.itemView.setTag(mValues.get(position));
                    holder.itemView.setOnClickListener(mOnClickListener);
                }
            });


        }

        @Override
        public int getItemCount() {
            Log.d("onComplet","start_Scen7");
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
    public  boolean getStateOfGps(){//return true or false if the gps is working
        Log.d("onComplet","start_Scen12");
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(constants.gpsState,false);
    }
    public  String getlatOfGps(){//get latitude of gps
        Log.d("onComplet","start_Scen13");
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.latOfGps,"");
    }
    public  String getlongOfGps(){//get longtitude of gps
        Log.d("onComplet","start_Scen14");
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.longOfGps,"");
    }
    //       try {//must be try and catch,  lat and long for the array in size of 1 , then get into tv_address the address
    //                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
    //                tv_addressInString=addresses.get(0).getAddressLine(0);
    //                tv_address.setText(tv_addressInString);
    //            } catch (Exception e) {
    //                tv_address.setText("Unale to get address");
    //            }
    public static String Range(GeoPoint gpsLocation) {
        Log.d("onComplet","start_Scen15");
        String re="";
        if(gpsState){
            double latCurrent=Double.parseDouble(getlatofgps);
            double lonCurrent=Double.parseDouble(getlongofgps);
            double latScenerio=gpsLocation.getLatitude();
            double lonScenerio=gpsLocation.getLongitude();


            double result=Math.pow(Math.pow((111*(latCurrent-latScenerio)),2.0)+Math.pow((111*(lonCurrent-lonScenerio)),2.0),0.5);
            Log.d("onComplet","start_Scen15="+result);
            System.out.println(result);
            return String.valueOf(result);

        }

        return re;
    }

}

