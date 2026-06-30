package com.examp.springmvc.user.presentation;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;
import com.examp.springmvc.user.application.address.command.AddAddressCommand;
import com.examp.springmvc.user.application.address.command.AddAddressInputPort;
import com.examp.springmvc.user.application.address.command.RemoveAddressInputPort;
import com.examp.springmvc.user.application.address.query.AddressDTO;
import com.examp.springmvc.user.application.address.query.GetUserAddressesInputPort;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users/addresses")
public class UserAddressController {

    private final GetUserAddressesInputPort getUserAddressesUseCase;
    private final AddAddressInputPort addAddressUseCase;
    private final RemoveAddressInputPort removeAddressUseCase;

    public UserAddressController(
            GetUserAddressesInputPort getUserAddressesUseCase,
            AddAddressInputPort addAddressUseCase,
            RemoveAddressInputPort removeAddressUseCase) {
        this.getUserAddressesUseCase = getUserAddressesUseCase;
        this.addAddressUseCase = addAddressUseCase;
        this.removeAddressUseCase = removeAddressUseCase;
    }

    private AuthenticatedUserDTO getLoggedInUser(HttpSession session) {
        Object userObj = session.getAttribute("currentUser");
        if (userObj instanceof AuthenticatedUserDTO) {
            return (AuthenticatedUserDTO) userObj;
        }
        return null;
    }

    @GetMapping
    public String listAddresses(HttpSession session, Model model) {
        AuthenticatedUserDTO loggedInUser = getLoggedInUser(session);
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<AddressDTO> addresses = getUserAddressesUseCase.execute(loggedInUser.getId());
        model.addAttribute("addresses", addresses);
        return "user/address-list";
    }

    @PostMapping("/add")
    public String addAddress(
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("province") String province,
            @RequestParam("district") String district,
            @RequestParam("ward") String ward,
            @RequestParam("streetDetail") String streetDetail,
            @RequestParam(value = "isDefault", required = false, defaultValue = "false") boolean isDefault,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthenticatedUserDTO loggedInUser = getLoggedInUser(session);
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            AddAddressCommand command = new AddAddressCommand(
                    loggedInUser.getId(),
                    receiverName,
                    receiverPhone,
                    province,
                    district,
                    ward,
                    streetDetail,
                    isDefault);
            addAddressUseCase.execute(command);
            redirectAttributes.addFlashAttribute("success", "Thêm địa chỉ thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Preserve form inputs
            redirectAttributes.addFlashAttribute("receiverName", receiverName);
            redirectAttributes.addFlashAttribute("receiverPhone", receiverPhone);
            redirectAttributes.addFlashAttribute("province", province);
            redirectAttributes.addFlashAttribute("district", district);
            redirectAttributes.addFlashAttribute("ward", ward);
            redirectAttributes.addFlashAttribute("streetDetail", streetDetail);
            redirectAttributes.addFlashAttribute("isDefault", isDefault);
        }
        return "redirect:/users/addresses";
    }

    @PostMapping("/delete/{addressId}")
    public String deleteAddress(
            @PathVariable("addressId") Long addressId, HttpSession session, RedirectAttributes redirectAttributes) {
        AuthenticatedUserDTO loggedInUser = getLoggedInUser(session);
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            removeAddressUseCase.execute(loggedInUser.getId(), addressId);
            redirectAttributes.addFlashAttribute("success", "Xóa địa chỉ thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users/addresses";
    }
}
