package com.GaoQue.controller;

import com.GaoQue.dto.UserDto;
import com.GaoQue.exceptions.AlreadyExistsException;
import com.GaoQue.model.User;
import com.GaoQue.request.CreateUserRequest;
import com.GaoQue.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model,
                        HttpSession session) {
        if (error != null) {
            model.addAttribute("error", "Tài khoản hoặc mật khẩu không đúng!!");
        }

        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công.");
        }

        // Kiểm tra xem người dùng đã đăng nhập chưa
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Lấy thông tin người dùng (email hoặc userId từ Spring Security)
            String username = authentication.getName();

            // Tìm người dùng từ database hoặc thông tin khác
            User user = userService.findByUser(username);

            if (user != null) {
                // Lưu userId vào session sau khi người dùng đăng nhập thành công
                session.setAttribute("userId", user.getId());
            }
        }
        return "User/login"; // Trả về trang đăng nhập
    }


    @GetMapping("/register")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "/User/register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") @Valid CreateUserRequest request,
                             BindingResult bindingResult,@RequestParam("confirmPassword") String confirmPassword,
                             Model model) {

        if (!request.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp!!");
            return "/User/register";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Nhập dữ liệu không đúng. Bạn hãy nhập lại!!");
            return "/User/register";
        }

        try {
            User user = userService.createUser(request);
            UserDto userDto = userService.convertUserToDto(user);
            model.addAttribute("message", "Tại tài khoản Gạo Quê thành công!.");
            model.addAttribute("user", userDto);
            return "/User/login";
        } catch (AlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "/User/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa toàn bộ session, bao gồm userId
        return "redirect:/login?logout=true"; // Chuyển hướng đến trang đăng nhập và hiển thị thông báo đăng xuất thành công
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUser(username);
        model.addAttribute("user", user);
        return "/User/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUser(username);
        model.addAttribute("user", user);
        return "User/profileEdit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("email") String email,
                                @RequestParam("address") String address,
                                @RequestParam("phoneNumber") String phoneNumber,
                                RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Tên người dùng (email)
        User user = userService.findByUser(username);
        userService.updateUser(user.getId(), firstName, lastName, email, address, phoneNumber);
        redirectAttributes.addFlashAttribute("message", "Thông tin của bạn đã được cập nhật thành công!");
        return "redirect:/profile";
    }

}
