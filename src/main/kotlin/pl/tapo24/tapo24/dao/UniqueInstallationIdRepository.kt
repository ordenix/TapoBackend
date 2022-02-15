package pl.tapo24.tapo24.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.tapo24.tapo24.dao.UniqueInstallationId

@Repository
interface UniqueInstallationIdRepository : JpaRepository<UniqueInstallationId, Long> {
    @Query("FROM UniqueInstallationId WHERE UID = :UID")
    fun findUID(@Param("UID") UID: String): UniqueInstallationId?
}