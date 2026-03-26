package com.spring.app.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration  // Spring 컨테이너가 처리해주는 클래스로서, 클래스내에 하나 이상의 @Bean 메소드를 선언만 해주면 런타임시 해당 빈에 대해 정의되어진 대로 요청을 처리해준다.
@EnableTransactionManagement // 스프링 부트에서 Transaction 처리를 위한 용도
//@MapperScan(basePackages={"com.spring.app.product.model"}, sqlSessionFactoryRef="sqlSessionFactory")
public class Datasource_myspringboot {

	@Value("${mybatis.mapper-locations}")  // *.yml 파일에 있는 설정값을 가지고 온 것으로서 mapper 파일의 위치를 알려주는 것이다.
    private String mapperLocations;
	
 //	@Bean(name = "dataSource")  와  @Bean @Qualifier("dataSource") 은 같은 것이다.  
	@Bean
 	@Qualifier("dataSource")
    @ConfigurationProperties(prefix = "spring.datasource-myspringbootuser") //.yml 파일 안에 정의해둔 datasource
 // @Primary
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }
   /*
     @ConfigurationProperties은 *.properties , *.yml 파일에 있는 property를 자바 클래스에 값을 가져와서 사용할 수 있게 해주는 어노테이션이다. 
     
     @ConfigurationProperties를 사용하기 위해서는 
     build.gradle 에서
     annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor:3.5.3'
     를 추가해야 한다.
   */
	
	
 // @Bean(name = "sqlSessionFactory") 또는 
    @Bean
    @Qualifier("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception{ 
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml")); 
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));

        return sqlSessionFactoryBean.getObject();

    }

 // @Bean(name = "sqlsession") 또는 
    @Bean
    @Qualifier("sqlsession")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    
    @Bean
    public PlatformTransactionManager transactionManager_myspringboot_user() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }
   
}
