package com.yidumen.datatransform.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 蔡迪旻
 *         2015年12月01日
 */
@Repository
public class ACERepository {
    @Autowired
    @Qualifier("aceJdbc")
    private JdbcTemplate aceJdbc;

    public List<Map<String, Object>> findAceVideos(){
        return aceJdbc.queryForList("SELECT * FROM video");
    }
}
