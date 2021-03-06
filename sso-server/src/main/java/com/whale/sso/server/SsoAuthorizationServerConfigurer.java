package com.whale.sso.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author ljy
 * @version V1.0
 * @Package com.whale
 * @Description: oauth2认证服务器
 * @date 2019/5/24 0024 9:34
 */
@Configuration
@EnableAuthorizationServer //认证服务器
public class SsoAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient("whale1")
                .secret(passwordEncoder().encode("whale1secret"))
                .authorizedGrantTypes("authorization_code","refresh_token")
                .scopes("all")
                .redirectUris("http://127.0.0.1:8080/client1/login")
                .accessTokenValiditySeconds(3600 * 1000)

            .and()
                .withClient("whale2")
                .secret(passwordEncoder().encode("whale2secret"))
                .authorizedGrantTypes("authorization_code","refresh_token")
                .scopes("all")
                .redirectUris("http://127.0.0.1:8060/client2/login")
                .accessTokenValiditySeconds(3600 * 1000);
    }

    /**
     * 生成令牌的配置 ，使用jwt生成
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(jwtTokenStore())
                .accessTokenConverter(jwtAccessTokenConverter());
    }

    /**
     * 认证服务器的安全配置
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        /**
         * 这个是授权表达式
         * 意思是在访问 tokenKey的 时候 需要进行身份认证
         *
         * tokenKey 就是 signingKey 签名秘钥
         *
         */
//        security.allowFormAuthenticationForClients();
//        security.tokenKeyAccess("isAuthenticated()");
//        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");

          security.tokenKeyAccess("isAuthenticated()");
    }

    //jwt token 配置
    //token 的存储
    @Bean
    public TokenStore jwtTokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    //token生成处理
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("whale");
        return  jwtAccessTokenConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();//也可以自定义 只要实现PasswordEncoder接口
    }

}
