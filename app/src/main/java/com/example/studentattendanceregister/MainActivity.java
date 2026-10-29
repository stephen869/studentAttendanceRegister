package com.example.studentattendanceregister;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // ---------- Simple in-memory data model ----------
    static final int UNMARKED = 0;
    static final int PRESENT = 1;
    static final int ABSENT = 2;

    static class Student {
        String name;
        int status = UNMARKED;

        Student(String name) {
            this.name = name;
        }
    }

    private final ArrayList<Student> students = new ArrayList<>();
    private StudentAdapter adapter;

    private EditText etStudentName;
    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etStudentName = findViewById(R.id.etStudentName);
        tvSummary = findViewById(R.id.tvSummary);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnReset = findViewById(R.id.btnReset);
        ListView lvStudents = findViewById(R.id.lvStudents);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new StudentAdapter();
        lvStudents.setAdapter(adapter);
        lvStudents.setEmptyView(tvEmpty);

        btnAdd.setOnClickListener(v -> addStudent());
        btnReset.setOnClickListener(v -> confirmReset());

        updateSummary();
    }

    // ---------- Add student, validate empty name ----------
    private void addStudent() {
        String name = etStudentName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a student name", Toast.LENGTH_SHORT).show();
            return;
        }

        students.add(new Student(name));
        etStudentName.setText("");
        adapter.notifyDataSetChanged();
        updateSummary();
        Toast.makeText(this, name + " added", Toast.LENGTH_SHORT).show();
    }

    // ---------- Summary ----------
    private void updateSummary() {
        int present = 0, absent = 0;
        for (Student s : students) {
            if (s.status == PRESENT) present++;
            else if (s.status == ABSENT) absent++;
        }
        tvSummary.setText("Total: " + students.size()
                + "  |  Present: " + present
                + "  |  Absent: " + absent);
    }

    // ---------- Reset with confirmation ----------
    private void confirmReset() {
        if (students.isEmpty()) {
            Toast.makeText(this, "The register is already empty", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Reset Register")
                .setMessage("This will remove all students. Continue?")
                .setPositiveButton("Yes, clear", (dialog, which) -> {
                    students.clear();
                    adapter.notifyDataSetChanged();
                    updateSummary();
                    Toast.makeText(this, "Register cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------- Custom adapter ----------
    private class StudentAdapter extends ArrayAdapter<Student> {

        StudentAdapter() {
            super(MainActivity.this, 0, students);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_student, parent, false);
            }

            final Student student = getItem(position);

            View root = convertView.findViewById(R.id.rowRoot);
            TextView tvName = convertView.findViewById(R.id.tvName);
            RadioGroup rg = convertView.findViewById(R.id.rgStatus);
            RadioButton rbPresent = convertView.findViewById(R.id.rbPresent);
            RadioButton rbAbsent = convertView.findViewById(R.id.rbAbsent);

            tvName.setText(student.name);

            rg.setOnCheckedChangeListener(null);
            rg.clearCheck();
            if (student.status == PRESENT) rbPresent.setChecked(true);
            else if (student.status == ABSENT) rbAbsent.setChecked(true);

            if (student.status == PRESENT) root.setBackgroundColor(Color.parseColor("#C8E6C9"));
            else if (student.status == ABSENT) root.setBackgroundColor(Color.parseColor("#FFCDD2"));
            else root.setBackgroundColor(Color.WHITE);

            rg.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbPresent) student.status = PRESENT;
                else if (checkedId == R.id.rbAbsent) student.status = ABSENT;
                notifyDataSetChanged();
                updateSummary();
            });

            return convertView;
        }
    }
}