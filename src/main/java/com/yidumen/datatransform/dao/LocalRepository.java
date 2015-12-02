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
public class LocalRepository {
    @Autowired
    @Qualifier("localJdbc")
    private JdbcTemplate localJdbc;

    public void insertVideo(List<Map<String, Object>> datas) {
        datas.forEach(data -> {
            localJdbc.update("INSERT INTO yidumen.resource_video(id, sort, file, pub_date, description, note, grade, duration, shoot_time, status, recommend) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                    data.get("id"),data.get("sort"),data.get("file"),data.get("pubDate"),data.get("descrpition"),data.get("note"),data.get("grade"),data.get("duration"),data.get("shootTime"),data.get("status"),data.get("remommend"));
        });
    }
}
