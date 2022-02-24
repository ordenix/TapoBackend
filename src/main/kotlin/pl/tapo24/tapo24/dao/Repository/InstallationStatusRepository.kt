package pl.tapo24.tapo24.dao.Repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import pl.tapo24.tapo24.dao.entity.InstallationStatus

interface InstallationStatusRepository: JpaRepository<InstallationStatus, Long> {


    @Query("select (count(i) > 0) from InstallationStatus i where i.UID = ?1")
    fun existsByUID(UID: String): Boolean


    @Transactional
    @Modifying
    @Query("update InstallationStatus i set i.last_start = ?1 where i.UID = ?2")
    fun updateLast_startByUID(last_start: Long, UID: String): Int

    @Query("select i from InstallationStatus i where i.UID = ?1")
    fun findByUID(UID: String): InstallationStatus


    @Transactional
    @Modifying
    @Query("update InstallationStatus i set i.version_number = ?1 where i.UID = ?2")
    fun updateVersion_numberByUID(version_number: String, UID: String): Int

}