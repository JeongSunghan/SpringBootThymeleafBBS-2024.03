package com.example.abbs.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.abbs.entity.Reply;

@Mapper
public interface ReplyDao {

	@Select("select r.*, u.uname from reply r"
			+ " join users u on r.uid=u.uid where r.bid=#{bid}")
	List<Reply> getReplyList(int bid);
	
	@Insert("insert into reply values(default, #{comment}, default, #{uid}, #{bid}, #{isMine})")
	void insertReply(Reply reply);
	
}