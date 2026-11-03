package com.example.ass3.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.ass3.R;
import com.example.ass3.models.Attachment;
import com.example.ass3.models.Project;

import java.util.ArrayList;
import java.util.List;

public class FilesFragment extends Fragment {

    private Project project;
    private ListView lvFiles;
    private EditText etSearchFile;
    private Spinner spinnerFileType;
    private Button btnAddAttachment;
    private LinearLayout layoutEmptyFiles;
    private TextView tvEmptyFileMessage;

    private List<Attachment> filteredAttachments;
    private FileAdapter fileAdapter;
    private String selectedTypeFilter = "All";

    private List<Attachment> masterCatalog;

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

        initMasterCatalog();

        lvFiles = view.findViewById(R.id.lvFiles);
        etSearchFile = view.findViewById(R.id.etSearchFile);
        spinnerFileType = view.findViewById(R.id.spinnerFileType);
        btnAddAttachment = view.findViewById(R.id.btnAddAttachment);
        layoutEmptyFiles = view.findViewById(R.id.layoutEmptyFiles);
        tvEmptyFileMessage = view.findViewById(R.id.tvEmptyFileMessage);

        filteredAttachments = new ArrayList<>();
        if (project != null && project.getAttachments() != null) {
            filteredAttachments.addAll(project.getAttachments());
        }

        fileAdapter = new FileAdapter();
        lvFiles.setAdapter(fileAdapter);

        setupFileTypeSpinner();
        applyFilters();

        etSearchFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddAttachment.setOnClickListener(v -> showAddAttachmentDialog());

