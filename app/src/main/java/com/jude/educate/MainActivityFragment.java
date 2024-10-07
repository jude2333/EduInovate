package com.jude.educate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jude.educate.Adapter.CustomAdapter2;
import com.jude.educate.Adapter.ViewPager2Adapter;
import com.jude.educate.Cource.CourceCreation;
import com.jude.educate.DataModel.DataCard1;
import com.jude.educate.DataModel.DataCard2;
import com.jude.educate.DataModel.DataModel1;
import com.jude.educate.DataModel.DataModel2;
import com.jude.educate.R;
import com.jude.educate.Student.StudentEnroll;
import com.jude.educate.databinding.ActivityCourceCreationBinding;
import com.jude.educate.login.LoginActivity;

import java.util.ArrayList;

public class MainActivityFragment extends Fragment {
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

    private FloatingActionButton fab;
    private DatabaseReference databaseReference;
    TextView currentCourse;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.recyclerViewPrimary);
        tabLayout = view.findViewById(R.id.tabLayout);

        data = new ArrayList<>();
        for (int i = 0; i < DataCard1.nameArray.length; ++i) {
            data.add(new DataModel1(DataCard1.nameArray[i], DataCard1.buttonName[i], DataCard1.id_[i], DataCard1.drawableArray[i]));
        }

        // Initialize FAB
        fab = view.findViewById(R.id.enrollOrCreate);  // Corrected initialization
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Set up FAB click listener
        fab.setOnClickListener(v -> showBottomSheetDialog());

        adapter = new ViewPager2Adapter(data, getContext());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setCustomView(R.layout.custom_tab)).attach();

        InitiateSecondCards(view);

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


    @Override
    public void onResume() {
        super.onResume();
        updateCurrentCourse();
    }

    private void InitiateSecondCards(View view) {
        recyclerView2 = view.findViewById(R.id.recyclerViewSecondary);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        data2 = new ArrayList<>();

        for (int i = 0; i < DataCard2.nameArray.length; ++i) {
            data2.add(new DataModel2(DataCard2.drawableArray[i], DataCard2.nameArray[i], DataCard2.id_[i]));
        }

        adapter2 = new CustomAdapter2(data2, getActivity());
        recyclerView2.setAdapter(adapter2);

        currentCourse = view.findViewById(R.id.currentIdFaculty);
        updateCurrentCourse();
    }

    private void showBottomSheetDialog() {
        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_options, null);

        bottomSheetView.findViewById(R.id.createCourse).setOnClickListener(v -> {
            createCourse();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.enrollStudent).setOnClickListener(v -> {
            enrollStudent();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void createCourse() {
        Intent intent = new Intent(getActivity(), CourceCreation.class);
        startActivity(intent);


        Toast.makeText(getActivity(), "Switching to Course Creation Activity", Toast.LENGTH_SHORT).show();
    }

    private void enrollStudent() {

        Intent intent = new Intent(getActivity(), StudentEnroll.class);
        startActivity(intent);


        Toast.makeText(getActivity(), "Switching Student Enroll Activity", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentCourse() {
        String selectedCourseCode = FacultyActivity.selectedCourse;  // Get the latest course
        if (currentCourse != null && selectedCourseCode != null) {
            currentCourse.setText(selectedCourseCode);  // Update the text view with the latest course
        }
    }
}
