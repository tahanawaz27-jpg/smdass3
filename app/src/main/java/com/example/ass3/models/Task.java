package com.example.ass3.models;

import java.io.Serializable;

public class Task implements Serializable {
    private String id;
    private String description;
    private String status; // "To do", "In progress", "Done"
    private String priority; // "High", "Normal", "Low"
    private String dueDate; // formatted string or empty
    private long createdAt;

    public Task(String id, String description, String status, String priority, String dueDate) {
        this.id = id;
        this.description = description;
        this.status = status != null ? status : "To do";
        this.priority = priority != null ? priority : "Normal";
        this.dueDate = dueDate != null ? dueDate : "";
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
