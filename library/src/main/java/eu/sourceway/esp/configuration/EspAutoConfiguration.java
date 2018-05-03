package eu.sourceway.esp.configuration;

import eu.sourceway.esp.handler.MultiAjaxViewHandlerMethodReturnValueHandler;
import eu.sourceway.esp.interceptor.EspAjaxLayoutInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static java.util.Collections.singleton;

@Configuration
public class EspAutoConfiguration {

	@Bean
	public EspProperties espProperties() {
		return new EspProperties();
	}

	@Bean
	@ConditionalOnMissingBean(EspAjaxLayoutInterceptor.class)
	public EspAjaxLayoutInterceptor thymeleafLayoutInterceptor(EspProperties espProperties) {
		return new EspAjaxLayoutInterceptor(espProperties);
	}

	@Configuration
	public class WebMvcConfig implements WebMvcConfigurer {

		private final EspAjaxLayoutInterceptor espAjaxLayoutInterceptor;
		private final EspProperties espProperties;

		@Autowired
		public WebMvcConfig(EspAjaxLayoutInterceptor espAjaxLayoutInterceptor, EspProperties espProperties) {
			this.espAjaxLayoutInterceptor = espAjaxLayoutInterceptor;
			this.espProperties = espProperties;
		}

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler(espProperties.getResourcesUrl() + "/**")
					.addResourceLocations("classpath:/esp-static/");
		}

		@Override
		public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
			returnValueHandlers.add(new MultiAjaxViewHandlerMethodReturnValueHandler());
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(espAjaxLayoutInterceptor);
		}
	}

	@Autowired
	public void registerInternalLayoutResolver(
	        ApplicationContext applicationContext,
	        SpringTemplateEngine templateEngine) {
		templateEngine.addTemplateResolver(
		    new InternalLayoutResolver(applicationContext));
	}

	private static class InternalLayoutResolver
	        extends SpringResourceTemplateResolver {

		private static final String ESP_AJAX_TEMPLATE =
		        "classpath:/esp-templates/ajax_template.html";

		private InternalLayoutResolver(ApplicationContext applicationContext) {
			setApplicationContext(applicationContext);
			setResolvablePatterns(
			    singleton(EspAjaxLayoutInterceptor.ESP_AJAX_LAYOUT));
		}

		@Override
		protected String computeResourceName(IEngineConfiguration configuration,
		        String ownerTemplate, String template, String prefix,
		        String suffix, boolean forceSuffix,
		        Map<String, String> templateAliases,
		        Map<String, Object> templateResolutionAttributes) {
			return ESP_AJAX_TEMPLATE;
		}
	}
}
