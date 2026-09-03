package com.complainthub.service;

import com.complainthub.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    List<Category> getActiveCategories();
    Category updateCategory(Category category);
    boolean activateCategory(long id);
    boolean deactivateCategory(long id);
}
