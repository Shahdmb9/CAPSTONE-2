package org.example.capstone2.service;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.Category;
import org.example.capstone2.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServices {

    private final CategoryRepository categoryRepository;



    // ──CRUD ────────────────────────────────────────────────────

    public void addCategory(Category Category) {
         categoryRepository.save(Category);
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public void updateCategory(Integer id,Category category) {
        Category oldCategory = categoryRepository.findCategoryById(id);
        if(category==null)
            throw new ApiException("Category not found: " + id);
        oldCategory.setName(category.getName());
         categoryRepository.save(oldCategory);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findCategoryById(id);
        if(category==null)
            throw new ApiException("Category not found: " );

        categoryRepository.delete(category);
    }




}
