package com.example.tazpitapp;

import android.util.Log;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    public static final List<DummyItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<>();
    public static  R R;
    private static final List <String> list = new ArrayList<>();


    static {
        // Add some sample items.null

       FirebaseFirestore.getInstance() .collection(constants.DOC_REF_SCENARIOS).get().addOnCompleteListener(task -> {
            Log.d("onComplet","in1");
            if (task.isSuccessful()) {
                Log.d("onComplet","in2");
                int i=0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    list.add(document.getId());
                    Log.d("document_id=",document.getId());
                    addItem(createDummyItem(i,list.get(i)));
                    i++;
                }
        //        ((TextView) _newRootView.findViewById(R.id.scenerios_detail)).setText("test");
                Log.d("onComplet","in3");
                Log.d("document=", list.toString());
            } else {
                Log.d("onComplet","No data");

            }
        });
//        Log.d("size_arr=",list);

    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        System.out.println(item.content);
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
        builder.append(com.example.tazpitapp.R.string.makedetails_detail_about).append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\n"+ com.example.tazpitapp.R.string.details_infromation);
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