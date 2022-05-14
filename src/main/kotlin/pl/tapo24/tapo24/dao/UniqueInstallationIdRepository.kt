package pl.tapo24.tapo24.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.tapo24.tapo24.dao.entity.UniqueInstallationId

@Repository
interface UniqueInstallationIdRepository : JpaRepository<UniqueInstallationId, Long> {
    @Query("FROM UniqueInstallationId WHERE UID = :UID")
    fun findUID(@Param("UID") UID: String): UniqueInstallationId?


    @Query("select (count(u) > 0) from UniqueInstallationId u where u.UID = ?1")
    fun existsByUIDIs(UID: String): Boolean

}