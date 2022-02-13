package com.lepse.integration.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pst {

    @JsonProperty("pst_lineno")
    private int lineno;
    @JsonProperty("pst_detitem")
    private String detitem;
    @JsonProperty("pst_noff")
    private float noff;
    @JsonProperty("pst_opno")
    private String opno;

    public Pst() {
    }

    public int getLineno() {
        return lineno;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public String getDetitem() {
        return detitem;
    }

    public void setDetitem(String detitem) {
        this.detitem = detitem;
    }

    public float getNoff() {
        return noff;
    }

    public void setNoff(float noff) {
        this.noff = noff;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    @Override
    public String toString() {
        return "Pst{" +
                "lineno=" + lineno +
                ", detitem='" + detitem.trim() + '\'' +
                ", noff=" + noff +
                ", opno='" + opno + '\'' +
                '}';
    }
}
