package com.spring.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final String FILE_PREFIX = "file:";

	@Value("${file.upload-dir}") // import 시 org.springframework.beans.factory.annotation.Value 로 해야 한다.
	private String uploadDir; // 파일 업로드시 필요한 경로를 잡아주는 것이다. 값은 application.yml 파일에 설정해둔 값을 사용한다는 뜻이다.

	@Value("${file.photoupload-dir}")
    private String photouploadDir; // 글쓰기시 스마트에디터를 통해 사진을 올리는 경로를 잡아주는 것이다.

    @Value("${file.emailattachfile-dir}")
    private String emailattachfileDir; // 이메일 작성시 첨부파일의 경로를 잡아주는 것이다.

    @Value("${file.images-dir}")
    private String imagesDir; // 이메일 작성시 첨부파일의 경로를 잡아주는 것이다.

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/upload/**")
    			.addResourceLocations(FILE_PREFIX + uploadDir + "/");

    	registry.addResourceHandler("/photoupload/**")
    		    .addResourceLocations(FILE_PREFIX + photouploadDir + "/");
    	// 스프링시큐리티 설정파일인 com.spring.app.security.config.SecurityConfig 에서 excludeUri 에 "/photoupload/**" 을 추가해 주어야 한다.

    	registry.addResourceHandler("/emailattachfile/**")
    			.addResourceLocations(FILE_PREFIX + emailattachfileDir + "/");
    	// 스프링시큐리티 설정파일인 com.spring.app.security.config.SecurityConfig 에서 excludeUri 에 "/emailattachfile/**" 을 추가해 주어야 한다.

    	registry.addResourceHandler("/images/**")
        		.addResourceLocations(FILE_PREFIX + imagesDir + "/", "classpath:/static/images/");

    }
}