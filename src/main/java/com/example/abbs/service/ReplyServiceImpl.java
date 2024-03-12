package com.example.abbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.abbs.dao.ReplyDao;
import com.example.abbs.entity.Reply;

@Service
public class ReplyServiceImpl implements ReplyService {
	@Autowired private ReplyDao replyDao;

	@Override
	public List<Reply> getReplyList(int bid) {
		return replyDao.getReplyList(bid);
	}

	@Override
	public void insertReply(Reply reply) {
		replyDao.insertReply(reply);
	}

}