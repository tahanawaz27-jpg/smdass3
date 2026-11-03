package com.example.ass3.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.ass3.R;
import com.example.ass3.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> tasks;
    private LayoutInflater inflater;
    private OnTaskStatusChangedListener listener;

    public interface OnTaskStatusChangedListener {
        void onStatusChanged(Task task, boolean isDone);
    }

    public TaskAdapter(Context context, List<Task> tasks, OnTaskStatusChangedListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();
            holder.cbTaskComplete = convertView.findViewById(R.id.cbTaskComplete);
            holder.tvTaskDesc = convertView.findViewById(R.id.tvTaskDesc);
            holder.tvTaskStatus = convertView.findViewById(R.id.tvTaskStatus);
            holder.tvTaskPriority = convertView.findViewById(R.id.tvTaskPriority);
            holder.tvTaskDueDate = convertView.findViewById(R.id.tvTaskDueDate);
            holder.tvTaskOverdueBadge = convertView.findViewById(R.id.tvTaskOverdueBadge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = tasks.get(position);
        holder.tvTaskDesc.setText(task.getDescription());
        holder.tvTaskStatus.setText(task.getStatus());
        holder.tvTaskPriority.setText(task.getPriority());

        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            holder.tvTaskDueDate.setText("Due: " + task.getDueDate());
            holder.tvTaskDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvTaskDueDate.setText("No due date");
            holder.tvTaskDueDate.setVisibility(View.VISIBLE);
        }

        boolean isDone = "Done".equalsIgnoreCase(task.getStatus());
        holder.cbTaskComplete.setOnCheckedChangeListener(null);
        holder.cbTaskComplete.setChecked(isDone);

        holder.cbTaskComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onStatusChanged(task, isChecked);
            }
        });

        // Check Overdue: due date before today and status not Done
        boolean isOverdue = false;
        if (!isDone && task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date dueDate = sdf.parse(task.getDueDate());
                Date today = new Date();
                if (dueDate != null && dueDate.before(today)) {
                    isOverdue = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (isOverdue) {
            holder.tvTaskOverdueBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvTaskOverdueBadge.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        CheckBox cbTaskComplete;
        TextView tvTaskDesc;
        TextView tvTaskStatus;
        TextView tvTaskPriority;
        TextView tvTaskDueDate;
        TextView tvTaskOverdueBadge;
    }
}
