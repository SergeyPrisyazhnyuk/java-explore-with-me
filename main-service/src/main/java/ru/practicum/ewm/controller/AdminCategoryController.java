package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Invoked AdminCategoryController.addCategory method");
        return categoryService.addCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategoryById(@PathVariable Long catId) {
        log.info("Invoked AdminCategoryController.deleteCategoryById method with catId = {}", catId);
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Invoked AdminCategoryController.updateCategory method with catId = {}", catId);
        return categoryService.updateCategory(catId, categoryDto);
    }
}
