package pl.tapo24.tapo24.dao.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class InstallationStatus(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @get: NotBlank
    @Column(unique = true)
    val UID: String = "",


    @Column()
    var version_number: String = "",

    @Column()
    var last_start: Long = 0
) {
}