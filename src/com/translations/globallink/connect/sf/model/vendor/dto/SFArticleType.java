package com.translations.globallink.connect.sf.model.vendor.dto;

import java.util.List;

public class SFArticleType {

    private String name;
    private String label;

    private List<SFArticleField> fields;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public List<SFArticleField> getFields() {
	return fields;
    }

    public void setFields(List<SFArticleField> fields) {
	this.fields = fields;
    }
}
