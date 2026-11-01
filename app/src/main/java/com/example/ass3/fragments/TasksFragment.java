package com.example.ass3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ass3.R;
import com.example.ass3.models.Project;
import com.example.ass3.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private Project project;
    private ListView lvTasks;
    private ArrayAdapter<String> adapter;
    private List<String> taskDescriptions;

    public static TasksFragment newInstance(Project project) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putSerializable("project", project);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false); // we'll create fragment_tasks or use simple view

        if (getArguments() != null) {
            project = (Project) getArguments().getSerializable("project");
        }

        lvTasks = view.findViewById(R.id.lvTasks);
        taskDescriptions = new ArrayList<>();
        if (project != null && project.getTasks() != null) {
            for (Task t : project.getTasks()) {
                taskDescriptions.add(t.getDescription() + " [" + t.getStatus() + "]");
            }
        }

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, taskDescriptions);
        lvTasks.setAdapter(adapter);

        return view;
    }

    public void updateData(Project updatedProject) {
        this.project = updatedProject;
        if (taskDescriptions != null && lvTasks != null) {
            taskDescriptions.clear();
            for (Task t : project.getTasks()) {
                taskDescriptions.add(t.getDescription() + " [" + t.getStatus() + "]");
            }
            adapter.notifyDataSetChanged();
        }
    }
}
