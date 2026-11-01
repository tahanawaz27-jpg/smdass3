package com.example.ass3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.ass3.ProjectActivity;
import com.example.ass3.R;
import com.example.ass3.models.Project;
import com.example.ass3.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OverviewFragment extends Fragment {

    private Project project;
    private TextView tvOverviewDesc, tvOverviewPercentage, tvOverviewTaskCountSummary;
    private TextView tvToDoCount, tvInProgressCount, tvDoneCount, tvOverdueCount;
    private ProgressBar progressOverview;

    public static OverviewFragment newInstance(Project project) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("project", project);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        if (getArguments() != null) {
            project = (Project) getArguments().getSerializable("project");
        }

        tvOverviewDesc = view.findViewById(R.id.tvOverviewDesc);
        tvOverviewPercentage = view.findViewById(R.id.tvOverviewPercentage);
        tvOverviewTaskCountSummary = view.findViewById(R.id.tvOverviewTaskCountSummary);
        progressOverview = view.findViewById(R.id.progressOverview);

        tvToDoCount = view.findViewById(R.id.tvToDoCount);
        tvInProgressCount = view.findViewById(R.id.tvInProgressCount);
        tvDoneCount = view.findViewById(R.id.tvDoneCount);
        tvOverdueCount = view.findViewById(R.id.tvOverdueCount);

        TextView tvEditProject = view.findViewById(R.id.tvEditProject);
        CardView cardToDo = view.findViewById(R.id.cardToDo);
        CardView cardInProgress = view.findViewById(R.id.cardInProgress);
        CardView cardDone = view.findViewById(R.id.cardDone);
        CardView cardOverdue = view.findViewById(R.id.cardOverdue);
        CardView cardLinkFiles = view.findViewById(R.id.cardLinkFiles);
        CardView cardLinkDiscussion = view.findViewById(R.id.cardLinkDiscussion);
        View btnAddOverviewTask = view.findViewById(R.id.btnAddOverviewTask);

        updateUI();

        tvEditProject.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).showEditProjectDialog();
            }
        });

        cardToDo.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).switchToTasksTab("To do", null);
            }
        });

        cardInProgress.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).switchToTasksTab("In progress", null);
            }
        });

        cardDone.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).switchToTasksTab("Done", null);
            }
        });

        cardOverdue.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).switchToTasksTab("All", "Overdue");
            }
        });

        cardLinkFiles.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).switchToFilesTab();
            }
        });

        cardLinkDiscussion.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).switchToDiscussionTab();
            }
        });

        btnAddOverviewTask.setOnClickListener(v -> {
            if (getActivity() instanceof ProjectActivity) {
                ((ProjectActivity) getActivity()).showAddTaskDialog();
            }
        });

        return view;
    }

    public void updateData(Project updatedProject) {
        this.project = updatedProject;
        updateUI();
    }

    private void updateUI() {
        if (project == null) return;

        tvOverviewDesc.setText(project.getDescription());

        int total = project.getTasks().size();
        int done = project.getCompletedTasksCount();
        int todo = 0, inProg = 0, overdue = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        for (Task t : project.getTasks()) {
            String status = t.getStatus();
            if ("To do".equalsIgnoreCase(status)) {
                todo++;
            } else if ("In progress".equalsIgnoreCase(status)) {
                inProg++;
            }

            // Check overdue: due date before today and status not Done
            if (!"Done".equalsIgnoreCase(status) && t.getDueDate() != null && !t.getDueDate().isEmpty()) {
                try {
                    Date dueDate = sdf.parse(t.getDueDate());
                    if (dueDate != null && dueDate.before(today)) {
                        overdue++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        int percentage = project.getProgressPercentage();
        tvOverviewPercentage.setText(percentage + "%");
        tvOverviewTaskCountSummary.setText(done + " of " + total + " tasks completed");
        progressOverview.setProgress(percentage);

        tvToDoCount.setText(String.valueOf(todo));
        tvInProgressCount.setText(String.valueOf(inProg));
        tvDoneCount.setText(String.valueOf(done));
        tvOverdueCount.setText(String.valueOf(overdue));
    }
}
