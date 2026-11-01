package com.example.ass3.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String id;
    private String name;
    private String tag;
    private String description;
    private List<Task> tasks;
    private List<Attachment> attachments;
    private int commentsCount;
    private String createdAt;

    public Project(String id, String name, String tag, String description, int commentsCount) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.tasks = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.commentsCount = commentsCount;
        this.createdAt = "28 May 2022";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getCompletedTasksCount() {
        int count = 0;
        if (tasks != null) {
            for (Task t : tasks) {
                if ("Done".equalsIgnoreCase(t.getStatus())) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getProgressPercentage() {
        if (tasks == null || tasks.isEmpty()) return 0;
        return (getCompletedTasksCount() * 100) / tasks.size();
    }
}
