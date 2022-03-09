package pl.tapo24.tapo24.dao.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Versions(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @get: NotBlank
    @Column(unique = true)
    val version_number: String = "",

    @Column(unique = false)
    val date_force_update: Long = 0,

    @Column(unique = false)
    val force_update: Boolean = false

) {

}