package com.example.ass3.models;

import java.io.Serializable;

public class Attachment implements Serializable {
    private String id;
    private String name;
    private String type; // "Image", "Text"
    private String content; // file size description or text preview content

    public Attachment(String id, String name, String type, String content) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.content = content;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
