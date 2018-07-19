package br.com.gabryel.logineer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class LogineerApplication {
    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            SpringApplication.run(LogineerApplication::class.java, *args)
        }
    }
}
