package pl.tapo24.tapo24.dao.Repository

import org.springframework.data.jpa.repository.JpaRepository
import pl.tapo24.tapo24.dao.entity.Versions

interface VersionsRepository: JpaRepository<Versions, Long> {


    fun findFirstByOrderByIdDesc(): Versions

}