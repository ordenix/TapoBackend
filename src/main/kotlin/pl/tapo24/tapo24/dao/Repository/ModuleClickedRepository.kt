package pl.tapo24.tapo24.dao.Repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import pl.tapo24.tapo24.dao.entity.ModuleClicked

interface ModuleClickedRepository: JpaRepository<ModuleClicked, Long> {

    @Query("select (count(m) > 0) from ModuleClicked m where m.moduleName = ?1")
    fun existsByModuleName(moduleName: String): Boolean


    @Transactional
    @Modifying
    @Query("update ModuleClicked m set m.times = ?1 where m.moduleName = ?2")
    fun updateTimesByModuleNameIs(times: Long, moduleName: String)


    @Query("select m from ModuleClicked m where m.moduleName = ?1")
    fun findByModuleNameIs(moduleName: String): ModuleClicked


}