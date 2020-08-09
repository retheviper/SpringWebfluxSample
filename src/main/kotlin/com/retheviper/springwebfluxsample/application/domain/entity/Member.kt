package com.retheviper.springwebfluxsample.application.domain.entity

import javax.persistence.*

@Entity
class Member {

    /** Primary Key  */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long = 0

    /** Member's id  */
    @Column(nullable = false, unique = true, length = 16)
    var uid: String? = null

    /** Member's name  */
    @Column(nullable = false, length = 16)
    var name: String? = null

    /** Member's password  */
    @Column(nullable = false)
    var password: String? = null
}