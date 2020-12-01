package ua.gorbatov.library.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ua.gorbatov.library.spring.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserService userService;

    @Qualifier("userDetailsService")
    @Autowired
        private UserDetailsService userDetailsService;

        public SecurityConfig(UserService userService) {
            this.userService = userService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/librarian/**").hasRole("LIBRARIAN")
                    .antMatchers("/user**").hasRole("USER")
                    .antMatchers("/registration","/welcome").permitAll()
                    .and().formLogin().loginPage("/login").successForwardUrl("/welcome")
                    .permitAll()
                    .and()
                    .logout().permitAll();
//                    .and()
//                    .logout().permitAll().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth,
                                    @Qualifier("userDetailsService") UserDetailsService userDetailsService,
                                    BCryptPasswordEncoder passwordEncoder)
                throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

