package pl.tapo24.tapo24

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages =  ["pl.tapo24.tapo24.controller"] )
@EnableJpaRepositories(basePackages = ["pl.tapo24.tapo24.dao"])
class Tapo24Application

fun main(args: Array<String>) {
	runApplication<Tapo24Application>(*args)
}
