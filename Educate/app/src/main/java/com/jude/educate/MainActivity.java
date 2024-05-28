//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jude.educate;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jude.educate.Adapter.CustomAdapter1;
import com.jude.educate.Adapter.CustomAdapter2;
import com.jude.educate.Adapter.ViewPager2Adapter;
import com.jude.educate.DataModel.DataCard1;
import com.jude.educate.DataModel.DataCard2;
import com.jude.educate.DataModel.DataModel1;
import com.jude.educate.DataModel.DataModel2;
import com.jude.educate.R.id;
import com.jude.educate.R.layout;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel1> data;
    private Context mContext;

    // for dots in layout
    private LinearLayout dotsLayout;
    private static RecyclerView.Adapter adapter2;
    private RecyclerView.LayoutManager layoutManager2;
    private static RecyclerView recyclerView2;
    private static ArrayList<DataModel2> data2;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ViewPager2Adapter adapter;


    public MainActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        this.setContentView(layout.activity_main);

//        InitiateMainCards();


        viewPager = findViewById(id.recyclerViewPrimary);
        tabLayout = findViewById(R.id.tabLayout);

        data = new ArrayList<>();
        for (int i = 0; i < DataCard1.nameArray.length; ++i) {
            data.add(new DataModel1(DataCard1.nameArray[i], DataCard1.buttonName[i], DataCard1.id_[i], DataCard1.drawableArray[i]));
        }

        adapter = new ViewPager2Adapter( data,this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(R.layout.custom_tab);
            }
        }).attach();

        InitiateSecondCards();

        // Inside onCreate after setting the adapter
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1 - Math.abs(position);
                page.setScaleY(0.85f + v * 0.15f);
            }
        });

        viewPager.setPageTransformer(transformer);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);

    }
    private ArrayList<DataModel1> prepareData() {
        ArrayList<DataModel1> data = new ArrayList<>();
        for (int i = 0; i < DataCard1.nameArray.length; i++) {
            data.add(new DataModel1(DataCard1.nameArray[i], DataCard1.buttonName[i], DataCard1.id_[i], DataCard1.drawableArray[i]));
        }
        return data;
    }

//    private void InitiateMainCards() {
//        recyclerView = (RecyclerView)this.findViewById(id.recyclerViewPrimary);
//        recyclerView.setHasFixedSize(true);
//        dotsLayout = findViewById(R.id.dotsLayout);
//        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
//        recyclerView.setLayoutManager(this.layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        data = new ArrayList<>();
//
//
//        for(int i = 0; i < DataCard1.nameArray.length; ++i) {
//            data.add(new DataModel1(DataCard1.nameArray[i], DataCard1.buttonName[i], DataCard1.id_[i], DataCard1.drawableArray[i]));
//        }
//
//        adapter = new ViewPager2Adapter(data, this);
//        recyclerView.setAdapter(adapter);
//
//        // Initialize dots
//        addDotsIndicator(data.size());
//
//        // Set the listener for page changes
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//                updateDotsIndicator(position);
//            }
//        });
//    }

    private void addDotsIndicator(int count) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dot, params);
        }
    }

    private void updateDotsIndicator(int position) {
        int centerPosition = findCenterPosition();
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            dot.setImageResource(i == centerPosition ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private int findCenterPosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int center = (lastVisiblePosition - firstVisiblePosition) / 2 + firstVisiblePosition;
        return center;
    }

    private void InitiateSecondCards(){

        recyclerView2 = (RecyclerView)this.findViewById(id.recyclerViewSecondary);
        recyclerView2.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        recyclerView2.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        data2 = new ArrayList<>();

        for(int i = 0; i < DataCard2.nameArray.length; ++i) {
            data2.add(new DataModel2(DataCard2.drawableArray[i],DataCard2.nameArray[i],DataCard2.id_[i]));
        }

        adapter2 = new CustomAdapter2(data2, this);
        recyclerView2.setAdapter(adapter2);


    }


}
