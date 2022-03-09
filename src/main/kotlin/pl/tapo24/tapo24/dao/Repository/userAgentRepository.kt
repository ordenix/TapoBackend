package pl.tapo24.tapo24.dao.Repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import pl.tapo24.tapo24.dao.entity.userAgent

interface userAgentRepository:JpaRepository <userAgent, Long> {

    @Query("select (count(u) > 0) from userAgent u where u.UID = ?1")
    fun existsByUID(UID: String): Boolean

    @Transactional
    @Modifying
    @Query("update userAgent u set u.user_agent = ?1 where u.UID = ?2")
    fun updateUser_agentByUID(user_agent: String, UID: String): Int


    @Query("select u from userAgent u order by u.id")
    fun findByOrderByIdAsc(): List<userAgent>

}