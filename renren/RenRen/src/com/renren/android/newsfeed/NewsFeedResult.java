package com.renren.android.newsfeed;

import java.util.List;
import java.util.Map;

public class NewsFeedResult {

	/**
	 * 表示新鲜事用户的id
	 */
	private int actor_id;
	/**
	 * 表示新鲜事发起者的类型，目前有“user”，“page”。user代表人人网用户新鲜事，page代表公共主页新鲜事。
	 */
	private String actor_type;
	/**
	 * 表示新鲜事内容主体的id，例如日志id、相册id和分享id等等
	 */
	private int source_id;
	/**
	 * 表示新鲜事类型
	 */
	private int feed_type;
	/**
	 * 表示新鲜事的id
	 */
	private int post_id;
	/**
	 * 表示新鲜事用户的头像
	 */
	private String head_url;
	/**
	 * 表示新鲜事用户自定义输入内容，状态
	 */
	private String message;
	/**
	 * 表示新鲜事的主题内容
	 */
	private String title;
	/**
	 * 表示新鲜事更新时间
	 */
	private String update_time;
	/**
	 * 表示照片来源的名称
	 */
	private String source_text;
	/**
	 * 表示照片来源的url
	 */
	private String source_href;
	/**
	 * 表示赞中好友的数量
	 */
	private int likes_friend_count;
	/**
	 * 表示当前用户是否赞操作
	 */
	private int likes_user_like;
	/**
	 * 表示赞的总数量
	 */
	private int likes_total_count;
	/**
	 * 表示新鲜事用户的姓名
	 */
	private String name;
	/**
	 * 表示新鲜事内容的前缀
	 */
	private String prefix;
	/**
	 * 表示评论的数量 comments子节点
	 */
	private int comments_count;
	/**
	 * 表示新鲜事中包含的评论内容，目前返回最新和最早的评论 Map中Key说明： uid: 表示发表评论用户的id comment子节点 name:
	 * 表示发表评论的用户姓名 comment子节点 headurl: 表示发表评论的用户头像 comment子节点 time: 表示评论的时间
	 * comment子节点 comment_id: 表示评论的id comment子节点 text: 表示评论的内容 comment子节点
	 */
	private List<Map<String, Object>> comments;
	/**
	 * 表示媒体内容的数量
	 */
	private int attachment_count;
	/**
	 * 表示媒体文本相关内容，例如：media_type为“status”代表状态的内容；media_type为“photo”代表照片的描述信息。
	 */
	private String attachment_content;
	/**
	 * 表示媒体内容的类型，目前有“photo”, “album”, “link”, “video”, “audio”, “status”
	 * feed_media子节点
	 */
	private String attachment_media_type;
	/**
	 * 表示媒体内容的所有者姓名 feed_media子节点
	 */
	private String attachment_owner_name;
	/**
	 * 表示媒体内容的id，例如相片的id feed_media子节点
	 */
	private int attachment_media_id;
	/**
	 * 表示媒体内容的所有者id feed_media子节点
	 */
	private int attachment_owner_id;
	/**
	 * media_type为“photo”时特有，代表未加工过的原图URL。
	 */
	private String attachment_raw_src;
	/**
	 * 表示媒体内容的原地址 feed_media子节点
	 */
	private String attachment_src;
	/**
	 * 表示媒体内容的链接地址 feed_media子节点
	 */
	private String attachment_href;
	/**
	 * media_type为“album”时特有，代表相册中相片的数量。
	 */
	private int attachment_photo_count;
	/**
	 * 追踪共享此相册的人 Map中Key说明:id 分享的用户Id name 分享的用户姓名
	 */
	private List<Map<String, Object>> trace_node;
	/**
	 * 追踪共享此相册人的所有内容
	 */
	private String trace_text;
	/**
	 * 表示新鲜事的具体内容
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
