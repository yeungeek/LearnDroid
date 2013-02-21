package com.renren.android.newsfeed;

import java.util.List;
import java.util.Map;

public class NewsFeedResult {

	/**
	 * ��ʾ�������û���id
	 */
	private int actor_id;
	/**
	 * ��ʾ�����·����ߵ����ͣ�Ŀǰ�С�user������page����user�����������û������£�page��������ҳ�����¡�
	 */
	private String actor_type;
	/**
	 * ��ʾ���������������id��������־id�����id�ͷ���id�ȵ�
	 */
	private int source_id;
	/**
	 * ��ʾ����������
	 */
	private int feed_type;
	/**
	 * ��ʾ�����µ�id
	 */
	private int post_id;
	/**
	 * ��ʾ�������û���ͷ��
	 */
	private String head_url;
	/**
	 * ��ʾ�������û��Զ����������ݣ�״̬
	 */
	private String message;
	/**
	 * ��ʾ�����µ���������
	 */
	private String title;
	/**
	 * ��ʾ�����¸���ʱ��
	 */
	private String update_time;
	/**
	 * ��ʾ��Ƭ��Դ������
	 */
	private String source_text;
	/**
	 * ��ʾ��Ƭ��Դ��url
	 */
	private String source_href;
	/**
	 * ��ʾ���к��ѵ�����
	 */
	private int likes_friend_count;
	/**
	 * ��ʾ��ǰ�û��Ƿ��޲���
	 */
	private int likes_user_like;
	/**
	 * ��ʾ�޵�������
	 */
	private int likes_total_count;
	/**
	 * ��ʾ�������û�������
	 */
	private String name;
	/**
	 * ��ʾ���������ݵ�ǰ׺
	 */
	private String prefix;
	/**
	 * ��ʾ���۵����� comments�ӽڵ�
	 */
	private int comments_count;
	/**
	 * ��ʾ�������а������������ݣ�Ŀǰ�������º���������� Map��Key˵���� uid: ��ʾ���������û���id comment�ӽڵ� name:
	 * ��ʾ�������۵��û����� comment�ӽڵ� headurl: ��ʾ�������۵��û�ͷ�� comment�ӽڵ� time: ��ʾ���۵�ʱ��
	 * comment�ӽڵ� comment_id: ��ʾ���۵�id comment�ӽڵ� text: ��ʾ���۵����� comment�ӽڵ�
	 */
	private List<Map<String, Object>> comments;
	/**
	 * ��ʾý�����ݵ�����
	 */
	private int attachment_count;
	/**
	 * ��ʾý���ı�������ݣ����磺media_typeΪ��status������״̬�����ݣ�media_typeΪ��photo��������Ƭ��������Ϣ��
	 */
	private String attachment_content;
	/**
	 * ��ʾý�����ݵ����ͣ�Ŀǰ�С�photo��, ��album��, ��link��, ��video��, ��audio��, ��status��
	 * feed_media�ӽڵ�
	 */
	private String attachment_media_type;
	/**
	 * ��ʾý�����ݵ����������� feed_media�ӽڵ�
	 */
	private String attachment_owner_name;
	/**
	 * ��ʾý�����ݵ�id��������Ƭ��id feed_media�ӽڵ�
	 */
	private int attachment_media_id;
	/**
	 * ��ʾý�����ݵ�������id feed_media�ӽڵ�
	 */
	private int attachment_owner_id;
	/**
	 * media_typeΪ��photo��ʱ���У�����δ�ӹ�����ԭͼURL��
	 */
	private String attachment_raw_src;
	/**
	 * ��ʾý�����ݵ�ԭ��ַ feed_media�ӽڵ�
	 */
	private String attachment_src;
	/**
	 * ��ʾý�����ݵ����ӵ�ַ feed_media�ӽڵ�
	 */
	private String attachment_href;
	/**
	 * media_typeΪ��album��ʱ���У������������Ƭ��������
	 */
	private int attachment_photo_count;
	/**
	 * ׷�ٹ���������� Map��Key˵��:id ������û�Id name ������û�����
	 */
	private List<Map<String, Object>> trace_node;
	/**
	 * ׷�ٹ��������˵���������
	 */
	private String trace_text;
	/**
	 * ��ʾ�����µľ�������
	 */
	private String description;

