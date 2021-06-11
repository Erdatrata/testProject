package com.example.tazpitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    static boolean isInit = true;
    public   List <String> list = new ArrayList<>();
    CollectionReference itemRef;
     List<DummyContent.DummyItem> ITEMS = new ArrayList<DummyContent.DummyItem>();
    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_scenerios_list);
        View recyclerView = findViewById(R.id.scenerios_list);
        creat_list(recyclerView);
    }
    public void  creat_list(View recyclerView){
        if (isInit) {//refresh the paga
            isInit = false;
            startActivity(new Intent(this, SceneriosListActivity.class));
            finish();
        }
        FirebaseFirestore firestoreRootRef = FirebaseFirestore.getInstance();
        itemRef = firestoreRootRef.collection("Scenarios");
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(List<String> list) {//this function is callback be after  readData function
                list.isEmpty();
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);//send the findViewById(R.id.scenerios_list) to setupRecyclerView function
            }
        });

        gpsState = getStateOfGps();//start_Scen12
        if (gpsState) {//check if have pramter in  gpsState
            //get lat and long
            Log.d("onComplet", "start_Scen_indx1");
            getlatofgps = getlatOfGps();
            getlongofgps = getlongOfGps();
        }

        if (findViewById(R.id.scenerios_detail_container) != null) { //if hava id findViewById(R.id.scenerios_detail_container) than make
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Log.d("onComplet", "start_Scen_indx2");
            mTwoPane = true;
        }


    }
    private  void readData( FirestoreCallback firestoreCallback){//this function connact to firebase and put the scenrios_list in ITEMS
        Log.d("onComplet","readData_1=");
        itemRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if (task.isSuccessful()) {
                    int p=0;
                    for(int i=0;i<list.size();i++){//clean list
                        list.remove(i);
                    }
                    for(int i=0;!ITEMS.isEmpty();i++){//clean ITEMS
                        if(!ITEMS.isEmpty()){
                            ITEMS.removeAll(ITEMS);
                        }
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                        ITEMS.add(new com.example.tazpitapp.DummyContent.DummyItem(String.valueOf(p),document.getId(),makeDetails(p)));
                        p++;

                    }
                    firestoreCallback.onCallback(list);// call to callback function
                }
                else{
                    Log.d("onComplet","inside onComplete () ERRORR");
                }
            }
        });
    }
    private  interface  FirestoreCallback{ //interface  to callback function
        void onCallback(List<String> list);
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {//put all scenrios in   recyclerView
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this,ITEMS, mTwoPane));
    }
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        private final SceneriosListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues; //items of the list showing
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(SceneriosDetailFragment.ARG_ITEM_ID, item.id);
                    SceneriosDetailFragment fragment = new SceneriosDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.scenerios_detail_container, fragment)
                            .commit();
                } else {//clicking on item send intent
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
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scenerios_list_content, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String str="";
            DocumentReference mDocRef= FirebaseFirestore.getInstance().document("Scenarios/"+mValues.get(position).content);
            mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mDocRef.collection("accepted").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int indicator=0;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if(user.getUid().toString().equals(document.getId().toString())) {// if the user is sing to even then

                                                String str="\r"+mValues.get(position).content+" "+addRange(documentSnapshot);
                                                if(str.equals(""))
                                                    continue;
                                                Spannable spannable = new SpannableString(str);
                                                spannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0 ,mValues.get(position).content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                ForegroundColorSpan fcsRed=new ForegroundColorSpan(Color.parseColor("#228b22"));
                                                ForegroundColorSpan fcsgray=new ForegroundColorSpan(Color.GRAY);
                                                spannable.setSpan(fcsgray,0,mValues.get(position).content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                spannable.setSpan(fcsRed,mValues.get(position).content.length()+1,str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
                                                holder.mContentView.setText(spannable);//gray color on user if he sign and green color the km and set her
                                                                                        //and put the scenrios on the app
                                                holder.itemView.setTag(mValues.get(position));
                                                Log.d("onComplete","po="+mValues.get(position));
                                                holder.itemView.setOnClickListener(mOnClickListener);
                                                break;
                                            }//if the not user is sing to even then
                                            String str=mValues.get(position).content+" "+addRange(documentSnapshot);
                                            ForegroundColorSpan fcsgreen=new ForegroundColorSpan(Color.parseColor("#bb1715"));
                                             SpannableStringBuilder sb = new SpannableStringBuilder(str);
                                             StyleSpan iss = new StyleSpan(Typeface.BOLD); //Span to make text italic
                                            sb.setSpan(iss, 0, mValues.get(position).content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make last 2 characters Italic
                                            sb.setSpan(fcsgreen,mValues.get(position).content.length()+1,str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            holder.mContentView.setText(sb);////Bold color on user if he  not sign and red color the km and set her
                                                                            //and put the scenrios on the app
                                            holder.itemView.setTag(mValues.get(position));
                                            holder.itemView.setOnClickListener(mOnClickListener);
                                        }
                                    }
                                    else {
                                        Log.d("output", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    return;
                }
            });
            FirebaseFirestore.getInstance() .collection("Scenarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {//this function is if the scenrios is new and he dont have
                                                                            //accpet in the firebase
                    if (task.isSuccessful()) {
                        int i=0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String str="\r"+mValues.get(position).content+" "+addRange(document);
                            ForegroundColorSpan fcsRed=new ForegroundColorSpan(Color.parseColor("#bb1715"));
                            SpannableStringBuilder sb = new SpannableStringBuilder(str);
                            StyleSpan iss = new StyleSpan(Typeface.BOLD); //Span to make text italic
                            sb.setSpan(iss, 0, mValues.get(position).content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make last 2 characters Italic
                            sb.setSpan(fcsRed,mValues.get(position).content.length()+1,str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holder.mContentView.setText(sb);/////Bold color on user if he  not sign and red color the km and set her
                                                                 //and put the scenrios on the app
                            holder.itemView.setTag(mValues.get(position));
                            holder.itemView.setOnClickListener(mOnClickListener);
                        }
                    } else {
                        Log.d("onComplet","No data");
                    }
                }
            });
        }
        @Override
        public int getItemCount() {//get the size of th scenrios
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
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(constants.gpsState,false);
    }
    public  String getlatOfGps(){//get latitude of gps
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.latOfGps,"");
    }
    public  String getlongOfGps(){//get longtitude of gps
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.longOfGps,"");
    }

    public static String Range(GeoPoint gpsLocation) {
        String re="";
        if(gpsState){
            double latCurrent=Double.parseDouble(getlatofgps);
            double lonCurrent=Double.parseDouble(getlongofgps);
            double latScenerio=gpsLocation.getLatitude();
            double lonScenerio=gpsLocation.getLongitude();
            double result=Math.pow(Math.pow((111*(latCurrent-latScenerio)),2.0)+Math.pow((111*(lonCurrent-lonScenerio)),2.0),0.5);
            System.out.println(result);
            return String.valueOf(result);

        }

        return re;
    }
    private  String makeDetails(int position) {// the function mkie
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
    private static String addRange(DocumentSnapshot documentSnapshot){//this function make the gps distanc to to digit after .
        if(gpsState) {
            String str_gps=Range((GeoPoint) documentSnapshot.getData().get("מיקום"));
            String str_res="";
            for(int i=0;i<str_gps.length();i++){
                if(!String.valueOf(str_gps.substring(i,i+1)).equals(".")){
                    str_res+=str_gps.substring(i,i+1);
                }
                else if (str_gps.substring(i,i+1).equals(".")){
                    for(int j=0;j<3;j++){
                        str_res+=str_gps.substring(i+j,i+j+1);
                    }
                    break;
                }
            }
            return "\n" +str_res+"km";
        }
        return "";
    }

}