        return view;
    }

    private void initMasterCatalog() {
        masterCatalog = new ArrayList<>();
        masterCatalog.add(new Attachment("cat_1", "sports-projectv2.fig", "Image", "2.4 MB - Figma Design File"));
        masterCatalog.add(new Attachment("cat_2", "briefing from client.pdf", "Text", "450 KB - PDF Document"));
        masterCatalog.add(new Attachment("cat_3", "mount asset.png", "Image", "1.1 MB - PNG Asset"));
        masterCatalog.add(new Attachment("cat_4", "architecture-diagram.png", "Image", "3.2 MB - Architecture Overview"));
        masterCatalog.add(new Attachment("cat_5", "api-specs.txt", "Text", "120 KB - Text Document"));
    }

    private void setupFileTypeSpinner() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All", "Images", "Text"});
        spinnerFileType.setAdapter(typeAdapter);
        spinnerFileType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sel = parent.getItemAtPosition(position).toString();
                if (sel.equals("Images")) selectedTypeFilter = "Image";
                else if (sel.equals("Text")) selectedTypeFilter = "Text";
                else selectedTypeFilter = "All";
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyFilters() {
        if (project == null || project.getAttachments() == null) return;

        filteredAttachments.clear();
        String query = etSearchFile.getText().toString().toLowerCase().trim();

        for (Attachment a : project.getAttachments()) {
            boolean matchesSearch = query.isEmpty() || a.getName().toLowerCase().contains(query);
            boolean matchesType = selectedTypeFilter.equals("All") || a.getType().equalsIgnoreCase(selectedTypeFilter);

            if (matchesSearch && matchesType) {
                filteredAttachments.add(a);
            }
        }

        fileAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        int totalFiles = project != null && project.getAttachments() != null ? project.getAttachments().size() : 0;
        if (totalFiles == 0) {
            lvFiles.setVisibility(View.GONE);
            layoutEmptyFiles.setVisibility(View.VISIBLE);
            tvEmptyFileMessage.setText("No attachments yet");
        } else if (filteredAttachments.isEmpty()) {
            lvFiles.setVisibility(View.GONE);
            layoutEmptyFiles.setVisibility(View.VISIBLE);
            tvEmptyFileMessage.setText("No attachments match your search or type");
        } else {
            lvFiles.setVisibility(View.VISIBLE);
            layoutEmptyFiles.setVisibility(View.GONE);
        }
    }

    private void showAddAttachmentDialog() {
        List<Attachment> unlinked = new ArrayList<>();
        List<String> unlinkedNames = new ArrayList<>();
        for (Attachment catalogItem : masterCatalog) {
            boolean linked = false;
            if (project.getAttachments() != null) {
                for (Attachment existing : project.getAttachments()) {
                    if (existing.getId().equals(catalogItem.getId()) || existing.getName().equals(catalogItem.getName())) {
                        linked = true;
                        break;
                    }
                }
            }
            if (!linked) {
                unlinked.add(catalogItem);
                unlinkedNames.add(catalogItem.getName() + " (" + catalogItem.getType() + ")");
            }
        }

        if (unlinked.isEmpty()) {
            Toast.makeText(requireContext(), "All available catalog items are already attached", Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] selectedIndex = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Attachment from Catalog")
                .setSingleChoiceItems(unlinkedNames.toArray(new String[0]), -1, (dialog, which) -> {
                    selectedIndex[0] = which;
                })
                .setPositiveButton("Add", (dialog, which) -> {
                    if (selectedIndex[0] >= 0) {
                        Attachment chosen = unlinked.get(selectedIndex[0]);
                        project.getAttachments().add(chosen);
                        applyFilters();
                        Toast.makeText(requireContext(), "Attachment linked successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        ListView lv = dialog.getListView();
        lv.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex[0] = position;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        });
    }

    private void showPreviewDialog(Attachment attachment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_preview_attachment, null);
        TextView tvTitle = view.findViewById(R.id.tvPreviewTitle);
        TextView tvContent = view.findViewById(R.id.tvPreviewContent);
        ImageView ivPreview = view.findViewById(R.id.ivPreviewImage);

        tvTitle.setText(attachment.getName());
        tvContent.setText("Type: " + attachment.getType() + "\nDetails: " + attachment.getContent());

        if ("Image".equalsIgnoreCase(attachment.getType())) {
            ivPreview.setVisibility(View.VISIBLE);
            ivPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            ivPreview.setVisibility(View.GONE);
        }

        builder.setView(view)
                .setPositiveButton("Close", null)
                .show();
    }

    private void confirmRemoveAttachment(Attachment attachment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_remove, null);
        EditText etConfirmName = view.findViewById(R.id.etConfirmName);
        TextView tvPrompt = view.findViewById(R.id.tvConfirmPrompt);
        tvPrompt.setText("Type '" + attachment.getName() + "' to confirm removal:");

        builder.setView(view)
                .setTitle("Confirm Deletion")
                .setPositiveButton("Remove", (dialog, which) -> {
                    String typed = etConfirmName.getText().toString().trim();
                    if (typed.equals(attachment.getName())) {
                        project.getAttachments().remove(attachment);
                        applyFilters();
                        Toast.makeText(requireContext(), "Attachment removed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Name did not match. Deletion cancelled.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void updateData(Project updatedProject) {
        this.project = updatedProject;
        applyFilters();
    }

    private class FileAdapter extends ArrayAdapter<Attachment> {
        public FileAdapter() {
            super(requireContext(), R.layout.item_attachment, filteredAttachments);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(requireContext()).inflate(R.layout.item_attachment, parent, false);
            }

            Attachment attachment = getItem(position);
            ImageView ivFileIcon = convertView.findViewById(R.id.ivFileIcon);
            TextView tvFileName = convertView.findViewById(R.id.tvFileName);
            TextView tvFileType = convertView.findViewById(R.id.tvFileType);
            Button btnPreview = convertView.findViewById(R.id.btnPreview);
            ImageView ivRemoveFile = convertView.findViewById(R.id.ivRemoveFile);

            if (attachment != null) {
                tvFileName.setText(attachment.getName());
                tvFileType.setText(attachment.getType() + " • " + attachment.getContent());

                if ("Image".equalsIgnoreCase(attachment.getType())) {
                    ivFileIcon.setImageResource(android.R.drawable.ic_menu_gallery);
                } else {
                    ivFileIcon.setImageResource(android.R.drawable.ic_menu_agenda);
                }

                btnPreview.setOnClickListener(v -> showPreviewDialog(attachment));
                ivRemoveFile.setOnClickListener(v -> confirmRemoveAttachment(attachment));
            }

            return convertView;
        }
    }
}
