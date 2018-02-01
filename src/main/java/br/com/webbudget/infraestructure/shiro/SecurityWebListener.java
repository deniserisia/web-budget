package br.com.webbudget.infraestructure.shiro;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;

/**
 *
 * @author Arthur Gregorio
 *
 * @since 3.0.0
 * @version 1.0.0, 31/01/2018
 */
@WebListener
public class SecurityWebListener extends EnvironmentLoaderListener {

    @Inject
    private WebSecurityManager webSecurityManager;
    @Inject
    private FilterChainResolver filterChainResolver;
    
    /**
     * 
     * @param event 
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        
        event.getServletContext().setInitParameter(
                ENVIRONMENT_CLASS_PARAM, DefaultWebEnvironment.class.getName());
        
        super.contextInitialized(event);
    }

    /**
     * 
     * @param servletContext
     * @return 
     */
    @Override
    protected WebEnvironment createEnvironment(ServletContext servletContext) {
        
        final DefaultWebEnvironment environment = (DefaultWebEnvironment) 
                super.createEnvironment(servletContext);
        
        environment.setSecurityManager(this.webSecurityManager);
        environment.setFilterChainResolver(this.filterChainResolver);
        
        return environment;
    }
}