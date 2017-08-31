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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.TemplateResolver;

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
	public class WebMvcConfig extends WebMvcConfigurerAdapter {

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
	public void registerInternalLayoutResolver(SpringTemplateEngine templateEngine) {
		templateEngine.addTemplateResolver(new InternalLayoutResolver());
	}

	private static class InternalLayoutResolver extends TemplateResolver {

		private static final String ESP_AJAX_TEMPLATE = "esp-templates/ajax_template.html";

		private InternalLayoutResolver() {
			setResourceResolver(new ClassLoaderResourceResolver());
			setResolvablePatterns(singleton(EspAjaxLayoutInterceptor.ESP_AJAX_LAYOUT));
		}

		@Override
		protected String computeResourceName(TemplateProcessingParameters params) {
			return ESP_AJAX_TEMPLATE;
		}
	}
}
