package com.planelite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Plane Lite 应用启动类
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@SpringBootApplication
public class PlaneApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaneApplication.class, args);
        System.out.println("""

                ============================================
                    Plane Lite 启动成功！
                    Swagger UI: http://localhost:8080/swagger-ui.html
                    API Docs: http://localhost:8080/api/v1/docs
                ============================================
                """);
    }
}
