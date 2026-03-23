package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.jobseeker.domain.OfferReceivedDTO;
import com.spring.app.jobseeker.service.OfferService;

/**
 * 구직자 받은 제안 - 페이지 컨트롤러
 */
@Controller
@RequestMapping("/jobseeker/offer")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }


    // 받은 제안 목록
    // GET /jobseeker/offer/list
    @GetMapping("/list")
    public ModelAndView offerList(ModelAndView mav, Principal principal) {

        mav.addObject("activeMenu", "offer");

        String memberId = principal.getName();

        // === 제안 목록 (DB) === //
        List<OfferReceivedDTO> offers = offerService.getReceivedOffers(memberId);
        mav.addObject("offers", offers);

        // === 상태별 건수 (DB) === //
        Map<String, Object> counts = offerService.getOfferCounts(memberId);
        mav.addObject("unreadCount",   counts.get("unreadCount"));
        mav.addObject("pendingCount",  counts.get("pendingCount"));
        mav.addObject("acceptedCount", counts.get("acceptedCount"));
        mav.addObject("rejectedCount", counts.get("rejectedCount"));
        mav.addObject("expiredCount",  counts.get("expiredCount"));

        mav.setViewName("jobseeker/offer/list");
        return mav;
    }
}
