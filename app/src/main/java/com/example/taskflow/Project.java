package com.example.taskflow;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable {
    private String tag;
    private String name;
    private String description;
    private ArrayList<String> attachments;
    private ArrayList<Task> tasks;

    public Project(String tag, String name, String description,
                    ArrayList<String> attachments, ArrayList<Task> tasks) {
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.attachments = attachments != null ? attachments : new ArrayList<String>();
        this.tasks = tasks != null ? tasks : new ArrayList<Task>();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<String> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public int getTotalTaskCount() {
        return tasks.size();
    }

    public int getCompletedTaskCount() {
        int count = 0;
        for (Task t : tasks) {
            if (t.isDone()) count++;
        }
        return count;
    }
}
