package pl.tapo24.tapo24.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pl.tapo24.tapo24.dao.entity.FavoritesOffenses


interface FavoritesOffensesRepository: JpaRepository<FavoritesOffenses,Long> {

    @Query("FROM FavoritesOffenses WHERE UID = :UID")
    fun findUID(@Param("UID") UID: String): FavoritesOffenses?
}