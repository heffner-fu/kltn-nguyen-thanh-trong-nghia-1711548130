package com.khoaluan.admin.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khoaluan.admin.exporter.product.ProductCsvExporter;
import com.khoaluan.admin.security.ShopmeUserDetails;
import com.khoaluan.admin.service.BrandService;
import com.khoaluan.admin.service.CategoryService;
import com.khoaluan.admin.service.ProductService;
import com.khoaluan.admin.util.FileUploadUtil;
import com.khoaluan.admin.util.Util;
import com.khoaluan.common.exception.ProductNotFoundException;
import com.khoaluan.common.model.Brand;
import com.khoaluan.common.model.Category;
import com.khoaluan.common.model.Product;
import com.khoaluan.common.model.ProductImage;

@Controller
public class ProductController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	
	@Autowired
	ProductService productService;
	
	@Autowired
	BrandService brandService;
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null, 0);
	}
	
	@GetMapping("/products/page/{pageNumber}")
	public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
				@Param("sortField") String sortField, 
				@Param("sortDir") String sortDir, 
				@Param("keyword") String keyword,
				@Param("categoryId") Integer categoryId) {
		Page<Product> page = productService.listByPage(pageNumber, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		List<Category> lisCategories = categoryService.getListCategoriesUsedInForm();
 		long start = (pageNumber - 1) * Util.PRODUCT_PER_PAGE + 1;
		long end = start + Util.PRODUCT_PER_PAGE - 1;
		if(end > page.getTotalElements())
			end = page.getTotalElements();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		if (categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}			
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("totalItem", page.getTotalElements());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("lisCategories", lisCategories);
		model.addAttribute("moduleURL", "/products");
		return "products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes, 
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile, 
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipartFile,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser // To check role for user. For update all or just update price (role: saleperson)
			) throws IOException {
		if (loggedUser.hasRole("Saleperson")) {
			productService.saveProductPrice(product);
			redirectAttributes.addFlashAttribute("message", "The prosduct has been saved successfully");
			return "redirect:/products";
		}
		setMainImageName(product, mainImageMultipartFile);
		setExistingExtraImageName(imageIDs, imageNames, product);
		setNewExtraImageName(product, extraImageMultipartFile);
		setProductDetail(detailIDs, detailNames, detailValues, product);
		Product savedProduct = productService.save(product);
		saveUploadedImage(mainImageMultipartFile, extraImageMultipartFile, savedProduct);
		deleteExtraImageWereRemovedOnForm(product);
		redirectAttributes.addFlashAttribute("message", "The prosduct has been saved successfully");
		return "redirect:/products";
	}
	
	private void deleteExtraImageWereRemovedOnForm(Product product) {
		String extraImage = "../product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(extraImage);
		try {
			Files.list(dirPath).forEach(file -> {
				String fileName = file.toFile().getName();
				if (!product.containsImageName(fileName)) {
					try {
						Files.delete(file);
						LOGGER.info("Deleted extra image: " + fileName);
					} catch (IOException e) {
						LOGGER.error("Could not delete extra image: " + fileName);
					}
				}
			});
		} catch (Exception e) {
			LOGGER.error("Could not list directory: " + dirPath);
		}
	}

	private void setExistingExtraImageName(String[] imageIDs, String[] imageNames, Product product) {
		if (imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		for (int i = 0; i < imageIDs.length; i++) {
			Integer id = Integer.parseInt(imageIDs[i]);
			String name = imageNames[i];
			images.add(new ProductImage(id, name, product));
		}
		product.setImages(images);
	}

	private void setProductDetail(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
		if (detailNames == null || detailNames.length == 0) return;
		for (int i = 0; i < detailNames.length; i++) {
			String name = detailNames[i];
			String value = detailValues[i];
			Integer id = Integer.parseInt(detailIDs[i]);
			if (id != 0) {
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
	}

	private void saveUploadedImage(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipartFile, Product savedProduct) throws IOException {
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId(); 
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
		}
		
		if (extraImageMultipartFile.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				if (multipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);		
			}
		}
	}
		

	private void setNewExtraImageName(Product product, MultipartFile[] extraImageMultipartFile) {
		if (extraImageMultipartFile.length > 0) {
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}	
				}
			}
		}	
	}

	private void setMainImageName(Product product, MultipartFile mainImageMultipartFile) {
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateEnableStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
		productService.updateEnabledStatus(id, status);
		String result = status ? "Enabled" : "Disabled";
		redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been " + result);
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) throws ProductNotFoundException {
		try {
			productService.delete(id);
			String productExtraImageDir = "../product-images/" + id + "/extras";
			String productImageDir = "../product-images/" + id;
			FileUploadUtil.removeDir(productExtraImageDir);
			FileUploadUtil.removeDir(productImageDir);
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been delete successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product ID " + product.getId());
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			
			
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}") 
	public String viewProductDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);
			
			model.addAttribute("product", product);			
			
			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/export/csv") 
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Product> listProduct = productService.getListAll();
		ProductCsvExporter exporter = new ProductCsvExporter();
		exporter.export(listProduct, response);
	}
}
