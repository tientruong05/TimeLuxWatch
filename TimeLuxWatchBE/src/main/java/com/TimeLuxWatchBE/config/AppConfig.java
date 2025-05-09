package com.TimeLuxWatchBE.config;

// import org.springframework.beans.factory.annotation.Autowired; // No longer needed here
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.web.servlet.config.annotation.InterceptorRegistry; // No longer needed here
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
public class AppConfig implements WebMvcConfigurer {
    // Remove AuthInterceptor registration as Spring Security handles it now
    /*
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/java5/asm/crud/**", "/java5/asm/profile", 
                                 "/java5/asm/cart", "/java5/asm/cart/update", "/java5/asm/cart/remove/**",
                                 "/java5/asm/cart/checkout", "/java5/asm/checkout", "/java5/asm/cart/complete",
                                 "/java5/asm/statistics/**", "/java5/asm/changePassword", 
                                 "/java5/asm/users/shopping-history", "/java5/asm/users/orders", "/java5/asm/order/detail/**",
                                 "/java5/asm/logout", "/java5/asm/change_pw")
                .excludePathPatterns(
                        "/java5/asm/cart/add/**", 
                        "/java5/asm/cart/count",
                        "/java5/asm/cart/check-stock",
                        "/java5/asm/index", "/java5/asm/list-product", "/java5/asm/forgot-pass", 
                        "/java5/asm/products/detail/**", "/java5/asm/login", "/java5/asm/register", 
                        "/java5/asm/activate", "/java5/asm/account/resetPassword", 
                        "/java5/asm/crud/orders/placeOrder",
                        // Loại trừ tất cả API endpoint không yêu cầu xác thực
                        "/api/**"
                );
    }
    */
}
