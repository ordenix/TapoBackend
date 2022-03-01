package pl.tapo24.tapo24.dao.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class userAgent(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @get: NotBlank
    @Column(unique = false)
    val UID: String = "",

    @get: NotBlank
    @Column(unique = false)
    val user_agent: String = ""
) {
}