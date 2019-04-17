package cz.muni.fi.pv260.hw02;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumTests {

    @Test
    public void positiveTest1CheckPostcode() {
        WebDriver browser;
        System.setProperty("webdriver.chrome.driver", "/home/xvanko/everything/pv260-hw02/chromedriver");
        browser = new ChromeDriver();
        browser.get("https://cs.wikipedia.org/wiki/Povina");
        String text = ((ChromeDriver) browser).findElementByClassName("infobox").getText();
        assertTrue(text.contains("PSČ 023 33"));
        browser.close();
    }

    @Test
    public void positiveTest2CheckWebsiteURL() {
        WebDriver browser;
        System.setProperty("webdriver.chrome.driver", "/home/xvanko/everything/pv260-hw02/chromedriver");
        browser = new ChromeDriver();
        browser.get("https://cs.wikipedia.org/wiki/Povina");
        List<WebElement> elements = ((ChromeDriver) browser).findElementsByClassName("interlanguage-link-target");
        assertTrue(elements.parallelStream().map(WebElement::getText).anyMatch(e -> e.contains("Slovenčina")));
        browser.close();
    }

    @Test
    public void negativeTest1LookForEmperor() {
        WebDriver browser;
        System.setProperty("webdriver.chrome.driver", "/home/xvanko/everything/pv260-hw02/chromedriver");
        browser = new ChromeDriver();
        browser.get("https://cs.wikipedia.org/wiki/Slovensko");
        assertFalse(((ChromeDriver) browser).findElementsByClassName("mw-redirect").parallelStream().map(WebElement::getText).anyMatch(e -> e.contains("Prezident") && e.contains("Gašparovič")));
        browser.close();
    }

}
