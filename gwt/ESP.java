package plx.update.thiz.client;

import java.util.Optional;

import plx.update.thiz.client.jsi.Document;
import plx.update.thiz.client.jsi.FormData;
import plx.update.thiz.client.jsi.History;
import plx.update.thiz.client.jsi.Window;
import plx.update.thiz.client.jsi.XMLHttpRequest;
import plx.update.thiz.client.jsi.events.Event;
import plx.update.thiz.client.jsi.events.MouseEvent;
import elemental.dom.Element;

public class ESP {

	private final Settings settings;

	public ESP() {
		this(new Settings());
	}

	public ESP(Settings settings) {
		this.settings = settings;

		if (!History.get().isHistoryAvailable()) {
			return;
		}

		Document.get().addSubmitHandler(this::handleFormSubmit);

		Document.get().addClickHandler(this::handleClickEvent);

		History.get().replaceState(Document.get().location.toString());

		Window.get().addEventListener("popstate", event -> {
			History.State state = History.get().state;
			if (state != null) {
				loadContent(state.url, false);
			}
			return null;
		});
	}

	private void formSubmit(Element form) {
		String url = form.getAttribute("action");
		String method = form.getAttribute("method");
		if (method == null) {
			method = "post";
		}
		FormData formData = new FormData(form);

		boolean isGet = "get".equalsIgnoreCase(method);
		if (isGet) {
			if (url.contains("?")) {
				url += "&";
			} else {
				url += "?";
			}
			url += formData.asQueryString();
		}

		fireXhr(method, url, true, formData);
	}

	private void loadContent(String url, boolean updateHistory) {
		fireXhr("get", url, updateHistory, null);
	}

	private void fireXhr(String method, String url, boolean updateHistory,
	        FormData formData) {
		XMLHttpRequest xhr = new XMLHttpRequest();
		xhr.open(method, url);

		xhr.setRequestHeader("X-ESP-AJAX-REQUEST", "true");
		xhr.setRequestHeader("Accept", "text/html");

		xhr.onLoad(() -> {
			if (xhr.status == 200) {
				String response = xhr.responseText;
				String currentUrl = xhr.getResponseHeader("X-ESP-CURRENT-URL");
				if (currentUrl == null) {
					currentUrl = url;
				}

				setContent(currentUrl, response, updateHistory);

			} else {

				setContent(url, xhr.responseText, updateHistory);
			}
		});

		if (formData == null) {
			xhr.send();
		} else {
			xhr.send(formData);
		}
	}

	private void setContent(String url, String data, boolean updateHistory) {
		if (updateHistory) {
			History.get().pushState(url);
		}

		Element helper = Document.get().createElement("div");
		helper.setInnerHTML(data);

		for (Element el = helper.getFirstElementChild(); el != null; el =
		        el.getNextElementSibling()) {
			String selector = el.getAttribute("data-esp-target-selector");

			Element target = "$default$".equalsIgnoreCase(selector)
			        ? Document.get()
			            .getElementById(settings.getDefaultContentId())
			        : Document.get().getElementById(selector);

			if (target != null) {
				target.setInnerHTML(el.getInnerHTML());
			}
		}
	}

	private Optional<Element> findElement(String tag, Event event) {
		Optional<Element> result = Optional.empty();
		if (tag.equalsIgnoreCase(event.target.getTagName())) {
			result = Optional.of(event.target);
		}
		int length = event.path.length();
		for (int i = 0; i < length; i++) {
			Element element = event.path.get(i);
			if (tag.equalsIgnoreCase(element.getTagName())) {
				result = Optional.of(element);
				break;
			}
		}
		return result.filter(this::isAjaxEnabled).filter(this::isInternalHref);
	}

	// @formatter:off
	protected native boolean isExternalRegex(String href)/*-{
        return href !== null
            && href !== undefined
            && href.search(/^(\w+:)?\/\//) !== -1;
    }-*/;
    // @formatter:on

	private boolean isInternalHref(Element element) {
		String tagName = element.getTagName();
		String href =
		        "a".equalsIgnoreCase(tagName) ? element.getAttribute("href")
		                : "form".equalsIgnoreCase(tagName)
		                        ? element.getAttribute("action") : null;
		return href != null && href.indexOf("#") != 0 && !isExternalRegex(href);
	}

	private boolean isAjaxEnabled(Element element) {
		String ajaxEnabled = element.getAttribute("data-esp-ajax");
		return ajaxEnabled == null ? settings.isEnableAjaxByDefaut()
		        : "true".equalsIgnoreCase(ajaxEnabled);
	}

	private boolean handleFormSubmit(Event event) {
		return findElement("form", event).map(element -> {
			event.preventDefault();

			formSubmit(element);

			return false;
		}).orElse(true);
	}

	private boolean handleClickEvent(MouseEvent event) {
		// ignore middle mouse button or events with ctrl/meta modifier
		if (event.which == 2 || event.metaKey || event.ctrlKey) {
			return true;
		}

		return findElement("a", event).map(element -> {
			event.preventDefault();

			loadContent(element.getAttribute("href"), true);

			return false;
		}).orElse(true);
	}

	public static class Settings {

		private boolean enableAjaxByDefaut = true;
		private String defaultContentId = "content";

		public boolean isEnableAjaxByDefaut() {
			return enableAjaxByDefaut;
		}

		public Settings enableAjaxByDefaut(boolean enableAjaxByDefaut) {
			this.enableAjaxByDefaut = enableAjaxByDefaut;
			return this;
		}

		public String getDefaultContentId() {
			return defaultContentId;
		}

		public Settings defaultContentId(String defaultContentId) {
			this.defaultContentId = defaultContentId;
			return this;
		}
	}
}
