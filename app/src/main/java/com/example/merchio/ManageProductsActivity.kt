package com.example.merchio

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.adapters.AdminProductAdapter
import com.example.merchio.data.model.CategoryDto
import com.example.merchio.data.model.ProductDto
import com.example.merchio.data.model.ProductWriteDto
import com.example.merchio.data.repository.ProductRepository
import com.example.merchio.data.repository.StorageRepository
import com.example.merchio.utils.toast
import com.example.merchio.utils.visible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ManageProductsActivity : AppCompatActivity() {
    private val productRepository = ProductRepository()
    private val storageRepository = StorageRepository()
    private lateinit var adapter: AdminProductAdapter
    private var products: List<ProductDto> = emptyList()
    private var categories: List<CategoryDto> = emptyList()
    private var activeImageTarget: EditText? = null
    private var activeNameTarget: EditText? = null

    private val pickProductImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        val target = activeImageTarget ?: return@registerForActivityResult
        val productName = activeNameTarget?.text?.toString()?.trim().orEmpty().ifBlank { "product" }
        lifecycleScope.launch {
            runCatching { storageRepository.uploadProductImage(productName, uri, contentResolver) }
                .onSuccess { target.setText(it); toast("Foto produk berhasil diupload") }
                .onFailure { toast(it.message ?: "Upload foto gagal") }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_products)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        adapter = AdminProductAdapter({ showProductDialog(it) }, { toggleActive(it) })
        findViewById<RecyclerView>(R.id.rv_products_admin).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rv_products_admin).adapter = adapter
        findViewById<FloatingActionButton>(R.id.fab_add_product).setOnClickListener { showProductDialog(null) }
        load()
    }

    override fun onResume() { super.onResume(); if (::adapter.isInitialized) load() }

    private fun load() {
        lifecycleScope.launch {
            runCatching {
                categories = productRepository.categories(includeInactive = true)
                products = productRepository.allProductsForAdmin()
            }.onSuccess {
                adapter.submitList(products)
                findViewById<android.view.View>(R.id.tv_empty_products).visible(products.isEmpty())
            }.onFailure { toast(it.message ?: "Gagal load produk") }
        }
    }

    private fun showProductDialog(product: ProductDto?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_product_editor, null)
        val categorySpinner = view.findViewById<Spinner>(R.id.spinnerCategory)
        val name = view.findViewById<EditText>(R.id.edtProductName)
        val brand = view.findViewById<EditText>(R.id.edtProductBrand)
        val price = view.findViewById<EditText>(R.id.edtProductPrice)
        val stock = view.findViewById<EditText>(R.id.edtProductStock)
        val desc = view.findViewById<EditText>(R.id.edtProductDescription)
        val imageUrl = view.findViewById<EditText>(R.id.edtProductImageUrl)
        val active = view.findViewById<CheckBox>(R.id.cbProductActive)
        val promote = view.findViewById<CheckBox>(R.id.cbProductPromote)
        view.findViewById<android.view.View>(R.id.btnUploadProductImage).setOnClickListener {
            activeImageTarget = imageUrl
            activeNameTarget = name
            pickProductImage.launch("image/*")
        }

        val categoryNames = listOf("No Category") + categories.map { it.name }
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryNames)
        val selectedCategoryIndex = categories.indexOfFirst { it.id == product?.categoryId }.let { if (it >= 0) it + 1 else 0 }
        categorySpinner.setSelection(selectedCategoryIndex)

        product?.let {
            name.setText(it.name)
            brand.setText(it.brand.orEmpty())
            price.setText(it.price.toLong().toString())
            stock.setText(it.stock.toString())
            desc.setText(it.description.orEmpty())
            imageUrl.setText(it.imageUrl.orEmpty())
            active.isChecked = it.isActive
            promote.isChecked = it.isPaidPromote
        } ?: run { active.isChecked = true }

        AlertDialog.Builder(this)
            .setTitle(if (product == null) "Tambah Produk" else "Edit Produk")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ -> saveProduct(product, categorySpinner, name, brand, price, stock, desc, imageUrl, active, promote) }
            .setNegativeButton("Tutup", null)
            .show()
    }

    private fun saveProduct(
        product: ProductDto?,
        categorySpinner: Spinner,
        name: EditText,
        brand: EditText,
        price: EditText,
        stock: EditText,
        desc: EditText,
        imageUrl: EditText,
        active: CheckBox,
        promote: CheckBox
    ) {
        val selectedCategory = categorySpinner.selectedItemPosition.takeIf { it > 0 }?.let { categories[it - 1].id }
        val data = ProductWriteDto(
            categoryId = selectedCategory,
            name = name.text.toString().trim(),
            brand = brand.text.toString().trim().ifBlank { null },
            description = desc.text.toString().trim().ifBlank { null },
            price = price.text.toString().trim().toDoubleOrNull() ?: 0.0,
            stock = stock.text.toString().trim().toIntOrNull() ?: 0,
            imageUrl = imageUrl.text.toString().trim().ifBlank { null },
            isActive = active.isChecked,
            isPaidPromote = promote.isChecked
        )
        if (data.name.isBlank()) { toast("Nama produk wajib diisi"); return }
        lifecycleScope.launch {
            runCatching {
                if (product == null) productRepository.addProduct(data) else productRepository.updateProduct(product.id, data)
            }.onSuccess { toast("Produk tersimpan"); load() }
                .onFailure { toast(it.message ?: "Gagal simpan produk") }
        }
    }

    private fun toggleActive(product: ProductDto) {
        lifecycleScope.launch {
            runCatching { productRepository.setProductActive(product.id, !product.isActive) }
                .onSuccess { toast("Status produk diubah"); load() }
                .onFailure { toast(it.message ?: "Gagal update produk") }
        }
    }
}
