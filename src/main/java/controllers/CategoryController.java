package controllers;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import cache.CacheUtil;
import dao.CategoryDAO;
import entities.Category;

public class CategoryController {

    private final CategoryDAO categoryDAO;

    private final Connection conn;

    public CategoryController(Connection conn) {
        this.conn = conn;
        this.categoryDAO = new CategoryDAO(conn);
    }

    public void save(Category category) {
        this.categoryDAO.save(category);
        CacheUtil.invalidateCategory();
    }

    public void delete(int id) {
        this.categoryDAO.delete(id);
        CacheUtil.invalidateCategory();
    }

    public Category findById(int id) {
    	if(CacheUtil.isValidCategory()) {
			Optional<Category> category = this.list().stream()
					.filter(p -> p.getId() == id)
					.findAny();
			return category.isPresent() ? category.get() : null;
		}
		return this.categoryDAO.findById(id);
    }

    public void update(Category category) {
        this.categoryDAO.update(category);
        CacheUtil.invalidateCategory();
    }

    public List<Category> list() {
    	if (CacheUtil.isValidCategory()) {
			return CacheUtil.getCategoriesFromCache();
		}
		List<Category> list = this.categoryDAO.list();
		CacheUtil.initCategory(list);
        return this.categoryDAO.list();
    }

    public List<Category> listProductByCategory() {
        return this.categoryDAO.listProductByCategory();
    }

}
