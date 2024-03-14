package com.example.abbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.abbs.dao.LikeDao;
import com.example.abbs.entity.Like;

@Service
public class LikeServiceImpl implements LikeService{
	@Autowired private LikeDao likeDao;
		
	@Override
	public Like getLike(int bid, String uid) {
		return likeDao.getLike(bid, uid);
	}

	@Override
	public List<Like> getLikeList(int bid) {
		return likeDao.getLikeList(bid);
	}

	@Override
	public void insertLike(Like like) {
		likeDao.insertLike(like);
		
	}

	@Override
	public int toggleLike(Like like) {
		like = likeDao.getLike(like.getBid(), like.getUid());
		int value = like.getValue() == 0 ? 1 : 0 ;				//0 이면 1, 1 이면 0
		like.setValue(value);
		likeDao.updateLike(like);
		return value;
	}

	@Override
	public int getLikeCount(int bid) {
		List<Like> list = likeDao.getLikeList(bid);
		int count = 0;
		for (Like like: list)
			count += like.getValue();
		return count;
	}

	@Override
	public Like getLikeByLid(int lid) {
		return likeDao.getLikeByLid(lid);
	}

}
