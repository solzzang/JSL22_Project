<<<<<<< HEAD
package com.disaster.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.disaster.domain.CommentDTO;
import com.disaster.domain.CommunityDTO;
import com.disaster.mapper.CommunityMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
	
	private final CommunityMapper communityMapper;
	
	//전체 게시글 수
	@Transactional(readOnly = true)
	public int getTotalPostCount() {
        return communityMapper.getTotalPostCount();
    }
	
	//전체 댓글 수
	@Transactional(readOnly = true)
    public int getTotalCommentCount() {
        return communityMapper.getTotalCommentCount();
    }
	
	//게시글 조회 (신고된 게시글 우선)
	@Transactional(readOnly = true)
    public List<CommunityDTO> findAllPostsWithReportedFirst() {
        return communityMapper.findAllPostsWithReportedFirst();
    }

	
	//신고된 댓글만 조회
    @Transactional(readOnly = true)
    public List<CommentDTO> findReportedComments() {
        return communityMapper.findReportedComments();
    }
	
	//게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        communityMapper.deletePostById(postId);
    }

	
	//댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        communityMapper.deleteCommentById(commentId);
    }
	
}
=======
package com.disaster.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.disaster.domain.AdminLogDTO;
import com.disaster.domain.CommentDTO;
import com.disaster.domain.CommunityDTO;
import com.disaster.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityMapper communityMapper;
    private final AdminLogService adminLogService;

     
    @Transactional(readOnly = true)
    public int getTotalPostCount() {
        return communityMapper.getTotalPostCount();
    }

     
    @Transactional(readOnly = true)
    public int getTotalCommentCount() {
        return communityMapper.getTotalCommentCount();
    }

     
    
    @Transactional(readOnly = true)
    public List<CommunityDTO> findPostsForAdmin(int page, int pageSize) {
       
        int offset = (page - 1) * pageSize;

      
        Map<String, Object> params = new HashMap<>();
        
        params.put("offset", offset);
        params.put("pageSize", pageSize);

       
        return communityMapper.findPostsForAdmin(params);
    }

   //신고된 댓글 페이징
    @Transactional(readOnly = true)
    public List<CommentDTO> getReportedComments(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.findReportedCommentsPaginated(offset, pageSize);
    }

    
    @Transactional(readOnly = true)
    public int getReportedCommentCount() {
        return communityMapper.countReportedComments();
    }

     
    @Transactional
    public void softDeletePost(Long postId, Long adminId, String reason) {
        communityMapper.softDeletePostById(postId);

        AdminLogDTO log = new AdminLogDTO();
        log.setAdminMemberId(adminId);
        log.setTargetPostId(postId);
        log.setAction("HIDE_POST");
        log.setNote(reason);
        adminLogService.recordLog(log);
    }

     
    @Transactional
    public void softDeleteComment(Long commentId, Long adminId, String reason) {
        communityMapper.softDeleteCommentById(commentId);

        AdminLogDTO log = new AdminLogDTO();
        log.setAdminMemberId(adminId);
        log.setTargetCommentId(commentId);
        log.setAction("HIDE_COMMENT");
        log.setNote(reason);
        adminLogService.recordLog(log);
    }

     
    @Transactional(readOnly = true)
    public List<CommunityDTO> getSoftDeletedPosts(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.findSoftDeletedPostsPaginated(offset, pageSize);
    }

    @Transactional(readOnly = true)
    public int getSoftDeletedPostCount() {
        return communityMapper.countSoftDeletedPosts();
    }

     
    @Transactional(readOnly = true)
    public List<CommentDTO> getSoftDeletedComments(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.findSoftDeletedCommentsPaginated(offset, pageSize);
    }

    @Transactional(readOnly = true)
    public int getSoftDeletedCommentCount() {
        return communityMapper.countSoftDeletedComments();
    }

     
    @Transactional
    public void hardDeletePostById(Long postId) {
       
        communityMapper.hardDeletePostById(postId);
    }

     
    @Transactional
    public void hardDeleteCommentById(Long commentId) {
       
        communityMapper.hardDeleteCommentById(commentId);
    }
    
    
    
    
}
>>>>>>> refs/remotes/origin/승범
