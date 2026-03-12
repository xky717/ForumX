package io.github.xky717.forumX.event;

import com.alibaba.fastjson2.JSONObject;
import io.github.xky717.forumX.entity.DiscussPost;
import io.github.xky717.forumX.entity.Event;
import io.github.xky717.forumX.entity.Message;
import io.github.xky717.forumX.service.DiscussPostService;
import io.github.xky717.forumX.service.ElasticsearchService;
import io.github.xky717.forumX.service.MessageService;
import io.github.xky717.forumX.util.ForumxConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements ForumxConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("empty message.");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event == null){
            logger.error("message format error.");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String ,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if (!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }
    //消费发帖
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("empty message.");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("message format error.");
            return;
        }

        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);

    }
}
