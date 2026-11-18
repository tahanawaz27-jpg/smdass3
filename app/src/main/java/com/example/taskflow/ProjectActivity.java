package com.example.taskflow;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ProjectActivity extends AppCompatActivity {

    private Project project;
    private int index;

    private TextView tvTag, tvName, tvDescription, tvTaskCount;
    private LinearLayout llAttachments, llTasksContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Requirement 6: project data received via Intent, nothing hard-coded
        project = (Project) getIntent().getSerializableExtra("project");
        index = getIntent().getIntExtra("index", -1);

        if (project == null) {
            finish();
            return;
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        tvTag = findViewById(R.id.tvTag);
        tvName = findViewById(R.id.tvProjectName);
        tvDescription = findViewById(R.id.tvProjectDescription);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        llAttachments = findViewById(R.id.llAttachments);
        llTasksContainer = findViewById(R.id.llTasksContainer);
        Button btnAddTask = findViewById(R.id.btnAddTask);

        // Requirement 5: back arrow returns to HomeScreen
        btnBack.setOnClickListener(v -> returnResult());
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        renderProjectInfo();
        renderTasks();
    }

    private void renderProjectInfo() {
        tvTag.setText(project.getTag());
        tvName.setText(project.getName());
        tvDescription.setText(project.getDescription());

        llAttachments.removeAllViews();
        for (String attachment : project.getAttachments()) {
            TextView tv = new TextView(this);
            tv.setText("\u2022 " + attachment);
            tv.setTextColor(ContextCompat.getColor(this, R.color.colorTextSecondary));
            tv.setTextSize(14);
            tv.setPadding(0, 4, 0, 4);
            llAttachments.addView(tv);
        }
    }

    private void renderTasks() {
        llTasksContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < project.getTasks().size(); i++) {
            Task task = project.getTasks().get(i);

            View row = inflater.inflate(R.layout.item_task_row, llTasksContainer, false);
            CheckBox cbTask = row.findViewById(R.id.cbTask);

            cbTask.setText(task.getDescription());
            cbTask.setChecked(task.isDone());
            applyStrikeThrough(cbTask, task.isDone());

            // Requirement 4: ticking updates status, reflected in HomeScreen when we go back
            cbTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setDone(isChecked);
                applyStrikeThrough(cbTask, isChecked);
                updateTaskCount();
            });

            llTasksContainer.addView(row);
        }

        updateTaskCount();
    }

    private void applyStrikeThrough(CheckBox cb, boolean strike) {
        if (strike) {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            cb.setPaintFlags(cb.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void updateTaskCount() {
        tvTaskCount.setText(project.getCompletedTaskCount() + "/" + project.getTotalTaskCount());
    }

    /** Requirement 3: add task via AlertDialog with EditText, max 500 characters */
    private void showAddTaskDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        EditText etTask = dialogView.findViewById(R.id.etTaskDescription);
        etTask.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");
        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String description = etTask.getText().toString().trim();
            if (TextUtils.isEmpty(description)) {
                Toast.makeText(ProjectActivity.this, "Task description is required", Toast.LENGTH_SHORT).show();
                return;
            }
            project.getTasks().add(new Task(description, false));
            renderTasks();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /** Sends the (possibly updated) project + its index back to HomeActivity via Intent */
    private void returnResult() {
        Intent result = new Intent();
        result.putExtra("project", project);
        result.putExtra("index", index);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnResult();
        super.onBackPressed();
    }
}
