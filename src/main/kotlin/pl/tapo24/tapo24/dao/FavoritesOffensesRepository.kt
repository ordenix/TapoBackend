package pl.tapo24.tapo24.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface FavoritesOffensesRepository: JpaRepository<FavoritesOffenses,Long> {

    @Query("FROM FavoritesOffenses WHERE UID = :UID")
    fun findUID(@Param("UID") UID: String): FavoritesOffenses?
}