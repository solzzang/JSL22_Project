package com.disaster.controller;

<<<<<<< HEAD
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/community")
public class CommunityController {
    
    @GetMapping("")
    public String community() {
        return "community/communityList"; // templates/community/list.html
    }
    
    @GetMapping("/write")
    public String write() {
        return "community/communityWrite"; // templates/community/write.html
    }
    
    @GetMapping("/detail")
    public String communityDetail() {
        return "community/communityDetail"; // templates/community/write.html
    }
}
=======
import com.disaster.domain.CommentDTO;
import com.disaster.domain.CustomUserDetails;
import com.disaster.domain.MemberAddressDTO;
import com.disaster.domain.WriteDTO;
import com.disaster.service.CommentService;
import com.disaster.service.MemberService;
import com.disaster.service.WriteService;
import lombok.RequiredArgsConstructor;

import org.jsoup.Jsoup;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final WriteService writeService;
    private final MemberService memberService;
    private final CommentService commentService;

    
    @GetMapping({"/main", "/communityList"})
    public String communityList(Model model, 
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                // 1. 'searchType' 파라미터를 추가로 받습니다. 기본값은 'all'입니다.
                                @RequestParam(value = "searchType", defaultValue = "all") String searchType,
                                @RequestParam(value = "keyword", required = false) String keyword) {
        
        int pageSize = 5;
        List<WriteDTO> postList;
        int totalPosts;

        // 검색어가 있을 때만 검색 로직을 수행합니다.
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 2. 서비스 메소드에 'searchType'을 함께 전달합니다.
            postList = writeService.searchPosts(searchType, keyword, page, pageSize);
            totalPosts = writeService.getSearchPostCount(searchType, keyword);
            
            // 3. 사용자가 선택한 검색 조건을 View로 다시 보내서 화면에 유지시킵니다.
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchType", searchType); 
        } else {
            // 검색어가 없을 경우 (기존 로직과 동일)
            postList = writeService.getAllPosts(page, pageSize);
            totalPosts = writeService.getPostCount();
            model.addAttribute("searchType", "all"); // 검색 안했을 때 기본값
        }
        
        // 본문 요약 처리
        for (WriteDTO post : postList) {
            String plainText = Jsoup.parse(post.getBody()).text();
            String summary = plainText.length() > 100 ? plainText.substring(0, 100) + "..." : plainText;
            post.setSummary(summary);
        }
        
        int totalPages = (int) Math.ceil((double) totalPosts / pageSize);

        model.addAttribute("postList", postList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "community/communityList";
    }

    // 글 상세 조회
    @GetMapping("/communityDetail/{postId}")
    public String communityDetail(@PathVariable("postId") Long id, Model model) {
        WriteDTO post = writeService.getPostById(id);
        List<CommentDTO> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "community/communityDetail";
    }

    // 글쓰기 폼을 열 때, 현재 로그인한 사용자의 주소 정보를 가져와 지도에 표시하도록 수정합니다.
    @GetMapping("/communityWrite")
    public String writeForm(Model model, Authentication authentication) {
        WriteDTO writeDto = new WriteDTO();

        // 현재 로그인한 사용자의 정보를 가져옵니다.
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long memberId = userDetails.getMemberId();

            // 서비스 계층을 통해 사용자의 기본 주소 정보를 DB에서 조회합니다.
            MemberAddressDTO memberAddress = memberService.findAddressByMemberId(memberId);

            // 주소 정보가 존재하고, 위도/경도 값이 있다면 DTO에 설정합니다.
            if (memberAddress != null && memberAddress.getLat() != null && memberAddress.getLon() != null) {
                // ✅ BigDecimal을 Double로 변환하여 DTO에 설정합니다.
                writeDto.setLat(memberAddress.getLat().doubleValue());
                writeDto.setLon(memberAddress.getLon().doubleValue());
            }
        }

        model.addAttribute("writeDto", writeDto);
        return "community/communityWrite";
    }

    // 글쓰기 저장
    @PostMapping("/communityWrite")
    public String submitPost(@ModelAttribute("writeDto") WriteDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            dto.setMemberId(userDetails.getMemberId());
            dto.setNickname(userDetails.getNickname());

            MemberAddressDTO memberAddress = memberService.findAddressByMemberId(userDetails.getMemberId());

            if (memberAddress != null) {
                dto.setAddressId(memberAddress.getAddressId());
                dto.setMuniCode(memberAddress.getMuniCode());
            } else {
                return "redirect:/member/profile?error=noAddress";
            }
        }

        writeService.createPost(dto);
        return "redirect:/community/main";
    }

    // 썸머노트 이미지 업로드
    @PostMapping("/uploadSummernoteImage")
    @ResponseBody
    public String uploadSummernoteImage(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadFolder = "C:/upload/disaster/";

        File uploadDir = new File(uploadFolder);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File saveFile = new File(uploadFolder, fileName);
        file.transferTo(saveFile);

        // 이미지에 접근할 수 있는 URL 경로를 반환합니다.
        return "/summer/" + fileName;
    }
    
    // 글 삭제
    @GetMapping("/delete/{postId}")
    public String deletePost(@PathVariable("postId") Long id) {
        writeService.deletePost(id);
        return "redirect:/community/main";
    }
    
    // 글 수정 폼
    @GetMapping("/edit/{postId}")
    public String editForm(@PathVariable("postId") Long id, Model model) {
        WriteDTO post = writeService.getPostById(id);
        if (post == null) {
            return "redirect:/community/main";
        }
        model.addAttribute("writeDto", post);
        return "community/communityEdit";
    }

    // 글 수정 처리
    @PostMapping("/edit/{postId}")
    public String updatePost(@PathVariable("postId") Long id,
                             @ModelAttribute("writeDto") WriteDTO dto) {
        dto.setPostId(id);
        writeService.updatePost(dto);
        return "redirect:/community/communityDetail/" + id;
    }
    
}

>>>>>>> refs/remotes/origin/승범
