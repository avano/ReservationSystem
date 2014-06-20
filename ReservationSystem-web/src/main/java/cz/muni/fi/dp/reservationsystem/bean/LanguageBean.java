/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.other.CookieManager;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Bean that implements methods with locale
 *
 * @author Andrej
 */
public class LanguageBean implements Serializable {
    private static final Logger log = Logger.getLogger("LanguageBean");
    private String locale;
    private String localePattern;

    /**
     * Creates a new instance of language bean
     */
    public LanguageBean() {
        locale = getLocaleFromCookie();
        changeLocalePattern();
    }

    /**
     * Gets locale from cookie
     *
     * @return "en" if there is no cookie, cookie value otherwise
     */
    public static String getLocaleFromCookie() {
        String ret = CookieManager.getCookieValue((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(), "remember_locale");
        if (ret != null) {
            return ret;
        } else {
            return "en";
        }
    }

    /**
     *
     * @return
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Changes language to slovak
     */
    public void changeToSk() {
        locale = "sk";
        CookieManager.addCookie((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), "remember_locale", "sk", 2592000);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("sk"));
        changeLocalePattern();
    }

    /**
     * Changes language to czech
     */
    public void changeToCs() {
        locale = "cs";
        CookieManager.addCookie((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), "remember_locale", "cs", 2592000);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("cs"));
        changeLocalePattern();
    }

    /**
     * Changes language to english
     */
    public void changeToEn() {
        locale = "en";
        CookieManager.addCookie((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), "remember_locale", "en", 2592000);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("en"));
        changeLocalePattern();
    }

    /**
     *
     * @return
     */
    public String getLocalePattern() {
        return localePattern;
    }

    /**
     * Changes locale pattern
     */
    public void changeLocalePattern() {
        if (locale.equals("en")) {
            this.localePattern = "MMM d, yyyy h:mm:ss a";
        }
        else {
            this.localePattern = "d.M.yyyy H:mm:ss";
        }
    }
}
