package pl.tapo24.tapo24.dao.Repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pl.tapo24.tapo24.dao.entity.VipAccess

interface VipAccessRepository: JpaRepository<VipAccess, Long> {


    @Query("select v from VipAccess v where v.uid = ?1")
    fun findByUid(uid: String): VipAccess


    fun existsByUid(uid: String): Boolean

}