package com.example.abbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.abbs.dao.BoardDao;
import com.example.abbs.entity.Board;

@Service
public class BoardServiceImpl implements BoardService {
	@Autowired private BoardDao boardDao;

	@Override
	public Board getBoard(int bid) {
		return boardDao.getBoard(bid);
	}

	@Override
	public int getBoardCount(String field, String query) {
		query = "%" + query + "%";
		return boardDao.getBoardCount(field, query);
	}

	@Override
	public List<Board> getBoardList(int page, String field, String query) {
		int offset = (page - 1) * COUNT_PER_PAGE;
		query = "%" + query + "%";
		return boardDao.getBoardList(field, query, COUNT_PER_PAGE, offset);
	}

	@Override
	public void insertBoard(Board board) {
		boardDao.insertBoard(board);
	}

	@Override
	public void updateBoard(Board board) {
		boardDao.updateBoard(board);
	}

	@Override
	public void deleteBoard(int bid) {
		boardDao.deleteBoard(bid);
	}

	@Override
	public void increaseViewCount(int bid) {
		String field = "viewCount";
		boardDao.increaseCount(field, bid);
	}

	@Override
	public void increaseReplyCount(int bid) {
		String field = "replyCount";
		boardDao.increaseCount(field, bid);
	}

	@Override
	public void increaseLikeCount(int bid) {
		String field = "likeCount";
		boardDao.increaseCount(field, bid);
	}

}
