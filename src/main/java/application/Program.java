package application;

public class Program {

    public static void main(String[] args) {

//		EntityManager em = JPAUtil.getEntityManager();

//		Product product = new Product("carro eletrico", "movido a energia", "https://", LocalDate.now(), new BigDecimal("100000.00"));
//		Category catgory = new Category("Tech");
//		product.addCategory(catgory);
//
//		Inventory inventory = new Inventory();
//		inventory.setProduct(product);
//		inventory.setQuantity(10);
//		BigDecimal price = product.getPrice().multiply(new BigDecimal(inventory.getQuantity()));
//		inventory.setPrice(price);
//
//		em.getTransaction().begin();
//		em.persist(catgory);
//
//		ProductController repo = new ProductController(em);
//		repo.save(product);
//
//		Product productSaved = repo.findById(1L);
//		productSaved.setName("Testa");
//		repo.update(productSaved);
//		System.out.println(repo.findById(1L));
//
//		CategoryController categoryController = new CategoryController(em);
//
//		Category category = categoryController.findById(1L);
//		System.out.println(category);
//
//		category.setName("Tecnologia");
//		categoryController.save(category);
//		System.out.println(categoryController.findById(1L));
//
//		InventoryController controller = new InventoryController(em);
//		Inventory item = new Inventory(product, 10, "nice", product.getPrice().multiply(new BigDecimal("10")));
//		controller.save(item);
//
//		System.out.println(controller.findById(1L));

//		UserController controller = new UserController(em);
//		User user = new User("ana", "123");
//		user.addPerfil(Perfil.ADMIN);
//		controller.save(user);
//		em.close();
    }

}
