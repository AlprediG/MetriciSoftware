package com.gmail.merikbest2015.ecommerce.controller;

import com.gmail.merikbest2015.ecommerce.constants.Pages;
import com.gmail.merikbest2015.ecommerce.constants.PathConstants;
import com.gmail.merikbest2015.ecommerce.dto.request.PerfumeRequestPartOne;
import com.gmail.merikbest2015.ecommerce.dto.request.PerfumeRequestPartTwo;
import com.gmail.merikbest2015.ecommerce.dto.request.SearchRequest;
import com.gmail.merikbest2015.ecommerce.dto.response.UserInfoResponse;
import com.gmail.merikbest2015.ecommerce.service.AdminOrdersService;
import com.gmail.merikbest2015.ecommerce.service.AdminPerfumeService;
import com.gmail.merikbest2015.ecommerce.service.AdminService;
import com.gmail.merikbest2015.ecommerce.service.AdminUsersService;
import com.gmail.merikbest2015.ecommerce.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping(PathConstants.ADMIN)
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ControllerUtils controllerUtils;
    private final AdminOrdersService adminOrdersService;
    private final AdminUsersService adminUsersService;
    private final AdminPerfumeService adminPerfumeService;

    @GetMapping("/perfumes")
    public String getPerfumes(Pageable pageable, Model model) {
        controllerUtils.addPagination(model, adminPerfumeService.getPerfumes(pageable));
        return Pages.ADMIN_PERFUMES;
    }

    @GetMapping("/perfumes/search")
    public String searchPerfumes(SearchRequest request, Pageable pageable, Model model) {
        controllerUtils.addPagination(request, model, adminPerfumeService.searchPerfumes(request, pageable));
        return Pages.ADMIN_PERFUMES;
    }

    @GetMapping("/users")
    public String getUsers(Pageable pageable, Model model) {
        controllerUtils.addPagination(model, adminUsersService.getUsers(pageable));
        return Pages.ADMIN_ALL_USERS;
    }

    @GetMapping("/users/search")
    public String searchUsers(SearchRequest request, Pageable pageable, Model model) {
        controllerUtils.addPagination(request, model, adminUsersService.searchUsers(request, pageable));
        return Pages.ADMIN_ALL_USERS;
    }

    @GetMapping("/order/{orderId}")
    public String getOrder(@PathVariable Long orderId, Model model) {
        model.addAttribute("order", adminOrdersService.getOrder(orderId));
        return Pages.ORDER;
    }

    @GetMapping("/orders")
    public String getOrders(Pageable pageable, Model model) {
        controllerUtils.addPagination(model, adminOrdersService.getOrders(pageable));
        return Pages.ORDERS;
    }

    @GetMapping("/orders/search")
    public String searchOrders(SearchRequest request, Pageable pageable, Model model) {
        controllerUtils.addPagination(request, model, adminOrdersService.searchOrders(request, pageable));
        return Pages.ORDERS;
    }

    @GetMapping("/perfume/{perfumeId}")
    public String getPerfume(@PathVariable Long perfumeId, Model model) {
        model.addAttribute("perfume", adminPerfumeService.getPerfumeById(perfumeId));
        return Pages.ADMIN_EDIT_PERFUME;
    }

    @PostMapping("/edit/perfume")
    public String editPerfume(@Valid PerfumeRequestPartOne perfumeRequestPartOne, PerfumeRequestPartTwo perfumeRequestPartTwo, BindingResult bindingResult, Model model,
                              @RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        if (controllerUtils.validateInputFields(bindingResult, model, "perfumeRequestPartOne", perfumeRequestPartOne) && controllerUtils.validateInputFields(bindingResult, model, "perfumeRequestPartTwo", perfumeRequestPartTwo)) {
            return controllerUtils.setAlertFlashMessage(attributes, "/admin/perfumes", adminPerfumeService.editPerfume(perfumeRequestPartOne, perfumeRequestPartTwo, file));
        }
        return  Pages.ADMIN_EDIT_PERFUME;
    }

    @GetMapping("/add/perfume")
    public String addPerfume() {
        return Pages.ADMIN_ADD_PERFUME;
    }

    @PostMapping("/add/perfume")
    public String addPerfume(@Valid PerfumeRequestPartOne perfumeRequestPartOne,PerfumeRequestPartTwo perfumeRequestPartTwo, BindingResult bindingResult, Model model,
                             @RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        if (controllerUtils.validateInputFields(bindingResult, model, "perfumeRequestPartOne", perfumeRequestPartOne) && controllerUtils.validateInputFields(bindingResult, model, "perfumeRequestPartTwo", perfumeRequestPartTwo)) {
            return Pages.ADMIN_ADD_PERFUME;
        }
        return controllerUtils.setAlertFlashMessage(attributes, "/admin/perfumes", adminPerfumeService.addPerfume(perfumeRequestPartOne,perfumeRequestPartTwo, file));
    }

    @GetMapping("/user/{userId}")
    public String getUserById(@PathVariable Long userId, Model model, Pageable pageable) {
        UserInfoResponse userResponse = adminUsersService.getUserById(userId, pageable);
        model.addAttribute("user", userResponse.getUser());
        controllerUtils.addPagination(model, userResponse.getOrders());
        return Pages.ADMIN_USER_DETAIL;
    }
}
