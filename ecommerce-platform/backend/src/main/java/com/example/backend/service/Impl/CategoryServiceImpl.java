package com.example.backend.service.Impl;

import com.example.backend.dto.CategoryRequest;
import com.example.backend.dto.CategoryResponse;
import com.example.backend.entity.Category;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.service.CategoryService;
import com.example.backend.dto.ProductResponse;
import com.example.backend.repository.ProductRepository;
import com.example.backend.dto.MessageResponse;
import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new RuntimeException("Category already exists.");
        }
        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
            category.setParent(parent);
        }
        categoryRepository.save(category);

        return mapToResponse(category, "Category created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToResponse(category , "Category found successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAll();
        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());

        return rootCategories.stream()
                .map(c -> mapToResponse(c, ""))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
                throw new RuntimeException("Category name already in use.");
            }
            category.setName(newName);
        }

        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new RuntimeException("Category cannot be its own parent.");
            }
            Category newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));

            Category cur = newParent;
            while (cur != null) {
                if (cur.getId().equals(category.getId())) {
                    throw new RuntimeException("Circular reference in categories.");
                }
                cur = cur.getParent();
            }
            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        categoryRepository.save(category);
        return mapToResponse(category, "Category updated successfully");
    }

    @Override
    public MessageResponse deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            throw new RuntimeException("Cannot delete category with subcategories.");
        }

        if (productRepository.existsByCategory(category)) {
            throw new RuntimeException("Cannot delete category with linked products.");
        }

        categoryRepository.deleteById(id);
        return new MessageResponse("Category deleted successfully");
    }

    private CategoryResponse mapToResponse(Category c, String message) {
        Long parentId = c.getParent() != null ? c.getParent().getId() : null;
        String parentName = c.getParent() != null ? c.getParent().getName() : null;

        List<CategoryResponse> subCat = (c.getSubCategories() == null ? Collections.emptyList() : c.getSubCategories())
                .stream()
                .map(sub -> mapToResponse((Category) sub, ""))
                .collect(Collectors.toList());

        List<ProductResponse> products = c.getProducts() != null
                ? c.getProducts().stream()
                .map(p -> ProductResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .stock(p.getStock())
                        .active(p.getActive())
                        .imageUrl(p.getImageUrl())
                        .categoryId(c.getId())
                        .categoryName(c.getName())
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();

        return CategoryResponse.builder()
                .message(message)
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .parentId(parentId)
                .parentName(parentName)
                .subCategories(subCat)
                .products(products)
                .build();
    }
}