	public int getActor_id() {
		return actor_id;
	}

	public void setActor_id(int actor_id) {
		this.actor_id = actor_id;
	}

	public String getActor_type() {
		return actor_type;
	}

	public void setActor_type(String actor_type) {
		this.actor_type = actor_type;
	}

	public int getSource_id() {
		return source_id;
	}

	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}

	public int getFeed_type() {
		return feed_type;
	}

	public void setFeed_type(int feed_type) {
		this.feed_type = feed_type;
	}

	public int getPost_id() {
		return post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public String getHead_url() {
		return head_url;
	}

	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getSource_text() {
		return source_text;
	}

	public void setSource_text(String source_text) {
		this.source_text = source_text;
	}

	public String getSource_href() {
		return source_href;
	}

	public void setSource_href(String source_href) {
		this.source_href = source_href;
	}

	public int getLikes_friend_count() {
		return likes_friend_count;
	}

	public void setLikes_friend_count(int likes_friend_count) {
		this.likes_friend_count = likes_friend_count;
	}

	public int getLikes_user_like() {
		return likes_user_like;
	}

	public void setLikes_user_like(int likes_user_like) {
		this.likes_user_like = likes_user_like;
	}

	public int getLikes_total_count() {
		return likes_total_count;
	}

	public void setLikes_total_count(int likes_total_count) {
		this.likes_total_count = likes_total_count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getComments_count() {
		return comments_count;
	}

	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}

	public List<Map<String, Object>> getComments() {
		return comments;
	}

	public void setComments(List<Map<String, Object>> comments) {
		this.comments = comments;
	}

	public int getAttachment_count() {
		return attachment_count;
	}

	public void setAttachment_count(int attachment_count) {
		this.attachment_count = attachment_count;
	}

	public String getAttachment_content() {
		return attachment_content;
	}

	public void setAttachment_content(String attachment_content) {
		this.attachment_content = attachment_content;
	}

	public String getAttachment_media_type() {
		return attachment_media_type;
	}

	public void setAttachment_media_type(String attachment_media_type) {
		this.attachment_media_type = attachment_media_type;
	}

	public String getAttachment_owner_name() {
		return attachment_owner_name;
	}

	public void setAttachment_owner_name(String attachment_owner_name) {
		this.attachment_owner_name = attachment_owner_name;
	}

	public int getAttachment_media_id() {
		return attachment_media_id;
	}

	public void setAttachment_media_id(int attachment_media_id) {
		this.attachment_media_id = attachment_media_id;
	}

	public int getAttachment_owner_id() {
		return attachment_owner_id;
	}

	public void setAttachment_owner_id(int attachment_owner_id) {
		this.attachment_owner_id = attachment_owner_id;
	}

	public String getAttachment_raw_src() {
		return attachment_raw_src;
	}

	public void setAttachment_raw_src(String attachment_raw_src) {
		this.attachment_raw_src = attachment_raw_src;
	}

	public String getAttachment_src() {
		return attachment_src;
	}

	public void setAttachment_src(String attachment_src) {
		this.attachment_src = attachment_src;
	}

	public String getAttachment_href() {
		return attachment_href;
	}

	public void setAttachment_href(String attachment_href) {
		this.attachment_href = attachment_href;
	}

	public int getAttachment_photo_count() {
		return attachment_photo_count;
	}

	public void setAttachment_photo_count(int attachment_photo_count) {
		this.attachment_photo_count = attachment_photo_count;
	}

	public List<Map<String, Object>> getTrace_node() {
		return trace_node;
	}

	public void setTrace_node(List<Map<String, Object>> trace_node) {
		this.trace_node = trace_node;
	}

	public String getTrace_text() {
		return trace_text;
	}

	public void setTrace_text(String trace_text) {
		this.trace_text = trace_text;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
