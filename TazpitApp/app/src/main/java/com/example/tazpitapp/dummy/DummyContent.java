package com.example.tazpitapp.dummy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import com.example.tazpitapp.cal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;
    private static List <String> list = new ArrayList<>();
    static {
        // Add some sample items.

        Task<QuerySnapshot> docRef;
        docRef = FirebaseFirestore.getInstance() .collection("Scenarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    int i=0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                        addItem(createDummyItem(i,list.get(i)),(GeoPoint)document.getData().get("מיקום"));
                        i++;
                    }
                    Log.d("document=", list.toString());
                } else {
                    Log.d("Document","No data");

                }
            }
        });
//        Log.d("size_arr=",list);

    }

    private static void addItem(DummyItem item,GeoPoint gpsLocation) {
        ITEMS.add(item);
        cal gp=new cal();
        System.out.println(item.content+"-"+gp.Range(gpsLocation));
        System.out.println(item.id);
        System.out.println(item.details);

        ITEM_MAP.put(item.id, item);
    }
    //list=sacrio 1

    private static DummyItem createDummyItem(int position,String list) {

        return new DummyItem(String.valueOf(position),list,makeDetails(position));
    }
    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }

}