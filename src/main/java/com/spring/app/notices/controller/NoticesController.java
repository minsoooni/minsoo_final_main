package com.spring.app.notices.controller;

import com.spring.app.admin.domain.AdminNoticeDTO;
import com.spring.app.admin.service.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticesController {

    private final AdminNoticeService noticeAdminService;

    @GetMapping("")
    public String list(@RequestParam(value="search", required=false) String search,
                       @RequestParam(value="page", defaultValue="1") int page,
                       Model model) {
        int limit = 10;
        int totalCount = noticeAdminService.getPublicNoticeCount(search);
        int totalPages = (int) Math.ceil((double) totalCount / limit);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("notices",     noticeAdminService.getPublicNotices(search, page, limit));
        model.addAttribute("totalCount",  totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  totalPages);
        return "notices/notices";
    }

    @GetMapping("/{id}/detail")
    @ResponseBody
    public AdminNoticeDTO detail(@PathVariable("id") Long id) {
        return noticeAdminService.getNoticeDetail(id);
    }
    
    @GetMapping("/popup")
    @ResponseBody
    public AdminNoticeDTO popup() {
        return noticeAdminService.getActivePopup();
    }
}