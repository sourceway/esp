package eu.sourceway.esp.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("esp")
public class EspProperties {

    /**
     * Layout to use if none is specified with
     * {@link eu.sourceway.esp.interceptor.Layout}
     */
    private String defaultLayout = "layouts/default";

    /**
     * Name of the model attribute to be used in the layout to include the
     * fragment
     */
    private String viewAttributeName = "view";

    /**
     * Name of the fragment to be used in the layout and the view template.
     */
    private String contentFragmentName = "content";

    /**
     * Path prefix to history.js
     */
    private String resourcesUrl = "/esp";


    public String getDefaultLayout() {
        return defaultLayout;
    }

    public void setDefaultLayout(String defaultLayout) {
        this.defaultLayout = defaultLayout;
    }

    public String getViewAttributeName() {
        return viewAttributeName;
    }

    public void setViewAttributeName(String viewAttributeName) {
        this.viewAttributeName = viewAttributeName;
    }

    public String getContentFragmentName() {
        return contentFragmentName;
    }

    public void setContentFragmentName(String contentFragmentName) {
        this.contentFragmentName = contentFragmentName;
    }

    public String getResourcesUrl() {
        return resourcesUrl;
    }

    public void setResourcesUrl(String resourcesUrl) {
        this.resourcesUrl = resourcesUrl;
    }
}
