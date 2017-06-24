package eu.sourceway.demo;

import eu.sourceway.esp.MultiAjaxView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/index")
    public String indexRedirect() {
        return "redirect:/";
    }

    @GetMapping("/page1")
    public String page1() {
        return "page1";
    }

    @GetMapping("/page2")
    public String page2() {
        return "page2";
    }

    @GetMapping("/page3")
    public String page3() {
        return "page3";
    }

    @GetMapping("/multi1")
    public MultiAjaxView multi1() {
        MultiAjaxView mav = new MultiAjaxView("page1");
        mav.registerView("#otherContent", "page2");
        return mav;
    }

    @GetMapping("/multi2")
    public MultiAjaxView multi2() {
        MultiAjaxView mav = new MultiAjaxView("page2");
        mav.registerView("#otherContent", "page3");
        return mav;
    }

    @GetMapping("/multi3")
    public MultiAjaxView multi3() {
        MultiAjaxView mav = new MultiAjaxView("page3");
        mav.registerView("#otherContent", "page1");
        return mav;
    }
}
