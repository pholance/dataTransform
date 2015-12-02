package com.yidumen.datatransform.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author 蔡迪旻
 *         2015年12月01日
 */
@Repository
@Transactional
public class LocalRepository {
    private static Logger LOG = LoggerFactory.getLogger("新数据");
    @Autowired
    @Qualifier("localJdbc")
    private JdbcTemplate localJdbc;
    @Autowired
    private ACERepository aceRepository;

    public void transTag() {
        aceRepository.tags().forEach(tag -> localJdbc.update("INSERT INTO yidumen.web_tag(tag_name, hits, type) VALUES (?,?,?)", tag.get("tagname"), tag.get("hits"), tag.get("type")));
        LOG.info("标签迁移完成");
    }

    public void transRecording() {
        aceRepository.recordings().forEach(recording -> localJdbc.update("INSERT INTO yidumen.cms_recording(file_name) VALUES (?)", recording.get("file")));
        LOG.info("来源视频信息迁移完成");
    }

    public void insertVideo() {
        aceRepository.findAceVideos().forEach(data -> {
            //1.把原数据分别存入resource和video
            localJdbc.update("INSERT INTO yidumen.resource(table_name, title, create_date, group_id) VALUES (?,?,?,?)",
                    "resource_video", data.get("title"), data.get("pubDate"), null);
            final Object id = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
            localJdbc.update("INSERT INTO yidumen.resource_video(id, sort, file, pub_date, description, note, grade, duration, shoot_time, status, recommend) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                    id, data.get("sort"), data.get("file"), data.get("pubDate"), data.get("descrpition"), data.get("note"), data.get("grade"), data.get("duration"), data.get("shootTime"), data.get("status"), data.get("remommend"));
            //2.转换ext info
            aceRepository.videoInfos(data.get("id")).forEach(info -> localJdbc.update("INSERT INTO yidumen.resource_video_ext_info(video_id, resolution, width, height, file_size) VALUES (?,?,?,?,?)",
                    id, info.get("resolution"), info.get("width"), info.get("height"), info.get("fileSize")));
            //3.转换剪辑信息
            aceRepository.clipinfos(data.get("id")).forEach(clip -> {
                final Map<String, Object> oldRecording = aceRepository.recording(clip.get("recording_id"));
                final Map<String, Object> newRecording = localJdbc.queryForMap("SELECT * FROM yidumen.cms_recording WHERE file_name=?", oldRecording.get("file"));
                localJdbc.update("INSERT INTO yidumen.resource_video_clip_info(video_id, recording_id, in_time, out_time, start_time, end_time) VALUES (?,?,?,?,?,?)",
                        id, newRecording.get("id"), clip.get("in"), clip.get("out"), clip.get("start"), clip.get("end"));
            });
            //4.转换标签信息
            aceRepository.tags_videos().forEach(tagVideo -> {
                final Map<String, Object> oldTag = aceRepository.tag(tagVideo.get("tags_id"));
                final Map<String, Object> newTag = localJdbc.queryForMap("SELECT * FROM yidumen.web_tag WHERE tag_name=?", oldTag.get("tagname"));
                localJdbc.update("INSERT INTO yidumen.related_video_tag(video_id, tag_id) VALUES (?,?)", id, newTag.get("id"));
            });
            LOG.info("视频 {} 已迁移", data.get("title"));
        });
    }

    public void transSutra() {
        aceRepository.sutras().forEach(sutra -> {
            localJdbc.update("INSERT INTO yidumen.web_sutra(part_identifier, title, left_value, right_value, content) VALUES (?,?,?,?,?)",
                    sutra.get("part_identifier"), sutra.get("title"), sutra.get("left_value"), sutra.get("right_value"), sutra.get("contentzo"));
            final Object id = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
        });
    }

    public void transOther() {
        aceRepository.goodses().forEach(goods -> localJdbc.update("INSERT INTO yidumen.web_goods(name, address, phone_number, post_code, post_number, create_date, status) VALUES (?,?,?,?,?,?,?)",
                goods.get("name"), goods.get("address"), goods.get("phone"), goods.get("postCode"), goods.get("postNumber"), goods.get("createdate"), goods.get("status")));
        aceRepository.accounts().forEach(account -> localJdbc.update("INSERT INTO yidumen.web_account(email, phone, password, nick_name, buddhism_name, real_name, sex, birthday, head_pic, province, city, area, status, create_date, last_login_time, account_group) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                account.get("email"), account.get("phone"), account.get("password"), account.get("nickname"), account.get("buddhismname"), account.get("realname"), account.get("sex"), account.get("born"), account.get("headpic"),
                account.get("province"), account.get("city"), account.get("area"), account.get("status"), account.get("createdate"), account.get("lastlogintime"), account.get("userGroup")));
    }

    public void transWechat() {
        aceRepository.replyRules().forEach(rule -> aceRepository.cmsMsg(rule.get("ID")).forEach(cmsMsg -> {
            if (cmsMsg == null) return;
            final Map<String, Object> message = aceRepository.message(cmsMsg.get("MESSAGE_ID"));
            final Map<String, Object> specialMessage = aceRepository.specialMessage(message.get("ID"), message.get("DTYPE").toString());
            switch (message.get("DTYPE").toString()) {
                case "WECHAT_TEXT": {
                    localJdbc.update("INSERT INTO yidumen.wechat_message(table_name) VALUES ('wechat_message_text')");
                    final Object id = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
                    localJdbc.update("INSERT INTO yidumen.wechat_replymessage(name, type, message_id) VALUES (?,?,?)",
                            cmsMsg.get("NAME"), cmsMsg.get("TYPE"), id);
                    final Object replyMsgid = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
                    aceRepository.msgKey(rule.get("ID")).forEach(msgKey -> localJdbc.update("INSERT INTO yidumen.wechat_replykey(name, keyword, type,reply_message_id) VALUES (?,?,?,?)",
                            rule.get("NAME"), msgKey.get("REPLYKEY"), msgKey.get("TYPE"), replyMsgid));
                    localJdbc.update("INSERT INTO yidumen.wechat_message_text(id, content) VALUES (?,?)", id, specialMessage.get("CONTENT"));
                }
                break;
                case "WECHAT_NEWS": {
                    localJdbc.update("INSERT INTO yidumen.wechat_message(table_name) VALUES ('wechat_message_news')");
                    final Object id = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
                    localJdbc.update("INSERT INTO yidumen.wechat_replymessage(name, type, message_id) VALUES (?,?,?)",
                            cmsMsg.get("NAME"), cmsMsg.get("TYPE"), id);
                    final Object replyMsgid = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
                    aceRepository.msgKey(rule.get("ID")).forEach(msgKey -> localJdbc.update("INSERT INTO yidumen.wechat_replykey(name, keyword, type,reply_message_id) VALUES (?,?,?,?)",
                            rule.get("NAME"), msgKey.get("REPLYKEY"), msgKey.get("TYPE"), replyMsgid));
                    localJdbc.update("INSERT INTO yidumen.wechat_message_news(id) VALUES (?)", id);
                    aceRepository.aritcles(specialMessage.get("ID")).forEach(aritcle -> {
                        localJdbc.update("INSERT INTO yidumen.wechat_message_news_aritcle(seq, title, description, pic_url, link_url) VALUES (?,?,?,?,?)",
                                aritcle.get("SEQ"), aritcle.get("TITLE"), aritcle.get("DESCRIPTION"), aritcle.get("PICURL"), aritcle.get("URL"));
                        final Object aritcleId = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
                        localJdbc.update("INSERT INTO yidumen.related_news_aritcle(news_id, aritcle_id) VALUES (?,?)", id, aritcleId);
                    });
                }
                break;
                case "ActionMsg":
                    localJdbc.update("INSERT INTO yidumen.wechat_message(table_name) VALUES ('wechat_message_action')");
                    final Object id = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
                    localJdbc.update("INSERT INTO yidumen.wechat_replymessage(name, type, message_id) VALUES (?,?,?)",
                            cmsMsg.get("NAME"), cmsMsg.get("TYPE"), id);
                    final Object replyMsgid = localJdbc.queryForMap("SELECT last_insert_id() AS id").get("id");
                    aceRepository.msgKey(rule.get("ID")).forEach(msgKey -> localJdbc.update("INSERT INTO yidumen.wechat_replykey(name, keyword, type,reply_message_id) VALUES (?,?,?,?)",
                            rule.get("NAME"), msgKey.get("REPLYKEY"), msgKey.get("TYPE"), replyMsgid));
                    localJdbc.update("INSERT INTO yidumen.wechat_message_action(class_name, name, id) VALUES (?,?,?)", specialMessage.get("CLASSNAME"), specialMessage.get("NAME"), id);
            }
        }));
    }
}
