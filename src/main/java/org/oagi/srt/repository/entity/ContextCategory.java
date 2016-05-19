package org.oagi.srt.repository.entity;

import java.io.Serializable;

public class ContextCategory implements Serializable {

    private int ctxCategoryId;
    private String guid;
    private String name;
    private String description;

    public int getCtxCategoryId() {
        return ctxCategoryId;
    }

    public void setCtxCategoryId(int ctxCategoryId) {
        this.ctxCategoryId = ctxCategoryId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    @Override
    public String toString() {
        return "ContextCategory{" +
                "ctxCategoryId=" + ctxCategoryId +
                ", guid='" + guid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
