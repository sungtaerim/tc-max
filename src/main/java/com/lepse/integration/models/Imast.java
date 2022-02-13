package com.lepse.integration.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.text.SimpleDateFormat;
import java.util.Date;

@JsonRootName("item")
public class Imast {

    @JsonProperty("pst_detitem")
    private String imastItem;
    @JsonProperty("imast_descext")
    private String imastDesckey;
    @JsonProperty("imast_stockuom")
    private String imastStockoum;
    @JsonProperty("imast_entrydate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date imastEntrydate;
    @JsonProperty("imast_effclass")
    private String imastEffclass;

    public Imast() {
    }

    public String getImastItem() {
        return this.imastItem;
    }

    public void setImastItem(String imastItem) {
        this.imastItem = imastItem;
    }

    public String getImastDesckey() {
        return this.imastDesckey;
    }

    public void setImastDesckey(String imastDesckey) {
        this.imastDesckey = imastDesckey;
    }

    public String getImastStockoum() {
        return this.imastStockoum;
    }

    public void setImastStockoum(String imastStockoum) {
        this.imastStockoum = imastStockoum;
    }

    public Date getImastEntrydate() {
        return this.imastEntrydate == null ? new java.sql.Date((new Date()).getTime()) : new java.sql.Date(this.imastEntrydate.getTime());
    }

    @Override
    public String toString() {
        return "Imast{" +
                "imastItem='" + imastItem.trim() + '\'' +
                ", imastDesckey='" + imastDesckey.trim() + '\'' +
                ", imastStockoum='" + imastStockoum.trim() + '\'' +
                ", imastEntrydate=" + new SimpleDateFormat("yyyy-MM-dd").format(imastEntrydate) +
                ", imastEffclass='" + imastEffclass.trim() + '\'' +
                '}';
    }
}
