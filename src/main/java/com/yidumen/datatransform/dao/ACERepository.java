package com.yidumen.datatransform.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 蔡迪旻
 *         2015年12月01日
 */
@Repository
@Transactional(readOnly = true)
public class ACERepository {
    @Autowired
    @Qualifier("aceJdbc")
    private JdbcTemplate aceJdbc;

    public List<Map<String, Object>> findAceVideos() {
        return aceJdbc.queryForList("SELECT * FROM video");
    }

    public List<Map<String, Object>> videoInfos(Object videoId) {
        return aceJdbc.queryForList("SELECT * FROM videoinfo WHERE video_id=?", videoId);
    }

    public List<Map<String, Object>> recordings() {
        return aceJdbc.queryForList("SELECT * FROM recording");
    }

    public Map<String, Object> recording(Object id) {
        return aceJdbc.queryForMap("SELECT * FROM recording WHERE id=?", id);
    }

    public List<Map<String, Object>> clipinfos(Object videoId) {
        return aceJdbc.queryForList("SELECT * FROM video_recording WHERE video_id=?", videoId);
    }

    public List<Map<String, Object>> tags() {
        return aceJdbc.queryForList("SELECT * FROM tag");
    }

    public Map<String, Object> tag(Object id) {
        return aceJdbc.queryForMap("SELECT * FROM tag WHERE id=?", id);
    }

    public List<Map<String, Object>> tags_videos() {
        return aceJdbc.queryForList("SELECT * FROM tag_video");
    }

    public List<Map<String, Object>> sutras() {
        return aceJdbc.queryForList("SELECT * FROM sutra");
    }

    public List<Map<String, Object>> goodses() {
        return aceJdbc.queryForList("SELECT * FROM goods");
    }

    public List<Map<String, Object>> accounts() {
        return aceJdbc.queryForList("SELECT * FROM account");
    }

    public List<Map<String, Object>> replyRules() {
        return aceJdbc.queryForList("SELECT * FROM replyrule");
    }

    public List<Map<String, Object>> msgKey(Object ruleId) {
        return aceJdbc.queryForList("SELECT * FROM msgkey WHERE RULE_ID=?", ruleId);
    }

    public List<Map<String, Object>> cmsMsg(Object ruleId) {
        final List<Map<String, Object>> result = new ArrayList<>();
        aceJdbc.queryForList("SELECT * FROM cms_msg_replyrule WHERE rule_ID=?", ruleId).forEach(cmsRule -> result.add(aceJdbc.queryForMap("SELECT * FROM cms_msg WHERE ID=?", cmsRule.get("messages_ID"))));
        return result;
    }

    public Map<String, Object> message(Object messageId) {
        return aceJdbc.queryForMap("SELECT * FROM wechat_msg WHERE ID=?", messageId);
    }

    public Map<String, Object> specialMessage(Object id, String table) {
        return aceJdbc.queryForMap("SELECT * FROM " + table + " WHERE ID=?", id);
    }

    public List<Map<String, Object>> aritcles(Object newsId) {
        final List<Map<String, Object>> result = new ArrayList<>();
        aceJdbc.queryForList("SELECT * FROM wechat_news_aritcle WHERE WECHAT_NEWS_ID=?", newsId).forEach(newsAritcle -> result.add(aceJdbc.queryForMap("SELECT * FROM aritcle WHERE ID=?", newsAritcle.get("aritcles_ID"))));
        return result;
    }

}
