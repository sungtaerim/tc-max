package com.lepse.integration.dao;

import com.lepse.integration.models.Imast;
import com.lepse.integration.models.PstModel;
import com.lepse.integrations.dao.StatementEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@Component
public class MaxDAO {

    private final JdbcTemplate jdbcTemplate;
    private final Charset charset = Charset.forName("ISO-8859-5");

    /**
     * Creates a new instance of the MAX tables data access object and embeds dependencies on the JdbcTemplate
     * @param jdbcTemplate JdbcTemplate instance
     */
    @Autowired
    public MaxDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Retrieves data from the IMAST table by item id
     * @param itemId Item id
     * @return returns a product or if no such product exists an empty product
     * */
    public List<Imast> getItem(String itemId) {
        String query = "SELECT * FROM imast WHERE imast_item =  ?";
        StatementEncoder statementEncoder = new StatementEncoder(query, charset);
        PreparedStatementCreator psc = statementEncoder.createEncodedPreparedStatementCreator(Collections.singletonList(itemId));

        List<Imast> items = new ArrayList<>(jdbcTemplate.query(psc, new ImastRowMapper()));
        return !items.isEmpty() ? items : new ArrayList<>();
    }

    /** Retrieves data from the EMOD table by parent item id and date emod_effective
     * @param parentId Parent item id
     * @param effective emod_effective
     * @return returns a product or if no such product exists an empty product
     * */
    public String getEmodModser(String parentId, String effective) {
        String query = "SELECT emod_modser FROM emod WHERE emod_effective = '" + effective + "' AND emod_paritem = '" + parentId + "'";
        try {
            return jdbcTemplate.queryForObject(query, String.class);
        } catch (EmptyResultDataAccessException exception) {
            return "Doc for paritem " + parentId + " on date " + effective + " is not exist";
        }
    }

    /** Retrieves data from the MMSER table
     * @return returns last notification number
     * */
    public String getLastEmodModser() {
        String query = "SELECT mmser_serlast FROM mmser WHERE mmser_sergroup = '_EC'";
        return jdbcTemplate.queryForObject(query, String.class);
    }

    /** Retrieves data from the PST table by parent item id
     * @param parentId Parent item id
     * @return returns list of structure
     * */
    public PstModel getPst(String parentId) {
        PstRowMapper pstRowMapper = new PstRowMapper();
        String query = "SELECT * FROM pst WHERE pst_paritem = ?";
        StatementEncoder statementEncoder = new StatementEncoder(query, Charset.forName("ISO-8859-5"));
        PreparedStatementCreator psc = statementEncoder.createEncodedPreparedStatementCreator(Collections.singletonList(parentId));
        PstModel pstModel = new PstModel();
        pstModel.setItems(jdbcTemplate.query(psc, pstRowMapper));
        return pstModel;
    }
}
