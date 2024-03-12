package com.example.abbs.service;

import java.util.List;

import com.example.abbs.entity.Like;

public interface LikeService {

	Like getLike(int bid, String uid);
	
	Like getLikeByLid(int lid);
	
	List<Like> getLikeList(int bid);
	
	void insertLike(Like like);
	
	void toggleLike(Like like);		//value가 0이면 1로 바꾸고, 1이면 0으로 바꾸기(좋아요, 좋아요 취소)
	
	int getLikeCount(int bid);		//좋아요가 몇개인지
	
}
