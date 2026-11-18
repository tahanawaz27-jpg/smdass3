package com.example.taskflow;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_PROJECT = 100;

    private LinearLayout llProjectsContainer;
    private TextView tvUpcomingCount;
    private List<Project> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        llProjectsContainer = findViewById(R.id.llProjectsContainer);
        tvUpcomingCount = findViewById(R.id.tvUpcomingCount);
        Button btnNewProject = findViewById(R.id.btnNewProject);

        projectList = new ArrayList<>();
        seedInitialProject();

        btnNewProject.setOnClickListener(v -> showAddProjectDialog());

        renderProjects();
    }

    /** Requirement 2c seed data so HomeScreen matches the design on first run */
    private void seedInitialProject() {
        ArrayList<String> attachments = new ArrayList<>();
        attachments.add("sports-projectv2.fig");
        attachments.add("briefing from client.pdf");
        attachments.add("mount asset");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Create brand guideline for website design", true));
        tasks.add(new Task("Create low fidelity service page website", false));

        Project project = new Project(
                "User Interface",
                "Service Page Website",
                "Create a page display about services for the company website in blue and red and provide some price information on each product card.",
                attachments,
                tasks
        );
        projectList.add(project);
    }

    private void renderProjects() {
        llProjectsContainer.removeAllViews();
        tvUpcomingCount.setText(String.valueOf(projectList.size()));

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < projectList.size(); i++) {
            final int index = i;
            Project project = projectList.get(i);

            View card = inflater.inflate(R.layout.item_project_card, llProjectsContainer, false);

            TextView tvTag = card.findViewById(R.id.tvTag);
            TextView tvName = card.findViewById(R.id.tvProjectName);
            TextView tvDesc = card.findViewById(R.id.tvProjectDesc);
            TextView tvTaskCount = card.findViewById(R.id.tvTaskCount);
            TextView tvAttachmentCount = card.findViewById(R.id.tvAttachmentCount);

            tvTag.setText(project.getTag());
            tvName.setText(project.getName());
            tvDesc.setText(project.getDescription());
            tvTaskCount.setText(project.getCompletedTaskCount() + "/" + project.getTotalTaskCount());
            tvAttachmentCount.setText(project.getAttachments().size() + " Attachment");

            // Requirement 3 & 6: open ProjectActivity, passing the project via Intent
            card.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
                intent.putExtra("project", project);
                intent.putExtra("index", index);
                startActivityForResult(intent, REQUEST_PROJECT);
            });

            llProjectsContainer.addView(card);
        }
    }

    /** Requirement 2b: alert dialog to add a new project (tag, name, description, attachment) */
    private void showAddProjectDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_project, null);

        EditText etName = dialogView.findViewById(R.id.etProjectName);
        EditText etTag = dialogView.findViewById(R.id.etProjectTag);
        EditText etAttachment = dialogView.findViewById(R.id.etAttachment);
        EditText etDescription = dialogView.findViewById(R.id.etProjectDescription);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Project");
        builder.setView(dialogView);

        builder.setPositiveButton("Add Project", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String tag = etTag.getText().toString().trim();
            String attachment = etAttachment.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(HomeActivity.this, "Project name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(tag)) tag = "General";

            ArrayList<String> attachments = new ArrayList<>();
            if (!TextUtils.isEmpty(attachment)) attachments.add(attachment);

            // New project is added to the upcoming section (Requirement 2b)
            Project newProject = new Project(tag, name, description, attachments, new ArrayList<>());
            projectList.add(newProject);
            renderProjects();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /** Requirement 4: when a task is ticked in ProjectActivity, HomeScreen counts must update too */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PROJECT && resultCode == RESULT_OK && data != null) {
            int index = data.getIntExtra("index", -1);
            Project updatedProject = (Project) data.getSerializableExtra("project");
            if (index >= 0 && index < projectList.size() && updatedProject != null) {
                projectList.set(index, updatedProject);
                renderProjects();
            }
        }
    }
}
