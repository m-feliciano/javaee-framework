package servlets.base;

import com.mchange.util.AssertException;

public final class Base {
    public static final String FORWARD_PAGES_FORM_LOGIN_JSP = "forward:pages/formLogin.jsp";
    public static final String FORWARD_PAGES_INVENTORY_FORM_LIST_ITEM_JSP = "forward:pages/inventory/formListItem.jsp";
    public static final String FORWARD_PAGES_INVENTORY_LIST_ITEMS_JSP = "forward:pages/inventory/listItems.jsp";
    public static final String FORWARD_PAGES_INVENTORY_FORM_CREATE_ITEM_JSP = "forward:pages/inventory/formCreateItem.jsp";
    public static final String FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP = "forward:pages/inventory/formUpdateItem.jsp";
    public static final String FORWARD_PAGES_NOT_FOUND_JSP = "forward:pages/not-found.jsp";
    public static final String REDIRECT_INVENTORY_ACTION_LIST_ITEMS = "redirect:inventory?action=list";
    public static final String REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID = "redirect:inventory?action=list&id=";
    public static final String FORWARD_PAGES_PRODUCT_FORM_LIST_PRODUCT_JSP = "forward:pages/product/formListProduct.jsp";
    public static final String FORWARD_PAGES_PRODUCT_LIST_PRODUCTS_JSP = "forward:pages/product/listProducts.jsp";
    public static final String FORWARD_PAGES_PRODUCT_FORM_UPDATE_PRODUCT_JSP = "forward:pages/product/formUpdateProduct.jsp";
    public static final String FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP = "forward:pages/product/formCreateProduct.jsp";
    public static final String REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS = "redirect:product?action=list";
    public static final String REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID = "redirect:product?action=list&id=";
    public static final String FORWARD_PAGES_CATEGORY_FORM_CREATE_CATEGORY_JSP = "forward:pages/category/formCreateCategory.jsp";
    public static final String FORWARD_PAGES_CATEGORY_LIST_CATEGORIES_JSP = "forward:pages/category/listCategories.jsp";
    public static final String FORWARD_PAGES_CATEGORY_FORM_LIST_CATEGORY_JSP = "forward:pages/category/formListCategory.jsp";
    public static final String FORWARD_PAGES_CATEGORY_FORM_UPDATE_CATEGORY_JSP = "forward:pages/category/formUpdateCategory.jsp";
    public static final String REDIRECT_CATEGORY_ACTION_LIST_CATEGORIES = "redirect:category?action=list";
    public static final String REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID = "redirect:category?action=list&id=";
    public static final String FORWARD_PAGES_USER_FORM_CREATE_USER_JSP = "forward:pages/user/formCreateUser.jsp";
    public static final String FORWARD_PAGES_USER_FORM_LIST_USER_JSP = "forward:pages/user/formListUser.jsp";
    public static final String FORWARD_PAGES_USER_FORM_UPDATE_USER_JSP = "forward:pages/user/formUpdateUser.jsp";
    public static final String REDIRECT_PRODUCT_ACTION_CREATE_USER = "redirect:product?action=create";
    public static final String REDIRECT_USER_ACTION_LIST_USER_BY_ID = "redirect:user?action=list&id=";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String CONFIRM_PASSWORD = "confirmPassword";
    public static final String EMAIL = "email";
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CATEGORY = "category";
    public static final String CATEGORIES = "categories";
    public static final String PRODUCT = "product";
    public static final String PRODUCTS = "products";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String PRICE = "price";
    public static final String ITEM = "item";
    public static final String ITEMS = "items";
    public static final String QUANTITY = "quantity";
    public static final String PRODUCT_ID = "productId";

    private Base() {
        throw new AssertException("Base is a utility class and should not be instantiated");
    }
}
