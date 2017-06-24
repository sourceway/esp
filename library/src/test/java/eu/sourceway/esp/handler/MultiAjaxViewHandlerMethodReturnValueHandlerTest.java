package eu.sourceway.esp.handler;

import eu.sourceway.esp.MultiAjaxView;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MultiAjaxViewHandlerMethodReturnValueHandlerTest {

    private MultiAjaxViewHandlerMethodReturnValueHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new MultiAjaxViewHandlerMethodReturnValueHandler();
    }

    @Test
    public void supportsReturnType() throws Exception {
        Method supportedEndpoint = DummyController.class.getMethod("supportedEndpoint");
        MethodParameter supportedReturnType = new MethodParameter(supportedEndpoint, -1);
        assertThat(handler.supportsReturnType(supportedReturnType)).isTrue();


        Method notSupportedEndpoint = DummyController.class.getMethod("notSupportedEndpoint");
        MethodParameter notSupportedReturnType = new MethodParameter(notSupportedEndpoint, -1);
        assertThat(handler.supportsReturnType(notSupportedReturnType)).isFalse();
    }

    @Test
    public void handleReturnValue_requestHandledForNullValue() throws Exception {
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        handler.handleReturnValue(null, mock(MethodParameter.class), mavContainer, mock(NativeWebRequest.class));

        assertThat(mavContainer.isRequestHandled()).isTrue();
    }

    @Test
    public void handleReturnValue_hasViewNameAndEmptyRegistry() throws Exception {

        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        handler.handleReturnValue(new MultiAjaxView("dummy"), mock(MethodParameter.class), mavContainer, mock(NativeWebRequest.class));

        assertThat(mavContainer.getViewName()).isEqualTo("dummy");
        assertThat(mavContainer.getModel()).containsKey("ESP_VIEW_REGISTRY");
        assertThat(mavContainer.getModel().get("ESP_VIEW_REGISTRY")).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> registry = (Map<String, String>) mavContainer.getModel().get("ESP_VIEW_REGISTRY");
        assertThat(registry).hasSize(0);
    }

    @Test
    public void handleReturnValue_hasViewNameAndNonEmptyRegistry() throws Exception {
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        MultiAjaxView returnValue = new MultiAjaxView("dummy");
        returnValue.registerView("#test", "test");

        handler.handleReturnValue(returnValue, mock(MethodParameter.class), mavContainer, mock(NativeWebRequest.class));

        assertThat(mavContainer.getViewName()).isEqualTo("dummy");
        assertThat(mavContainer.getModel()).containsKey("ESP_VIEW_REGISTRY");
        assertThat(mavContainer.getModel().get("ESP_VIEW_REGISTRY")).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> registry = (Map<String, String>) mavContainer.getModel().get("ESP_VIEW_REGISTRY");
        assertThat(registry).hasSize(1);
        assertThat(registry).containsEntry("#test", "test");
    }


    public static class DummyController {
        public MultiAjaxView supportedEndpoint() {
            return null;
        }

        public String notSupportedEndpoint() {
            return null;
        }
    }
}