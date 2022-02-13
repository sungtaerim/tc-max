package com.lepse.integration.models;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

@JsonRootName("items")
public class PstModel {

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Pst> items = new ArrayList<>();

    public PstModel() {
    }

    public List<Pst> getItems() {
        return items;
    }

    public void setItems(List<Pst> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PstModel{" +
                "items=" + items.toString() +
                '}';
    }
}
