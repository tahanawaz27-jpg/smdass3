package com.example.ass3.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.ass3.ProjectActivity;
import com.example.ass3.R;
import com.example.ass3.adapters.TaskAdapter;
import com.example.ass3.models.Project;
import com.example.ass3.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksFragment extends Fragment {

    private Project project;
    private ListView lvTasks;
    private TaskAdapter adapter;
    private List<Task> filteredTasks;
    private EditText etSearchTask;
    private Spinner spinnerStatus, spinnerPriority, spinnerDate, spinnerSort;
    private TextView tvShowingTasksCount, tvResetFilters, tvEmptyMessage;
    private LinearLayout layoutEmptyTasks;
    private Button btnAddTaskFragment, btnEmptyAction;

    private String selectedStatusFilter = "All statuses";
    private String selectedPriorityFilter = "All priorities";
    private String selectedDateFilter = "All dates";
    private String selectedSort = "Due date ascending";

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
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        if (getArguments() != null) {
            project = (Project) getArguments().getSerializable("project");
        }

        lvTasks = view.findViewById(R.id.lvTasks);
        etSearchTask = view.findViewById(R.id.etSearchTask);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        spinnerDate = view.findViewById(R.id.spinnerDate);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        tvShowingTasksCount = view.findViewById(R.id.tvShowingTasksCount);
        tvResetFilters = view.findViewById(R.id.tvResetFilters);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        layoutEmptyTasks = view.findViewById(R.id.layoutEmptyTasks);
        btnAddTaskFragment = view.findViewById(R.id.btnAddTaskFragment);
        btnEmptyAction = view.findViewById(R.id.btnEmptyAction);

        filteredTasks = new ArrayList<>();
        if (project != null && project.getTasks() != null) {
            filteredTasks.addAll(project.getTasks());
        }

        adapter = new TaskAdapter(getContext(), filteredTasks, (task, isDone) -> {
            task.setStatus(isDone ? "Done" : "To do");
            applyFiltersAndSort();
        });
        lvTasks.setAdapter(adapter);

        lvTasks.setOnItemClickListener((parent, view1, position, id) -> {
            Task t = filteredTasks.get(position);
            showEditTaskDialog(t);
        });

        setupSpinners();
        applyFiltersAndSort();

        etSearchTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tvResetFilters.setOnClickListener(v -> resetFilters());
        btnAddTaskFragment.setOnClickListener(v -> showAddTaskDialog());
        btnEmptyAction.setOnClickListener(v -> {
            if (project != null && project.getTasks() != null && !project.getTasks().isEmpty()) {
                resetFilters();
            } else {
                showAddTaskDialog();
            }
        });

        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All statuses", "To do", "In progress", "Done"});
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatusFilter = parent.getItemAtPosition(position).toString();
                applyFiltersAndSort();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All priorities", "High", "Normal", "Low"});
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPriorityFilter = parent.getItemAtPosition(position).toString();
                applyFiltersAndSort();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All dates", "Today", "Overdue", "No due date"});
        spinnerDate.setAdapter(dateAdapter);
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDateFilter = parent.getItemAtPosition(position).toString();
                applyFiltersAndSort();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Due date ascending", "Due date descending", "Priority", "Description"});
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSort = parent.getItemAtPosition(position).toString();
                applyFiltersAndSort();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void resetFilters() {
        etSearchTask.setText("");
        spinnerStatus.setSelection(0);
        spinnerPriority.setSelection(0);
        spinnerDate.setSelection(0);
        spinnerSort.setSelection(0);
        selectedStatusFilter = "All statuses";
        selectedPriorityFilter = "All priorities";
        selectedDateFilter = "All dates";
        selectedSort = "Due date ascending";
        applyFiltersAndSort();
    }

    public void applyFiltersAndSort() {
        if (project == null || project.getTasks() == null) return;

        filteredTasks.clear();
        String query = etSearchTask.getText().toString().toLowerCase().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        for (Task t : project.getTasks()) {
            // Search match
            boolean matchesSearch = query.isEmpty() || t.getDescription().toLowerCase().contains(query);

            // Status match
            boolean matchesStatus = selectedStatusFilter.equals("All statuses") || t.getStatus().equalsIgnoreCase(selectedStatusFilter);

            // Priority match
            boolean matchesPriority = selectedPriorityFilter.equals("All priorities") || t.getPriority().equalsIgnoreCase(selectedPriorityFilter);

            // Date match
            boolean matchesDate = true;
            boolean isDone = "Done".equalsIgnoreCase(t.getStatus());
            boolean isOverdue = false;
            if (!isDone && t.getDueDate() != null && !t.getDueDate().isEmpty()) {
                try {
                    Date dueDate = sdf.parse(t.getDueDate());
                    if (dueDate != null && dueDate.before(today)) {
                        isOverdue = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (selectedDateFilter.equals("Today")) {
                matchesDate = (t.getDueDate() != null && t.getDueDate().equals(sdf.format(today)));
            } else if (selectedDateFilter.equals("Overdue")) {
                matchesDate = isOverdue;
            } else if (selectedDateFilter.equals("No due date")) {
                matchesDate = (t.getDueDate() == null || t.getDueDate().isEmpty());
            }

            if (matchesSearch && matchesStatus && matchesPriority && matchesDate) {
                filteredTasks.add(t);
            }
        }

        // Sorting
        Collections.sort(filteredTasks, (t1, t2) -> {
            if (selectedSort.equals("Due date ascending")) {
                String d1 = t1.getDueDate() != null ? t1.getDueDate() : "9999-12-31";
                String d2 = t2.getDueDate() != null ? t2.getDueDate() : "9999-12-31";
                return d1.compareTo(d2);
            } else if (selectedSort.equals("Due date descending")) {
                String d1 = t1.getDueDate() != null ? t1.getDueDate() : "";
                String d2 = t2.getDueDate() != null ? t2.getDueDate() : "";
                return d2.compareTo(d1);
            } else if (selectedSort.equals("Description")) {
                return t1.getDescription().compareTo(t2.getDescription());
            }
            return 0;
        });

        adapter.updateTasks(filteredTasks);
        updateEmptyState();
    }

    private void updateEmptyState() {
        int totalTasks = project != null && project.getTasks() != null ? project.getTasks().size() : 0;
        tvShowingTasksCount.setText("Showing " + filteredTasks.size() + " of " + totalTasks + " tasks");

        if (totalTasks == 0) {
            lvTasks.setVisibility(View.GONE);
            layoutEmptyTasks.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("No tasks yet");
            btnEmptyAction.setText("+ Add Task");
        } else if (filteredTasks.isEmpty()) {
            lvTasks.setVisibility(View.GONE);
            layoutEmptyTasks.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("No tasks match these filters");
            btnEmptyAction.setText("Reset Filters");
        } else {
            lvTasks.setVisibility(View.VISIBLE);
            layoutEmptyTasks.setVisibility(View.GONE);
        }
    }

    public void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task_full, null);
        EditText etDesc = dialogView.findViewById(R.id.etTaskDescFull);
        TextView tvCounter = dialogView.findViewById(R.id.tvCharCounter);
        Spinner spinnerStatusDialog = dialogView.findViewById(R.id.spinnerDialogStatus);
        Spinner spinnerPriorityDialog = dialogView.findViewById(R.id.spinnerDialogPriority);
        Button btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);
        Button btnClearDate = dialogView.findViewById(R.id.btnClearDate);

        final String[] dueDateHolder = {""};

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"To do", "In progress", "Done"});
        spinnerStatusDialog.setAdapter(statusAdapter);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Normal", "High", "Low"});
        spinnerPriorityDialog.setAdapter(priorityAdapter);
        spinnerPriorityDialog.setSelection(0); // Normal default

        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                tvCounter.setText(len + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                dueDateHolder[0] = dateStr;
                btnSelectDate.setText(dateStr);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnClearDate.setOnClickListener(v -> {
            dueDateHolder[0] = "";
            btnSelectDate.setText("Select due date");
        });

        builder.setView(dialogView)
                .setTitle("Add New Task")
                .setPositiveButton("Add", (dialog, which) -> {
                    String desc = etDesc.getText().toString().trim();
                    if (TextUtils.isEmpty(desc) || desc.length() > 500) {
                        Toast.makeText(getContext(), "Task description must be 1-500 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String status = spinnerStatusDialog.getSelectedItem().toString();
                    String priority = spinnerPriorityDialog.getSelectedItem().toString();
                    String dueDate = dueDateHolder[0];

                    String taskId = "t_" + System.currentTimeMillis();
                    Task newTask = new Task(taskId, desc, status, priority, dueDate);
                    project.getTasks().add(newTask);

                    applyFiltersAndSort();
                    Toast.makeText(getContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void updateData(Project updatedProject) {
        this.project = updatedProject;
        applyFiltersAndSort();
    }

    private void showEditTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task_full, null);
        EditText etDesc = dialogView.findViewById(R.id.etTaskDescFull);
        TextView tvCounter = dialogView.findViewById(R.id.tvCharCounter);
        Spinner spinnerStatusDialog = dialogView.findViewById(R.id.spinnerDialogStatus);
        Spinner spinnerPriorityDialog = dialogView.findViewById(R.id.spinnerDialogPriority);
        Button btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);
        Button btnClearDate = dialogView.findViewById(R.id.btnClearDate);

        etDesc.setText(task.getDescription());
        tvCounter.setText(task.getDescription().length() + "/500");

        final String[] dueDateHolder = {task.getDueDate()};
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            btnSelectDate.setText(task.getDueDate());
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"To do", "In progress", "Done"});
        spinnerStatusDialog.setAdapter(statusAdapter);
        if ("Done".equalsIgnoreCase(task.getStatus())) spinnerStatusDialog.setSelection(2);
        else if ("In progress".equalsIgnoreCase(task.getStatus())) spinnerStatusDialog.setSelection(1);
        else spinnerStatusDialog.setSelection(0);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Normal", "High", "Low"});
        spinnerPriorityDialog.setAdapter(priorityAdapter);
        if ("High".equalsIgnoreCase(task.getPriority())) spinnerPriorityDialog.setSelection(1);
        else if ("Low".equalsIgnoreCase(task.getPriority())) spinnerPriorityDialog.setSelection(2);
        else spinnerPriorityDialog.setSelection(0);

        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                tvCounter.setText(len + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                dueDateHolder[0] = dateStr;
                btnSelectDate.setText(dateStr);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnClearDate.setOnClickListener(v -> {
            dueDateHolder[0] = "";
            btnSelectDate.setText("Select due date");
        });

        builder.setView(dialogView)
                .setTitle("Edit / Delete Task")
                .setPositiveButton("Save", (dialog, which) -> {
                    String desc = etDesc.getText().toString().trim();
                    if (TextUtils.isEmpty(desc) || desc.length() > 500) {
                        Toast.makeText(requireContext(), "Task description must be 1-500 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    task.setDescription(desc);
                    task.setStatus(spinnerStatusDialog.getSelectedItem().toString());
                    task.setPriority(spinnerPriorityDialog.getSelectedItem().toString());
                    task.setDueDate(dueDateHolder[0]);

                    applyFiltersAndSort();
                    Toast.makeText(requireContext(), "Task updated", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Delete", (dialog, which) -> {
                    project.getTasks().remove(task);
                    applyFiltersAndSort();
                    Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void applyExternalFilters(String statusFilter, String dateFilter) {
        if (statusFilter != null && spinnerStatus != null) {
            selectedStatusFilter = statusFilter;
            for (int i = 0; i < spinnerStatus.getCount(); i++) {
                if (spinnerStatus.getItemAtPosition(i).toString().equalsIgnoreCase(statusFilter)) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }
        if (dateFilter != null && spinnerDate != null) {
            selectedDateFilter = dateFilter;
            if ("Overdue".equals(dateFilter)) {
                selectedStatusFilter = "All statuses";
                if (spinnerStatus != null) spinnerStatus.setSelection(0);
            }
            for (int i = 0; i < spinnerDate.getCount(); i++) {
                if (spinnerDate.getItemAtPosition(i).toString().equalsIgnoreCase(dateFilter)) {
                    spinnerDate.setSelection(i);
                    break;
                }
            }
        }
        applyFiltersAndSort();
    }
}
