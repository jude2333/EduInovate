package com.jude.educate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.educate.Adapter.CustomAdapter2Student;
import com.jude.educate.DataModel.DataCard2;
import com.jude.educate.DataModel.DataModel2;

import java.util.ArrayList;

public class StudentActivityFragment extends Fragment {


    private static RecyclerView.Adapter adapter2;
    private RecyclerView.LayoutManager layoutManager2;
    private static RecyclerView recyclerView2;
    private static ArrayList<DataModel2> data2;
    TextView currentCourse;

    String spinnerSelectedCourseCode = StudentActivity.selectedCourse;

    public StudentActivityFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        InitiateSecondCards(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateCurrentCourse();  // Update course whenever fragment is resumed
    }

    private void InitiateSecondCards(View view) {
        recyclerView2 = view.findViewById(R.id.recyclerViewSecondaryS);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        data2 = new ArrayList<>();

        for (int i = 0; i < DataCard2.nameArray.length; ++i) {
            data2.add(new DataModel2(DataCard2.drawableArray[i], DataCard2.nameArray[i], DataCard2.id_[i]));
        }

        adapter2 = new CustomAdapter2Student(data2, getActivity());
        recyclerView2.setAdapter(adapter2);

        currentCourse = view.findViewById(R.id.courseIdStudent);
        updateCurrentCourse();
    }

    private void updateCurrentCourse() {
        String selectedCourseCode = StudentActivity.selectedCourse;  // Get the latest course
        if (currentCourse != null && selectedCourseCode != null) {
            currentCourse.setText(selectedCourseCode);  // Update the text view with the latest course
        }
    }
}