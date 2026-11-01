package com.example.ass3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ass3.R;
import com.example.ass3.models.Project;

import java.util.List;

public class ProjectAdapter extends BaseAdapter {
    private Context context;
    private List<Project> projects;
    private LayoutInflater inflater;

    public ProjectAdapter(Context context, List<Project> projects) {
        this.context = context;
        this.projects = projects;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateList(List<Project> newProjects) {
        this.projects = newProjects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Object getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_project, parent, false);
            holder = new ViewHolder();
            holder.tvProjectTag = convertView.findViewById(R.id.tvProjectTag);
            holder.tvProjectName = convertView.findViewById(R.id.tvProjectName);
            holder.tvProjectDesc = convertView.findViewById(R.id.tvProjectDesc);
            holder.tvTaskProgressText = convertView.findViewById(R.id.tvTaskProgressText);
            holder.tvProgressPercentage = convertView.findViewById(R.id.tvProgressPercentage);
            holder.progressBar = convertView.findViewById(R.id.progressBar);
            holder.tvAttachmentsCount = convertView.findViewById(R.id.tvAttachmentsCount);
            holder.tvCommentsCount = convertView.findViewById(R.id.tvCommentsCount);
            holder.btnProjectAction = convertView.findViewById(R.id.btnProjectAction);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Project project = projects.get(position);
        holder.tvProjectTag.setText(project.getTag());
        holder.tvProjectName.setText(project.getName());
        holder.tvProjectDesc.setText(project.getDescription());

        int completed = project.getCompletedTasksCount();
        int total = project.getTasks().size();
        int percentage = project.getProgressPercentage();

        holder.tvTaskProgressText.setText(completed + "/" + total + " tasks");
        holder.tvProgressPercentage.setText(percentage + "%");
        holder.progressBar.setProgress(percentage);

        int attachCount = project.getAttachments().size();
        holder.tvAttachmentsCount.setText(attachCount + " Attachments");
        holder.tvCommentsCount.setText(project.getCommentsCount() + " Comments");

        holder.btnProjectAction.setOnClickListener(v -> {
            // Invite action or similar
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvProjectTag;
        TextView tvProjectName;
        TextView tvProjectDesc;
        TextView tvTaskProgressText;
        TextView tvProgressPercentage;
        ProgressBar progressBar;
        TextView tvAttachmentsCount;
        TextView tvCommentsCount;
        Button btnProjectAction;
    }
}
