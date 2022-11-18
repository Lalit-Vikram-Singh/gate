/*
 * Copyright 2022 OpsMx, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opsmx.spinnaker.gate.config;

import com.opsmx.spinnaker.gate.filters.APIKeyAuthFilter;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@Order(1)
public class APISecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${auth.token.principal.header}")
  private String principalRequestHeader;

  @Value("${auth.token.credential.header}")
  private String credentialRequestHeader;

  @Value("${auth.token.credential.value}")
  private String credentialRequestValue;

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader, credentialRequestHeader);
    filter.setRequiresAuthenticationRequestMatcher(new AuthHeaderCheckingRequestMatcher());
    filter.setAuthenticationManager(
        new AuthenticationManager() {

          @Override
          public Authentication authenticate(Authentication authentication)
              throws AuthenticationException {
            String credentials = (String) authentication.getCredentials();
            if (!credentialRequestValue.equals(credentials)) {
              throw new BadCredentialsException(
                  "The API key was not found or not the expected value.");
            }
            authentication.setAuthenticated(true);
            return authentication;
          }
        });
    httpSecurity
        .antMatcher("/dashboardservice/v2/users/user2/dynamicMenu")
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(filter)
        .authorizeRequests()
        .anyRequest()
        .authenticated();
  }

  private class AuthHeaderCheckingRequestMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest request) {
      String header = request.getHeader(principalRequestHeader);
      return header != null;
    }
  }
}
