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
import com.example.ass3.models.Attachment;
import com.example.ass3.models.Project;

import java.util.ArrayList;
import java.util.List;

public class FilesFragment extends Fragment {

    private Project project;
    private ListView lvFiles;
    private ArrayAdapter<String> adapter;
    private List<String> fileNames;

    public static FilesFragment newInstance(Project project) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putSerializable("project", project);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);

        if (getArguments() != null) {
            project = (Project) getArguments().getSerializable("project");
        }

        lvFiles = view.findViewById(R.id.lvFiles);
        fileNames = new ArrayList<>();
        if (project != null && project.getAttachments() != null) {
            for (Attachment a : project.getAttachments()) {
                fileNames.add(a.getName() + " (" + a.getType() + ")");
            }
        }

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, fileNames);
        lvFiles.setAdapter(adapter);

        return view;
    }

    public void updateData(Project updatedProject) {
        this.project = updatedProject;
        if (fileNames != null && lvFiles != null) {
            fileNames.clear();
            for (Attachment a : project.getAttachments()) {
                fileNames.add(a.getName() + " (" + a.getType() + ")");
            }
            adapter.notifyDataSetChanged();
        }
    }
}
