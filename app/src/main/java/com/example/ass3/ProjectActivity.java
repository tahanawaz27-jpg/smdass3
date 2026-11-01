package com.example.ass3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ass3.fragments.DiscussionFragment;
import com.example.ass3.fragments.FilesFragment;
import com.example.ass3.fragments.OverviewFragment;
import com.example.ass3.fragments.TasksFragment;
import com.example.ass3.models.Project;
import com.example.ass3.models.Task;
import com.google.android.material.tabs.TabLayout;

public class ProjectActivity extends AppCompatActivity {

    private Project project;
    private TabLayout tabLayoutProject;
    private OverviewFragment overviewFragment;
    private TasksFragment tasksFragment;
    private FilesFragment filesFragment;
    private DiscussionFragment discussionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (getIntent() != null && getIntent().hasExtra("project")) {
            project = (Project) getIntent().getSerializableExtra("project");
        }
        if (project == null) {
            project = new Project("default", "Sample Project", "General", "Default description", 0);
        }

        Toolbar toolbarProject = findViewById(R.id.toolbarProject);
        setSupportActionBar(toolbarProject);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(project.getName());
            getSupportActionBar().setSubtitle(project.getTag());
        }

        toolbarProject.setNavigationOnClickListener(v -> finishWithResult());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishWithResult();
            }
        });

        tabLayoutProject = findViewById(R.id.tabLayoutProject);
        setupTabs();

        overviewFragment = OverviewFragment.newInstance(project);
        tasksFragment = TasksFragment.newInstance(project);
        filesFragment = FilesFragment.newInstance(project);
        discussionFragment = DiscussionFragment.newInstance(project);

        loadFragment(overviewFragment);

        tabLayoutProject.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        loadFragment(overviewFragment);
                        break;
                    case 1:
                        loadFragment(tasksFragment);
                        break;
                    case 2:
                        loadFragment(filesFragment);
                        break;
                    case 3:
                        loadFragment(discussionFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupTabs() {
        int tasksCount = project.getTasks() != null ? project.getTasks().size() : 0;
        int filesCount = project.getAttachments() != null ? project.getAttachments().size() : 0;
        int commentsCount = project.getCommentsCount();

        tabLayoutProject.addTab(tabLayoutProject.newTab().setText("Overview"));
        tabLayoutProject.addTab(tabLayoutProject.newTab().setText("Tasks (" + tasksCount + ")"));
        tabLayoutProject.addTab(tabLayoutProject.newTab().setText("Files (" + filesCount + ")"));
        tabLayoutProject.addTab(tabLayoutProject.newTab().setText("Discussion (" + commentsCount + ")"));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void showEditProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_project, null);
        EditText etName = dialogView.findViewById(R.id.etEditProjectName);
        EditText etTag = dialogView.findViewById(R.id.etEditProjectTag);
        EditText etDesc = dialogView.findViewById(R.id.etEditProjectDesc);

        etName.setText(project.getName());
        etTag.setText(project.getTag());
        etDesc.setText(project.getDescription());

        builder.setView(dialogView)
                .setTitle("Edit Project")
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String tag = etTag.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || name.length() > 60) {
                        Toast.makeText(this, "Name must be 1-60 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(tag) || tag.length() > 20) {
                        Toast.makeText(this, "Tag must be 1-20 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(desc) || desc.length() > 500) {
                        Toast.makeText(this, "Description must be 1-500 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    project.setName(name);
                    project.setTag(tag);
                    project.setDescription(desc);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(name);
                        getSupportActionBar().setSubtitle(tag);
                    }

                    if (overviewFragment != null) overviewFragment.updateData(project);
                    if (tasksFragment != null) tasksFragment.updateData(project);
                    if (filesFragment != null) filesFragment.updateData(project);

                    Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText etTaskDesc = dialogView.findViewById(R.id.etTaskDesc);

        builder.setView(dialogView)
                .setTitle("Add New Task")
                .setPositiveButton("Add", (dialog, which) -> {
                    String desc = etTaskDesc.getText().toString().trim();
                    if (TextUtils.isEmpty(desc) || desc.length() > 500) {
                        Toast.makeText(this, "Task description must be 1-500 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String taskId = "t_" + System.currentTimeMillis();
                    Task newTask = new Task(taskId, desc, "To do", "Normal", "");
                    project.getTasks().add(newTask);

                    if (overviewFragment != null) overviewFragment.updateData(project);
                    if (tasksFragment != null) tasksFragment.updateData(project);
                    updateTabCounts();

                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTabCounts() {
        int tasksCount = project.getTasks() != null ? project.getTasks().size() : 0;
        if (tabLayoutProject.getTabAt(1) != null) {
            tabLayoutProject.getTabAt(1).setText("Tasks (" + tasksCount + ")");
        }
    }

    public void switchToTasksTab(String statusFilter, String dateFilter) {
        if (tabLayoutProject.getTabAt(1) != null) {
            tabLayoutProject.getTabAt(1).select();
        }
    }

    public void switchToFilesTab() {
        if (tabLayoutProject.getTabAt(2) != null) {
            tabLayoutProject.getTabAt(2).select();
        }
    }

    public void switchToDiscussionTab() {
        if (tabLayoutProject.getTabAt(3) != null) {
            tabLayoutProject.getTabAt(3).select();
        }
    }

    private void finishWithResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_project", project);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
