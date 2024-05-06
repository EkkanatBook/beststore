package com.example.beststore.controllers;

import com.example.beststore.dto.MenuDto;
import com.example.beststore.dto.ProductDto;
import com.example.beststore.models.Menu;
import com.example.beststore.models.Product;
import com.example.beststore.services.MenuRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private MenuRepository repo;

    @GetMapping({"","/"})
    public String showMenuList(Model model){
        List<Menu> menus = repo.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("menus", menus);
        return "menus/index";
    }

    @GetMapping("/createMenu")
    public String showCreatePage(Model model){
        MenuDto menuDto = new MenuDto();
        model.addAttribute("menuDto", menuDto);
        return "menus/CreateMenu";
    }

    @PostMapping("/createMenu")
    public String createMenu(
            @Valid @ModelAttribute MenuDto menuDto,
            BindingResult result
    ){
        if (menuDto.getImageFile().isEmpty()){
            result.addError(new FieldError("menuDto", "imageFile", "กรุณาใส่รูปภาพอาหาร"));
        }

        if (result.hasErrors()) {
            return "menus/CreateMenu";
        }

        // save image
        MultipartFile image = menuDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: "+ ex.getMessage());
        }

        Menu menu = new Menu();
        menu.setName(menuDto.getName());
        menu.setPrice(menuDto.getPrice());
        menu.setCreatedAt(createdAt);
        menu.setImageFileName(storageFileName);

        repo.save(menu);

        return "redirect:/menus";
    }

    @GetMapping("/edit")
    public String showEditMenuPage(
            Model model,
            @RequestParam int id
    ){

        try {
            Menu menu = repo.findById(id).get();
            model.addAttribute("menu", menu);

            MenuDto menuDto = new MenuDto();
            menuDto.setName(menu.getName());
            menuDto.setPrice(menu.getPrice());

            model.addAttribute("menuDto", menuDto);

        } catch (Exception ex){
            System.out.println("Exception: "+ ex.getMessage());
            return "redirect:/menus";
        }

        return "menus/EditMenu";
    }

    @PostMapping("/edit")
    public String updateMenu(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute MenuDto menuDto,
            BindingResult result
    ){
        try {

            Menu menu = repo.findById(id).get();
            model.addAttribute("menu", menu);

            if (result.hasErrors()){
                return "menus/EditMenu";
            }

            if (!menuDto.getImageFile().isEmpty()) {
                // delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + menu.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                // save new image file
                MultipartFile image = menuDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()){
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                menu.setImageFileName(storageFileName);
            }

            menu.setName(menuDto.getName());
            menu.setPrice(menuDto.getPrice());

            repo.save(menu);

        } catch (Exception ex){
            System.out.println("Exception: "+ ex.getMessage());
        }

        return "redirect:/menus";
    }

    @GetMapping("/delete")
    public String deleteMenu(
            @RequestParam int id
    ){
        try {
            Menu menu = repo.findById(id).get();

            // delete menu image
            Path imagePath = Paths.get("public/images/" + menu.getImageFileName());
            try {
                Files.delete(imagePath);
            }catch (Exception ex){
                System.out.println("Exception: "+ ex.getMessage());
            }

            //delete menu
            repo.delete(menu);
        } catch (Exception ex){
            System.out.println("Exception: "+ ex.getMessage());
        }
        return "redirect:/menus";
    }
}
