package com.example.abbs.service;

import java.util.List;

import com.example.abbs.entity.Reply;

public interface ReplyService {

	List<Reply> getReplyList(int bid);
	
	void insertReply(Reply reply);
	
}