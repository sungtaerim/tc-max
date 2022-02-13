package com.lepse.integration.dao;

import com.lepse.integration.models.Imast;
import org.springframework.jdbc.core.RowMapper;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImastRowMapper implements RowMapper<Imast> {
    private ResultSet resultSet;

    public ImastRowMapper() {
    }

    public Imast mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        this.resultSet = resultSet;
        Imast imastModel = new Imast();
        imastModel.setImastItem(this.decodeResultSetMember("imast_item"));
        imastModel.setImastDesckey(this.decodeResultSetMember("imast_descext"));
        imastModel.setImastStockoum(this.decodeResultSetMember("imast_stockuom"));
        imastModel.setImastEntrydate(resultSet.getDate("imast_entrydate"));
        imastModel.setImastEffclass(this.decodeResultSetMember("imast_effclass"));
        return imastModel;
    }

    private String decodeResultSetMember(String memberName) throws SQLException {
        Charset decodingCharset = Charset.forName("ISO-8859-5");
        return new String(this.resultSet.getBytes(memberName), decodingCharset);
    }
}
