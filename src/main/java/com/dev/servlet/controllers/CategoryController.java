package com.dev.servlet.controllers;

import java.util.List;

import javax.persistence.EntityManager;

import com.dev.servlet.dao.CategoryDAO;
import com.dev.servlet.domain.Category;
import com.dev.servlet.interfaces.IController;

public final class CategoryController implements IController<Category> {

	private final CategoryDAO categoryDao;

	public CategoryController(EntityManager em) {
		this.categoryDao = new CategoryDAO(em);
	}

	@Override
	public void save(Category category) {
		this.categoryDao.save(category);
	}

	@Override
	public void update(Category category) {
		this.categoryDao.update(category);
	}

	@Override
	public void delete(Category cat) {
		this.categoryDao.delete(cat);
	}

	@Override
	public Category findById(Long id) {
		return this.categoryDao.findById(id);
	}

	@Override
	public List<Category> findAll(Category category) {
		return this.categoryDao.findAll(category);
	}

}
