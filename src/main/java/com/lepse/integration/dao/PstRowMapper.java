package com.lepse.integration.dao;

import com.lepse.integration.models.Pst;
import org.springframework.jdbc.core.RowMapper;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PstRowMapper implements RowMapper<Pst> {

    private ResultSet resultSet;

    @Override
    public Pst mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        this.resultSet = resultSet;

        Pst pst = new Pst();
        pst.setLineno(resultSet.getInt("pst_lineno"));
        pst.setDetitem(this.decodeResultSetMember("pst_detitem"));
        pst.setNoff(resultSet.getFloat("pst_noff"));
        pst.setOpno(this.decodeResultSetMember("pst_opno"));
        return pst;
    }

    private String decodeResultSetMember(String memberName) throws SQLException {
        Charset decodingCharset = Charset.forName("ISO-8859-5");
        return new String(this.resultSet.getBytes(memberName), decodingCharset);
    }
}
