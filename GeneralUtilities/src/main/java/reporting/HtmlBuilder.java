package reporting;

import java.util.ArrayList;
import java.util.List;

/**
 * A fluent HTML builder that provides a readable way to construct HTML documents.
 * Uses the builder pattern to chain method calls for creating well-formed HTML.
 */
public class HtmlBuilder {
    private final StringBuilder html;
    private final List<String> openTags;
    private static final String TABLE_CLOSE_TAG = "</td>";
    private static final String LISTITEM_CLOSE_TAG = "</li>";
    
    public HtmlBuilder() {
        this.html = new StringBuilder();
        this.openTags = new ArrayList<>();
    }
    
    /**
     * Adds HTML5 DOCTYPE declaration.
     */
    public HtmlBuilder doctype() {
        html.append("<!DOCTYPE html>");
        return this;
    }
    
    /**
     * Opens an HTML tag with optional language attribute.
     */
    public HtmlBuilder html(String lang) {
        if (lang != null && !lang.isEmpty()) {
            html.append("<html lang=\"").append(lang).append("\">");
        } else {
            html.append("<html>");
        }
        openTags.add("html");
        return this;
    }
    
    /**
     * Opens a head tag.
     */
    public HtmlBuilder head() {
        html.append("<head>");
        openTags.add("head");
        return this;
    }
    
    /**
     * Adds a title element with the specified text.
     */
    public HtmlBuilder title(String title) {
        html.append("<title>").append(escapeHtml(title)).append("</title>");
        return this;
    }
    
    /**
     * Adds a meta tag with name and content attributes.
     */
    public HtmlBuilder meta(String name, String content) {
        html.append("<meta name=\"").append(name).append("\" content=\"").append(content).append("\">");
        return this;
    }
    
    /**
     * Adds a meta tag with charset attribute.
     */
    public HtmlBuilder metaCharset(String charset) {
        html.append("<meta charset=\"").append(charset).append("\">");
        return this;
    }
    
    /**
     * Adds a style element with CSS content.
     */
    public HtmlBuilder style(String css) {
        html.append("<style>").append(css).append("</style>");
        return this;
    }
    
    /**
     * Adds a script element with JavaScript content.
     */
    public HtmlBuilder script(String js) {
        html.append("<script>").append(js).append("</script>");
        return this;
    }
    
    /**
     * Opens a body tag.
     */
    public HtmlBuilder body() {
        html.append("<body>");
        openTags.add("body");
        return this;
    }
    
    /**
     * Adds an h3 heading element.
     */
    public HtmlBuilder h3(String text) {
        html.append("<h3>").append(escapeHtml(text)).append("</h3>");
        return this;
    }
    
    /**
     * Opens a table element with optional CSS class.
     */
    public HtmlBuilder table(String cssClass) {
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append("<table class=\"").append(cssClass).append("\">");
        } else {
            html.append("<table>");
        }
        openTags.add("table");
        return this;
    }
    
    /**
     * Opens a table row element with optional CSS class.
     */
    public HtmlBuilder tr(String cssClass) {
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append("<tr class=\"").append(cssClass).append("\">");
        } else {
            html.append("<tr>");
        }
        openTags.add("tr");
        return this;
    }
    
    /**
     * Adds a table header cell with optional CSS class.
     */
    public HtmlBuilder th(String text, String cssClass) {
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append("<th class=\"").append(cssClass).append("\">").append(escapeHtml(text)).append("</th>");
        } else {
            html.append("<th>").append(escapeHtml(text)).append("</th>");
        }
        return this;
    }
    
    /**
     * Adds a table data cell with optional CSS class.
     */
    public HtmlBuilder td(String text, String cssClass) {
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append("<td class=\"").append(cssClass).append("\">").append(escapeHtml(text)).append(TABLE_CLOSE_TAG);
        } else {
            html.append("<td>").append(escapeHtml(text)).append(TABLE_CLOSE_TAG);
        }
        return this;
    }
    
    /**
     * Adds a table data cell with raw HTML content (no escaping).
     */
    public HtmlBuilder tdRaw(String rawHtml, String cssClass) {
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append("<td class=\"").append(cssClass).append("\">").append(rawHtml).append(TABLE_CLOSE_TAG);
        } else {
            html.append("<td>").append(rawHtml).append(TABLE_CLOSE_TAG);
        }
        return this;
    }
    
    /**
     * Adds a paragraph element.
     */
    public HtmlBuilder p(String text) {
        html.append("<p>").append(escapeHtml(text)).append("</p>");
        return this;
    }
    
    /**
     * Adds a span element with optional CSS class and onclick attribute.
     */
    public HtmlBuilder span(String text, String cssClass, String onclick) {
        html.append("<span");
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append(" class=\"").append(cssClass).append("\"");
        }
        if (onclick != null && !onclick.isEmpty()) {
            html.append(" onclick=\"").append(onclick).append("\"");
        }
        html.append(">").append(escapeHtml(text)).append("</span>");
        return this;
    }
    
    /**
     * Adds a div element with id and CSS class.
     */
    public HtmlBuilder div(String content, String id, String cssClass) {
        html.append("<div");
        if (id != null && !id.isEmpty()) {
            html.append(" id=\"").append(id).append("\"");
        }
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append(" class=\"").append(cssClass).append("\"");
        }
        html.append(">").append(escapeHtml(content)).append("</div>");
        return this;
    }
    
    /**
     * Opens an unordered list element.
     */
    public HtmlBuilder ul() {
        html.append("<ul>");
        openTags.add("ul");
        return this;
    }
    
    /**
     * Adds a list item with optional CSS class.
     */
    public HtmlBuilder li(String content, String cssClass) {
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append("<li class=\"").append(cssClass).append("\">").append(escapeHtml(content)).append(LISTITEM_CLOSE_TAG);
        } else {
            html.append("<li>").append(escapeHtml(content)).append(LISTITEM_CLOSE_TAG);
        }
        return this;
    }
    
    /**
     * Adds a list item with raw HTML content (no escaping).
     */
    public HtmlBuilder liRaw(String rawHtml, String cssClass) {
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append("<li class=\"").append(cssClass).append("\">").append(rawHtml).append(LISTITEM_CLOSE_TAG);
        } else {
            html.append("<li>").append(rawHtml).append(LISTITEM_CLOSE_TAG);
        }
        return this;
    }
    
    /**
     * Adds a bold element.
     */
    public HtmlBuilder b(String text) {
        html.append("<b>").append(escapeHtml(text)).append("</b>");
        return this;
    }
    
    /**
     * Adds a line break.
     */
    public HtmlBuilder br() {
        html.append("<br>");
        return this;
    }
    
    /**
     * Closes the most recently opened tag.
     */
    public HtmlBuilder closeTag() {
        if (!openTags.isEmpty()) {
            String tag = openTags.remove(openTags.size() - 1);
            html.append("</").append(tag).append(">");
        }
        return this;
    }
    
    /**
     * Closes all currently open tags in reverse order.
     */
    public HtmlBuilder closeAll() {
        while (!openTags.isEmpty()) {
            closeTag();
        }
        return this;
    }
    
    /**
     * Adds raw HTML content without any escaping or validation.
     */
    public HtmlBuilder raw(String rawHtml) {
        html.append(rawHtml);
        return this;
    }
    
    /**
     * Escapes special HTML characters to prevent XSS and ensure valid HTML.
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }
    public HtmlBuilder addRawCell(String text) {
        html.append("    <td>")
                .append(text)
                .append("</td>\n");
        return this;
    }

    @Override
    public String toString() {
        return html.toString();
    }
}
