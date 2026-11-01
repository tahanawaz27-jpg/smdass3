package com.example.ass3;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ass3.adapters.ProjectAdapter;
import com.example.ass3.models.Attachment;
import com.example.ass3.models.Project;
import com.example.ass3.models.Task;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private List<Project> projectList;
    private List<Project> filteredList;
    private ProjectAdapter adapter;
    private ListView lvProjects;
    private EditText etSearchProject;
    private TextView tvShowingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lvProjects = findViewById(R.id.lvProjects);
        etSearchProject = findViewById(R.id.etSearchProject);
        tvShowingCount = findViewById(R.id.tvShowingCount);
        Button btnNewProject = findViewById(R.id.btnNewProject);

        initSampleData();

        filteredList = new ArrayList<>(projectList);
        adapter = new ProjectAdapter(this, filteredList);
        lvProjects.setAdapter(adapter);

        updateShowingCount();

        etSearchProject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProjects(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvProjects.setOnItemClickListener((parent, view, position, id) -> {
            Project selectedProject = filteredList.get(position);
            // Open ProjectActivity (to be implemented in commit 2)
            Toast.makeText(this, "Opening: " + selectedProject.getName(), Toast.LENGTH_SHORT).show();
        });

        btnNewProject.setOnClickListener(v -> {
            // New Project dialog (to be enhanced in commit 2)
            Toast.makeText(this, "+ New Project clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void initSampleData() {
        projectList = new ArrayList<>();

        // 1. Service Page Website
        Project p1 = new Project("p1", "Service Page Website", "User Interface", "Make a page display about services for company-website with blue and red colors", 3);
        List<Task> t1 = new ArrayList<>();
        t1.add(new Task("t1", "Build header layout", "To do", "High", "2022-05-18"));
        t1.add(new Task("t2", "Add form validation", "To do", "High", "2022-05-18"));
        t1.add(new Task("t3", "Create brand guideline for website design", "Done", "Normal", "2022-05-18"));
        t1.add(new Task("t4", "Create low fidelity service page website", "To do", "Normal", "2022-05-15"));
        p1.setTasks(t1);
        List<Attachment> a1 = new ArrayList<>();
        a1.add(new Attachment("a1", "sports-projectv2.fig", "Image", "2.4 MB"));
        a1.add(new Attachment("a2", "briefing from client.pdf", "Text", "450 KB"));
        p1.setAttachments(a1);
        projectList.add(p1);

        // 2. Login API
        Project p2 = new Project("p2", "Login API", "Backend", "Build authentication endpoints and JWT token refresh handlers.", 2);
        List<Task> t2 = new ArrayList<>();
        t2.add(new Task("t21", "Setup database schema", "Done", "High", "2022-05-10"));
        t2.add(new Task("t22", "Implement JWT filter", "In progress", "High", "2022-05-20"));
        t2.add(new Task("t23", "Write unit tests", "To do", "Normal", "2022-05-25"));
        p2.setTasks(t2);
        projectList.add(p2);

        // 3. Mobile Dashboard
        Project p3 = new Project("p3", "Mobile Dashboard", "Frontend", "Responsive overview widgets and telemetry cards.", 3);
        List<Task> t3 = new ArrayList<>();
        t3.add(new Task("t31", "Design widget grid", "Done", "Normal", "2022-05-12"));
        t3.add(new Task("t32", "Connect telemetry API", "In progress", "High", "2022-05-22"));
        p3.setTasks(t3);
        projectList.add(p3);

        // 4. Release Checklist
        Project p4 = new Project("p4", "Release Checklist", "QA & Test", "Pre-production verification and smoke tests", 3);
        List<Task> t4 = new ArrayList<>();
        t4.add(new Task("t41", "Smoke test release build", "Done", "High", "2022-05-14"));
        t4.add(new Task("t42", "Security audit check", "Done", "High", "2022-05-15"));
        p4.setTasks(t4);
        projectList.add(p4);

        // 5. Design Assets
        Project p5 = new Project("p5", "Design Assets", "Design", "Vector icons and illustration library for landing pages", 5);
        List<Task> t5 = new ArrayList<>();
        t5.add(new Task("t51", "Export SVG icons", "To do", "Low", "2022-05-30"));
        p5.setTasks(t5);
        projectList.add(p5);
    }

    private void filterProjects(String query) {
        filteredList.clear();
        String q = query.toLowerCase().trim();
        if (q.isEmpty()) {
            filteredList.addAll(projectList);
        } else {
            for (Project p : projectList) {
                if (p.getName().toLowerCase().contains(q) || p.getTag().toLowerCase().contains(q)) {
                    filteredList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateShowingCount();
    }

    private void updateShowingCount() {
        tvShowingCount.setText("Showing " + filteredList.size() + " projects");
    }
}
