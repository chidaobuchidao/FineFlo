package com.inclusivefinance.controller;

import com.inclusivefinance.security.JwtTokenProvider;
import com.inclusivefinance.security.SecurityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Enterprise (B端) Thymeleaf page controller.
 * Serves SSR-rendered enterprise pages with user context.
 */
@Controller
@RequestMapping("/enterprise")
public class EnterprisePageController {

    private final JwtTokenProvider jwtTokenProvider;

    public EnterprisePageController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /* ============================================================
       Auth pages (public)
       ============================================================ */

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "企业登录 — 普惠金融管理平台");
        model.addAttribute("showParticles", true);
        return "enterprise/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "企业注册 — 普惠金融管理平台");
        model.addAttribute("showParticles", true);
        return "enterprise/register";
    }

    /* ============================================================
       Dashboard pages (auth required)
       ============================================================ */

    @GetMapping("/loans")
    public String loanListPage(HttpServletRequest request, Model model) {
        Map<String, Object> user = extractUser(request);
        if (user == null) return "redirect:/enterprise/login";

        model.addAttribute("pageTitle", "贷款列表 — 普惠金融管理平台");
        model.addAttribute("user", user);
        return "enterprise/loan-list";
    }

    @GetMapping("/loans/apply")
    public String loanApplyPage(HttpServletRequest request, Model model) {
        Map<String, Object> user = extractUser(request);
        if (user == null) return "redirect:/enterprise/login";

        model.addAttribute("pageTitle", "贷款申请 — 普惠金融管理平台");
        model.addAttribute("user", user);
        return "enterprise/loan-apply";
    }

    @GetMapping("/loans/{id}")
    public String loanDetailPage(@PathVariable Long id,
                                 HttpServletRequest request,
                                 Model model) {
        Map<String, Object> user = extractUser(request);
        if (user == null) return "redirect:/enterprise/login";

        model.addAttribute("pageTitle", "贷款详情 — 普惠金融管理平台");
        model.addAttribute("loanId", id);
        model.addAttribute("user", user);
        return "enterprise/loan-detail";
    }

    @GetMapping("/repayments")
    public String repaymentPage(HttpServletRequest request, Model model) {
        Map<String, Object> user = extractUser(request);
        if (user == null) return "redirect:/enterprise/login";

        model.addAttribute("pageTitle", "还款计划 — 普惠金融管理平台");
        model.addAttribute("user", user);
        return "enterprise/repayments";
    }

    @GetMapping("/calculator")
    public String calculatorPage(HttpServletRequest request, Model model) {
        Map<String, Object> user = extractUser(request);
        if (user == null) return "redirect:/enterprise/login";

        model.addAttribute("pageTitle", "贷款计算器 — 普惠金融管理平台");
        model.addAttribute("user", user);
        return "enterprise/calculator";
    }

    @GetMapping("/overdue")
    public String overduePage(HttpServletRequest request, Model model) {
        Map<String, Object> user = extractUser(request);
        if (user == null) return "redirect:/enterprise/login";

        model.addAttribute("pageTitle", "逾期记录 — 普惠金融管理平台");
        model.addAttribute("user", user);
        return "enterprise/overdue";
    }

    /* ============================================================
       Helpers
       ============================================================ */

    private Map<String, Object> extractUser(HttpServletRequest request) {
        Long userId = SecurityUtils.getUserId(request);
        if (userId == null) return null;
        return Map.of(
            "userId", userId,
            "username", SecurityUtils.getUsername(request),
            "role", SecurityUtils.getRole(request),
            "enterpriseId", SecurityUtils.getEnterpriseId(request)
        );
    }
}
