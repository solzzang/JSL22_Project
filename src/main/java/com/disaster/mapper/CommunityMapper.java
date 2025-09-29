package com.disaster.mapper;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.disaster.domain.CommentDTO;
import com.disaster.domain.CommunityDTO;

@Mapper
public interface CommunityMapper {
	
	//전체 게시글 수
	int getTotalPostCount();
	
	//전체 댓글 수
	int getTotalCommentCount();
	
	// 검색된 게시글 수
	int countSearchedPosts(Map<String, Object> params);
	
	// 게시글 조회 (검색/페이징/정렬 포함)
	List<CommunityDTO> findPostsForAdmin(Map<String, Object> params);
	
	//신고된 댓글만 조회
	List<CommentDTO> findReportedCommentsPaginated(@Param("offset") int offset, @Param("limit") int limit);
	//신고된 댓글 개수
	int countReportedComments();
	
	
	//게시글 소프트 삭제
	void softDeletePostById(Long postId);
	
	//댓글 소프트 삭제
	void softDeleteCommentById(Long commentId);
	
	// 소프트 삭제된 게시물 조회 (페이징 적용)
	List<CommunityDTO> findSoftDeletedPostsPaginated(@Param("offset") int offset, @Param("limit") int limit);
	
	// 소프트 삭제된 게시물 개수
	int countSoftDeletedPosts();
	
	
	// 소프트 삭제된 댓글 조회
    List<CommentDTO> findSoftDeletedCommentsPaginated(@Param("offset") int offset, @Param("limit") int limit); 
    
    // 소프트 삭제된 댓글 개수
    int countSoftDeletedComments();
    
    
    
    // 게시물 영구 삭제
    void hardDeletePostById(Long postId); 
    
    // 댓글 영구 삭제
    void hardDeleteCommentById(Long commentId);
    
    
    
    
	
}
