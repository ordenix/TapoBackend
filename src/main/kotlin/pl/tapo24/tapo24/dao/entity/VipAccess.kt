package pl.tapo24.tapo24.dao.entity

import javax.persistence.*


@Entity
class VipAccess(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = false)
    val uid: String = "",

    @Column(unique = false)
    val skipIntro: Boolean = false,

    @Column(unique = false)
    val accessToCalc: Boolean = false,

) {
}