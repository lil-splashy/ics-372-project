package edu.UI;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Translate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvgLoader {

    private static final Pattern D_ATTR  = Pattern.compile("\\bd=\"([^\"]+)\"");
    private static final Pattern SW_ATTR = Pattern.compile("\\bstroke-width=\"([^\"]+)\"");

    /** Load an SVG from resources/images/ and apply stroke/fill overrides. */
    public static Group load(String filename, Color stroke, Color fill) {
        return load(filename, stroke, fill, 0, 0);
    }

    /**
     * Load an SVG from resources/images/ and apply stroke/fill overrides.
     * dx/dy correct for SVGs whose paths are offset due to embedded group
     * transforms (e.g. home.svg exported from Sketch uses translate(-285,-560)).
     */
    public static Group load(String filename, Color stroke, Color fill, double dx, double dy) {
        Group group = new Group();
        try (InputStream is = SvgLoader.class.getResourceAsStream("resources/images/" + filename)) {
            if (is == null) {
                System.err.println("[SvgLoader] Not found: " + filename);
                return group;
            }
            String svg = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            Matcher swm = SW_ATTR.matcher(svg);
            double strokeWidth = swm.find() ? Double.parseDouble(swm.group(1)) : 2.0;

            Matcher dm = D_ATTR.matcher(svg);
            while (dm.find()) {
                SVGPath path = new SVGPath();
                path.setContent(dm.group(1));
                path.setFill(fill   != null ? fill   : Color.TRANSPARENT);
                path.setStroke(stroke != null ? stroke : Color.TRANSPARENT);
                if (stroke != null && !stroke.equals(Color.TRANSPARENT)) {
                    path.setStrokeWidth(strokeWidth);
                }
                if (dx != 0 || dy != 0) {
                    path.getTransforms().add(new Translate(dx, dy));
                }
                group.getChildren().add(path);
            }
        } catch (Exception e) {
            System.err.println("[SvgLoader] Error loading " + filename + ": " + e.getMessage());
        }
        return group;
    }
}