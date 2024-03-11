package jp.ikigai.kwallet.data.repository

import jp.ikigai.kwallet.data.dao.CategoryDao
import jp.ikigai.kwallet.data.entity.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun saveCategory(category: Category) = categoryDao.upsertCategory(category)

    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    fun getCategory(categoryId: Long) = categoryDao.getCategory(categoryId)

    suspend fun getCategoryById(categoryId: Long) = categoryDao.getCategoryById(categoryId)

    fun getCategoryCount() = categoryDao.getCategoryCount()

    fun getAllCategory() = categoryDao.getCategories()

    fun getAllTransactionsForCategory(
        categoryId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ) = categoryDao.getAllTransactionsForCategory(categoryId, currency, startDate, endDate)
}