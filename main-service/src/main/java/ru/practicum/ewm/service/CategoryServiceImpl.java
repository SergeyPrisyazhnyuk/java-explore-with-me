package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.dto.mapper.CategoryMapper;
import ru.practicum.ewm.exception.AlreadyExistsException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    CategoryRepository categoryRepository;
    EventRepository eventRepository;

    private Category checkCatId(Long catId) {

        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Not found category with id = " + catId));
    }

    private void checkUniqNameCategory(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new AlreadyExistsException(("Category already exists: " + name));
        }
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return categoryRepository.findAll(pageRequest)
                .stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {

        Category category = checkCatId(catId);

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {

        Category category = CategoryMapper.toCategoryNew(newCategoryDto);
        Category categoryToAdd = categoryRepository.save(category);

        return CategoryMapper.toCategoryDto(categoryToAdd);
    }

    @Override
    public void deleteCategoryById(Long catId) {
        Category category = checkCatId(catId);

        List<Event> events = eventRepository.findByCategory(category);

        if (events.isEmpty()) {
            throw new RuntimeException("Category can't be deleted cause it's being used in other events");
        }

        categoryRepository.deleteById(catId);

    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {

        Category category = checkCatId(catId);

        String categoryNewName = categoryDto.getName();

        if (categoryNewName != null && !category.getName().equals(categoryNewName)) {
            checkUniqNameCategory(categoryNewName);
        }

        category.setName(categoryNewName);
        Category newCategory = categoryRepository.save(category);

        return CategoryMapper.toCategoryDto(newCategory);
    }
}
