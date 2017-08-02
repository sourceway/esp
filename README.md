# esp

[![](https://jitpack.io/v/eu.sourceway/esp.svg)](https://jitpack.io/#eu.sourceway/esp)
[![Build Status](https://travis-ci.org/sourceway/esp.svg?branch=develop)](https://travis-ci.org/sourceway/esp)

### Usage

Add the jitpack repository
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add dependency
```xml
<dependency>
    <groupId>eu.sourceway</groupId>
    <artifactId>esp</artifactId>
    <version>develop-SNAPSHOT</version>
</dependency>
```

Add the script to your layout
```xml
<head>
    <script data-th-src="@{/esp/history.js}"></script>
</head>
```

Configure the tempalte processor
```
esp.content-fragment-name=content     # name of the thymeleaf fragment to include in the contetn
esp.default-layout=layouts/default    # name of the default layout
esp.resources-url=/esp                # prefix for the esp resources (currently only history.js)
esp.view-attribute-name=view          # name of the thymeleaf attribute that contains the view name
```

Setup your layout
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>ESP-Demo</title>
    <!-- jquery is required -->
    <script data-th-src="@{/webjars/jquery/dist/jquery.min.js}"></script>
    
    <!-- the actual js for this library -->
    <script data-th-src="@{/esp/history.js}"></script>
</head>
<body>

<!-- navigation and other stuff -->

<!-- by default esp uses #content as selector to place new content -->
<!-- to see how you can change this selector see below -->
<div id="content">
    <div data-th-include="${view} :: content" data-th-remove="tag">
        This div will be replaced by the
    </div>
</div>

<!-- optional: configure js part -->
<script>
    $(document).ready(function () {
        ESP.options({
            // regex pattern to tweak external url detection
            externalUrlPattern: /^(\w+:)?\/\//,
            
            // default selector to place requested content 
            contentSelector: "#content",
            
            // the following callbacks will be called directly by jquery.ajax()
            // for parameters passed to each method check: http://api.jquery.com/jquery.ajax/
            beforeSendCallback: null,
            alwaysCallback: null,
            failCallback: null,
            doneCallback: null
        });
    });
</script>
</body>
</html>
```

Create a view template
```html
<!-- only this div is required, but for easier development it can be wrapped by full html dom -->
<!-- to benefit from thymeleafs natural templates -->
<div data-th-fragment="content">

    this is content from index template

</div>
```

Create a controller
```java
@Controller
public class DemoController {

    @GetMapping("/")
    public String index() {
        // return template name
        return "index";
    }
}
```

Create links
```html
<!-- these links are handled by esp -->
<a data-th-href="@{/}"> / </a>
<a data-th-href="@{/page1}">Page 1</a>
<a data-th-href="@{/page2}">Page 2</a>
<a data-th-href="@{/page3}">Page 3</a>

<!-- but you can add `data-esp-ajax="false"` to ignore links -->
<a data-esp-ajax="false" data-th-href="@{/}"> / </a>
<a data-esp-ajax="false" data-th-href="@{/page1}">Page 1</a>
<a data-esp-ajax="false" data-th-href="@{/page2}">Page 2</a>
<a data-esp-ajax="false" data-th-href="@{/page3}">Page 3</a>
```

Forms can be used too
```html
<!-- this for is posted via ajax -->
<form method="post" data-th-action="@{/formtarget}">
    <!-- file uploads are supported -->
</form>

<!-- this for is not handled by this library -->
<form method="post" data-esp-ajax="false" data-th-action="@{/formtarget}">

</form>
```
