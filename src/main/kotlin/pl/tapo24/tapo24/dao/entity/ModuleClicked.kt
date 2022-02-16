package pl.tapo24.tapo24.dao.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class ModuleClicked (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @get: NotBlank
    @Column(unique = true)
    var moduleName: String = "",


    var times: Long = 0
)