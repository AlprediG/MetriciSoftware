package com.gmail.merikbest2015.ecommerce.controller;

import com.gmail.merikbest2015.ecommerce.domain.Perfume;
import com.gmail.merikbest2015.ecommerce.domain.Role;
import com.gmail.merikbest2015.ecommerce.domain.User;
import com.gmail.merikbest2015.ecommerce.service.PerfumeService;
import com.gmail.merikbest2015.ecommerce.service.UserService;
import com.gmail.merikbest2015.ecommerce.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Value("${upload.path}")
    private String uploadPath;
    private final UserService userService;
    private final PerfumeService perfumeService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("productlist")
    public String getAllProducts(@PageableDefault(sort = {"id"}, size = 12) Pageable pageable, Model model) {
        Page<Perfume> perfumes = perfumeService.getPerfumes(pageable);
        model.addAttribute("pagination", ControllerUtils.computePagination(perfumes));
        model.addAttribute("url", "productlist");
        model.addAttribute("page", perfumes);
        return "admin/productList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("productlist/{perfume}")
    public String editProduct(@PathVariable Perfume perfume, Model model) {
        model.addAttribute("perfume", perfume);
        return "admin/productEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("productlist")
    public String saveEditedProduct(Perfume perfume, @RequestParam("file") MultipartFile file) throws IOException {
        saveFile(perfume, file);
        perfumeService.saveProductInfoById(perfume.getPerfumeTitle(), perfume.getPerfumer(), perfume.getYear(),
                perfume.getCountry(), perfume.getPerfumeGender(), perfume.getFragranceTopNotes(), perfume.getFragranceMiddleNotes(),
                perfume.getFragranceBaseNotes(), perfume.getDescription(), perfume.getFilename(), perfume.getPrice(),
                perfume.getVolume(), perfume.getType(), perfume.getId());
        return "redirect:/user/productlist";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("add")
    public String addProductToBd() {
        return "admin/addToDb";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("add")
    public String addProductToBd(@Valid Perfume perfume, BindingResult bindingResult, Model model,
                                 @RequestParam("file") MultipartFile file) throws IOException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            return "admin/addToDb";
        } else {
            saveFile(perfume, file);
            perfumeService.save(perfume);
        }
        return "admin/addToDb";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "admin/userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSaveEditForm(@RequestParam String username,
                                   @RequestParam Map<String, String> form,
                                   @RequestParam("userId") User user) {
        userService.userSave(username, form, user);
        return "redirect:/user";
    }

    @GetMapping("edit")
    public String getProfileInfo(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        return "user/userEditProfile";
    }

    @PostMapping("edit")
    public String updateProfileInfo(@AuthenticationPrincipal User user,
                                    @RequestParam String password,
                                    @RequestParam String email) {
        userService.updateProfile(user, password, email);
        return "redirect:/cabinet";
    }

    private void saveFile(Perfume perfume, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));
            perfume.setFilename(resultFilename);
        }
    }
}